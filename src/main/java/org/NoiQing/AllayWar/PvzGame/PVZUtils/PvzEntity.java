package org.NoiQing.AllayWar.PvzGame.PVZUtils;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.*;

public class PvzEntity {
    private static final Map<Entity, List<Display>> plantDisplays = new HashMap<>();
    private static final Map<Entity, Integer> plantAttackCD = new HashMap<>();
    private static final Map<Entity,Entity> bulletOwner = new HashMap<>();
    private static final Map<Entity, org.bukkit.util.Vector> bulletVector = new HashMap<>();
    private static final Map<Entity, Location> entityLastLoc = new HashMap<>();
    private static final Map<Entity,Map<Entity,Location>> entityLastLocation = new HashMap<>();
    private static final Map<Player, Integer> playerSun = new HashMap<>();
    private static final Map<Mob,Entity> mobTarget = new HashMap<>();
    public static List<Display> getPlantDisplays(Entity plant){
        plantDisplays.computeIfAbsent(plant, k -> new ArrayList<>());
        return plantDisplays.getOrDefault(plant, null);
    }
    public static void removePlantDisplays(Entity plant) {
        plantDisplays.remove(plant);
    }
    public static void setPlantAttackCD(Entity a, int cd){
        plantAttackCD.put(a, cd);
    }
    public static int getPlantAttackCD(Entity a) {
        return plantAttackCD.getOrDefault(a,-1);
    }
    public static void setBulletOwner(Entity bullet, Entity owner) {
        bulletOwner.put(bullet,owner);
    }
    public static Entity getBulletOwner(Entity bullet) {
        return bulletOwner.getOrDefault(bullet,null);
    }
    public static void setBulletVector(Entity bullet, org.bukkit.util.Vector vector) {
        bulletVector.put(bullet,vector);
    }
    public static org.bukkit.util.Vector getBulletVector(Entity bullet) {
        return bulletVector.getOrDefault(bullet,bullet.getVelocity());
    }
    public static void setEntityLastLoc(Entity e, Location loc) {
        entityLastLoc.put(e,loc);
    }
    public static Location getEntityLastLoc(Entity e) {
        return entityLastLoc.getOrDefault(e,e.getLocation());
    }
    public static void clearEntityLastLoc(Entity e) {
        entityLastLoc.remove(e);
    }
    public static void setEntityLastLocation(Entity e, Entity plant) {
        entityLastLocation.getOrDefault(e,new HashMap<>()).put(plant,e.getLocation());
    }
    public static Location getEntityLastLocation(Entity e, Entity plant) {
        return entityLastLocation.getOrDefault(e,new HashMap<>()).getOrDefault(plant,e.getLocation());
    }
    public static Integer getPlayerSun(Player p) {
        return playerSun.getOrDefault(p,0);
    }
    public static void setPlayerSun(Player p, int sun) {
        playerSun.put(p,sun);
    }
    public static void setMobTarget(Mob m, Entity target) {
        mobTarget.put(m,target);
    }
    public static Entity getMobTarget(Mob m) {
        return mobTarget.getOrDefault(m,null);
    }
}
