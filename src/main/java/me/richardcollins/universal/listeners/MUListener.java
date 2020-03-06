package me.richardcollins.universal.listeners;

import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Universal;
import me.richardcollins.universal.Settings;
import me.richardcollins.universal.managers.ServerManager;
import me.richardcollins.universal.managers.TimeManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class MUListener implements Listener {
    public Universal plugin;

    public MUListener(Universal pt) {
        plugin = pt;
    }

    /*
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String body = e.getMessage();

        if (!e.isCancelled()) {
            String cleanName = ChatColor.stripColor(e.getPlayer().getDisplayName());

            if (e.getPlayer().getDisplayName().startsWith("#")) {
                e.setCancelled(true);

                body = body.trim();

                if (ChatColor.stripColor(body).replaceAll(" ", "").isEmpty()) {
                    return;
                }

                if (e.getPlayer().hasPermission("myuniversal.chat.color")) {
                    body = Helper.parse(body, e.getPlayer().hasPermission("myuniversal.chat.magic"));
                }

                String prefix = Helper.parse(PlayerManager.getPrefix(e.getPlayer()));
                String clan = "";
                String level = "";

                if (Bukkit.getPluginManager().isPluginEnabled("MyClans")) {
                    if (ClanPlayerManager.isMember(e.getPlayer().getName())) {
                        String add = new ClanPlayer(e.getPlayer().getName()).getClan().getTag();
                        clan = ChatColor.WHITE + "[" + add + ChatColor.WHITE + "] ";
                    }
                }

                if (Bukkit.getPluginManager().isPluginEnabled("FifteenSeconds")) {
                    if (UserManager.has(e.getPlayer())) {
                        level = ChatColor.YELLOW + "" + (UserManager.get(e.getPlayer()).getLevel() + 1) + ": ";
                    } else {
                        level = ChatColor.LIGHT_PURPLE + "SPEC: ";
                    }
                }

                if (body.isEmpty() || body.trim().isEmpty()) {
                    return;
                }

                String last = ChatColor.getLastColors(prefix);

                DynamicMessage chat = new DynamicMessage("");
                chat.addAndFormat(level);
                chat.addAndFormat(clan);
                chat.addAndFormat(prefix);
                chat.addAndFormat(last);
                chat.add(cleanName).tooltip(ChatColor.GREEN + "Real Name: " + ChatColor.WHITE + e.getPlayer().getName());
                chat.add(": ").color(Helper.toChatColor(prefix.trim())).add(body).color(ChatColor.WHITE);

                boolean check = false;
                if (MyUniversal.ess != null) {
                    check = true;
                }

                if (check) {
                    ArrayList<Player> players = new ArrayList<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));

                    for (Player p : players) {
                        if (!MyUniversal.ess.getUser(p).isIgnoredPlayer(MyUniversal.ess.getUser(e.getPlayer()))) {
                            chat.send(p);
                        }
                    }
                } else {
                    chat.send(Arrays.asList(Bukkit.getOnlinePlayers()));
                }

                System.out.println(ChatColor.stripColor(clan + prefix + ChatColor.stripColor(e.getPlayer().getDisplayName()) + ": " + ChatColor.WHITE + body));

                return;
            }

            body = body.trim();

            if (ChatColor.stripColor(body).replaceAll(" ", "").isEmpty()) {
                e.setCancelled(true);
                return;
            }

            if (e.getPlayer().hasPermission("myuniversal.chat.color")) {
                body = Helper.parse(body, e.getPlayer().hasPermission("myuniversal.chat.magic"));
            }

            e.setMessage(body);

            String prefix = Helper.parse(PlayerManager.getPrefix(e.getPlayer()));
            String clan = "";
            String level = "";

            if (Bukkit.getPluginManager().isPluginEnabled("MyClans")) {
                if (ClanPlayerManager.isMember(e.getPlayer().getName())) {
                    String add = new ClanPlayer(e.getPlayer().getName()).getClan().getTag();
                    clan = ChatColor.WHITE + "[" + add + ChatColor.WHITE + "] ";
                }
            }

            if (Bukkit.getPluginManager().isPluginEnabled("FifteenSeconds")) {
                if (UserManager.has(e.getPlayer())) {
                    level = ChatColor.YELLOW + "" + (UserManager.get(e.getPlayer()).getLevel() + 1) + ": ";
                } else {
                    level = ChatColor.LIGHT_PURPLE + "SPEC: ";
                }
            }

            if (Bukkit.getPluginManager().isPluginEnabled("MyClans")) {
                if (ClanPlayerManager.isMember(e.getPlayer().getName())) {
                    String add = new ClanPlayer(e.getPlayer().getName()).getClan().getTag();
                    clan = ChatColor.WHITE + "[" + add + ChatColor.WHITE + "] ";
                }
            }

            if (body.isEmpty() || body.trim().isEmpty()) {
                e.setCancelled(true);
                return;
            }

            String last = ChatColor.getLastColors(prefix);

            try {
                e.setFormat(level + clan + prefix + last + ChatColor.stripColor(e.getPlayer().getDisplayName()) + ": " + ChatColor.WHITE + "%2$s");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }*/


    @EventHandler
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) throws Exception {
        new BukkitRunnable() {

            @Override
            public void run() {
                Player player = event.getPlayer();
                String[] messageParts = event.getMessage().split(" ");
                String command = messageParts[0].replaceFirst("/", "");

                if (Settings.servers.containsKey(command)) {
                    event.setCancelled(true);

                    Helper.sendPlayerToServer(player, Settings.servers.get(command));
                }
            }
        }.runTaskAsynchronously(Universal.getPlugin());
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Chunk chunk = event.getTo().getChunk();

                if (!chunk.isLoaded()) {
                    chunk.load();
                }

                boolean isLoaded = chunk.isLoaded();
                while (!isLoaded) {
                    isLoaded = chunk.isLoaded();
                }

                final Player player = event.getPlayer();
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (online.canSee(player)) {
                                online.hidePlayer(player);
                                online.showPlayer(player);
                            }

                            if (player.canSee(online)) {
                                player.hidePlayer(online);
                                player.showPlayer(online);
                            }
                        }
                    }.runTask(Universal.getPlugin());
                }
            }
        }.runTask(Universal.getPlugin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        //Protect portals!

        Material inHand = e.getPlayer().getInventory().getItemInHand().getType();
        if (inHand == Material.LAVA_BUCKET || inHand == Material.WATER_BUCKET) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                e.setCancelled(true);
                return;
            }

            Block block = e.getClickedBlock();

            int blockX = block.getX();
            int blockY = block.getY();
            int blockZ = block.getZ();

            int tBlockX = e.getBlockFace().getModX();
            int tBlockY = e.getBlockFace().getModY();
            int tBlockZ = e.getBlockFace().getModZ();

            int fBlockX = blockX + tBlockX;
            int fBlockY = blockY + tBlockY;
            int fBlockZ = blockZ + tBlockZ;

            Location tLoc = new Location(block.getWorld(), fBlockX, fBlockY, fBlockZ);
            Block check = tLoc.getBlock();

            if (check.getType() == Material.ENDER_PORTAL) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        new BukkitRunnable() {

            @Override
            public void run() {
                final Player player = event.getPlayer();
                ServerManager.getServer().updatePopulation();

                TimeManager.addPlayer(player.getUniqueId());

                Bukkit.getScheduler().scheduleSyncDelayedTask(Helper.getUniversalPlugin(), new Runnable() {
                    public void run() {
                        //LoginManager.Login(player);

                        Helper.handleNewPlayer(event.getPlayer());
                        ServerManager.getServer().updatePopulation();

                        if (Settings.autobrowser) {
                            event.getPlayer().performCommand("s");
                        }
                    }
                }, 1L);

                if (Universal.getPlayerStats().getBoolean(player.getName(), "trail.enabled", false)) {
                    String id = Universal.getPlayerStats().getString(player.getName(), "trail.type");
                }

                if (Universal.getPlayerStats().has(player.getName(), "all.group")) {
                    String group = Universal.getPlayerStats().getString(player.getName(), "all.group");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + player.getName() + " " + group);
                }
            }
        }.runTaskAsynchronously(Universal.getPlugin());

        if (event.getJoinMessage() == null)
            return;

        if (!event.getPlayer().hasPermission("vanish.silentjoin")) {
            event.setJoinMessage(ChatColor.YELLOW + " \u27A6 " + event.getJoinMessage());
        } else {
            event.setJoinMessage("");
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    TimeManager.saveTime(e.getPlayer().getUniqueId());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        ServerManager.getServer().updatePopulation();
                    }
                }.runTaskLater(Universal.getPlugin(), 10L);
            }
        }.runTaskAsynchronously(Universal.getPlugin());

        if (e.getQuitMessage() == null)
            return;

        if (!e.getPlayer().hasPermission("vanish.silentquit")) {
            e.setQuitMessage(ChatColor.YELLOW + " \u27A5 " + e.getQuitMessage());
        } else {
            e.setQuitMessage("");
        }
    }
}
