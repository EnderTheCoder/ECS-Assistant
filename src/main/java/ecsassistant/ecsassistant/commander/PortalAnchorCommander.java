package ecsassistant.ecsassistant.commander;

import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.core.portalanchor.PortalAnchorCore;
import ecsassistant.ecsassistant.data.PortalAnchor;
import ecsassistant.ecsassistant.database.Mysql;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.bukkit.Bukkit.*;

//import javax.annotation.ParametersAreNonnullByDefault;

public class PortalAnchorCommander implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        ConfigReader config = new ConfigReader();
        if (args.length < 1) return false;
        switch (args[0]) {
            case "set": {
                if (args.length < 3) return false;
                if (PortalAnchorCore.isExists(args[2])) {
                    sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]已经有一个名称为" + args[2] + "的传送锚存在，无法创建更多");
                    break;
                }
                switch (args[1]) {
                    case "public": {

                        if (args.length != 3) return false;

                        if (Vault.checkCurrency(player.getUniqueId()) < config.getCosts("PortalAnchor.Public")) {//检测钱是否足够
                            sender.sendMessage(ChatColor.RED + "[PortalAnchor]你的余额不足以支付创建新传送锚的费用");
                            break;
                        }

                        Vault.subtractCurrency(uuid, config.getCosts("PortalAnchor.Public"));//扣钱

                        PortalAnchorCore.set(args[2], args[1], player, 0.0);//创建

                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]传送锚已经创建，类型" + args[1]);
                        break;
                    }
                    case "private": {
                        if (args.length != 3) return false;

                        if (Vault.checkCurrency(player.getUniqueId()) < config.getCosts("PortalAnchor.Private")) {
                            sender.sendMessage(ChatColor.RED + "[PortalAnchor]你的余额不足以支付创建新传送锚的费用");
                            break;
                        }
                        Vault.subtractCurrency(uuid, config.getCosts("PortalAnchor.Private"));

                        PortalAnchorCore.set(args[2], args[1], player, 0.0);

                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]传送锚已经创建，类型" + args[1]);
                        break;
                    }
                    case "commercial": {
                        if (args.length != 4) return false;

                        if (Double.parseDouble(args[3]) <= 0) {
                            sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]你输入的价格不合法");
                            break;
                        }

                        if (Vault.checkCurrency(player.getUniqueId()) < config.getCosts("PortalAnchor.Commercial")) {
                            sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]你的余额不足以支付创建新传送锚的费用");
                            break;
                        }
                        Vault.subtractCurrency(uuid, config.getCosts("PortalAnchor.commercial"));

                        PortalAnchorCore.set(args[2], args[1], player, Double.parseDouble(args[3]));

                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]传送锚已经创建，类型" + args[1]);

                        break;
                    }
                    default: {
                        return false;
                    }
                }

                break;
            }
            case "remove": {
                if (args.length != 2) return false;
                if (!PortalAnchorCore.isExists(args[1]) || !Objects.equals(PortalAnchorCore.getOwner(args[1]), player.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]没有找到属于您的，且名称为" + args[1] + "的传送锚，请检查名称是否正确");
                } else {
                    PortalAnchorCore.remove(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]名称为" + args[1] + "的传送锚删除成功");
                }
                break;
            }
            case "tp": {
                if (args.length < 2) return false;
                if (!PortalAnchorCore.isExists(args[1])) {
                    sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]你输入的传送锚不存在");
                    break;
                }

                PortalAnchor portalAnchor = PortalAnchorCore.get(args[1]);

                switch (portalAnchor.type) {
                    case "public": {

                        player.teleport(portalAnchor.location);

                        if (portalAnchor.owner.isOnline()) {
                            portalAnchor.owner.getPlayer().sendMessage(ChatColor.AQUA + "[PortalAnchor]玩家" + player.getDisplayName() + "使用了你的公共传送锚" + args[1]);
                        }
                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你已被成功传送至目标传送锚" + args[1]);
                        break;
                    }
                    case "private": {
                        if (portalAnchor.owner.getName().equals(player.getName())) {
                            player.teleport(portalAnchor.location);
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你没有使用目标传送锚" + args[1] + "的权限");
                        }
                        if (portalAnchor.owner.isOnline()) {
                            portalAnchor.owner.getPlayer().sendMessage(ChatColor.AQUA + "[PortalAnchor]玩家" + player.getDisplayName() + "使用了你的私人传送锚" + args[1]);
                        }
                        player.teleport(portalAnchor.location);
                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你已被成功传送至目标传送锚" + args[1]);
                        break;
                    }
                    case "commercial": {
                        if (Vault.checkCurrency(player.getUniqueId()) < portalAnchor.costs) {
                            sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]你的账户余额不足以支付该传送锚设置者设置的花费，该传送锚的传送花费为" + portalAnchor.costs);
                        } else {
                            Vault.subtractCurrency(uuid, portalAnchor.costs);
                            Vault.addVaultCurrency(portalAnchor.owner.getUniqueId(), portalAnchor.costs);
                            if (portalAnchor.owner.isOnline()) {
                                portalAnchor.owner.getPlayer().sendMessage(ChatColor.AQUA + "[PortalAnchor]玩家" + player.getDisplayName() + "使用了你的商业传送锚" + args[1]);
                            }
                            player.teleport(portalAnchor.location);
                            sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你已被成功传送至目标传送锚" + args[1] + "，本次传送花费" + Vault.curtate(portalAnchor.costs));
                        }
                        break;
                    }
                }
                break;
            }
            case "list": {

                PortalAnchor[] portalAnchors = PortalAnchorCore.getAll();

                for (PortalAnchor portalAnchor : portalAnchors) {
                    if (portalAnchor == null) break;
                    String message = "名称：" + portalAnchor.name + "  类型" + portalAnchor.type + "  拥有者" + portalAnchor.owner.getName() + "  位置(" + portalAnchor.location.getWorld().getName()+ "/x:" + Vault.curtate(portalAnchor.location.getX()) + "/y:" + Vault.curtate(portalAnchor.location.getY()) + "/z:" + Vault.curtate(portalAnchor.location.getZ()) + ")";
                    World world = portalAnchor.location.getWorld();
                    switch (portalAnchor.type) {
                        case "public": {
                            message = ChatColor.GREEN + message;
                            break;
                        }
                        case "private": {
                            if (!Objects.equals(portalAnchor.owner.getName(), player.getName())) continue;
                            message = ChatColor.RED + message;
                            break;
                        }
                        case "commercial": {
                            message = message + "  传送花费:" + Vault.curtate(portalAnchor.costs);
                            message = ChatColor.GOLD + message;
                            break;
                        }
                    }
                    player.sendMessage(message);
                }

                break;
            }
            default: {
                return false;
            }
        }

        return true;
    }
}