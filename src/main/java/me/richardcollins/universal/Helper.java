package me.richardcollins.universal;

import me.richardcollins.tools.objects.elements.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Helper extends me.richardcollins.tools.Tools {
    public static Menu getMenu(Player player) {
        Menu menu = Settings.menu;

        ResultSet set = Universal.getPlayerStats().getResults(player.getName());
        HashMap<String, String> hash = new HashMap<String, String>();

        if (set != null) {
            try {
                while (set.next()) {
                    String k = set.getString("key").toLowerCase();
                    String v = set.getString("value");
                    hash.put(k, v);
                }
            } catch (SQLException e) {
            }
        }

        menu.setMeta("p_stats", hash);
        menu.setOwner(player);

        return menu;
    }

    public static boolean inBeta(Player player) {
        if (player == null) {
            return false;
        }

        return inBeta(player.getName());
    }

    public static boolean inBeta(String player) {
        if (player == null || player.equalsIgnoreCase("")) {
            return false;
        }

        if (Universal.getPlayerStats().has(player, "beta")) {
            return Universal.getPlayerStats().getBoolean(player, "beta");
        }

        return false;
    }

    public static Team getVIPTeam(Player player) {
        return getVIPTeam(player.getScoreboard());
    }

    public static Team getVIPTeam(Scoreboard board) {
        Team vips = board.getTeam("vips");

        if (vips == null) {
            vips = board.registerNewTeam("vips");

            vips.setAllowFriendlyFire(true);
            vips.setCanSeeFriendlyInvisibles(false);

            vips.setPrefix(Settings.VIP_COLOR + "");
        }

        return vips;
    }

    public static Team getModTeam(Player player) {
        return getModTeam(player.getScoreboard());
    }

    public static Team getModTeam(Scoreboard board) {
        Team mods = board.getTeam("mods");

        if (mods == null) {
            mods = board.registerNewTeam("mods");

            mods.setAllowFriendlyFire(true);
            mods.setCanSeeFriendlyInvisibles(false);

            mods.setPrefix(Settings.MOD_COLOR + "");
        }

        return mods;
    }

    public static Team getAdminTeam(Player player) {
        return getAdminTeam(player.getScoreboard());
    }

    public static Team getAdminTeam(Scoreboard board) {
        Team admins = board.getTeam("admins");

        if (admins == null) {
            admins = board.registerNewTeam("admins");

            admins.setAllowFriendlyFire(true);
            admins.setCanSeeFriendlyInvisibles(false);

            admins.setPrefix(Settings.ADMIN_COLOR + "");
        }

        return admins;
    }

    public static void addToAllScoreboards(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean adding = true;

            Team admins = getAdminTeam(p);
            Team mods = getModTeam(p);
            Team vips = getVIPTeam(p);

            if (player.hasPermission("admin") && adding) {
                if (!admins.hasPlayer(player)) {
                    admins.addPlayer(player);
                }

                adding = false;
            } else {
                if (admins.hasPlayer(player))
                    admins.removePlayer(player);
            }

            if (player.hasPermission("mod") && adding) {
                if (!mods.hasPlayer(player)) {
                    mods.addPlayer(player);
                }

                adding = false;
            } else {
                if (mods.hasPlayer(player))
                    mods.removePlayer(player);
            }

            if (player.hasPermission("vip") && adding) {
                if (!vips.hasPlayer(player)) {
                    vips.addPlayer(player);
                }
            } else {
                if (vips.hasPlayer(player))
                    vips.removePlayer(player);
            }
        }
    }

    public static void sortScoreboardNames(Scoreboard board) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean adding = true;

            Team admins = getAdminTeam(board);
            Team mods = getModTeam(board);
            Team vips = getVIPTeam(board);

            if (player.hasPermission("admin") && adding) {
                if (!admins.hasPlayer(player)) {
                    admins.addPlayer(player);
                }

                adding = false;
            } else {
                if (admins.hasPlayer(player))
                    admins.removePlayer(player);
            }

            if (player.hasPermission("mod") && adding) {
                if (!mods.hasPlayer(player)) {
                    mods.addPlayer(player);
                }

                adding = false;
            } else {
                if (mods.hasPlayer(player))
                    mods.removePlayer(player);
            }

            if (player.hasPermission("vip") && adding) {
                if (!vips.hasPlayer(player)) {
                    vips.addPlayer(player);
                }
            } else {
                if (vips.hasPlayer(player))
                    vips.removePlayer(player);
            }

        }
    }

    public static void handleNewPlayer(Player player) {
        setCleanScoreboard(player);
        addToAllScoreboards(player);
    }

    public static void setCleanScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();

        if (board == null || board.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        sortScoreboardNames(board);

        player.setScoreboard(board);
    }

    public static String cleanIP(String ip) {
        return ip.replaceAll("/", "");
    }

    public static Universal getUniversalPlugin() {
        return Universal.getPlugin();
    }

    /**
     * Registers a user on the xenregister
     *
     * @param user     Username of the user
     * @param password Password for the user. A randomly generated one is recommended.
     * @param email    Email address for the user
     * @return True if reqistration was successful
     */
    public static int registerUser(String user, String password, String email) {
        try {
            String link = "api.php?action=register&hash=" + Settings.apiHash + "&username=" + user +
                    "&password=" + password + "&email=" + email + "&custom_fields=minecraftusername=" + user
                    + "&user_state=valid"; //email_confirm

            URL url = new URL(Settings.site + link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Open URL connection
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // User already exists?
                if (inputLine.contains("{\"error\":7,\"message\":\"Something went wrong when \\\"registering user\\\": \\\"" +
                        "User already exists\\\"\",\"user_error_id\":40,\"user_error_field\":\"username\",\"" +
                        "user_error_key\":\"usernames_must_be_unique\",\"user_error_phrase\":\"Usernames must be unique." +
                        " The specified username is already in use.\"}")) return 1;

                // Email already in use?
                if (inputLine.contains("{\"error\":7,\"message\":\"Something went wrong when \\\"registering user\\\": \\\"" +
                        "Email already used\\\"\",\"user_error_id\":42,\"user_error_field\":\"email\",\"user_error_key\":\"" +
                        "email_addresses_must_be_unique\",\"user_error_phrase\":\"Email addresses must be unique. " +
                        "The specified email address is already in use.\"}")) return 2;
            }

        } catch (Exception e) {
            return 3;
        }

        return 0;
    }
}
