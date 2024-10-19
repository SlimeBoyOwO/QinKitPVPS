package org.NoiQing.util;

import org.NoiQing.AllayWar.PvzGame.PVZAPI.PvzMap;
import org.NoiQing.api.QinMap;

import java.util.HashMap;
import java.util.Map;

public class QinMapsDataSave {
    private static final Map<String, QinMap> mapsStorage = new HashMap<>();
    private static final Map<String, QinMap> teamedMapsStorage = new HashMap<>();
    private static final Map<String, QinMap> allayMapsStorage = new HashMap<>();
    private static final Map<String, PvzMap> pvzMapsStorage = new HashMap<>();
    public static Map<String, QinMap> getMapsCache() { return mapsStorage; }
    public static Map<String, QinMap> getTeamedMapsCache() {return teamedMapsStorage;}
    public static Map<String, QinMap> getAllayMapsStorage() {return allayMapsStorage;}
    public static Map<String, PvzMap> getPvzMapsStorage() {return pvzMapsStorage;}
    public static void clearQinMapsDataSave() {
        mapsStorage.clear();
        teamedMapsStorage.clear();
        allayMapsStorage.clear();
        pvzMapsStorage.clear();
    }

}
