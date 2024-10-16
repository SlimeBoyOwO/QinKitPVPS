package org.NoiQing.AllayWar.AWListeners;

import org.NoiQing.EventListener.GuiListeners.CustomMenuListeners;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinMenusDataSave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CustomShopListener implements Listener {
    @EventHandler
    public void onPlayerShop(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getCustomName() == null) return;
        if(event.getRightClicked() instanceof Player) return;
        Player p = event.getPlayer();
        if(p.getScoreboardTags().contains("raider_mode")) return;

        String name = event.getRightClicked().getCustomName();
        name = name.replaceAll(" ","_");
        if(!QinKitPVPS.getPlugin().getResource().getShop().contains("Traders."+ Function.getNameWithoutColor(name))) return;

        String menuName = QinKitPVPS.getPlugin().getResource().getShop().getString("Traders."+ Function.getNameWithoutColor(name) + ".Menu");
        if(menuName == null) {
            Function.sendPlayerSystemMessage(p,"菜单出错！请联系管理员");
            return;
        }

        CustomMenuListeners.openCreatedInventory(p, QinMenusDataSave.getMenuInventory(QinKitPVPS.getPlugin().getGame().getQinMenus().getQinMenuFromQinMenuID(menuName), p));
        event.setCancelled(true);

    }
}
