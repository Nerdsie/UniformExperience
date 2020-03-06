package me.richardcollins.universal.managers;

import me.richardcollins.universal.Universal;
import me.richardcollins.universal.Settings;
import me.richardcollins.universal.objects.Server;

import java.util.ArrayList;

public class ServerManager {
    public static String getThisServer() {
        return Settings.name;
    }

    public static String getServerName() {
        return getThisServer();
    }

    public static Server getServer(String name) {
        return new Server(name);
    }

    public static Server getServer() {
        return new Server(getThisServer());
    }

    public static boolean hasServer(String name) {
        return Universal.getServerStats().has(name);
    }

    public static ArrayList<String> getAllServers() {
        return Universal.getServerStats().getIdentifiers();
    }
}
