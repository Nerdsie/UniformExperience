package me.richardcollins.universal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ColorCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "==[" + ChatColor.YELLOW + " COLOR INFORMATION " + ChatColor.GOLD + "]==");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.BLACK + " &0 TEST " + ChatColor.DARK_GRAY + "&8 TEST " + ChatColor.GRAY + "&7 TEST " + ChatColor.WHITE + "&f TEST");
        sender.sendMessage(ChatColor.DARK_BLUE + " &1 TEST " + ChatColor.DARK_GREEN + "&2 TEST " + ChatColor.DARK_AQUA + "&3 TEST " + ChatColor.DARK_RED + "&4 TEST " + ChatColor.DARK_PURPLE + "&5 TEST " + ChatColor.GOLD + "&6 TEST");
        sender.sendMessage(ChatColor.BLUE + " &9 TEST " + ChatColor.GREEN + "&a TEST " + ChatColor.AQUA + "&b TEST " + ChatColor.RED + "&c TEST " + ChatColor.LIGHT_PURPLE + "&d TEST " + ChatColor.YELLOW + "&e TEST");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RESET + " &r " + "Reset");
        sender.sendMessage(ChatColor.RESET + " &k " + ChatColor.MAGIC + "Magic");
        sender.sendMessage(ChatColor.RESET + " &o " + ChatColor.ITALIC + "Italics");
        sender.sendMessage(ChatColor.RESET + " &l " + ChatColor.BOLD + "Bold");
        sender.sendMessage(ChatColor.RESET + " &m " + ChatColor.STRIKETHROUGH + "Strikethrough");
        sender.sendMessage(ChatColor.RESET + " &n " + ChatColor.UNDERLINE + "Underline");

        return true;
    }
}
