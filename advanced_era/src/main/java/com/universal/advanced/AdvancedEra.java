package com.universal.advanced;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedEra extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("AdvancedEra Enabled: Explosions restricted, Poison disabled, Particles hidden.");
    }

    // --- EXPLOSION REMOVAL ---
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCrystalExplode(EntityExplodeEvent event) {
        // Remove End Crystal explosions outside of the Nether
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
            if (event.getLocation().getWorld().getEnvironment() != World.Environment.NETHER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnchorExplode(BlockExplodeEvent event) {
        // Remove Respawn Anchor explosions outside of the Nether
        if (event.getBlock().getType() == Material.RESPAWN_ANCHOR) {
            if (event.getBlock().getWorld().getEnvironment() != World.Environment.NETHER) {
                event.setCancelled(true);
            }
        }
    }

    // --- POISON & PARTICLE REMOVAL ---
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // 1. Instantly remove Poison
        if (event.getNewEffect() != null && event.getNewEffect().getType().equals(PotionEffectType.POISON)) {
            event.setCancelled(true);
            player.removePotionEffect(PotionEffectType.POISON);
        }

        // 2. Remove particles from Invisibility (Keep only for players)
        if (event.getNewEffect() != null && event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            if (event.getNewEffect().hasParticles()) {
                event.setCancelled(true);
                PotionEffect current = event.getNewEffect();
                // Re-apply with ambient=false, particles=false, icon=true
                player.addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY, 
                    current.getDuration(), 
                    current.getAmplifier(), 
                    false, false, true
                ));
            }
        }
    }
}
