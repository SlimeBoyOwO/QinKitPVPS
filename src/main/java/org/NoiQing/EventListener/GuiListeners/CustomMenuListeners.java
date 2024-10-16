package org.NoiQing.EventListener.GuiListeners;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMenu;
import org.NoiQing.mainGaming.Game;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinMenusDataSave;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomMenuListeners implements Listener {
    private final Game game;

    public CustomMenuListeners(QinKitPVPS plugin) {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        this.game = plugin.getGame();
    }

    @EventHandler
    public void onPlayerRightClickQinMenu(PlayerInteractEvent event){
        if(!Function.isRightClicking(event)) return;
        if(event.getItem() == null) return;
        if(event.getItem().getItemMeta() == null) return;

        QinMenu qinMenu = game.getQinMenus().getQinMenuFromQinMenuID(Function.getNameWithoutColor(event.getItem().getItemMeta().getDisplayName()));
        if(qinMenu == null) return;

        openCreatedInventory(event.getPlayer(), QinMenusDataSave.getMenuInventory(qinMenu,event.getPlayer()));
        Function.executeCommands(event.getPlayer(),qinMenu.getOpenCommands(),"","");
    }

    public static void openCreatedInventory(HumanEntity player, Inventory inv) {
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        for(String title : QinMenusDataSave.getMenuTitles()) {
            if(!e.getWhoClicked().getOpenInventory().getTitle().equals(Function.changeColorCharacters(title))) continue;
            e.setCancelled(true);
            final ItemStack clickedItem = e.getCurrentItem();

            if (clickedItem == null || clickedItem.getType().isAir()) return;

            Player p = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();

            QinMenu menu = QinMenusDataSave.getMenuTitleToMenu().get(title);
            if(menu == null) return;
            QinMenu.MenuItem item = Function.meetRequirements(p,menu.getMenuItem().get(slot).requirements()) ?
                    menu.getMenuItem().get(slot) : menu.getMenuItem().get(slot).alternativeMenuItem();
            if(e.isLeftClick()) Function.executeCommands(p, item.itemsLeftCommands(),"","");
            if(e.isRightClick()) Function.executeCommands(p,item.itemsRightCommands(),"","");
            break;
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        for(String title : QinMenusDataSave.getMenuTitles()) {
            if(e.getWhoClicked().getOpenInventory().getTitle().equals(Function.changeColorCharacters(title))) {
                e.setCancelled(true);
                break;
            }
        }
    }
}
