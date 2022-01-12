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

import static org.bukkit.Bukkit.getPlayer;

//import javax.annotation.ParametersAreNonnullByDefault;

public class AdminCommander implements CommandExecutor {
    private static final Economy econ = Vault.getEconomy();

    public static Map<Player, Boolean> isFlying = new HashMap<>();;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        ConfigReader config = new ConfigReader();

        switch (args[0]) {
            case "money": {
                Player targetPlayer = getPlayer(args[1]);
                if (targetPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "[ecsadmin]玩家不存在");
                    return false;
                }
                Vault.addVaultCurrency(targetPlayer.getUniqueId(), Double.parseDouble(args[2]));
                sender.sendMessage(ChatColor.GREEN + String.format("成功增加账户余额 %s ", args[2]));
                break;
            }
            default: {

                return false;
            }
        }
        return true;
    }
}