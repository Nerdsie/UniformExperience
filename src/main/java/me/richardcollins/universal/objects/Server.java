package me.richardcollins.universal.objects;

import me.richardcollins.universal.Universal;
import me.richardcollins.universal.managers.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Server {
    private String name = "";

    public Server(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StatsManager getStats() {
        return Universal.getServerStats();
    }

    public boolean getStatus() {
        if (getStats().has(getName())) {
            return getStats().getBoolean(getName(), "online");
        }

        return false;
    }

    public void setOnline(boolean online) {
        getStats().set(getName(), "online", online);
    }

    public ArrayList<String> getPlayersOnline() {
        ArrayList<String> people = new ArrayList<String>();

        return people;
    }

    public void addPlayer(Player player) {
        addPlayer(player.getName());
    }

    public void addPlayer(String name) {

    }

    public void removePlayer(Player player) {
        removePlayer(player.getName());
    }

    public void removePlayer(String name) {

    }

    public int getPopulation() {
        if (getStats().has(getName())) {
            return getStats().getInteger(getName(), "population");
        }

        return 0;
    }

    public void setPopulation(int newPopulation) {
        getStats().set(getName(), "population", newPopulation);
        updateMostOline();
    }

    public void updatePopulation() {
        setPopulation(Bukkit.getOnlinePlayers().size());
    }

    public int getMax() {
        if (getStats().has(getName())) {
            return getStats().getInteger(getName(), "max");
        }

        return 0;
    }

    public void setMax(int max) {
        getStats().set(getName(), "max", max);
    }

    public void updateMax() {
        setMax(Bukkit.getMaxPlayers());
    }

    public int getMostOnline() {
        if (getStats().has(getName())) {
            return getStats().getInteger(getName(), "most");
        }

        return 0;
    }

    public void updateMostOline() {
        if (!getStats().has(getName(), "most")) {
            setMostOnline(Bukkit.getOnlinePlayers().size());
        } else {
            if (Bukkit.getOnlinePlayers().size() > getMostOnline()) {
                setMostOnline(Bukkit.getOnlinePlayers().size());
            }
        }
    }

    public void setMostOnline(int most) {
        getStats().set(getName(), "most", most);
    }

    public int getUnique() {
        if (getStats().has(getName())) {
            return getStats().getInteger(getName(), "unique");
        }

        return 0;
    }

    public boolean isOnline() {
        return getStatus();
    }

    public void setStatus(boolean stat) {
        setOnline(stat);
    }

    public void setUnique(int unique) {
        getStats().set(getName(), "unique", unique);
    }
}
