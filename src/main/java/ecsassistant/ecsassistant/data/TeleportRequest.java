package ecsassistant.ecsassistant.data;

import org.bukkit.entity.Player;

import java.security.Timestamp;

public class TeleportRequest {
    public double costs;
    public Player sender;
    public Player receiver;
    public Timestamp setTime;
}
