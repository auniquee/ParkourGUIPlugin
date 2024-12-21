package me.jonis.database_test;

import commands.ParkourCommand;
import commands.ParkourListCommand;
import commands.ViewStats;
import database.DatabaseManager;
import listeners.InventoryClickHanlder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import utils.ConfigManager;

public final class Database_test extends JavaPlugin {
    public DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public void onEnable() {
        // Initialize the database

        db.initialize();
        getLogger().info("Database connected successfully!");
        ConfigManager.initialize(getConfig());
        getCommand("parkour").setExecutor(new ParkourCommand());
        getCommand("parkours").setExecutor(new ParkourListCommand(this));
        getCommand("viewstats").setExecutor(new ViewStats());
        getServer().getPluginManager().registerEvents(new InventoryClickHanlder(), this);
    }

    @Override
    public void onDisable() {
        if (db != null) {
            db.close();
            getLogger().info("Database connection closed.");
        }
    }
}
