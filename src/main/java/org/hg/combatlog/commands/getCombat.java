package org.hg.combatlog.commands;

import net.md_5.bungee.api.chat.*;
import net.kyori.adventure.text.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.hg.combatlog.CombatLog;
import org.jetbrains.annotations.NotNull;
import org.hg.combatlog.*;
import org.hg.combatlog.PlayerSerializer.*;
import java.util.Collections;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

public class getCombat implements CommandExecutor {
    private final CombatLog plugin;

    public getCombat(CombatLog combatLog) {
        plugin = combatLog;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int time = -1;
        int radius = 10;
        String victim = null;
        String attacker = null;
        for (String str: strings){
            if (str.contains("time:") || str.contains("t:")){
                try {
                    time = (int) forDisplay.parseTime(str.replace("time:", "").replace("t:", ""));
                }
                catch (Exception e){
                    commandSender.sendMessage("Укажите нормальное время!");
                    return true;
                }
            }
            else if (str.contains("r:") || str.contains("radius:")){
                try {
                    radius = Integer.parseInt(str.replace("r:", "").replace("radius:", ""));
                }
                catch (Exception e){
                    commandSender.sendMessage("Укажите нормальный радиус!");
                    return true;
                }
//                if (radius > 60){
//                    commandSender.sendMessage("Меньше радиус сделай!");
//                    return true;
//                }
            }
            else if (str.contains("attacking:") || str.contains("a:")){
                attacker = str.replace("attacking:", "").replace("a:", "");
            } else if (str.contains("v:") || str.contains("victim:")) {
                victim = str.replace("victim:", "").replace("v:", "");
            }
        }
        if (time > 604800){
            commandSender.sendMessage(ChatColor.RED+"Меньше время укажи!");
            return true;
        }
        List<TextComponent> messages = forDisplay.getLastCombat((Player) commandSender, plugin, time, radius, attacker, victim);
        double size = messages.size();
        if (size <= 0){
            commandSender.sendMessage(ChatColor.RED+"Ничего не найдено!");
            return true;
        }
        size = size/10;
        size = Math.ceil(size);
        if (messages.size() > 10) {
            messages.removeAll(messages.subList(11, messages.size()));
        }
        Collections.reverse(messages);
        new decorationMessageLog(messages, 1, (int) size);
        if (time > 0){
            for (TextComponent message: messages) {
                commandSender.sendMessage(message);
            }
        }

        return true;
    }


}
