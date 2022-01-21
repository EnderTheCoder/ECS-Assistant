package ecsassistant.ecsassistant.config;

import ecsassistant.ecsassistant.ECSAssistant;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
    static FileConfiguration config = ECSAssistant.instance.getConfig();

//    public static double getFlyCostsPerSec() {
//        return config.getInt("FlyCostsPerSec");
//    }

    public static double getFlyCostsStart() {
        return config.getInt("FlyCostsStart");
    }

    public static double getTeleportCosts() {
        return config.getDouble("TeleportCosts");
    }

    public static double getKeepInventoryCosts() {
        return config.getDouble("KeepInventoryCosts");
    }

    public static double getCrossDimensionTeleportCosts() {
        return config.getDouble("CrossDimensionTeleportCosts");
    }

    public static String getMysqlConfig(String mysqlConfigTag) {
        return config.getString(mysqlConfigTag);
    }

    public static String getPlayerDefaultConfig(String playerConfigTag) {
        return config.getString("PlayerDefaultConfig." + playerConfigTag);
    }
    public static Double getCosts(String costsTag) {
        return config.getDouble("Costs." + costsTag);
    }

    public static Double getDistance(String distanceTag) {
        return config.getDouble("Distance." + distanceTag);
    }

    public static int getCD(String cdTag) {
        return config.getInt("CD." + cdTag);
    }

    public static int getMax(String maxTag) {
        return config.getInt("Max." + maxTag);
    }
}
