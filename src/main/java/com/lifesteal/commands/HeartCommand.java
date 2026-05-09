package com.lifesteal.commands;

import com.lifesteal.LifeStealPlugin;
import com.lifesteal.managers.HeartManager;
import com.lifesteal.utils.HeartItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartCommand implements CommandExecutor {

    private final LifeStealPlugin plugin;
    private final HeartManager heartManager;

    public HeartCommand(LifeStealPlugin plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "check" -> {
                Player target = resolveTarget(sender, args);
                if (target == null) return true;
                int hearts = heartManager.getHearts(target);
                sender.sendMessage("§e" + target.getName() + " §7has §c" + hearts + " §7hearts.");
            }

            case "give" -> {
                if (!sender.hasPermission("lifesteal.admin")) {
                    sender.sendMessage("§cNo permission."); return true;
                }
                if (args.length < 3) { sender.sendMessage("§cUsage: /heart give <player> <amount>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }
                int amount = parseAmount(sender, args[2]);
                if (amount < 0) return true;
                heartManager.addHeart(target, amount);
                sender.sendMessage("§aGave §e" + amount + " §aheart(s) to §e" + target.getName() + "§a.");
                target.sendMessage("§aYou received §e" + amount + " §aheart(s) from an admin!");
            }

            case "set" -> {
                if (!sender.hasPermission("lifesteal.admin")) {
                    sender.sendMessage("§cNo permission."); return true;
                }
                if (args.length < 3) { sender.sendMessage("§cUsage: /heart set <player> <amount>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }
                int amount = parseAmount(sender, args[2]);
                if (amount < 0) return true;
                heartManager.setHearts(target, amount);
                sender.sendMessage("§aSet §e" + target.getName() + "§a's hearts to §e" + amount + "§a.");
            }

            case "revive" -> {
                if (!sender.hasPermission("lifesteal.admin")) {
                    sender.sendMessage("§cNo permission."); return true;
                }
                if (args.length < 2) { sender.sendMessage("§cUsage: /heart revive <player>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage("§cPlayer not found or offline."); return true; }
                heartManager.revivePlayer(target);
                sender.sendMessage("§aRevived §e" + target.getName() + "§a.");
            }

            case "item" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cOnly players can use this."); return true;
                }
                if (!sender.hasPermission("lifesteal.admin")) {
                    sender.sendMessage("§cNo permission."); return true;
                }
                int amount = args.length >= 2 ? parseAmount(sender, args[1]) : 1;
                if (amount < 0) return true;
                player.getInventory().addItem(HeartItem.createHeartItem(amount));
                sender.sendMessage("§aGiven §e" + amount + " §aheart item(s).");
            }

            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l--- LifeSteal Commands ---");
        sender.sendMessage("§e/heart check §7[player] §f- Check hearts");
        if (sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage("§e/heart give §7<player> <amount> §f- Give hearts");
            sender.sendMessage("§e/heart set §7<player> <amount> §f- Set hearts");
            sender.sendMessage("§e/heart revive §7<player> §f- Revive eliminated player");
            sender.sendMessage("§e/heart item §7[amount] §f- Get heart item(s).");
        }
    }

    private Player resolveTarget(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            Player t = Bukkit.getPlayer(args[1]);
            if (t == null) sender.sendMessage("§cPlayer not found.");
            return t;
        }
        if (sender instanceof Player p) return p;
        sender.sendMessage("§cSpecify a player.");
        return null;
    }

    private int parseAmount(CommandSender sender, String s) {
        try {
            int v = Integer.parseInt(s);
            if (v < 1) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount: " + s);
            return -1;
        }
    }
}
