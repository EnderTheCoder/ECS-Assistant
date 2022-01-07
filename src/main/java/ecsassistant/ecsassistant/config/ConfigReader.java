package ecsassistant.ecsassistant.config;

import ecsassistant.ecsassistant.ECSAssistant;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
    FileConfiguration config = ECSAssistant.instance.getConfig();

    public int getFlyCostsPerMin() {
        return config.getInt("FlyCostsPerMin");
    }

    public int getFlyCostsStart() {
        return config.getInt("FlyCostsStart");
    }

    public double getTeleportCosts() {
        return config.getDouble("TeleportCosts");
    }
}
