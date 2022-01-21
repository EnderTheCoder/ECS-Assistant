package ecsassistant.ecsassistant.watcher;

import ecsassistant.ecsassistant.commander.FlyCommander;
import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.TimerTask;

import static ecsassistant.ecsassistant.commander.FlyCommander.flyStartTime;
import static org.bukkit.Bukkit.getLogger;

public class FlyWatcher extends TimerTask {

    private Player player;

    private int counter = -1;

    private Location startFlyingLocation;

    public void setUser(Player user) {
        player = user;
        startFlyingLocation = player.getLocation();
    }

    private void setFlyOn() {
        player.setAllowFlight(true);
    }

   private void setFlyOff() {
        player.setAllowFlight(false);
   }


    //平面勾股定理
    public double distance(double x, double z) {
        return Math.pow(x * x + z * z, 0.5);
    }

    public void run() {

        //判断用户是否飞行途中离线
        if (!player.isOnline()) {
            getLogger().info("[flyx]User's Offline. Stopping flying.");

            FlyCommander.isFlying.put(player, false);
            setFlyOff();
            this.cancel();
            return;
        }

        //判断用户是否用光钱
        if (ConfigReader.getCosts("FlyPerSec") > Vault.checkCurrency(player.getUniqueId()))
        {
            FlyCommander.isFlying.put(player, false);

            setFlyOff();
            getLogger().info("[flyx]User's money running out. Stopping flying.");

            player.sendMessage(ChatColor.RED + "[flyx]没钱了，您不配");
            this.cancel();
            return;
        }

        //判断用户是否超出飞行范围，有两个标准：1.若在同一世界按照距离计算2.若不再同一世界则判定为超出范围
        if (!startFlyingLocation.getWorld().equals(player.getLocation().getWorld()) || distance(player.getLocation().getX() - startFlyingLocation.getX(), player.getLocation().getZ() - startFlyingLocation.getZ()) > ConfigReader.getDistance("Fly")) {
            setFlyOff();
            FlyCommander.isFlying.put(player, false);
            getLogger().info("[flyx]User flied too far.");
            player.sendMessage(ChatColor.RED + "[flyx]你超出了飞行范围");
            this.cancel();
            return;
        }



        //检测用户是否已经手动停止飞行
        if (!FlyCommander.isFlying.get(player)) {

            setFlyOff();
            this.cancel();
            return;
        }

        //这段代码没有必要
        if (!player.getAllowFlight()) {
            getLogger().info("[flyx]User started flying.");
            setFlyOn();
        }
        flyStartTime.put(player, new Timestamp(System.currentTimeMillis()));//录入飞行时间戳
        //每秒钟扣钱
        Vault.subtractCurrency(player.getUniqueId(), ConfigReader.getCosts("FlyPerSec"));

        this.counter++;
        //每60秒钟提醒用户一次计费
        if (this.counter % 60 == 0)
            player.sendMessage(ChatColor.AQUA + String.format("[flyx]您已累计飞行 %s 分钟，共消耗您的账号余额 %s", this.counter / 60, this.counter * ConfigReader.getCosts("FlyPerSec") + ConfigReader.getFlyCostsStart()));
    }
}
