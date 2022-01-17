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


public class FlyCommander implements CommandExecutor {

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
            return false;
        } else {

            isFlying.put(player, true);//修改用户状态为飞行中
            Vault.subtractCurrency(player.getUniqueId(), config.getFlyCostsStart());//扣起步价
            player.sendMessage(ChatColor.GREEN + "[flyx]芜湖起飞");
            Timer t = new Timer();//启动飞行监视线程
            FlyWatcher w = new FlyWatcher();
            w.setUser(player);
            t.schedule(w, 1, 60 * 60 * 24);//最多飞行多少秒？
            return true;

        }

    }
}