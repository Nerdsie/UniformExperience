package me.richardcollins.universal.commands;

import me.richardcollins.universal.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatCommand {
	public static void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		String[] args = e.getMessage().split(" ");

		if (e.getPlayer().hasPermission(Settings.basePerms + "forcechat")) {
			if (args[0].equalsIgnoreCase("/chat")) {
				String name = args[1];

				Player player = Bukkit.getPlayerExact(name);

				if (player != null && player.isOnline()) {
					player.chat(e.getMessage().replaceFirst("/chat " + name + " ", ""));
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + "Error: Player not found.");
				}

				e.setCancelled(true);
			}
		} else {
			e.setCancelled(false);
		}
	}
}
