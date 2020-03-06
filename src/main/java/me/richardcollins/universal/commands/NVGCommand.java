package me.richardcollins.universal.commands;

import me.richardcollins.universal.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NVGCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("nvg")) {
			if (sender instanceof Player) {
				String requiredPermission = Settings.basePerms + "nvg";

				if (!sender.hasPermission(requiredPermission)) {
					sender.sendMessage(ChatColor.RED + "Error: You need permission '" + requiredPermission + "' to do this.");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
				return true;
			}

			Player player = (Player) sender;

			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				player.sendMessage(ChatColor.AQUA + "Night vision " + ChatColor.GREEN + "DISABLED");
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * (60 * 60), 0));
				player.sendMessage(ChatColor.AQUA + "Night vision " + ChatColor.GREEN + "ENABLED");
			}

			return true;
		}

		return false;
	}
}
