package ecsassistant.ecsassistant.commander;

import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.money.Vault;
import ecsassistant.ecsassistant.watcher.FlyWatcher;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;

//import javax.annotation.ParametersAreNonnullByDefault;

public class PortalAnchorCommander implements CommandExecutor {
    private static final Economy econ = Vault.getEconomy();

    public static Map<Player, Boolean> isFlying = new HashMap<>();;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        ConfigReader config = new ConfigReader();

        return true;
    }
}