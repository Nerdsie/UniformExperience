package me.richardcollins.universal.managers;

import me.richardcollins.tools.LocationTools;
import me.richardcollins.tools.storage.MySQL;
import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Universal;
import me.richardcollins.universal.objects.MySQLInfo;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StatsManager {
    String database = "stats";
    String tableName = "table";

    public MySQL sql = null;

    public StatsManager(String tName) {
        MySQLInfo info = Universal.getMySQLInfo();

        Universal.activeManagers.add(this);

        tableName = tName;

        sql = new MySQL(info.getHost(), database, info.getUsername(), info.getPassword());

        sql.open();

        open();
    }

    public void open() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ( `id` bigint(20) NOT NULL auto_increment, `identifier` varchar(100) NOT NULL, `key` text NOT NULL, `value` text NOT NULL, PRIMARY KEY (`id`));";
            sql.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean insertUniqueKey(String identifier, String key, Object value) {
        //If it already exists (key only) DO NOT DO ANYTHING!

        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        if (!has(identifier, key)) {
            String string = value.toString();

            boolean good = true;

            PreparedStatement statement = null;
            try {
                statement = sql.getConnection().prepareStatement("INSERT INTO " + tableName + " (`identifier`,`key`,`value`)VALUES(?,?,?);");
                statement.setString(1, identifier);
                statement.setString(2, key);
                statement.setString(3, value.toString());
                statement.executeUpdate();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();

                good = false;
            }

            return good;
        }

        return false;
    }

    public void insert(String identifier, String key, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        //If already exists (key) UPDATE if values do not match.

        if (!insertUniqueKey(identifier, key, value)) {
            if (!has(identifier, key, value)) {
                try {
                    PreparedStatement statement = sql.getConnection().prepareStatement("INSERT INTO " + tableName + " (`identifier`,`key`,`value`)VALUES(?,?,?);");
                    statement.setString(1, identifier);
                    statement.setString(2, key);
                    statement.setString(3, value.toString());
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void set(String identifier, String key, Object value) {
        update(identifier, key, value);
    }

    public void update(String identifier, String key, Object check, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        //If already exists (key) UPDATE if values do not match.

        if (!insertUniqueKey(identifier, key, value)) {
            if (has(identifier, key, check)) {
                try {
                    PreparedStatement statement = sql.getConnection().prepareStatement("UPDATE " + tableName + " SET `value` = ? WHERE LOWER(`identifier`) = LOWER(?) AND LOWER (`key`) = LOWER(?) AND LOWER(`value`) = LOWER(?)");
                    statement.setString(1, value.toString());
                    statement.setString(2, identifier);
                    statement.setString(3, key);
                    statement.setString(4, check.toString());
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(String identifier, String key, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        //If already exists (key) UPDATE if values do not match.
        if (!insertUniqueKey(identifier, key, value)) {

            if (!has(identifier, key, value)) {

                try {
                    PreparedStatement statement = sql.getConnection().prepareStatement("UPDATE " + tableName + " SET value = ? WHERE LOWER(`identifier`) = LOWER(?) AND LOWER (`key`) = LOWER(?)");

                    statement.setString(1, value.toString());
                    statement.setString(2, identifier);
                    statement.setString(3, key);
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }

    }

    public void delete(String identifier, String key) {
        if (has(identifier, key)) {
            String query = "DELETE FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "');";
            sql.delete(query);
            return;
        }
    }

    public void deleteKey(String key) {
        if (hasKey(key)) {
            String query = "DELETE FROM `" + tableName + "` WHERE LOWER(`key`) = LOWER('" + key + "');";
            sql.delete(query);
            return;
        }
    }

    public void deleteKey(String key, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        if (hasKey(key, value)) {
            String query = "DELETE FROM `" + tableName + "` WHERE LOWER(`key`) = LOWER('" + key + "') AND LOWER(`value`) = LOWER('" + value + "');";
            sql.delete(query);
            return;
        }
    }

    public void delete(String identifier, String key, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        if (has(identifier, key, value)) {
            String query = "DELETE FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "') AND LOWER(`value`) = LOWER('" + value + "');";
            sql.delete(query);
            return;
        }
    }

    public void delete(String identifier) {
        if (has(identifier)) {
            String query = "DELETE FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "');";
            sql.delete(query);
            return;
        }
    }

    public void deleteValues(String identifier, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        if (hasValue(identifier, value)) {
            String query = "DELETE FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`value`) = LOWER('" + value + "');";
            sql.delete(query);
            return;
        }
    }

    public boolean has(String identifier, String key) {
        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean has(String identifier) {
        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean has(String identifier, String key, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "') AND LOWER(`value`) = LOWER('" + value + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasValue(String identifier, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`value`) = LOWER('" + value + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasValue(Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`value`) = LOWER('" + value + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasKey(String key) {
        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`key`) = LOWER('" + key + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean hasKey(String key, Object value) {
        if (value instanceof Location) {
            value = LocationTools.serialize((Location) value);
        }

        try {
            ResultSet res = sql.select("SELECT * FROM `" + tableName + "` WHERE LOWER(`key`) = LOWER('" + key + "') AND LOWER(`value`) = LOWER('" + value + "');");

            res.last();
            int count = res.getRow();

            if (count == 0)
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getBoolean(String identifier, String key) {
        String parse = getString(identifier, key);

        try {
            boolean ret = Boolean.parseBoolean(parse);
            return ret;
        } catch (Exception ee) {

        }

        if (parse.equalsIgnoreCase("1") || parse.equalsIgnoreCase("t") || parse.equalsIgnoreCase("true")) {
            return true;
        }

        return false;
    }

    public boolean getBoolean(String identifier, String key, boolean def) {
        if (has(identifier, key)) {
            String parse = getString(identifier, key);

            try {
                boolean ret = Boolean.parseBoolean(parse);
                return ret;
            } catch (Exception ee) {

            }

            if (parse.equalsIgnoreCase("1") || parse.equalsIgnoreCase("t") || parse.equalsIgnoreCase("true")) {
                return true;
            }

            return false;
        } else {
            return def;
        }
    }

    public String get(String identifier, String key) {
        if (!has(identifier, key)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "');";
        ResultSet res = sql.select(query);

        try {
            if (res.next()) {
                String toRet = res.getString("value");
                return toRet;
            }
        } catch (Exception e) {

        }

        return null;
    }

    public String get(String identifier, String key, String def) {
        if (!has(identifier, key)) {
            return def;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "');";
        ResultSet res = sql.select(query);

        try {
            if (res.next()) {
                String toRet = res.getString("value");
                return toRet;
            }
        } catch (Exception e) {

        }

        return null;
    }

    public ResultSet getAll() {
        String query = "SELECT * FROM `" + tableName + "`;";
        ResultSet res = sql.select(query);

        return res;
    }

    public ArrayList<String> getIdentifiers() {
        String query = "SELECT DISTINCT `identifier` FROM `" + tableName + "`;";
        ResultSet res = sql.select(query);

        ArrayList<String> toRet = new ArrayList<String>();

        try {
            while (res.next()) {
                if (!toRet.contains(res.getString("identifier").toLowerCase())) {
                    toRet.add(res.getString("identifier").toLowerCase());
                }
            }
        } catch (SQLException e) {
        }

        return toRet;
    }

    public ResultSet getResults(String identifier, String key) {
        if (!has(identifier, key)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "');";
        ResultSet res = sql.select(query);

        return res;
    }

    public ResultSet getResults(String identifier, String key, String value) {
        if (!has(identifier, key)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`key`) = LOWER('" + key + "') AND LOWER(`value`) = LOWER('" + value + "');";
        ResultSet res = sql.select(query);

        return res;
    }

    public ResultSet getResults(String identifier) {
        if (!has(identifier)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "');";
        ResultSet res = sql.select(query);

        return res;
    }

    public ResultSet getResultsFromValue(String identifier, String value) {
        if (!hasValue(identifier, value)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`identifier`) = LOWER('" + identifier + "') AND LOWER(`value`) = LOWER('" + value + "');";
        ResultSet res = sql.select(query);

        return res;
    }

    public ResultSet getResultsFromValue(String value) {
        if (!hasValue(value)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`value`) = LOWER('" + value + "');";
        ResultSet res = sql.select(query);

        return res;
    }

    public ResultSet getResultsFromKey(String key) {
        if (!hasKey(key)) {
            return null;
        }

        String query = "SELECT * FROM `" + tableName + "` WHERE LOWER(`key`) = LOWER('" + key + "');";
        ResultSet res = sql.select(query);

        return res;
    }

    public String getString(String identifier, String key, String def) {
        return get(identifier, key, def);
    }

    public String getString(String identifier, String key) {
        return get(identifier, key);
    }

    public int getInteger(String identifier, String key) {
        String parse = get(identifier, key);

        try {
            int ret = Integer.parseInt(parse);
            return ret;
        } catch (Exception e) {

        }

        return 0;
    }

    public int getInteger(String identifier, String key, int def) {
        if (!has(identifier, key)) {
            return def;
        }

        String parse = get(identifier, key);

        try {
            int ret = Integer.parseInt(parse);
            return ret;
        } catch (Exception e) {

        }

        return 0;
    }

    public double getDouble(String identifier, String key) {
        String parse = get(identifier, key);

        try {
            double ret = Double.parseDouble(parse);
            return ret;
        } catch (Exception e) {

        }

        return 0.0;
    }

    public Float getFloat(String identifier, String key) {
        String parse = get(identifier, key);

        try {
            Float ret = Float.parseFloat(parse);
            return ret;
        } catch (Exception e) {

        }

        return 0F;
    }

    public Location getLocation(String identifier, String key) {
        String parse = get(identifier, key);

        try {
            return LocationTools.deserialize(parse);
        } catch (Exception e) {

        }

        return null;
    }

    public static ArrayList<String> toArray(ResultSet res, String key) {
        ArrayList<String> toRet = new ArrayList<String>();

        try {
            while (res.next()) {
                toRet.add(res.getString("key"));
            }
        } catch (Exception e) {
            return new ArrayList<String>();
        }

        return toRet;
    }
}
