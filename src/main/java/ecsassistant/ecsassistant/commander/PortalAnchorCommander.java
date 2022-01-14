package ecsassistant.ecsassistant.commander;

import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.database.Mysql;
import ecsassistant.ecsassistant.money.Vault;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

        Mysql m = new Mysql();
        if (args.length < 1) return false;

        switch (args[0]) {
            case "set": {
                if (args.length < 3) return false;
                m.prepareSql("SELECT name from portal_anchors WHERE name = ?");
                m.setData(1, args[2]);
                m.execute();
                ResultSet resultSet = m.getResult();
                try {
                    resultSet.next();
                    if (resultSet.getString("name").equals(args[2])) {
                        sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]已经有一个名称为" + args[2] + "的传送锚存在，无法创建更多");
                        break;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                switch (args[1]) {
                    case "public": {
                        if (Vault.checkCurrency(player.getUniqueId()) < config.getCosts("PortalAnchor.Public")) {
                            sender.sendMessage(ChatColor.RED + "[PortalAnchor]你的余额不足以支付创建新传送锚的费用");
                            break;
                        }
                        Vault.subtractCurrency(uuid, config.getCosts("PortalAnchor.Public"));
                        m.prepareSql("INSERT INTO portal_anchors (name, type, owner_uuid, x, y, z, teleport_costs, players_to_be_trusted, world) VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        m.setData(1, args[2]);
                        m.setData(2, args[1]);
                        m.setData(3, player.getUniqueId().toString());
                        m.setData(4, String.valueOf(player.getLocation().getX()));
                        m.setData(5, String.valueOf(player.getLocation().getY()));
                        m.setData(6, String.valueOf(player.getLocation().getZ()));
                        m.setData(7, "0.00");
                        m.setData(8, "{}");
                        m.setData(9, String.valueOf(player.getLocation().getWorld().getName()));

                        m.execute();
                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]传送锚已经创建，类型" + args[1]);
                        break;
                    }
                    case "private": {
                        if (Vault.checkCurrency(player.getUniqueId()) < config.getCosts("PortalAnchor.Private")) {
                            sender.sendMessage(ChatColor.RED + "[PortalAnchor]你的余额不足以支付创建新传送锚的费用");
                            break;
                        }
                        Vault.subtractCurrency(uuid, config.getCosts("PortalAnchor.Private"));

                        m.prepareSql("INSERT INTO portal_anchors (name, type, owner_uuid, x, y, z, teleport_costs, players_to_be_trusted, world) VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        m.setData(1, args[2]);
                        m.setData(2, args[1]);
                        m.setData(3, String.valueOf(((Player) sender).getUniqueId()));
                        m.setData(4, String.valueOf(player.getLocation().getX()));
                        m.setData(5, String.valueOf(player.getLocation().getY()));
                        m.setData(6, String.valueOf(player.getLocation().getZ()));
                        m.setData(7, "0.00");
                        m.setData(8, "{}");
                        m.setData(9, String.valueOf(player.getLocation().getWorld().getName()));

                        m.execute();
                        sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]传送锚已经创建，类型" + args[1]);
                        break;
                    }
                    case "commercial": {
                        if (args.length < 4) return false;

                        if (Vault.checkCurrency(player.getUniqueId()) < config.getCosts("PortalAnchor.Commercial")) {
                            sender.sendMessage(ChatColor.RED + "[PortalAnchor]你的余额不足以支付创建新传送锚的费用");
                            break;
                        }
                        Vault.subtractCurrency(uuid, config.getCosts("PortalAnchor.commercial"));

                        m.prepareSql("INSERT INTO portal_anchors (name, type, owner_uuid, x, y, z, teleport_costs, players_to_be_trusted, world) VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        m.setData(1, args[2]);
                        m.setData(2, args[1]);
                        m.setData(3, String.valueOf(((Player) sender).getUniqueId()));
                        m.setData(4, String.valueOf(player.getLocation().getX()));
                        m.setData(5, String.valueOf(player.getLocation().getY()));
                        m.setData(6, String.valueOf(player.getLocation().getZ()));
                        m.setData(7, args[3]);
                        m.setData(8, "{}");
                        m.setData(9, String.valueOf(player.getLocation().getWorld().getName()));

                        m.execute();
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
                if (args.length < 2) return false;

                m.prepareSql("SELECT * FROM portal_anchors WHERE owner_uuid = ?");
                m.setData(1, String.valueOf(player.getUniqueId()));
                m.execute();
                ResultSet resultSet = m.getResult();
                boolean isOwner = false;
                try {
                    while (resultSet.next()) {
                        if (resultSet.getString("owner_uuid").equals(String.valueOf(player.getUniqueId()))) {
                            if (resultSet.getString("name").equals(args[1])) {
                                isOwner = true;
                            }
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (isOwner) {
                    m.prepareSql("DELETE FROM portal_anchors WHERE name = ?");
                    m.setData(1, args[1]);
                    m.execute();
                    sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]名称为" + args[1] + "的传送锚删除成功");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]没有找到属于您的，且名称为" + args[1] + "的传送锚，请检查名称是否正确");
                }
                break;
            }
            case "tp": {
                if (args.length < 2) return false;

                m.prepareSql("SELECT * FROM portal_anchors WHERE name = ?");
                m.setData(1, args[1]);
                m.execute();
                ResultSet resultSet = m.getResult();
                boolean isPortalExists = false;

                Location location = null;
                double costs = 0;
                String type = "";
                String owner_uuid = "";


                try {
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        isPortalExists = true;
                        location = new Location(getWorld(resultSet.getString("world")), Double.parseDouble(resultSet.getString("x")), Double.parseDouble(resultSet.getString("y")), Double.parseDouble(resultSet.getString("z")));

                        costs = resultSet.getDouble("teleport_costs");
                        type = resultSet.getString("type");
                        owner_uuid = resultSet.getString("owner_uuid");

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                if (isPortalExists) {
                    Player owner = getPlayer(UUID.fromString(owner_uuid));

                    switch (type) {
                        case "public": {

                            player.teleport(location);

                            if (owner != null && owner.isOnline()) {
                                owner.sendMessage(ChatColor.AQUA + "[PortalAnchor]玩家" + player.getDisplayName() + "使用了你的公共传送锚" + args[1]);
                            }

                                sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你已被成功传送至目标传送锚" + args[1]);
                            break;
                        }
                        case "private": {
                            if (owner_uuid.equals(String.valueOf(player.getUniqueId()))) {
                                player.teleport(location);
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你没有使用目标传送锚" + args[1] + "的权限");
                            }
                            break;
                        }
                        case "commercial": {
                            if (Vault.checkCurrency(player.getUniqueId()) < costs) {
                                sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]你的账户余额不足以支付该传送锚设置者设置的花费，该传送锚的传送花费为" + costs);
                            } else {
                                Vault.subtractCurrency(uuid, costs);
                                Vault.addVaultCurrency(UUID.fromString(owner_uuid), costs);

                                player.teleport(location);
                                if (owner != null && owner.isOnline()) {
                                    owner.sendMessage(ChatColor.AQUA + "[PortalAnchor]玩家" + player.getDisplayName() + "使用了你的付费传送锚" + args[1] + "并向你支付了" + costs);
                                }
                                sender.sendMessage(ChatColor.GREEN + "[PortalAnchor]你已被成功传送至目标传送锚" + args[1] + "，本次传送花费" + costs);

                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "[PortalAnchor]没有找到属于您的，且名称为" + args[1] + "的传送锚，请检查名称是否正确");
                }

                break;
            }
            case "list": {
                m.prepareSql("SELECT * FROM portal_anchors");
                m.execute();
                ResultSet resultSet = m.getResult();
                player.sendMessage(ChatColor.AQUA + "[PortalAnchor]传送锚列表");
                    try {
                        while (resultSet.next()) {
                            if (resultSet.getString("type").equals("private")) {
                                if (!(resultSet.getString("owner_uuid").equals(String.valueOf(player.getUniqueId())))) {
                                    continue;
                                }
                            }

                            String message = "名称："+resultSet.getString("name")+"  类型"+resultSet.getString("type")+"  拥有者"+getOfflinePlayer(UUID.fromString(resultSet.getString("owner_uuid"))).getName()+"  位置("+resultSet.getString("world")+"/x:"+resultSet.getString("x")+"/y:"+resultSet.getString("y")+"/z:"+resultSet.getString("z")+")";

                            switch (resultSet.getString("type")) {
                                case "public": {
                                    message = ChatColor.GREEN + message;
                                    break;
                                }
                                case "private": {
                                    message = ChatColor.RED + message;
                                    break;
                                }
                                case "commercial": {
                                    message = ChatColor.GOLD +message;
                                    break;
                                }
                            }
                            player.sendMessage(message);

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
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