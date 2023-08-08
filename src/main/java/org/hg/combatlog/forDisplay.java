package org.hg.combatlog;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class forDisplay {
    public static List<TextComponent> getLastCombat(Player player, CombatLog plugin ,int time){
        List<TextComponent> messages = new ArrayList<>();
        for (database.CombatLine log: plugin.log.getLinesLastSeconds(time)){
            messages.add(forDisplay.generateLine(log.time, log.victim, log.attacker, log.damage));
        }
        if (messages.size() > 10) {
            messages.removeAll(messages.subList(11, messages.size()));
        }
        return messages;
    }
    private static TextComponent generateLine(long time, PlayerSerializer.decompressedPlayer victim, PlayerSerializer.decompressedPlayer attacker, double damage){
        TextComponent message = new TextComponent("");
        message.addExtra(ChatColor.GRAY+getTime(time)+" ago: "+ChatColor.RESET);
        TextComponent damager = new TextComponent(attacker.name);
        damager.setColor(ChatColor.RED.asBungee());
        String damagerLore = "Находился на "+(int)attacker.location.getX()+" "+(int)attacker.location.getY()+" "+(int)attacker.location.getZ();
        damager.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(damagerLore)));
        damager.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, locationToTpCommand(attacker.location)));
        message.addExtra(damager);
        TextComponent weapon = new TextComponent(" 🗡 ");
        weapon.setColor(ChatColor.YELLOW.asBungee());
        weapon.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getDisplayWeapon(attacker.weapon)));
        message.addExtra(weapon);
        TextComponent attacked = new TextComponent(victim.name);
        attacked.setColor(ChatColor.GREEN.asBungee());
        String victimLore = "Находился на "+(int)victim.location.getX()+" "+(int)victim.location.getY()+" "+(int)victim.location.getZ();
        attacked.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(victimLore)));
        attacked.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, locationToTpCommand(victim.location)));
        message.addExtra(attacked);
        message.addExtra(ChatColor.AQUA+ " -"+damage+"❤");
        message.addExtra(".");
        return message;
    }
    private static void addLine(TextComponent properties, String text){
        if (properties.toLegacyText().length()>2) {
            properties.addExtra(ChatColor.RESET + "\n" + text + ChatColor.RESET);
        }
        else {
            properties.addExtra(text + ChatColor.RESET);
        }
    }
    private static BaseComponent[] getDisplayWeapon(ItemStack itemStack){
        TextComponent properties = new TextComponent();
        if (itemStack!=null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (!itemMeta.getDisplayName().isEmpty()) {
                    addLine(properties, itemMeta.getDisplayName());
                }
                else {
                    addLine(properties, itemStack.getType().name());
                }
                for (Enchantment enchantment : getEnchantments(itemStack).keySet()) {
                    addLine(properties, ChatColor.GRAY + enchantment.getName() + " " + itemStack.getEnchantmentLevel(enchantment));
                }
                if (itemMeta.getAttributeModifiers() != null) {
                    addLine(properties, "");
                    for (Attribute attribute : itemMeta.getAttributeModifiers().keySet()) {
                        double number = 0;
                        for (AttributeModifier modifier : itemMeta.getAttributeModifiers().get(attribute)) {
                            number = modifier.getAmount();
                        }
                        addLine(properties, ChatColor.GREEN + attribute.name() + " " + number);
                    }
                }
            }
            addLine(properties, ChatColor.DARK_GRAY + itemStack.getType().name().toLowerCase());
        }
        return new TextComponent[]{properties};
    }
    private static Map<Enchantment, Integer> getEnchantments(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta != null) {
                map.putAll(meta.getStoredEnchants());
            }
            map.putAll(item.getEnchantments());
        } else {
            return new HashMap<>(item.getEnchantments());
        }
        return map;
    }
    private static String locationToTpCommand(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return "/tp @p " + x + " " + y + " " + z + " " + yaw + " " + pitch;
    }

    private static String getTime(long time){
        long time_now = System.currentTimeMillis();
        long seconds = (time_now-time)/1000;
        if (seconds < 0) {
            throw new IllegalArgumentException("Количество секунд не может быть отрицательным.");
        }
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            double minutes = seconds / 60.0;
            return String.format("%.1fm", minutes);
        } else if (seconds < 86400) {
            double hours = seconds / 3600.0;
            return String.format("%.1fh", hours);
        } else if (seconds < 604800) {
            double days = seconds / 86400.0;
            return String.format("%.1fd", days);
        } else {
            double weeks = seconds / 604800.0;
            return String.format("%.1fw", weeks);
        }
    }
    public static long parseTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            throw new IllegalArgumentException("Некорректная строка времени.");
        }
        char unit = timeString.charAt(timeString.length() - 1);
        double value = Double.parseDouble(timeString.substring(0, timeString.length() - 1));
        switch (unit) {
            case 's':
                return (long) value;
            case 'm':
                return (long) (value * 60);
            case 'h':
                return (long) (value * 3600);
            case 'd':
                return (long) (value * 86400);
            case 'w':
                return (long) (value * 604800);
            default:
                throw new IllegalArgumentException("Некорректный формат строки времени.");
        }
    }
}
