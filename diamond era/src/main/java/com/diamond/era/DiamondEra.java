package com.diamond.era;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DiamondEra extends JavaPlugin implements Listener {

    private final Set<Material> bannedTrades = EnumSet.of(
        Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
        Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.DIAMOND_HELMET,
        Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS
    );

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Remove Netherite Upgrade Template from everyone every 2 seconds
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().remove(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
            }
        }, 0L, 40L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTradeAcquire(VillagerAcquireTradeEvent event) {
        if (bannedTrades.contains(event.getRecipe().getResult().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            List<MerchantRecipe> recipes = new ArrayList<>();
            for (MerchantRecipe recipe : villager.getRecipes()) {
                // Remove Diamond Trades
                if (bannedTrades.contains(recipe.getResult().getType())) continue;
                
                // Reset Trades: Max Uses to 999,999 and Price Multiplier to 0
                recipe.setMaxUses(999999);
                recipe.setUses(0);
                recipe.setDemand(0);
                recipe.setPriceMultiplier(0.0f);
                recipes.add(recipe);
            }
            villager.setRecipes(recipes);
        }
    }

    @EventHandler
    public void onTradeSelect(TradeSelectEvent event) {
        // Instant Restock: Whenever a trade is clicked, reset its uses
        if (event.getInventory().getHolder() instanceof Villager villager) {
            MerchantRecipe recipe = villager.getRecipe(event.getIndex());
            recipe.setUses(0);
            recipe.setDemand(0);
        }
    }

    @EventHandler
    public void onSmithingClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.SMITHING) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                event.setCancelled(true);
                event.getWhoClicked().getInventory().remove(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
                event.setCurrentItem(null);
            }
        }
    }
}
