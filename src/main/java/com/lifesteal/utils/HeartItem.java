package com.lifesteal.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class HeartItem {

    private static final String HEART_ITEM_TAG = "§4§lHeart";

    public static ItemStack createHeartItem(int amount) {
        ItemStack item = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(HEART_ITEM_TAG);
            List<String> lore = Arrays.asList(
                "§7Right-click to gain §c+1 §7max heart.",
                "§7Craft with §64 Netherite Ingots §7+ §64 Beacons §7+ §fNether Star§7."
            );
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isHeartItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && HEART_ITEM_TAG.equals(meta.getDisplayName());
    }
}
