package org.NoiQing.mainGaming;

import org.NoiQing.AllayWar.PvzGame.PVZAPI.PvzMap;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.QinMapsDataSave;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

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
    public PvzMap getPvzMapByMapID(int mapID) {return loadPvzMapFromCacheOrCreate(mapID);}

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

    private PvzMap loadPvzMapFromCacheOrCreate(int mapID) {
        String stringPvzMapId = String.valueOf(mapID);
        if(!QinMapsDataSave.getPvzMapsStorage().containsKey(stringPvzMapId)){
            if(mapConfigs.getPluginDirectoryFilesLevelTwo("Maps","PvzMaps",false).contains(stringPvzMapId)){
                Configuration map = mapConfigs.getPvzMap(stringPvzMapId);
                try {
                    QinMapsDataSave.getPvzMapsStorage().put(stringPvzMapId, createPvzMapFromResource(map));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return QinMapsDataSave.getPvzMapsStorage().get(stringPvzMapId);
    }

    private PvzMap createPvzMapFromResource(Configuration resource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PvzMap pvzMap = new PvzMap();
        pvzMap.setMapID(resource.getInt("MapID"));
        pvzMap.setMapName(resource.getString("MapName"));
        Location center = new Location(Bukkit.getWorld("world"),0,0,0);
        center.setWorld(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))));
        center.setX(resource.getDouble("Center.X"));
        center.setY(resource.getDouble("Center.Y"));
        center.setZ(resource.getDouble("Center.Z"));
        pvzMap.setCenterLocation(center);
        // 设置村民区域
        Location villagerArea = new Location(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))),
                resource.getDouble("VillagerCenter.X"),
                resource.getDouble("VillagerCenter.Y"),
                resource.getDouble("VillagerCenter.Z"));
        pvzMap.setVillagerArea(villagerArea);

        // 设置出怪位置
        // 设置怪物生成区域
        ConfigurationSection monsterAreaSection = resource.getConfigurationSection("MonsterArea");
        if (monsterAreaSection != null) {
            // 获取两个角落的坐标
            Location corner1 = new Location(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))),
                    monsterAreaSection.getDouble("1.X"),
                    monsterAreaSection.getDouble("1.Y"),
                    monsterAreaSection.getDouble("1.Z"));

            Location corner2 = new Location(Bukkit.getWorld(Objects.requireNonNull(resource.getString("World"))),
                    monsterAreaSection.getDouble("2.X"),
                    monsterAreaSection.getDouble("2.Y"),
                    monsterAreaSection.getDouble("2.Z"));

            // 设置怪物生成区域
            pvzMap.setMonsterSpawnArea(corner1, corner2);
        }

        // 设置关卡信息
        ConfigurationSection levelsSection = resource.getConfigurationSection("Levels");
        if (levelsSection != null) {
            for (String levelKey : levelsSection.getKeys(false)) {
                PvzMap.LevelData levelData = new PvzMap.LevelData(levelKey,levelsSection.getInt(levelKey + ".TotalTime"));

                ConfigurationSection wavesSection = levelsSection.getConfigurationSection(levelKey + ".Waves");
                if (wavesSection != null) {
                    for (String waveKey : wavesSection.getKeys(false)) {
                        String type = wavesSection.getString(waveKey + ".Type");
                        int amount = wavesSection.getInt(waveKey + ".Amount");
                        levelData.addWaveData(Integer.parseInt(waveKey), new PvzMap.WaveData(type, amount));
                    }
                }

                pvzMap.addLevelData(levelKey, levelData);
            }
        }

        return pvzMap;

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
