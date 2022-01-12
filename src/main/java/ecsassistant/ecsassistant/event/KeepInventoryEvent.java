package ecsassistant.ecsassistant.event;

import ecsassistant.ecsassistant.ECSAssistant;
import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.config.UserConfig;
import ecsassistant.ecsassistant.database.Mysql;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class KeepInventoryEvent implements Listener {


    @EventHandler
    public void keepInventory(PlayerDeathEvent e) {

        Player player = e.getEntity().getPlayer();
        UUID uuid = player.getUniqueId();
        ConfigReader config = new ConfigReader();

//        if (Vault.checkCurrency(uuid) < config.getKeepInventoryCosts()) {
//            e.
//        }

//        new BukkitRunnable() {
//            @Override
//            public void run() {
                if (UserConfig.getUserConfig(uuid, "isKeepInventoryEnabled").equals("true")) {
                    if (Vault.checkCurrency(uuid) < config.getKeepInventoryCosts()) {
                        player.sendMessage(ChatColor.YELLOW + "[ki]虽然你启动了死亡不掉落保护，但是你的账户余额不足以让你优雅的死亡");
                    } else {
                        e.setKeepInventory(true);
                        e.setKeepLevel(true);
                        e.getDrops().clear();
                        e.setDeathMessage(ChatColor.GREEN + "[ki]死亡不掉落保护成功启动，你的物品没有丢失");
                        Vault.subtractCurrency(uuid, config.getKeepInventoryCosts());
                    }
                }
//            }
//        }.runTaskAsynchronously(ECSAssistant.instance);

    }
}