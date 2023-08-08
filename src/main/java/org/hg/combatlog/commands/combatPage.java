package org.hg.combatlog.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hg.combatlog.CombatLog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import org.hg.combatlog.CombatLog.historyCommand;
import org.hg.combatlog.decorationMessageLog;
public class combatPage implements CommandExecutor {
    private final CombatLog plugin;

    public combatPage(CombatLog plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        int number = 0;
        try {
            number = Integer.parseInt(args[0])-1;
        }
        catch (Exception e){
            sender.sendMessage("Введите правильный номер страницы!");
            return true;
        }
        if (new historyCommand().contain(player.getName())){
            List<TextComponent> messages = new historyCommand().get(player.getName());
            double size = messages.size();
            size = size/10;
            size = Math.ceil(size);
            messages = Slice(messages, number*10, number*10+10);
            new decorationMessageLog(messages, number+1, (int) size);
            for (TextComponent message: messages){
                sender.sendMessage(message);
            }
            return true;
        }
        else {
            sender.sendMessage("Пропишите хоть одну команду!");
            return true;
        }
    }
    public static <T> List<T> Slice(List<T> list, int startIndex, int endIndex) {
        int size = list.size();
        if (startIndex >= size) {
            return new ArrayList<>();
        }
        endIndex = Math.min(endIndex, size);
        return list.subList(startIndex, endIndex);
    }
}
