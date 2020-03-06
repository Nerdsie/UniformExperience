package me.richardcollins.universal.commands;

import me.richardcollins.universal.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand implements CommandExecutor {
    String overrideCommand = "";
    boolean requireConsole = false;
    boolean requirePlayer = true;
    boolean requirePermission = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (shouldCancel(sender, command)) {
            return true;
        }

        // -------------------------- //
        // Insert command stuff here! //
        // -------------------------- //

        Player player = (Player) sender;

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
                    String requiredPermission = (Settings.basePerms + ((overrideCommand.equalsIgnoreCase("")) ? command.getName() : overrideCommand)).toLowerCase();

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
