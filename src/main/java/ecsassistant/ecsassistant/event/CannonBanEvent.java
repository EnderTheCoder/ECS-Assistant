package ecsassistant.ecsassistant.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CannonBanEvent implements Listener {
    //全服通报
    public void banMessage(Player player) {
        Bukkit.broadcast(ChatColor.RED + String.format("[ECS]警告：玩家%s试图使用蓝图大炮引起崩服，请不要尝试以任何手段使用蓝图大炮，因为他会导致服务器产生黑洞方块并反复崩溃", player.getName()), "ecs.broadcast");
    }
    //禁止点击大炮
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null && event.getCurrentItem().getType().name().equals("CREATE_SCHEMATICANNON")) {
            event.setCancelled(true);
            banMessage(player);
        }
    }
    //禁止交互大炮
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getMaterial().name().equals("CREATE_SCHEMATICANNON")) {
            event.setCancelled(true);
            event.getPlayer().getInventory().clear();
//            event.getClickedBlock().setBlockData(null);
            banMessage(event.getPlayer());
        }
    }
    //禁止扔出大炮
    @EventHandler
    public void onThrow(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType().name().equals("CREATE_SCHEMATICANNON")) {
            event.setCancelled(true);
            banMessage(event.getPlayer());
        }
    }
    //禁止捡起大炮
    @EventHandler
    public void onPickUp(EntityPickupItemEvent event){
        if (event.getItem().getItemStack().getType().name().equals("CREATE_SCHEMATICANNON")) {
            event.setCancelled(true);
            if (event.getEntity() instanceof Player) {
                banMessage((Player) event.getEntity());
            }
        }
    }
}
