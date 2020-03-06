package me.richardcollins.universal.managers;

import me.richardcollins.universal.Universal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TimeManager {
    public static HashMap<UUID, Long> times = new HashMap<UUID, Long>();

    public static void updateTime(final UUID uuid, final int seconds) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    Universal.getPlayerStats().update(player.getName(), ServerManager.getServerName() + ".time", seconds);
                }
            }
        }.runTaskAsynchronously(Universal.getPlugin());
    }

    public static int getTime(UUID uuid, String server) {
        //TODO: Check call async
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            return Universal.getPlayerStats().getInteger(player.getName(), ServerManager.getServerName() + ".time");
        }

        return 0;
    }

    public static int getTime(UUID uuid) {
        //TODO: Check call async
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            return getTime(uuid, ServerManager.getThisServer());
        }

        return 0;
    }

    public static int getTotalTime(UUID uuid) {
        //TODO: Check call async

        int total = 0;

        for (String server : ServerManager.getAllServers()) {
            total += getTime(uuid, server);
        }

        return total;
    }

    public static void updateTime(final UUID uuid) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    try {
                        saveTime(uuid);
                        addPlayer(uuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(Universal.getPlugin());
    }

    public static void saveTime(final UUID uuid) throws Exception {
        new BukkitRunnable() {

            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    int timeToAdd = TimeManager.getTimePassed(TimeManager.getTimeStarted(uuid));
                    int original = getTime(uuid);

                    int total = timeToAdd + original;

                    updateTime(uuid, total);

                    removePlayer(uuid);
                }
            }
        }.runTaskAsynchronously(Universal.getPlugin());
    }

    public static void saveTimeNoTask(final UUID uuid) throws Exception {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            int timeToAdd = TimeManager.getTimePassed(TimeManager.getTimeStarted(uuid));
            int original = getTime(uuid);

            int total = timeToAdd + original;

            updateTime(uuid, total);

            removePlayer(uuid);
        }
    }

    public static int getTimePassed(long then) {
        long now = System.currentTimeMillis();

        long diff = now - then;

        int seconds = (int) (diff / 1000);

        return seconds;
    }

    public static long getTimeStarted(UUID uuid) {
        if (times.containsKey(uuid)) {
            return times.get(uuid);
        }

        return System.currentTimeMillis();
    }

    public static void removePlayer(final UUID uuid) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!playerExists(uuid))
                    return;

                times.remove(uuid);
            }
        }.runTaskAsynchronously(Universal.getPlugin());
    }

    public static boolean playerExists(UUID uuid) {
        new BukkitRunnable() {

            @Override
            public void run() {

            }
        }.runTaskAsynchronously(Universal.getPlugin());

        return times.containsKey(uuid);
    }

    public static void addPlayer(final UUID uuid) {
        new BukkitRunnable() {

            @Override
            public void run() {
                times.put(uuid, System.currentTimeMillis());
            }
        }.runTaskAsynchronously(Universal.getPlugin());
    }
}
