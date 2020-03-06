package me.richardcollins.universal.commands;

import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Settings;
import me.richardcollins.universal.managers.ServerManager;
import me.richardcollins.universal.managers.StatsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsCommand implements CommandExecutor {
    String overrideCommand = "";
    boolean requireConsole = false;
    boolean requirePlayer = false;
    boolean requirePermission = false;

    public String parseString(String key) {
        key = key.toLowerCase().replaceAll("\\{server}", ServerManager.getServerName());

        return key;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (shouldCancel(sender, command)) {
            return true;
        }

        // -------------------------- //
        // Insert command stuff here! //
        // -------------------------- //

        String table = args[1];
        StatsManager stats = new StatsManager(table);

        if (args[0].equalsIgnoreCase("put") || args[0].equalsIgnoreCase("set")) {
            if (sender instanceof Player) {
                if (!Helper.isOwner((Player) sender)) {
                    sender.sendMessage(ChatColor.RED + "Error: You do not have permission to do this.");
                    return true;
                }
            }

            if (args.length <= 4) {
                return false;
            }

            String ident = args[2];
            String key = args[3];

            ident = parseString(ident);
            key = parseString(key);

            String value = parseString(args[4]);

            stats.set(ident, key, value);

            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + " " + table + "." + ident + ": " + ChatColor.AQUA + key + ChatColor.GRAY + " set to " + ChatColor.GREEN + value);
            sender.sendMessage("");
        }

        if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("read") || args[0].equalsIgnoreCase("view")) {
            if (args.length < 4) {
                return false;
            }

            String ident = args[2];
            String key = args[3];

            ident = parseString(ident);
            key = parseString(key);

            ResultSet stat = stats.getResults(ident, key);

            try {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.AQUA + " ||=== || " + ChatColor.GREEN + table + "." + ident + ChatColor.AQUA + " ||===========||");
                sender.sendMessage("");

                while (stat.next()) {
                    String name = stat.getString("value");

                    sender.sendMessage(ChatColor.GOLD + "   > " + stat.getString("key") + ": " + ChatColor.DARK_AQUA + name);
                }
                sender.sendMessage("");
            } catch (SQLException e) {

            }
        }

        if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("all")) {
            if (args.length == 3) {
                String ident = args[2];

                ident = parseString(ident);

                sender.sendMessage("");
                sender.sendMessage(ChatColor.AQUA + "|| ============ || " + ChatColor.RED + table + "." + ident + " ||");
                sender.sendMessage("");

                try {
                    ResultSet stat = stats.getResults(ident);

                    while (stat.next()) {
                        String name = stat.getString("value");

                        sender.sendMessage(ChatColor.YELLOW + " " + stat.getString("key") + ": " + ChatColor.AQUA + name);
                    }
                } catch (SQLException e) {
                }

                sender.sendMessage("");
            }
        }
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
