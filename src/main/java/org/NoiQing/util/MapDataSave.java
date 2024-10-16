package org.NoiQing.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class MapDataSave {
    private static final Map<World, Integer> mapStorage = new HashMap<>();
    public static Map<World, Integer> getMapStorage(){
        if(mapStorage.isEmpty()){
            mapStorage.put(Bukkit.getWorld("world"),0);
        }
        return mapStorage;
    }
    public static void clearMapStorage(){
        mapStorage.clear();
    }
}
