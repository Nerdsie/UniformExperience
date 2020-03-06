package me.richardcollins.universal.commands;

import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    String overrideCommand = "";
    boolean requireConsole = false;
    boolean requirePlayer = true;
    boolean requirePermission = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (shouldCancel(sender, command)) {
            return true;
        }

        // -------------------------- //
        // Insert command stuff here! //
        // -------------------------- //

        Player player = (Player) sender;

        if (args.length <= 2) {
            sender.sendMessage(ChatColor.RED + "Error: /register <email> <password> <confirm_password>");
            return true;
        }

        String email = args[0];

        String pass = args[1];
        String pass_c = args[2];

        if (!pass.equals(pass_c)) {
            sender.sendMessage(ChatColor.RED + "Error: /register <email> <password> <confirm_password>");
            player.sendMessage(ChatColor.RED + " Passwords did not match! (It is case sensitive!)");
            return true;
        }

        int status = Helper.registerUser(sender.getName(), pass, email);

        if (status == 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + "You have registered for the forums. " + ChatColor.AQUA + Settings.site);
            sender.sendMessage(ChatColor.GREEN + "  Email: " + ChatColor.AQUA + email);
            sender.sendMessage(ChatColor.GREEN + "  Username: " + ChatColor.AQUA + player.getName());
            sender.sendMessage(ChatColor.GREEN + "  Password: " + ChatColor.AQUA + pass);
            sender.sendMessage("");
            return true;
        }

        if (status == 1) {
            sender.sendMessage(ChatColor.RED + "Error: You have already registered on this Minecraft account.");
            return true;
        }

        if (status == 2) {
            sender.sendMessage(ChatColor.RED + "Error: This email is already in use.");
            return true;
        }

        if (status == 3) {
            sender.sendMessage(ChatColor.RED + "Error: This email or username is already registered.");
            return true;
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
