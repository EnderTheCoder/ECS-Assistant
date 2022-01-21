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

import java.sql.Timestamp;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;


public class FlyCommander implements CommandExecutor {

    public static Map<Player, Boolean> isFlying = new HashMap<>();
    public static Map<Player, Timestamp> flyStartTime = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();


        //判断用户是否在飞行中以便区分是要开始飞行还是停止飞行
        if (isFlying.get(player) != null && isFlying.get(player)) {
            isFlying.put(player, false);
            getLogger().info("[flyx]User stopped his flying by himself.");
            player.sendMessage(ChatColor.GOLD + "[flyx]飞行结束");
            player.setAllowFlight(false);
            return true;
        }

        //判断用户是否有足够的钱
        if (Vault.checkCurrency(uuid) < ConfigReader.getFlyCostsStart()) {
            player.sendMessage(ChatColor.RED + "[flyx]账户余额不足以支付飞行起步价，起飞失败");
            return true;
        }


        //判断是否在CD中
        if (flyStartTime.get(player) != null) {
            long currentTime = (new Timestamp(System.currentTimeMillis()).getTime())/1000;
            long afterCDTime = (flyStartTime.get(player).getTime()/1000 + ConfigReader.getCD("Fly"));

            if (afterCDTime > currentTime) {
                player.sendMessage(ChatColor.YELLOW + String.format("[flyx]飞行冷却中，剩余CD：%s秒", afterCDTime - currentTime));
                return true;
            }
        }

        flyStartTime.put(player, new Timestamp(System.currentTimeMillis()));//录入飞行时间戳


        isFlying.put(player, true);//修改用户状态为飞行中
        Vault.subtractCurrency(player.getUniqueId(), ConfigReader.getFlyCostsStart());//扣起步价
        player.sendMessage(ChatColor.GREEN + "[flyx]芜湖起飞");
        Timer t = new Timer();//启动飞行监视线程
        FlyWatcher w = new FlyWatcher();
        w.setUser(player);
        t.schedule(w, 1000, 1000);//最多飞行多少秒？
        return true;


    }
}