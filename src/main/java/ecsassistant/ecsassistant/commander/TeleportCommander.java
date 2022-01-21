package ecsassistant.ecsassistant.commander;

import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.data.TeleportRequest;
import ecsassistant.ecsassistant.money.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.Timestamp;
import java.util.*;

import static org.bukkit.Bukkit.*;


public class TeleportCommander implements CommandExecutor {
    private static final Economy econ = Vault.getEconomy();
    public static Map<Player, TeleportRequest> teleportRequest= new HashMap<>();
//    public static Map<Player, Double> teleportCosts= new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length == 0) {
            TeleportRequest request = teleportRequest.get(player);
            if (request == null) {
                player.sendMessage(ChatColor.RED + "[tpx]您没有等待中的传送请求");
                return false;
            }

            if (!request.sender.isOnline()) {
                player.sendMessage(ChatColor.RED + "[tpx]接受传送请求失败，因为对方已离线");
                return true;
            }

            if (request.costs > Vault.checkCurrency(request.sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "[tpx]对方的账户余额不足以支持此次传送");
                request.sender.sendMessage(ChatColor.RED + "[tpx]你的账户余额不足以支持此次传送");
                return false;
            }


            teleportRequest.put(player, null);

            request.sender.teleport(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "[tpx]对方已成功传送到你的身边");

            Vault.subtractCurrency(request.sender.getUniqueId(), request.costs);
            request.sender.sendMessage(ChatColor.GREEN + String.format("[tpx]传送成功，消耗账户余额 %s", request.costs));
            return true;
        }




        Player targetPlayer = getPlayerExact(args[0]);


        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "[tpx]你寻找的玩家不存在");
            return false;
        }


        TeleportRequest request = new TeleportRequest();
        request.sender = player;
        request.receiver = targetPlayer;
//        double costs;

        if (targetPlayer.getLocation().getWorld().equals(player.getLocation().getWorld())) {
            request.costs = player.getLocation().distance(targetPlayer.getLocation()) * ConfigReader.getTeleportCosts();//同一维度使用距离计算价格
        } else {
            request.costs = ConfigReader.getCrossDimensionTeleportCosts();//不同纬度使用固定价格
        }

        if (request.costs > Vault.checkCurrency(uuid)) {
            sender.sendMessage(ChatColor.RED + String.format("[tpx]你的账户余额不足以支持此次传送，需要 %s ，", request.costs));
            return false;
        }






        teleportRequest.put(targetPlayer, request);
        targetPlayer.sendMessage(ChatColor.YELLOW + String.format("[tpx]玩家 %s 请求传送到您身边，输入命令/tpx接受，如果您不同意忽略即可", player.getDisplayName()));
        player.sendMessage(ChatColor.AQUA + "[tpx]传送请求已发出，请等待对方同意");

        return true;
    }
}