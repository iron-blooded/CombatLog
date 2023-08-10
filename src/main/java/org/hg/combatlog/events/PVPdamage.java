package org.hg.combatlog.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.hg.combatlog.CombatLog;

public class PVPdamage implements Listener {
    private static CombatLog plugin;
    public PVPdamage(CombatLog plugin){
        PVPdamage.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()){
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
                return;
            }
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();
            double damage = event.getFinalDamage();
            plugin.log.addLine(victim, attacker, damage);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()){
            return;
        }
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        // Проверяем, что нанесен урон из лука
        if ((damager instanceof Arrow)) {
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
            return;
        } else if (damager instanceof Trident) {
            Trident trident = (Trident) damager;
            if (!(trident.getShooter() instanceof Player)) {
                return;
            }
            Player shooter = (Player) trident.getShooter();
            if (!(target instanceof Player)) {
                return;
            }
            Player victim = (Player) target;
            double damage = event.getFinalDamage();
            plugin.log.addLine(victim, ((Player) trident.getShooter()).getPlayer(), damage);
            return;
        }
    }

}
