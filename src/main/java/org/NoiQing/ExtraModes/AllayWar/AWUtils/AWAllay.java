package org.NoiQing.ExtraModes.AllayWar.AWUtils;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AWAllay {
    private static final Map<LivingEntity, Integer> allayAttackCD = new HashMap<>();
    private static final Map<Entity, List<Display>> tankDisplays = new HashMap<>();
    private static final Map<Entity, Location> tankLastPos = new HashMap<>();
    private static final Map<Mob, LivingEntity> mobMove = new HashMap<>();
    private static final Map<LivingEntity,Integer> moveCounts = new HashMap<>();
    public static void setTankDisplays(Entity tank, List<Display> displays){
        tankDisplays.put(tank, displays);
    }
    public static List<Display> getTankDisplays(Entity tank){
        tankDisplays.computeIfAbsent(tank, k -> new ArrayList<>());
        return tankDisplays.getOrDefault(tank, null);
    }
    public static void removeTankDisplays(Entity tank) {
        tankDisplays.remove(tank);
    }
    public static void setAllayAttackCD(LivingEntity a, int cd){
        allayAttackCD.put(a, cd);
    }
    public static int getAllayAttackCD(LivingEntity a) {
        return allayAttackCD.getOrDefault(a,0);
    }

    public static void setTankLastPos(Entity e, Location l){
        tankLastPos.put(e, l);
    }
    public static Location getTankLastPos(Entity e) {
        return tankLastPos.getOrDefault(e, null);
    }
    public static void removeTankLastPos(Entity e) {
        tankLastPos.remove(e);
    }
    public static void setMobMove(Mob e, LivingEntity target){
        removeMobMove(e);
        mobMove.put(e, target);
        moveCounts.put(target, moveCounts.getOrDefault(target,0) + 1);
        e.setTarget(target);
    }
    public static LivingEntity getMobMove(Mob e) {
        return mobMove.getOrDefault(e, null);
    }
    public static void removeMobMove(Mob e) {
        LivingEntity target = mobMove.getOrDefault(e, null);
        e.removeScoreboardTag("attacked");
        if (target != null) {
            moveCounts.put(target, moveCounts.getOrDefault(target,0) - 1);
            if(moveCounts.getOrDefault(target,0) <= 0) target.remove();
        }
        e.getScoreboardTags().remove("mob_moving");
        mobMove.remove(e);
    }


}
