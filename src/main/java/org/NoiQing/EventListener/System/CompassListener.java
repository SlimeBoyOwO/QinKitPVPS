package org.NoiQing.EventListener.System;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassListener implements Listener {
    private final QinKitPVPS plugin;
    public CompassListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerCompass(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        ItemStack itemHeld = player.getInventory().getItem(event.getNewSlot());
        if(itemHeld != null && itemHeld.getType() == Material.COMPASS){
            new BukkitRunnable(){
                @Override
                public void run(){
                    String[] nearestPlayerData = null;

                    if (player.getWorld().getPlayers().size() == 1) {
                        cancel();
                    }else {
                        nearestPlayerData = Function.getNearestPlayer(player, 500);
                    }
                    updateTrackingCompass(player, itemHeld, nearestPlayerData);
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    private void updateTrackingCompass(Player player, ItemStack compass, String[] nearestPlayerData) {
        ItemMeta compassMeta = compass.getItemMeta();

        if (nearestPlayerData != null) {
            Player nearestPlayer = Function.getPlayer(player.getWorld(), nearestPlayerData[0]);
            double nearestPlayerDistance = Function.round(Double.parseDouble(nearestPlayerData[1]), 1);

            if (nearestPlayer != null && nearestPlayer.isOnline()) {
                if (compassMeta != null) {
                    compassMeta.setDisplayName("§b§l追踪目标：§3" + nearestPlayer.getName() + "  §b§l距离：§3" + nearestPlayerDistance);
                }

                player.setCompassTarget(nearestPlayer.getLocation());
            }
        } else {
            if (compassMeta != null) {
                String message = "§b§l当前没有别的玩家在线(OAO)";
                compassMeta.setDisplayName(message);
            }
        }

        compass.setItemMeta(compassMeta);
    }
}
