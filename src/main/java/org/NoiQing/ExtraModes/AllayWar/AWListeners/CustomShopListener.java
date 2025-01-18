package org.NoiQing.ExtraModes.AllayWar.AWListeners;

import org.NoiQing.EventListener.GuiListeners.CustomMenuListeners;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinMenusDataSave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class CustomShopListener implements Listener {
    @EventHandler
    public void onPlayerShop(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getCustomName() == null) return;
        if(event.getRightClicked() instanceof Player) return;
        Player p = event.getPlayer();
        if(p.getScoreboardTags().contains("raider_mode")) return;

        String name = event.getRightClicked().getCustomName();
        name = name.replaceAll(" ","_");

        Configuration shop = QinKitPVPS.getPlugin().getResource().getShop();
        if(!shop.contains("Traders."+ Function.getNameWithoutColor(name))) return;

        if(shop.contains("Traders."+ Function.getNameWithoutColor(name) + ".Menu")) {
            String menuName = shop.getString("Traders."+ Function.getNameWithoutColor(name) + ".Menu");
            if(menuName == null) {
                Function.sendPlayerSystemMessage(p,"菜单出错！请联系管理员");
                return;
            }
            CustomMenuListeners.openCreatedInventory(p, QinMenusDataSave.getMenuInventory(QinKitPVPS.getPlugin().getKitGame().getQinMenus().getQinMenuFromQinMenuID(menuName), p));
        }
        if(shop.contains("Traders."+ Function.getNameWithoutColor(name) + ".Commands")) {
            if(event.getHand().equals(EquipmentSlot.HAND)) {
                List<String> commandList = shop.getStringList("Traders."+ Function.getNameWithoutColor(name) + ".Commands");
                Function.executeCommands(p,commandList,"","");
            }
        }

        event.setCancelled(true);

    }
}
