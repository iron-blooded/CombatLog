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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command syka, @NotNull String s, @NotNull String[] strings) {
        int time = 60*2;
        int radius = 10;
        String victim = null;
        String attacker = null;
        for (int i = 0; i < strings.length; i+=2){
            String command = strings[i];
            String arg = "";
            try {
                arg = strings[i + 1];
            } catch (Exception e){
                commandSender.sendMessage(ChatColor.RED+ "Вы забыли указать аргумент к "+command);
                return true;
            }
            if (command.equals("time:")||command.equals("t:")){
                try {
                    time = (int) forDisplay.parseTime(arg);
                } catch (Exception e){
                    commandSender.sendMessage(ChatColor.RED+"Укажите нормальное время!");
                    return true;
                }
            }
            else if (command.equals("radius:")||command.equals("r:")) {
                try {
                    radius = Integer.parseInt(arg);
                } catch (Exception e){
                    commandSender.sendMessage(ChatColor.RED+"Укажите нормальный радиус!");
                    return true;
                }
            }
            else if (command.equals("attacking:") || command.equals("r:")) {
                attacker = arg;
            } else if (command.equals("v:") || command.equals("victim:")) {
                victim = arg;
            }
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
