package org.NoiQing.mainGaming;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.QinMapsDataSave;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class QinMaps {
    private final CreateFileConfig mapConfigs;
    public QinMaps(QinKitPVPS plugin){
        this.mapConfigs = plugin.getResource();
    }
    public QinMap getQinMapByMapID(int mapID){return loadMapFromCacheOrCreate(mapID);}
    public QinMap getTeamedQinMapByMapID(int mapID) {return loadTeamedMapFromCacheOrCreate(mapID);}
    public QinMap getAllayQinMapByMapID(int mapID) {return loadAllayMapFromCacheOrCreate(mapID);}

    private QinMap loadMapFromCacheOrCreate(int mapID) {
        String stringMapId = String.valueOf(mapID);
        if(!QinMapsDataSave.getMapsCache().containsKey(stringMapId)){
            if(mapConfigs.getPluginDirectoryFilesLevelTwo("Maps","MixedMaps",false).contains(stringMapId)){
                Configuration map = mapConfigs.getMixedMap(stringMapId);
                try {
                    QinMapsDataSave.getMapsCache().put(stringMapId, createMapFromResource(map));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return QinMapsDataSave.getMapsCache().get(stringMapId);
    }

    private QinMap loadTeamedMapFromCacheOrCreate(int mapID) {
        String stringTeamedMapId = String.valueOf(mapID);
        if(!QinMapsDataSave.getTeamedMapsCache().containsKey(stringTeamedMapId)){
            if(mapConfigs.getPluginDirectoryFilesLevelTwo("Maps","TeamedMaps",false).contains(stringTeamedMapId)){
                Configuration map = mapConfigs.getTeamedMap(stringTeamedMapId);
                try {
                    QinMapsDataSave.getTeamedMapsCache().put(stringTeamedMapId, createMapFromResource(map));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return QinMapsDataSave.getTeamedMapsCache().get(stringTeamedMapId);
    }

    private QinMap loadAllayMapFromCacheOrCreate(int mapID) {
        String stringAllayMapId = String.valueOf(mapID);
        if(!QinMapsDataSave.getAllayMapsStorage().containsKey(stringAllayMapId)){
            if(mapConfigs.getPluginDirectoryFilesLevelTwo("Maps","AllayMaps",false).contains(stringAllayMapId)){
                Configuration map = mapConfigs.getAllayMap(stringAllayMapId);
                try {
                    QinMapsDataSave.getAllayMapsStorage().put(stringAllayMapId, createMapFromResource(map));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return QinMapsDataSave.getAllayMapsStorage().get(stringAllayMapId);
    }

    private QinMap createMapFromResource(Configuration resource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException{
        QinMap qinMap = new QinMap();
        qinMap.setMapID(resource.getInt("MapID"));
        qinMap.setMapName(resource.getString("MapName"));

        Location center = new Location(Bukkit.getWorld("world"),0,0,0);
        center.setWorld(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))));
        center.setX(resource.getDouble("Center.X"));
        center.setY(resource.getDouble("Center.Y"));
        center.setZ(resource.getDouble("Center.Z"));
        qinMap.setCenterLocation(center);

        Location radius = new Location(Bukkit.getWorld("world"),0,0,0);
        radius.setWorld(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))));
        radius.setX(resource.getDouble("Radius.X"));
        radius.setY(resource.getDouble("Radius.Y"));
        radius.setZ(resource.getDouble("Radius.Z"));
        qinMap.setSpreadRadius(radius);

        for(int i = 0; i < 100; i++){
            if(resource.contains("Locations." + i)){
                Location location = new Location(Bukkit.getWorld("world"),0,0,0);
                location.setWorld(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))));
                location.setX(resource.getDouble("Locations." + i + ".X"));
                location.setY(resource.getDouble("Locations." + i + ".Y"));
                location.setZ(resource.getDouble("Locations." + i + ".Z"));
                qinMap.setLocation(String.valueOf(i), location);
            }
        }

        return qinMap;
    }
}
