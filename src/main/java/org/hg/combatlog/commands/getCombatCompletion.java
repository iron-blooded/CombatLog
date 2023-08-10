package org.hg.combatlog.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.hg.combatlog.CombatLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class getCombatCompletion implements TabCompleter {
    private final CombatLog plugin;

    public getCombatCompletion(CombatLog plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command syka, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length <= 0){
            return list;
        }
        int i = args.length-2;
        if (args.length % 2 != 0){
            if (!Arrays.asList(args).contains("time:")) {
                list.add("time:");
            }
            if (!Arrays.asList(args).contains("radius:")) {
                list.add("radius:");
            }
            if (!Arrays.asList(args).contains("attacking:")) {
                list.add("attacking:");
            }
            if (!Arrays.asList(args).contains("victim:")) {
                list.add("victim:");
            }
        }
        else {
            String command = args[i];
            String arg = args[i + 1];
            if (command.contains("time:") || command.contains("t:")){
                try {
                    if (Integer.parseInt(arg)>=0){
                        list.add(arg + "s");
                        list.add(arg + "m");
                        list.add(arg + "h");
                        list.add(arg + "d");
                        list.add(arg + "w");
                    }
                }
                catch (Exception e){
                    if (arg.equals("")) {
                        for (int i1 = 0; i1 <= 9; i1++) {
                            list.add("" + i1);
                        }
                    }
                    else {
                        list.add(arg);
                    }
                }
            }
            else if (command.contains("attacking:") || command.contains("a:")||command.contains("v:") || command.contains("victim:")){
                for (Player player: Bukkit.getOnlinePlayers()){
                    list.add(player.getName());
                }
            }
            else if (command.contains("r:")||command.contains("radius:")){
                for (int i1 = 0; i1 <= 9; i1++){
                    list.add(""+i1);
                }
            }
        }
        return list;
        /*
        String arg = args[args.length-1];
        if (arg.contains(":")){
            if (arg.contains("time:") || arg.contains("t:")){
                if (arg.charAt(arg.length()-1) != ':') {
                    try {
                        if (Integer.parseInt(String.valueOf(arg.charAt(arg.length() - 1))) >= 0) {
                            list.add(arg + "s");
                            list.add(arg + "m");
                            list.add(arg + "h");
                            list.add(arg + "d");
                            list.add(arg + "w");
                        }
                    }
                    catch (Exception e){}
                }
                else {
                    for (int i = 0; i <= 9; i++){
                        list.add(arg+i);
                    }
                }
            }
            else if (arg.contains("attacking:") || arg.contains("a:")||arg.contains("v:") || arg.contains("victim:")){
                for (Player player: Bukkit.getOnlinePlayers()){
                    list.add(arg.split(":")[0]+":"+player.getName());
                }
            }
            else if (arg.contains("r:")||arg.contains("radius:")){
                for (int i = 0; i <= 9; i++){
                        list.add(arg+i);
                    }
            }
        }
        else {
            list.add("t:");
            list.add("time:");
            list.add("radius:");
            list.add("r:");
            list.add("attacking:");
            list.add("a:");
            list.add("victim:");
            list.add("v:");
        }
        return list;
         */
    }
}
