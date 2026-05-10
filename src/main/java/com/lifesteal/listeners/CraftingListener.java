package com.lifesteal.listeners;

import com.lifesteal.LifeStealPlugin;
import com.lifesteal.managers.HeartManager;
import com.lifesteal.utils.HeartItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.NamespacedKey;

public class CraftingListener implements Listener {

    private final LifeStealPlugin plugin;
    private final HeartManager heartManager;

    public CraftingListener(LifeStealPlugin plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
        registerHeartRecipe();
    }

    private void registerHeartRecipe() {
        ItemStack heartItem = HeartItem.createHeartItem(1);
        NamespacedKey key = new NamespacedKey(plugin, "heart_item");

        ShapedRecipe recipe = new ShapedRecipe(key, heartItem);
        recipe.shape("DDD", "DSD", "DDD");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('S', Material.NETHER_STAR);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void onCraftHeart(CraftItemEvent event) {
        if (HeartItem.isHeartItem(event.getRecipe().getResult())) {
            if (event.getWhoClicked() instanceof Player player) {
                player.sendMessage("§6§lYou crafted a Heart! §r§6Right-click it to gain +1 max heart.");
            }
        }
    }

    @EventHandler
    public void onUseHeart(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !HeartItem.isHeartItem(item)) return;

        event.setCancelled(true);

        int currentHearts = heartManager.getHearts(player);
        int maxHearts = heartManager.getMaxHearts();

        if (currentHearts >= maxHearts) {
            player.sendMessage("§c§lYou already have the maximum number of hearts! §r§c(" + maxHearts + "/" + maxHearts + ")");
            return;
        }

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().removeItem(item);
        }

        heartManager.addHeart(player, 1);
        int newHearts = heartManager.getHearts(player);

        player.sendMessage("§a§l+1 Heart! §r§aYou now have §e" + newHearts + "§a/" + maxHearts + " hearts.");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
}
