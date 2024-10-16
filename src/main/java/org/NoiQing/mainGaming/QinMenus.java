package org.NoiQing.mainGaming;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMenu;
import org.NoiQing.itemFunction.ItemsFunction;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.QinMenusDataSave;

import java.lang.reflect.InvocationTargetException;

public class QinMenus {
    private final CreateFileConfig menuConfigs;
    public QinMenus(QinKitPVPS plugin){
        this.menuConfigs = plugin.getResource();
    }

    public QinMenu getQinMenuFromQinMenuID(String menuID) {
        return loadMenuFromCacheOrCreate(menuID);
    }

    private QinMenu loadMenuFromCacheOrCreate(String menuID) {
        if(!QinMenusDataSave.getMenusCache().containsKey(menuID)){
            if(menuConfigs.getPluginDirectoryFiles("Menus",false).contains(menuID)){
                Configuration menu = menuConfigs.getMenu(menuID);
                try {
                    QinMenusDataSave.putMenuIntoCache(menuID,createMenuFromResource(menu));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return QinMenusDataSave.getMenusCache().get(menuID);
    }

    private QinMenu createMenuFromResource(Configuration resource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException{
        QinMenu menu = new QinMenu(resource.getName());
        menu.setMenuTitle(resource.getString("MenuTitle"));
        menu.setLines(resource.contains("Lines") ? resource.getInt("Lines") : 54);
        menu.setOpenCommands(resource.contains("OpenCommands") ? resource.getStringList("OpenCommands") : null);

        for (int i = 0; i < 54; i++)
            if (resource.contains("Items." + i)) {
                menu.setItems(i, ItemsFunction.getMenuItemFromPath(resource, "Items." + i));
            }


        return menu;
    }
}
