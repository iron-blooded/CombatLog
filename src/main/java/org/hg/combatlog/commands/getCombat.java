package org.hg.combatlog.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.kyori.adventure.text.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.ItemMeta;
import org.hg.combatlog.CombatLog;
import org.jetbrains.annotations.NotNull;
import org.hg.combatlog.*;
import org.hg.combatlog.PlayerSerializer.*;
import net.md_5.bungee.api.chat.ItemTag;
import java.util.List;

public class getCombat implements CommandExecutor {
    private final CombatLog plugin;

    public getCombat(CombatLog combatLog) {
        plugin = combatLog;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int time = -1;
        for (String str: strings){
            if (str.contains("time:")){
                time = (int) parseTime(str.replace("time:", ""));
            }
        }
        if (time > 604800){
            commandSender.sendMessage(ChatColor.RED+"Меньше время укажи!");
            return true;
        }
        if (time > 0){
            for (database.CombatLine log: plugin.log.getLinesLastSeconds(time)){
                commandSender.sendMessage(generateLine(log.time, log.victim, log.attacker, log.damage));
            }
        }
        return true;
    }
    private TextComponent generateLine(long time, decompressedPlayer victim, decompressedPlayer attacker, double damage){
        TextComponent message = new TextComponent("");
        message.addExtra(ChatColor.GRAY+getTime(time)+" ago: "+ChatColor.RESET);
        TextComponent damager = new TextComponent(attacker.name);
        damager.setColor(ChatColor.RED.asBungee());
        String damagerLore = "Находился на "+(int)attacker.location.getX()+" "+(int)attacker.location.getY()+" "+(int)attacker.location.getZ();
        damager.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(damagerLore)));
        damager.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, locationToTpCommand(attacker.location)));
        message.addExtra(damager);
        TextComponent weapon = new TextComponent(" \uD83D\uDDE1 ");
        weapon.setColor(ChatColor.YELLOW.asBungee());
        message.addExtra(weapon);
        /// ...
        ItemMeta itemMeta = attacker.weapon.getItemMeta();
        if (itemMeta != null) {
            TextComponent properties = new TextComponent();
            properties.addExtra(itemMeta.displayName().color()+"123");
            weapon.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{properties}));
        }
        /// ...
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
    public String locationToTpCommand(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return "/tp @p " + x + " " + y + " " + z + " " + yaw + " " + pitch;
    }

    private String getTime(long time){
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
    public long parseTime(String timeString) {
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
