package me.richardcollins.universal.commands;

import me.richardcollins.tools.objects.elements.Menu;
import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Universal;
import me.richardcollins.universal.Settings;
import me.richardcollins.universal.managers.TimeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ServersCommand implements CommandExecutor {
    String overrideCommand = "";
    boolean requireConsole = false;
    boolean requirePlayer = true;
    boolean requirePermission = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                if (sender.hasPermission("myuniversal.command.reload")) {
                    try {
                        Settings.load(Helper.getUniversalPlugin());

                        sender.sendMessage(ChatColor.AQUA + "[MyUniversal]" + ChatColor.GREEN + " Config reloaded!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    Helper.sendPlayerToServer(player, args[0]);
                    return true;
                }
            }
        }

        if (shouldCancel(sender, command)) {
            return true;
        }

        final Player player = (Player) sender;
        player.sendMessage(ChatColor.RED + "Server browser is loading...");
        player.sendMessage(ChatColor.RED + "One moment please...");

        new BukkitRunnable() {

            @Override
            public void run() {
                final Menu menu = Helper.getMenu(player);
                TimeManager.updateTime(player.getUniqueId());

                menu.updateAllIcons();
                menu.open();
            }
        }.runTaskAsynchronously(Universal.getPlugin());

        return true;
    }

    // Simply check for all required permissions

    public boolean shouldCancel(CommandSender sender, Command command) {
        if (requireConsole && requirePlayer) {
            requireConsole = false;
            requirePlayer = false;
        }

        if (sender instanceof Player) {
            if (requireConsole) {
                sender.sendMessage(ChatColor.RED + "You must be the console to do this.");
                return true;
            } else {
                if (requirePermission) {
                    String requiredPermission = ("myuniversal.command." + ((overrideCommand.equalsIgnoreCase("")) ? command.getName() : overrideCommand)).toLowerCase();

                    if (!sender.hasPermission(requiredPermission)) {
                        sender.sendMessage(ChatColor.RED + "Error: You need permission '" + requiredPermission + "' to do this.");
                        return true;
                    }
                }
            }
        } else {
            if (requirePlayer) {
                sender.sendMessage(ChatColor.RED + "You must be in game to do this.");
                return true;
            }
        }

        return false;
    }
}
