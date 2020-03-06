package me.richardcollins.universal;

import me.richardcollins.tools.custom.Item;
import me.richardcollins.tools.events.icons.IconClickEvent;
import me.richardcollins.tools.handlers.IconHandler;
import me.richardcollins.tools.handlers.ItemHandler;
import me.richardcollins.tools.objects.elements.Icon;
import me.richardcollins.tools.objects.elements.Menu;
import me.richardcollins.universal.managers.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Settings {

    // ************ CUSTOM ITEMS *********** //

    public static Item serverBrowser = new Item().setType(Material.COMPASS)
            .setDisplayName(ChatColor.LIGHT_PURPLE + "Nerds Network" + ChatColor.BLUE + " Server Selector!" + ChatColor.WHITE + " (RIGHT CLICK)").setHandler(
                    new ItemHandler() {
                        @Override
                        public void onInteract(PlayerInteractEvent e) {
                            super.onInteract(e);

                            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                e.getPlayer().performCommand("servers");
                            }
                        }
                    }
            );

    public static int GOLD_PER_VOTE = 20;
    public static int DIAMONDS_PER_VOTE = 1;
    public static int SITES = 4;

    // ------- MySQL info --------- //

    public static String host = "localhost";
    public static String username = "root";
    public static String password = "";

    // ----- Other info --------- //

    public static String name = "Nerds Network";
    public static boolean autobrowser = false;

    public static String basePerms = "universal.command.";

    public static final ChatColor ADMIN_COLOR = ChatColor.DARK_RED;
    public static final ChatColor MOD_COLOR = ChatColor.GREEN;
    public static final ChatColor VIP_COLOR = ChatColor.DARK_PURPLE;

    public static String site = "";
    public static String apiHash = "";

    public static Menu menu = null;

    public static HashMap<String, String> servers = new HashMap<String, String>();

    public static void load(Universal plugin) throws Exception {
        plugin.reloadConfig();

        name = plugin.getConfig().getString("name");

        FileConfiguration config = plugin.getUniversalConfig();

        host = config.getString("host");

        username = config.getString("username");
        password = config.getString("password");

        String title = config.getString("browser.title");

        int size = config.getInt("browser.slots");
        autobrowser = config.getBoolean("browser.auto");

        DIAMONDS_PER_VOTE = config.getInt("vote.diamonds");
        GOLD_PER_VOTE = config.getInt("vote.gold");
        SITES = config.getInt("vote.sites");

        menu = new Menu("", title, size);

        //autobrowser = config.getBoolean("serverBrowser." + name + ".autobrowser");

        try {
            for (String serv : Helper.getConfigChildren(config, "servers")) {
                servers.put(serv, serv);
            }
        } catch (Exception e) {

        }

        Set<String> iStrings = Helper.getConfigChildren(config, "icons");

        for (String i : iStrings) {
            int id = config.getInt("icons." + i + ".id");
            int data = config.getInt("icons." + i + ".data");
            String cleanname = config.getString("icons." + i + ".name");
            String prefix = ChatColor.DARK_AQUA + "\u2726" + ChatColor.DARK_GREEN;
            String name = prefix + cleanname;
            ArrayList<String> lore = Helper.getConfigList(config, "icons." + i + ".lore");
            ArrayList<String> stats = Helper.getConfigList(config, "icons." + i + ".stats");
            String server = config.getString("icons." + i + ".server");
            int x = config.getInt("icons." + i + ".x");
            int y = config.getInt("icons." + i + ".y");

            String perms = "";

            if (config.contains("icons." + i + ".perms")) {
                perms = config.getString("icons." + i + ".perms");
            }

            ItemStack item = new ItemStack(id, 1);
            item.setDurability((short) data);

            Icon icon = new Icon(x, y, name, item).setHandler(new IconHandler() {

                @Override
                public void onClick(IconClickEvent event) {
                    super.onClick(event);

                    Helper.sendPlayerToServer(event.getPlayer(), getIcon().getMetaString("server"));
                }

                @Override
                public void update() {
                    Player player = getIcon().getOwner();

                    if (player == null) {
                        return;
                    }

                    getIcon().getLore().clear();

                    String perms = getIcon().getMetaString("required");

                    if (getIcon().getMeta("required") == null) {
                        getIcon().setVisible(true);
                    } else if (perms.equalsIgnoreCase("")) {
                        getIcon().setVisible(true);
                    } else if (player.hasPermission(perms)) {
                        getIcon().setVisible(true);
                    } else {
                        getIcon().setVisible(false);
                    }

                    String[] list = getIcon().getMetaString("lore").split("\u2766");
                    String server = getIcon().getMetaString("server").trim();

                    if (Settings.name.equalsIgnoreCase(server)) {
                        String name = getIcon().getMetaString("name");
                        String prefix = getIcon().getMetaString("prefix");

                        getIcon().setName(prefix + ChatColor.BOLD + name);
                    }

                    boolean online = ServerManager.getServer(server).getStatus();
                    int population = 0;

                    if (online) {
                        population = ServerManager.getServer(server).getPopulation();
                    }
                    //int max = ServerManager.getServer(server).getMax();

                    if (!online) {
                        getIcon().addLore(ChatColor.RED + "" + ChatColor.BOLD + "OFFLINE!");
                    } else {
                        getIcon().addLore(ChatColor.DARK_PURPLE + "Online: " + ChatColor.AQUA + "" + population + " player(s)");
                    }

                    getIcon().addLore("");

                    for (String s : list) {
                        getIcon().addLore(ChatColor.GRAY + s);
                    }

                    if (getIcon().hasMeta("stats") && getIcon().getMetaString("stats").split("\u2766").length > 0) {
                        getIcon().addLore("");
                        String[] stats = getIcon().getMetaString("stats").split("\u2766");
                        getIcon().addLore(ChatColor.GREEN + "Your Stats:");

                        if (getIcon().hasMeta("stats")) {
                            HashMap<String, String> hash = (HashMap<String, String>) getIcon().getParent().getMeta("stats");

                            if (hash != null) {
                                for (String stat : stats) {
                                    if (!stat.equalsIgnoreCase("")) {
                                        try {
                                            StringBuilder sb = new StringBuilder("  " + ChatColor.RED + stat.split(":")[0].trim() + ": ");

                                            stat = stat.split(":")[1].trim();
                                            String toRep = stat;

                                            String parse[] = stat.split("stat\\(");

                                            for (int i = 1; i < parse.length; i++) {
                                                try {
                                                    String split[] = parse[i].split("\\)");
                                                    String inside = split[0].trim();

                                                    String args[] = inside.split(",");
                                                    String key = args[0].trim();
                                                    server = server.toLowerCase();

                                                    String value = hash.get((server + "." + key).toLowerCase());

                                                    int oTime = 0;
                                                    if (hash.containsKey((server + ".time").toLowerCase())) {
                                                        oTime = Integer.parseInt(hash.get((server + ".time").toLowerCase()));
                                                    }

                                                    if (key.equalsIgnoreCase("hours")) {
                                                        int time = oTime;

                                                        try {
                                                            time -= (time % 60);
                                                            time -= (time % (60 * 60));
                                                        } catch (Exception e) {

                                                        }


                                                        value = (time / (60 * 60)) + "";
                                                    }

                                                    if (key.equalsIgnoreCase("minutes")) {
                                                        int time = oTime;
                                                        try {
                                                            time -= (time % 60);
                                                        } catch (Exception e) {

                                                        }

                                                        value = ((time % (60 * 60)) / 60) + "";
                                                    }

                                                    if (key.equalsIgnoreCase("seconds")) {
                                                        int time = oTime;
                                                        value = (time % 60) + "";
                                                    }

                                                    String def = args[1].trim();

                                                    if (value == null || value.equalsIgnoreCase("")) {
                                                        value = def;
                                                    }
                                                    toRep = toRep.replaceAll("stat\\(" + key + "," + def + "\\)", value);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            sb.append(ChatColor.YELLOW + toRep);

                                            getIcon().addLore(sb.toString());
                                        } catch (Exception e) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });

            StringBuilder list = new StringBuilder(lore.get(0));

            for (int ii = 1; ii < lore.size(); ii++) {
                list.append("\u2766" + lore.get(ii));
            }

            try {
                icon.setMeta("server", server);
                icon.setMeta("lore", list.toString());
                icon.setMeta("name", cleanname);
                icon.setMeta("prefix", prefix);

                StringBuilder statslist = new StringBuilder(stats.get(0));

                for (String toAdd : lore) {
                    icon.addLore(ChatColor.GRAY + toAdd);
                }

                if (perms != null && !perms.equalsIgnoreCase("")) {
                    icon.setMeta("required", perms);
                }

                for (int ii = 1; ii < stats.size(); ii++) {
                    statslist.append("\u2766" + stats.get(ii));
                }

                if (statslist != null) {
                    icon.setMeta("stats", statslist.toString());
                }
            } catch (Exception e) {

            }

            icon.addLore("");
            icon.addLore(ChatColor.DARK_BLUE + "Starting up...");

            menu.addIcon(icon);
        }
    }
}
