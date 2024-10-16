package org.NoiQing.util;

import org.NoiQing.api.QinMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QinMenusDataSave {
    private static final Map<String, QinMenu> menusStorage = new HashMap<>();
    private static final Map<String, QinMenu> menuTitleToMenu = new HashMap<>();
    private static final ArrayList<String> menuTitles = new ArrayList<>();
    public static Map<String, QinMenu> getMenusCache() { return menusStorage; }
    public static Inventory getMenuInventory(QinMenu menu, Player player) {
        return createInventoryFromQinMenu(menu, player);
    }
    public static Map<String, QinMenu> getMenuTitleToMenu() { return menuTitleToMenu; }
    public static void clearQinMenusDataSave() {
        menusStorage.clear();
    }
    public static void putMenuIntoCache(String menuID, QinMenu menu) {
        menusStorage.put(menuID,menu);
        menuTitles.add(menu.getMenuTitle());
        menuTitleToMenu.put(menu.getMenuTitle(),menu);
    }

    private static Inventory createInventoryFromQinMenu(QinMenu qinMenu, Player player) {
        int safeLine = qinMenu.getLines();
        if(safeLine > 6 || safeLine < 1) safeLine = 6;

        Inventory inv = Bukkit.createInventory(null,safeLine * 9,Function.changeColorCharacters(qinMenu.getMenuTitle()));

        for(int i = 0; i < 54; i++) {
            if(qinMenu.getMenuItem().get(i) == null) continue;
            ItemStack item;
            if(Function.meetRequirements(player,qinMenu.getMenuItem().get(i).requirements()))
                item = qinMenu.getMenuItem().get(i).item().clone();
            else item = qinMenu.getMenuItem().get(i).alternativeMenuItem().item().clone();

            //占位符替换
            Function.changeItemPlaceHolders(item, player);
            inv.setItem(i,item);

        }

        return inv;
    }
    public static ArrayList<String> getMenuTitles() {return menuTitles;}
}
