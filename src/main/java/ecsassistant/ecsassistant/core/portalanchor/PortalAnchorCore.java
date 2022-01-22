package ecsassistant.ecsassistant.core.portalanchor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ecsassistant.ecsassistant.config.ConfigReader;
import ecsassistant.ecsassistant.data.PortalAnchor;
import ecsassistant.ecsassistant.database.Mysql;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.bukkit.Bukkit.*;

public class PortalAnchorCore {
    public static boolean isExists(String name) {
        Mysql m = new Mysql();
        m.prepareSql("SELECT name FROM portal_anchors WHERE name = ?");
        m.setData(1, name);
        m.execute();
        ResultSet resultSet = m.getResult();
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        m.close();

        return false;
    }

    public static boolean isMax(Player player) {
        Mysql m = new Mysql();
        m.prepareSql("SELECT owner_uuid FROM portal_anchors WHERE owner_uuid = ?");
        m.setData(1, String.valueOf(player.getUniqueId()));
        m.execute();
        ResultSet resultSet = m.getResult();
        try {
            int row = 0;
            while (resultSet.next()) row++;

            return row >= ConfigReader.getMax("PortalAnchor");
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isOwner(Player player, String name) {
        return player.getName().equals(getOwner(name));
    }

    public static String getOwner(String name) {
        Mysql m = new Mysql();
        m.prepareSql("SELECT owner_uuid FROM portal_anchors WHERE name = ?");
        m.setData(1, name);
        m.execute();
        ResultSet resultSet = m.getResult();
        OfflinePlayer player = null;
        try {
            if (resultSet.next()) player = getOfflinePlayer(UUID.fromString(resultSet.getString("owner_uuid")));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        m.close();

        assert player != null;
        return player.getName();
    }

    public static void set(String name, String type, Player player, Double costs) {
        Mysql m = new Mysql();
        m.prepareSql("INSERT INTO portal_anchors (name, type, owner_uuid, x, y, z, teleport_costs, players_to_be_trusted, world) VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        m.setData(1, name);
        m.setData(2, type);
        m.setData(3, String.valueOf(player.getUniqueId()));
        m.setData(4, String.valueOf(player.getLocation().getX()));
        m.setData(5, String.valueOf(player.getLocation().getY()));
        m.setData(6, String.valueOf(player.getLocation().getZ()));
        m.setData(7, String.valueOf(costs));
        m.setData(8, "{}");
        m.setData(9, String.valueOf(player.getLocation().getWorld().getName()));

        m.execute();
        m.close();

    }

    public static void remove(String name) {
        Mysql m = new Mysql();
        m.prepareSql("DELETE FROM portal_anchors WHERE name = ?");
        m.setData(1, name);
        m.execute();
        m.close();

    }

    public static PortalAnchor get(String name) {
        PortalAnchor portalAnchor = new PortalAnchor();

        Mysql m = new Mysql();
        m.prepareSql("SELECT * FROM portal_anchors WHERE name = ?");
        m.setData(1, name);
        m.execute();
        ResultSet resultSet = m.getResult();
        try {
            resultSet.next();
            portalAnchor.name = resultSet.getString("name");
            portalAnchor.type = resultSet.getString("type");
            portalAnchor.owner = getOfflinePlayer(UUID.fromString(resultSet.getString("owner_uuid")));
            portalAnchor.location = new Location(getWorld(resultSet.getString("world")), Double.parseDouble(resultSet.getString("x")), Double.parseDouble(resultSet.getString("y")), Double.parseDouble(resultSet.getString("z")));
            portalAnchor.costs = resultSet.getDouble("teleport_costs");
//            if (resultSet.getString("players_to_be_trusted").equals("{}")) portalAnchor.trustedPlayers = null;
//            else {
//                JSONArray jsonArray = JSON.parseArray(resultSet.getString("players_to_be_trusted"));
//                for (int i = 0; i < jsonArray.size(); i++) {
//                    portalAnchor.trustedPlayers.set(i, jsonArray.getString(i));
//                }
//            }
            portalAnchor.trustedPlayers = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        m.close();

        return portalAnchor;
    }


    public static PortalAnchor[] getAll() {
        PortalAnchor[] portalAnchors = new PortalAnchor[10000];
        Mysql m = new Mysql();
        m.prepareSql("SELECT * FROM portal_anchors");
        m.execute();
        ResultSet resultSet = m.getResult();
        try {
            int counter = 0;
            while (resultSet.next()) {
                portalAnchors[counter] = new PortalAnchor();
                portalAnchors[counter].name = resultSet.getString("name");
                portalAnchors[counter].type = resultSet.getString("type");
                portalAnchors[counter].owner = getOfflinePlayer(UUID.fromString(resultSet.getString("owner_uuid")));
                portalAnchors[counter].location = new Location(getWorld(resultSet.getString("world")), Double.parseDouble(resultSet.getString("x")), Double.parseDouble(resultSet.getString("y")), Double.parseDouble(resultSet.getString("z")));
                portalAnchors[counter].costs = resultSet.getDouble("teleport_costs");
//                if (resultSet.getString("players_to_be_trusted").equals("{}")) portalAnchors[counter].trustedPlayers = null;
//                else {
//                    JSONArray jsonArray = JSON.parseArray(resultSet.getString("players_to_be_trusted"));
//                    for (int i = 0; i < jsonArray.size(); i++) {
//                        portalAnchors[counter].trustedPlayers.set(i, jsonArray.getString(i));
//                    }
//                }
                portalAnchors[counter].trustedPlayers = null;
                counter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        m.close();
        return portalAnchors;
    }
}
