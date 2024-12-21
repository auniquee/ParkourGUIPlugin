package utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    private static ConfigManager instance;
    private final FileConfiguration config;

    public ConfigManager (FileConfiguration config){
        this.config = config;
    }
    public static void initialize(FileConfiguration config) {
        if (instance == null) {
            instance = new ConfigManager(config);
        }
    }

    public static ConfigManager getInstance(){
        return instance;
    }

    public List<String> getParkourList() {
        return config.getStringList("parkours");
    }
    public String getParkourBlock(String parkour) { return config.getString("parkour_blocks." + parkour); }

}
