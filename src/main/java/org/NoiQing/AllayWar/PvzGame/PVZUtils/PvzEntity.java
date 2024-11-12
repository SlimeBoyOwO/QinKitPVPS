package org.NoiQing.AllayWar.PvzGame.PVZUtils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.*;

public class PvzEntity {
    private static final Map<Entity, List<Display>> plantDisplays = new HashMap<>();
    private static final Map<Entity, List<Display>> effectDisplays = new HashMap<>();
    private static final Map<Entity, List<Display>> extraDisplays = new HashMap<>();
    private static final Map<Entity, Integer> plantAttackCD = new HashMap<>();
    private static final Map<Entity, Integer> effectDuration = new HashMap<>();
    private static final Map<Entity,Entity> bulletOwner = new HashMap<>();
    private static final Map<Entity,Map<Entity,Location>> entityLastLocation = new HashMap<>();
    private static final Map<Player, Integer> playerMoney = new HashMap<>();
    private static final Map<Mob,Entity> mobTarget = new HashMap<>();
    private static final Map<Player, Map<String, Long>> plantCoolDowns = new HashMap<>();
    private static final Map<Entity, Boolean> zombieSlowAttack = new HashMap<>();
    private static final Map<Entity, Double> zombieExtraHealth = new HashMap<>();
    private static final Map<Entity, Double> zombieMaxExtraHealth = new HashMap<>();

    /* 玩 家 冷 却 时 间 记 录 */
    public static boolean ifPlayerPlantPassCoolDownTime(Player player, String plant){
        plantCoolDownInitialization(player);
        long currentTime = System.currentTimeMillis();
        if(plantCoolDowns.get(player).get(plant) == null){
            return true;
        }else if(System.currentTimeMillis() < plantCoolDowns.get(player).get(plant)){
            int coolDownTimeLeft = (int) (plantCoolDowns.get(player).get(plant) - currentTime) /1000 + 1;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§3§l冷却剩余 §b"+ coolDownTimeLeft + " §3§l秒"));
            return false;
        }
        return true;
    }
    public static void setPlayerPlantCoolDownTime(Player player, String plant, double coolDownTime, Material type){
        long passCoolDownTime = (long) (System.currentTimeMillis() + coolDownTime* 1000L);

        plantCoolDownInitialization(player);
        plantCoolDowns.get(player).put(plant, passCoolDownTime);
        player.setCooldown(type, (int) coolDownTime * 20);
    }
    public static void clearPlayerSkillCoolDownTime(Player player){
        plantCoolDowns.remove(player);
    }
    public static void plantCoolDownInitialization(Player player){
        if (!plantCoolDowns.containsKey(player)) {
            plantCoolDowns.put(player, new HashMap<>());
        }
    }

    public static List<Display> getPlantDisplays(Entity plant){
        plantDisplays.computeIfAbsent(plant, k -> new ArrayList<>());
        return plantDisplays.getOrDefault(plant, null);
    }
    public static void removePlantDisplays(Entity plant) {
        plantDisplays.remove(plant);
    }
    public static List<Display> getEffectDisplays(Entity plant){
        effectDisplays.computeIfAbsent(plant, k -> new ArrayList<>());
        return effectDisplays.getOrDefault(plant, null);
    }
    public static void removeEffectDisplays(Entity plant) {
        effectDisplays.remove(plant);
    }
    public static List<Display> getExtraDisplays(Entity plant) {
        extraDisplays.computeIfAbsent(plant, k -> new ArrayList<>());
        return extraDisplays.getOrDefault(plant, null);
    }
    public static void removeExtraDisplays(Entity plant) {
        extraDisplays.remove(plant);
    }
    public static void setPlantAttackCD(Entity a, int cd){
        plantAttackCD.put(a, cd);
    }
    public static int getPlantAttackCD(Entity a) {
        return plantAttackCD.getOrDefault(a,-1);
    }
    public static void setEffectDuration(Entity a, int cd){
        effectDuration.put(a, cd);
    }
    public static int getEffectDuration(Entity a) {
        return effectDuration.getOrDefault(a,-1);
    }
    public static void setBulletOwner(Entity bullet, Entity owner) {
        bulletOwner.put(bullet,owner);
    }
    public static Entity getBulletOwner(Entity bullet) {
        return bulletOwner.getOrDefault(bullet,null);
    }
    public static void clearEntityLastLoc(Entity e) {
        entityLastLocation.remove(e);
    }
    public static void setEntityLastLocation(Entity e, Entity plant) {
        entityLastLocation.put(e,new HashMap<>());
        entityLastLocation.getOrDefault(e,new HashMap<>()).put(plant,e.getLocation());
    }
    public static Location getEntityLastLocation(Entity e, Entity plant) {
        return entityLastLocation.getOrDefault(e,new HashMap<>()).getOrDefault(plant,e.getLocation());
    }
    public static Integer getPlayerMoney(Player p) {
        return playerMoney.getOrDefault(p,0);
    }
    public static void setPlayerMoney(Player p, int money) {
        playerMoney.put(p,money);
    }
    public static void resetPlayersMoney() {playerMoney.clear();}
    public static Integer getAllPlayerSun() {
        int totalSun = 0;
        for(Map.Entry<Player,Integer> set : playerMoney.entrySet()) {
            totalSun += set.getValue();
        }
        return totalSun;
    }
    public static void addAllPlayerSun(int sun) {
        for(Map.Entry<Player,Integer> set : playerMoney.entrySet()) {
            set.setValue(getAllPlayerSun() + sun);
        }
    }

    public static void setMobTarget(Mob m, Entity target) {
        mobTarget.put(m,target);
    }
    public static Entity getMobTarget(Mob m) {
        return mobTarget.getOrDefault(m,null);
    }
    public static boolean isAttackFreeze(Entity zombie) {
        zombieSlowAttack.put(zombie,!zombieSlowAttack.getOrDefault(zombie,false));
        return zombieSlowAttack.get(zombie);
    }
    public static void setZombieExtraHealth(Entity zombie, double health) {
        zombieExtraHealth.put(zombie,health);
    }
    public static double getZombieExtraHealth(Entity zombie) {
        return zombieExtraHealth.getOrDefault(zombie,0d);
    }
    public static void setZombieMaxExtraHealth(Entity zombie, double health) {
        zombieMaxExtraHealth.put(zombie,health);
    }
    public static double getZombieMaxExtraHealth(Entity zombie) {
        return zombieMaxExtraHealth.getOrDefault(zombie,0d);
    }
}
