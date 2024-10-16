package org.NoiQing.api;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class QinMap {
    private int mapID;
    private final Map<String,Location> location = new HashMap<>();
    private Location centerLocation;
    private Location spreadRadius;
    private String mapName;
    public void setMapID(int mapID) {this.mapID = mapID;}
    public void setMapName(String mapName) {this.mapName = mapName;}
    public void setLocation(String id, Location location) {this.location.put(id,location);}
    public void setCenterLocation(Location location){this.centerLocation = location;}
    public void setSpreadRadius(Location radius){this.spreadRadius = radius;}
    public int getMapID() {return mapID;}
    public String getMapName() {return mapName;}
    public Map<String,Location> getLocation(){return location;}
    public Location getCenterLocation(){return centerLocation;}
    public Location getSpreadRadius(){return spreadRadius;}
}
