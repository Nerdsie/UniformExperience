package me.richardcollins.universal.commands;

import me.richardcollins.universal.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vote")) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + " Vote to help the server! " + ChatColor.YELLOW + "Rewards:");
            sender.sendMessage(ChatColor.AQUA + "   * " + ChatColor.AQUA + Settings.GOLD_PER_VOTE * Settings.SITES + " gold.");
            sender.sendMessage(ChatColor.AQUA + "   * " + ChatColor.AQUA + Settings.DIAMONDS_PER_VOTE * Settings.SITES + " diamonds.");
            sender.sendMessage("");
            sender.sendMessage(" " + ChatColor.GREEN + "To vote, go to " + ChatColor.RED + "vote.pvpsmash.com");
            sender.sendMessage("");
            return true;
        }

        return false;
    }
}
