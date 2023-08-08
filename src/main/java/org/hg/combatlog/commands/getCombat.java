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
        }
        if (time > 604800){
            commandSender.sendMessage(ChatColor.RED+"Меньше время укажи!");
            return true;
        }
//        commandSender.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"╔═════════Yog-Sothoth═════════╗");
        List<TextComponent> messages = forDisplay.getLastCombat((Player) commandSender, plugin, time);
        double size = messages.size();
        size = size/10;
        size = Math.ceil(size);
        if (messages.size() > 10) {
            messages.removeAll(messages.subList(11, messages.size()));
        }
        new decorationMessageLog(messages, 1, (int) size);
        if (time > 0){
            for (TextComponent message: messages) {
                commandSender.sendMessage(message);
            }
        }
        /// ... здесь список должен быть
        return true;
    }


}
