package ecsassistant.ecsassistant.watcher;

import ecsassistant.ecsassistant.commander.FlyCommander;
import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.TimerTask;

import static org.bukkit.Bukkit.getLogger;

public class FlyWatcher extends TimerTask {

    private Player player;

    private int counter = -1;

    public void setUser(Player user) {
        player = user;
    }

    private final ConfigReader config = new ConfigReader();

    private void setFlyOn() {
        player.setAllowFlight(true);
    }

   private void setFlyOff() {
        player.setAllowFlight(false);
   }


    public void run() {

        if (!player.isOnline()) {
            getLogger().info("[flyx]User's Offline. Stopping flying.");

            FlyCommander.isFlying.put(player, false);
            setFlyOff();
            this.cancel();
            return;
        }

        if (config.getFlyCostsPerMin() > Vault.checkCurrency(player.getUniqueId()))
        {
            FlyCommander.isFlying.put(player, false);

            setFlyOff();
            getLogger().info("[flyx]User's money running out. Stopping flying.");

            player.sendMessage(ChatColor.RED + "[flyx]没钱了，您不配");
            this.cancel();
            return;
        }

        if (!FlyCommander.isFlying.get(player)) {

            setFlyOff();
            this.cancel();
            return;
        }

        if (!player.getAllowFlight()) {
            getLogger().info("[flyx]User started flying.");
            setFlyOn();
        }

        counter++;

        if (counter == 0) return;

        player.sendMessage(ChatColor.AQUA + String.format("[flyx]您已累计飞行 %s 分钟，共消耗您的账号余额 %s", counter, counter * config.getFlyCostsPerMin() + config.getFlyCostsStart()));


    }
}
