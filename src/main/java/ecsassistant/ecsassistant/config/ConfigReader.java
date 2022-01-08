package ecsassistant.ecsassistant.config;

import ecsassistant.ecsassistant.ECSAssistant;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
    FileConfiguration config = ECSAssistant.instance.getConfig();

    public double getFlyCostsPerMin() {
        return config.getInt("FlyCostsPerMin");
    }

    public double getFlyCostsStart() {
        return config.getInt("FlyCostsStart");
    }

    public double getTeleportCosts() {
        return config.getDouble("TeleportCosts");
    }

    public double getKeepInventoryCosts() {
        return config.getDouble("KeepInventoryCosts");
    }

    public String getMysqlConfig(String mysqlConfigTag) {
        return config.getString(mysqlConfigTag);
    }

    public String getPlayerDefaultConfig(String playerConfigTag) {
        return config.getString("PlayerDefaultConfig." + playerConfigTag);
    }
}
