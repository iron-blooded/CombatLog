package org.hg.combatlog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hg.combatlog.PlayerSerializer;
import org.hg.combatlog.PlayerSerializer.*;

public class database {
    private static CombatLog plugin;
    public Connection connection;
    public database(CombatLog plugin){
        database.plugin = plugin;
        setupDatabase();

    }
    public void setupDatabase() {
        try {
            File folder = new File(plugin.getDataFolder() + File.separator);
            if (!folder.exists()) {
                folder.mkdir();
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "combat.db");
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS combat (time LONG, victim OBJECT, attacker OBJECT, damage DOUBLE)");
            statement.execute();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public static class CombatLine {
        public final long time;
        public decompressedPlayer victim;
        public decompressedPlayer attacker;
        public double damage;
        public CombatLine(long time, decompressedPlayer victim, decompressedPlayer attacker, double damage) {
            this.time = time;
            this.victim = victim;
            this.attacker = attacker;
            this.damage = ((int) (damage*100))/100;
            this.damage = this.damage / 2;
        }
    }
    public void addLine(Player victim, Player attacker, double damage) {
        addLine(PlayerSerializer.compressedPlayer(victim), PlayerSerializer.compressedPlayer(attacker), damage);
    }
    public void addLine(String victim_serialized, String attacker_serialized, double damage) {
        long time = System.currentTimeMillis();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO combat (time, victim, attacker, damage) VALUES (?, ?, ?, ?)");
            statement.setLong(1, time);
            statement.setObject(2, victim_serialized);
            statement.setObject(3, attacker_serialized);
            statement.setObject(4, damage);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private decompressedPlayer compPlayerToDecompressed(Object object){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new decompressedPlayer((Map<String, Object>) gson.fromJson(object.toString(), type));
    }
    public List<CombatLine> getLinesLastSeconds(int seconds) {
        List<CombatLine> combatLines = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long timeThreshold = currentTime - (seconds * 1000);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM combat WHERE time >= ?");
            statement.setLong(1, timeThreshold);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long time = resultSet.getLong("time");
                decompressedPlayer victim = compPlayerToDecompressed(resultSet.getObject("victim"));
                decompressedPlayer attacker = compPlayerToDecompressed(resultSet.getObject("attacker"));
                double damage = resultSet.getDouble("damage");
                CombatLine line = new CombatLine(time, victim, attacker, damage);
                combatLines.add(0, line);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return combatLines;
    }
    public final class rawData{
        public long time;
        public decompressedPlayer victim;
        public decompressedPlayer attacker;
        public double damage;
        public rawData(long time, decompressedPlayer victim, decompressedPlayer attacker, double damage){
            this.time = time;
            this.attacker = attacker;
            this.victim = victim;
            this.damage = damage;
        }
    }
    public List<rawData> getRawLinesLastSeconds(int seconds){
        List<rawData> data = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long timeThreshold = currentTime - (seconds * 1000);
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM combat WHERE time >= ?");
            statement.setLong(1, timeThreshold);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long time = resultSet.getLong("time");
                decompressedPlayer victim = compPlayerToDecompressed(resultSet.getObject("victim"));
                decompressedPlayer attacker = compPlayerToDecompressed(resultSet.getObject("attacker"));
                double damage = resultSet.getDouble("damage");
//                CombatLine line = new CombatLine(time, victim, attacker, damage);
                data.add(0, new rawData(time, victim, attacker, damage));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    public List<CombatLine> getLinesLastSecondsWithRadiusVictim(int seconds, Location location, double radius) {
        List<CombatLine> combatLines = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long timeThreshold = currentTime - (seconds * 1000);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM combat WHERE time >= ?");
            statement.setLong(1, timeThreshold);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long time = resultSet.getLong("time");
                decompressedPlayer victim = compPlayerToDecompressed(resultSet.getObject("victim"));
                decompressedPlayer attacker = compPlayerToDecompressed(resultSet.getObject("attacker"));
                double damage = resultSet.getDouble("damage");
                if (victim != null && victim.location.distance(location) <= radius) {
                    CombatLine line = new CombatLine(time, victim, attacker, damage);
                    combatLines.add(0, line);
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return combatLines;
    }
    public List<CombatLine> getLinesLastSecondsWithAttackerRadius(int seconds, Location location, double radius) {
        List<CombatLine> combatLines = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long timeThreshold = currentTime - (seconds * 1000);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM combat WHERE time >= ?");
            statement.setLong(1, timeThreshold);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long time = resultSet.getLong("time");
                decompressedPlayer victim = compPlayerToDecompressed(resultSet.getObject("victim"));
                decompressedPlayer attacker = compPlayerToDecompressed(resultSet.getObject("attacker"));
                double damage = resultSet.getDouble("damage");
                if (attacker != null && attacker.location.distance(location) <= radius) {
                    CombatLine line = new CombatLine(time, victim, attacker, damage);
                    combatLines.add(0, line);
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return combatLines;
    }

}
