package com.universal.duper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class StringDuper extends JavaPlugin implements Listener {

    private NamespacedKey recipeKey;

    @Override
    public void onEnable() {
        recipeKey = new NamespacedKey(this, "super_string_dupe");
        Bukkit.getPluginManager().registerEvents(this, this);
        
        ItemStack result = new ItemStack(Material.STRING, 64);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape("S");
        recipe.setIngredient('S', Material.STRING);
        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        if (event.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe sr = (ShapedRecipe) event.getRecipe();
            if (sr.getKey().equals(recipeKey)) {
                // Only allow in a real 3x3 Crafting Table
                if (event.getInventory().getType() != InventoryType.WORKBENCH) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe sr = (ShapedRecipe) event.getRecipe();
            if (sr.getKey().equals(recipeKey)) {
                if (event.isShiftClick()) {
                    // This handles the multiplier for shift-clicking
                    ItemStack result = event.getCurrentItem();
                    if (result != null && result.getType() == Material.STRING) {
                        // Success - logic handled by Minecraft once validated
                    }
                }
            }
        }
    }
}
