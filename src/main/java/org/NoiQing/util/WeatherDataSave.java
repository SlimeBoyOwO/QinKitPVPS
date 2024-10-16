package org.NoiQing.util;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class WeatherDataSave {
    private static final Map<World,String> weatherStorage = new HashMap<>();
    private static boolean weatherChange = true;
    public static void setWeatherSwitch(boolean ifSwitch){
        weatherChange = ifSwitch;
    }
    public static boolean getWeatherSwitch(){
        return weatherChange;
    }
    public static Map<World,String> getWeatherStorage(){return weatherStorage;}
    public static void clearWeatherStorage(){
        weatherStorage.clear();
    }
}
