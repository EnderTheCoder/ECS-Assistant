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

public class FlyCommander implements CommandExecutor {
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

        if (isFlying.get(player) != null && isFlying.get(player)) {

            isFlying.put(player, false);
            getLogger().info("[flyx]User stopped his flying by himself.");
            player.sendMessage(ChatColor.GOLD + "[flyx]飞行结束");
            player.setAllowFlight(false);
            return true;
        }

        if (Vault.checkCurrency(uuid) < config.getFlyCostsStart()) {
            player.sendMessage(ChatColor.RED + "[flyx]账户余额不足以支付飞行起步价，起飞失败");
        } else {

            isFlying.put(player, true);

            player.sendMessage(ChatColor.GREEN + "[flyx]芜湖起飞");
            Timer t = new Timer();
            FlyWatcher w = new FlyWatcher();

            w.setUser(player);

            t.schedule(w, 60, 86400);

        }

        return true;
    }
}