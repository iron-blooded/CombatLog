package org.hg.combatlog;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;

import org.hg.combatlog.commands.combatPage;
import org.hg.combatlog.commands.getCombat;
import org.hg.combatlog.events.PVPdamage;

public final class CombatLog extends JavaPlugin implements Listener{
    public database log;
    private static HashMap<String, List<TextComponent>> history_command = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new PVPdamage(this), this);
        Objects.requireNonNull(getCommand("get_combat")).setExecutor(new getCombat(this));
        Objects.requireNonNull(getCommand("combat_page")).setExecutor(new combatPage(this));
        log = new database(this);
    }
    public static class historyCommand{
        public void add(String string, List<TextComponent> list){
            list = new ArrayList<>(list);
            history_command.put(string, list);
        }
        public boolean contain(String string){
            return history_command.containsKey(string);
        }
        public List<TextComponent> get(String string){
            if (contain(string)){
                return new ArrayList<>(history_command.get(string));
            }
            else {
                return null;
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            log.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
