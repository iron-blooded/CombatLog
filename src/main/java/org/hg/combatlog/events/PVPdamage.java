package org.hg.combatlog.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.hg.combatlog.CombatLog;

public class PVPdamage implements Listener {
    private static CombatLog plugin;
    public PVPdamage(CombatLog plugin){
        PVPdamage.plugin = plugin;
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();
            double damage = event.getFinalDamage();
            plugin.log.addLine(victim, attacker, damage);
        }
    }
    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        // Проверяем, что нанесен урон из лука
        if (!(damager instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) damager;
        // Проверяем, что стрела была выпущена игроком
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) arrow.getShooter();
        // Проверяем, что попадание произошло по другому игроку
        if (!(target instanceof Player)) {
            return;
        }
        Player victim = (Player) target;
        double damage = event.getFinalDamage();
        plugin.log.addLine(victim, ((Player) arrow.getShooter()).getPlayer(), damage);
    }

}
