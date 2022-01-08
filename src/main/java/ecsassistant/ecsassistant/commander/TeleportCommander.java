package ecsassistant.ecsassistant.commander;

import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.money.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.*;


public class TeleportCommander implements CommandExecutor {
    private static final Economy econ = Vault.getEconomy();
    public static Map<Player, Player> teleportRequest= new HashMap<>();
    public static Map<Player, Double> teleportCosts= new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        ConfigReader config = new ConfigReader();

        if (args.length == 0) {
            Player targetPlayer = teleportRequest.get(player);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "[tpx]您没有等待中的传送请求");
                return false;
            }

            if (!targetPlayer.isOnline()) {
                player.sendMessage(ChatColor.RED + "[tpx]接受传送请求失败，因为对方已离线");
                return true;
            }



            targetPlayer.teleport(player.getLocation());
            targetPlayer.sendMessage(ChatColor.GREEN + String.format("[tpx]传送成功，消耗账户余额 %s", teleportCosts.get(player)));
            player.sendMessage(ChatColor.GREEN + "[tpx]对方已成功传送到你的身边");
            if (teleportCosts.get(player) > Vault.checkCurrency(targetPlayer.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "[tpx]对方的账户余额不足以支持此次传送");
                targetPlayer.sendMessage(ChatColor.RED + "[tpx]你的账户余额不足以支持此次传送");
                return false;
            }

            Vault.subtractCurrency(targetPlayer.getUniqueId(), teleportCosts.get(player));
            return true;
        }




        Player targetPlayer = getPlayerExact(args[0]);


        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "[tpx]你寻找的玩家不存在");
            return false;
        }


        double costs;

        if (targetPlayer.getLocation().getWorld().equals(player.getLocation().getWorld())) {
            costs = config.getCrossDimensionTeleportCosts();//不同纬度使用固定价格
        } else {
            costs = player.getLocation().distance(targetPlayer.getLocation()) * config.getTeleportCosts();//同一维度使用距离计算价格
        }

        if (costs > Vault.checkCurrency(uuid)) {
            sender.sendMessage(ChatColor.RED + String.format("[tpx]你的账户余额不足以支持此次传送，需要 %s ，", costs));
            return false;
        }



        teleportRequest.put(targetPlayer, player);
        teleportCosts.put(targetPlayer, costs);
        targetPlayer.sendMessage(ChatColor.YELLOW + String.format("[tpx]玩家 %s 请求传送到您身边，输入命令/tpx接受，如果您不同意忽略即可", player.getDisplayName()));
        player.sendMessage(ChatColor.AQUA + "[tpx]传送请求已发出，请等待对方同意");

        return true;
    }
}