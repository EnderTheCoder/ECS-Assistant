package ecsassistant.ecsassistant.data;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class PortalAnchor {
    public Location location;
    public String name;
    public OfflinePlayer owner;
    public Double costs;
    public List<String> trustedPlayers;
    public String type;
}
