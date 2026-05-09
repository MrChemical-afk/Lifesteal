package com.lifesteal;

import com.lifesteal.commands.HeartCommand;
import com.lifesteal.listeners.PlayerDeathListener;
import com.lifesteal.listeners.CraftingListener;
import com.lifesteal.managers.HeartManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LifeStealPlugin extends JavaPlugin {

    private HeartManager heartManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.heartManager = new HeartManager(this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);
        getCommand("heart").setExecutor(new HeartCommand(this));
        getLogger().info("LifeSteal plugin enabled!");
    }

    @Override
    public void onDisable() {
        heartManager.saveAllData();
        getLogger().info("LifeSteal plugin disabled!");
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }
}
