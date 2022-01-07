package ecsassistant.ecsassistant;

import ecsassistant.ecsassistant.commander.FlyCommander;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
