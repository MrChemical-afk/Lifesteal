package com.lifesteal.listeners;

import com.lifesteal.LifeStealPlugin;
import com.lifesteal.managers.HeartManager;
import com.lifesteal.utils.HeartItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener implements Listener {

    private final LifeStealPlugin plugin;
    private final HeartManager heartManager;

    public PlayerDeathListener(LifeStealPlugin plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) return;

        int heartsLost = plugin.getConfig().getInt("hearts-lost-on-death", 1);
        int heartsGained = plugin.getConfig().getInt("hearts-gained-on-kill", 1);

        heartManager.removeHeart(victim, heartsLost);
        heartManager.addHeart(killer, heartsGained);

        int killerHearts = heartManager.getHearts(killer);
        killer.sendMessage("§a§l+1 Heart! §r§aYou now have §e" + killerHearts + " §ahearts.");

        if (plugin.getConfig().getBoolean("drop-heart-on-death", true)) {
            victim.getWorld().dropItemNaturally(
                victim.getLocation(),
                HeartItem.createHeartItem(1)
            );
        }

        int victimHearts = heartManager.getHearts(victim);
        if (victimHearts > 0) {
            victim.sendMessage("§c§l-1 Heart! §r§cYou now have §e" + victimHearts + " §chearts.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int hearts = heartManager.getHearts(player);
        heartManager.applyHearts(player, hearts);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            int hearts = heartManager.getHearts(player);
            heartManager.applyHearts(player, hearts);
        }, 1L);
    }
}
