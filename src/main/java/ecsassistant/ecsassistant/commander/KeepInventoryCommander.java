package ecsassistant.ecsassistant.commander;

import ecsassistant.ecsassistant.ECSAssistant;
import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.config.UserConfig;
import ecsassistant.ecsassistant.money.Vault;
import ecsassistant.ecsassistant.watcher.FlyWatcher;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;

//import javax.annotation.ParametersAreNonnullByDefault;

public class KeepInventoryCommander implements CommandExecutor {
    private static final Economy econ = Vault.getEconomy();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            public void run() {
                if (UserConfig.getUserConfig(uuid, "isKeepInventoryEnabled").equals("true")) {
                    UserConfig.putUserConfig(uuid, "isKeepInventoryEnabled", "false");
                    player.sendMessage(ChatColor.YELLOW + "[ki]死亡不掉落保护现在已经关闭");
                } else {
                    UserConfig.putUserConfig(uuid, "isKeepInventoryEnabled", "true");
                    player.sendMessage(ChatColor.GREEN + "[ki]死亡不掉落保护现在已经开启");
                }
            }
        }.runTaskAsynchronously(ECSAssistant.instance);
        return true;
    }
}