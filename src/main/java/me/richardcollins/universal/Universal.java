package me.richardcollins.universal;

import me.richardcollins.tools.custom.particles.CustomParticle;
import me.richardcollins.tools.custom.particles.ParticleType;
import me.richardcollins.universal.listeners.MUListener;
import me.richardcollins.universal.managers.ServerManager;
import me.richardcollins.universal.managers.StatsManager;
import me.richardcollins.universal.managers.TimeManager;
import me.richardcollins.universal.objects.MySQLInfo;
import com.earth2me.essentials.Essentials;
import me.richardcollins.universal.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Universal extends JavaPlugin {
    public StatsManager playerStats;
    public StatsManager serverStats;

    public static Essentials ess = null;

    public static Universal plugin;

    public static ArrayList<StatsManager> activeManagers = new ArrayList<StatsManager>();
    public static HashMap<UUID, ParticleType> trails = new HashMap<UUID, ParticleType>();

    @Override
    public void onEnable() {
        plugin = this;

        if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
            ess = ((Essentials) getServer().getPluginManager().getPlugin("Essentials"));
        }

        try {
            Settings.load(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverStats = new StatsManager("servers");
        playerStats = new StatsManager("players");

        getServer().getPluginManager().registerEvents(new MUListener(this), this);

        getCommand("toggle").setExecutor(new OptionCommand());

        getCommand("servers").setExecutor(new ServersCommand());
        //getCommand("vote").setExecutor(new VoteCommand());

        getCommand("register").setExecutor(new RegisterCommand());

        getCommand("nvg").setExecutor(new NVGCommand());
        getCommand("color").setExecutor(new ColorCommand());

        getCommand("sounds").setExecutor(new SoundsCommand());
        getCommand("beta").setExecutor(new BetaCommand());

        getCommand("stats").setExecutor(new StatsCommand());


        new BukkitRunnable() {

            @Override
            public void run() {
                ServerManager.getServer().setOnline(true);
                ServerManager.getServer().updatePopulation();
                ServerManager.getServer().updateMax();
            }
        }.runTaskLater(Universal.getPlugin(), 20L);


        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Helper.handleNewPlayer(player);

                    if (Universal.getPlayerStats().getBoolean(player.getName(), "trail.enabled", false)) {
                        String id = Universal.getPlayerStats().getString(player.getName(), "trail.type");

                        trails.put(player.getUniqueId(), ParticleType.fromName(id));
                    }

                    /*
                    if (player.hasPermission("admin")) {
                        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                            player.performCommand("ungod");
                        }
                    }
                    */

                    try {
                        TimeManager.addPlayer(player.getUniqueId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLater(Helper.getUniversalPlugin(), 1L);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Universal.trails.containsKey(player.getUniqueId())) {
                        //TODO: Vanish check
                        //TODO: !Helper.isVanished(player) ||
                        if (!Universal.getPlayerStats().getBoolean(player.getName(), "vs", true)) {
                            ParticleType type = Universal.trails.get(player.getUniqueId());

                            new CustomParticle(type, player.getLocation()).xSpread(.2F).ySpread(.05F).zSpread(.2F).speed(0F).amount(25).showWhoCanSee(player);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Universal.getPlugin(), 1L, 1L);
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                TimeManager.saveTimeNoTask(player.getUniqueId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ServerManager.getServer().setOnline(false);

        for (StatsManager sM : activeManagers) {
            sM.sql.close();
        }

        Bukkit.getScheduler().cancelTasks(this);
    }

    public static MySQLInfo getMySQLInfo() {
        MySQLInfo info = new MySQLInfo(Settings.host, Settings.username, Settings.password);

        return info;
    }

    public static FileConfiguration getUniversalConfig() {
        File file = new File(getPlugin().getDataFolder(), "../../../universal.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config;
    }

    public static Universal getPlugin() {
        return plugin;
    }

    public static StatsManager getPlayerStats() {
        return getPlugin().playerStats;
    }

    public static StatsManager getServerStats() {
        return getPlugin().serverStats;
    }
}
