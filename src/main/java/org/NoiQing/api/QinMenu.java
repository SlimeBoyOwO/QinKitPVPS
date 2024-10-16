package org.NoiQing.api;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QinMenu {
    public record MenuItem(ItemStack item,
                           List<String> itemsLeftCommands,
                           List<String> itemsRightCommands,
                           List<String> requirements,
                           MenuItem alternativeMenuItem) { }

    public QinMenu(String menuID) {
        this.menuID = menuID;
        menuItem = new HashMap<>();
    }

    private String menuID;
    private String menuTitle;
    private int lines;
    private final Map<Integer, MenuItem> menuItem;
    private List<String> openCommands;
    public void setMenuID(String id) {menuID = id;}
    public void setMenuTitle(String title) {menuTitle = title;}
    public void setItems(int shot, MenuItem item) {menuItem.put(shot,item);}
    public void setLines(int lines) {this.lines = lines;}
    public void setOpenCommands(List<String> commands) {this.openCommands = commands;}
    public String getMenuID() {return menuID;}
    public String getMenuTitle() {return menuTitle;}
    public Map<Integer, MenuItem> getMenuItem() {return menuItem;}
    public int getLines() {return lines;}
    public List<String> getOpenCommands() {return openCommands;}

}
