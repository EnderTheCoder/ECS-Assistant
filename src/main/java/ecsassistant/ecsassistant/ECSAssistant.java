package ecsassistant.ecsassistant;

import ecsassistant.ecsassistant.commander.*;
import ecsassistant.ecsassistant.database.Mysql;
import ecsassistant.ecsassistant.event.KeepInventoryEvent;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ECSAssistant extends JavaPlugin {
    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Vault.vaultSetup();
        getLogger().info(ChatColor.GREEN + "ECS辅助插件启动成功，本插件由全知全能的Ender编写");

        if (Bukkit.getPluginCommand("flyx") != null) {
            Bukkit.getPluginCommand("flyx").setExecutor(new FlyCommander());
        }
        if (Bukkit.getPluginCommand("tpx") != null) {
            Bukkit.getPluginCommand("tpx").setExecutor(new TeleportCommander());
        }
        if (Bukkit.getPluginCommand("ecsadmin") != null) {
            Bukkit.getPluginCommand("ecsadmin").setExecutor(new AdminCommander());
        }
        if (Bukkit.getPluginCommand("keepinventory") != null) {
            Bukkit.getPluginCommand("keepinventory").setExecutor(new KeepInventoryCommander());
        }
        if (Bukkit.getPluginCommand("portalanchor") != null) {
            Bukkit.getPluginCommand("portalanchor").setExecutor(new PortalAnchorCommander());
        }
        Bukkit.getPluginManager().registerEvents(new KeepInventoryEvent(), this);
        Mysql m = new Mysql();
        if (!m.mysqlInit()) {
            getLogger().warning(ChatColor.RED + "Failed to connect to mysql. Check your config.yml to fix this. Plugin is shutting down.");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            getLogger().info(ChatColor.GREEN + "Mysql connected.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
