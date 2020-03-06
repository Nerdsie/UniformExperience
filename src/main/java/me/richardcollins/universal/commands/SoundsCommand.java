package me.richardcollins.universal.commands;

import me.richardcollins.tools.custom.sound.SoundTools;
import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class SoundsCommand implements CommandExecutor {
    String overrideCommand = "";
    boolean requireConsole = false;
    boolean requirePlayer = true;
    boolean requirePermission = true;

    public static HashMap<String, SoundTimer> sounds = new HashMap<String, SoundTimer>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (shouldCancel(sender, command)) {
            return true;
        }

        // -------------------------- //
        // Insert command stuff here! //
        // -------------------------- //

        final Player player = (Player) sender;

        int start = 0;

        boolean all = false;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("full") || args[0].equalsIgnoreCase("all")) {
                all = true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                stopSounds(player.getName());
                player.sendMessage(ChatColor.GREEN + "  Sounds " + ChatColor.RED + "stopped.");
                return true;
            } else if (args[0].equalsIgnoreCase("pause") || args[0].equalsIgnoreCase("p")) {
                pauseSounds(player.getName());
            } else if (args[0].equalsIgnoreCase("play") || args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")) {
                resumeSounds(player.getName());
            } else if (args[0].startsWith("s")) {
                String split[] = args[0].split(":");

                start = Integer.parseInt(split[1]);
            }
        }

        if (args.length > 1) {
            if (args[1].startsWith("s")) {
                String split[] = args[1].split(":");

                start = Integer.parseInt(split[1]);
            }
        }

        int secondsPerSound = 5;
        final boolean simple = !all;
        final int starting = start;

        stopSounds(player.getName());

        SoundTimer runnable = new SoundTimer(player, starting, simple);

        sounds.put(player.getName().toLowerCase(), runnable);
        runnable.runTaskTimer(Helper.getUniversalPlugin(), 0L, 20L * secondsPerSound);

        return true;
    }

    public void stopSounds(String player) {
        if (sounds.containsKey(player.toLowerCase())) {
            SoundTimer timer = sounds.get(player.toLowerCase());

            timer.cancel();
        }
    }

    public void resumeSounds(String player) {
        if (sounds.containsKey(player.toLowerCase())) {
            SoundTimer timer = sounds.get(player.toLowerCase());

            timer.running = true;
        }
    }

    public void pauseSounds(String player) {
        if (sounds.containsKey(player.toLowerCase())) {
            SoundTimer timer = sounds.get(player.toLowerCase());

            timer.running = false;
        }
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

    public static class SoundTimer extends BukkitRunnable {
        int index = 0;
        boolean simple = true;
        Player player = null;

        Float pitch = 3F;
        Sound[] sounds = Sound.values();
        public boolean running = true;

        public SoundTimer(Player p, int i, boolean s) {
            index = i;
            simple = s;
            player = p;
        }

        @Override
        public void run() {
            if (!running)
                return;

            if (simple) {
                pitch = 1F;
            } else {
                pitch -= 1F;
            }

            if (pitch == -1F) {
                pitch = 3F;
            }

            String pitchName = "NORMAL";
            int add = 1;

            if (pitch == 2F) {
                pitchName = "HIGH";
                add = 0;
            }

            if (pitch == 0F) {
                pitchName = "LOW";
                add = 2;
            }

            if (!simple && pitch != 3F) {
                SoundTools.play(player, sounds[index], 10F, pitch);
                player.sendMessage(ChatColor.GREEN + "  You are listening to "
                        + ChatColor.RED + sounds[index].name().toUpperCase() + ChatColor.GRAY + " (" + pitchName + ") " + ChatColor.DARK_GRAY + "  (" + ((index * 3) + add) + "/" + sounds.length * 3 + ") ");
            } else {
                if (simple) {
                    SoundTools.play(player, sounds[index], 10F, 1F);
                    player.sendMessage(ChatColor.GREEN + "  You are listening to "
                            + ChatColor.RED + sounds[index].name().toUpperCase() + ChatColor.DARK_GRAY + " (" + (index) + "/" + sounds.length + ") ");
                }

                index++;
            }
        }
    }
}