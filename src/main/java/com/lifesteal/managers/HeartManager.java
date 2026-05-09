package com.lifesteal.managers;

import com.lifesteal.LifeStealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HeartManager {

    private final LifeStealPlugin plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    private final int minHearts;
    private final int maxHearts;
    private final int startingHearts;
    private final boolean banOnElimination;

    public HeartManager(LifeStealPlugin plugin) {
        this.plugin = plugin;
        this.minHearts = plugin.getConfig().getInt("min-hearts", 1);
        this.maxHearts = plugin.getConfig().getInt("max-hearts", 20);
        this.startingHearts = plugin.getConfig().getInt("starting-hearts", 10);
        this.banOnElimination = plugin.getConfig().getBoolean("ban-on-elimination", true);
        loadDataFile();
    }

    private void loadDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveAllData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHearts(Player player) {
        String key = player.getUniqueId().toString() + ".hearts";
        if (!dataConfig.contains(key)) {
            setHearts(player, startingHearts);
            return startingHearts;
        }
        return dataConfig.getInt(key);
    }

    public void setHearts(Player player, int hearts) {
        hearts = Math.max(minHearts, Math.min(maxHearts, hearts));
        dataConfig.set(player.getUniqueId().toString() + ".hearts", hearts);
        saveAllData();
        applyHearts(player, hearts);
    }

    public void addHeart(Player player, int amount) {
        int current = getHearts(player);
        setHearts(player, current + amount);
    }

    public void removeHeart(Player player, int amount) {
        int current = getHearts(player);
        int newHearts = current - amount;
        if (newHearts < minHearts) {
            eliminatePlayer(player);
            return;
        }
        setHearts(player, newHearts);
    }

    public void applyHearts(Player player, int hearts) {
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr != null) {
            double newMax = hearts * 2.0;
            attr.setBaseValue(newMax);
            if (player.getHealth() > newMax) {
                player.setHealth(newMax);
            }
        }
    }

    public void eliminatePlayer(Player player) {
        dataConfig.set(player.getUniqueId().toString() + ".eliminated", true);
        dataConfig.set(player.getUniqueId().toString() + ".hearts", 0);
        saveAllData();
        player.sendMessage("§c§lYou have been eliminated! You have no hearts left.");
        if (banOnElimination) {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(
                player.getName(),
                "§c§lEliminated! You ran out of hearts on the Lifesteal server.",
                null,
                "LifeSteal"
            );
            player.kickPlayer("§c§lYou have been eliminated!\nYou ran out of hearts.");
        }
    }

    public void revivePlayer(Player player) {
        dataConfig.set(player.getUniqueId().toString() + ".eliminated", false);
        setHearts(player, startingHearts);
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(player.getName());
        player.sendMessage("§a§lYou have been revived with " + startingHearts + " hearts!");
    }

    public boolean isEliminated(UUID uuid) {
        return dataConfig.getBoolean(uuid.toString() + ".eliminated", false);
    }

    public int getMinHearts() { return minHearts; }
    public int getMaxHearts() { return maxHearts; }
    public int getStartingHearts() { return startingHearts; }
}
