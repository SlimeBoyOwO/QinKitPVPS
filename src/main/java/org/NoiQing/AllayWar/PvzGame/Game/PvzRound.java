package org.NoiQing.AllayWar.PvzGame.Game;

import org.NoiQing.AllayWar.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.itemFunction.ItemsFunction;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PvzRound {
    public static Villager brain = null;
    public static Set<Mob> zombies = new HashSet<>();
    public static List<BukkitRunnable> runnables = new ArrayList<>();
    public static boolean running = false;
    public static boolean waveEnd = true;
    public static int totalWaves = 0;
    public static void initRound(Villager v) {
        brain = v;
        running = true;
        waveEnd = false;
        for(Player p: v.getWorld().getPlayers()) {
            PvzEntity.setPlayerSun(p,200);
            if(p.getGameMode().equals(GameMode.ADVENTURE)) {
                p.setInvulnerable(false);
                ItemStack sword = new ItemStack(Material.IRON_SWORD);
                ItemsFunction.setUnbreakable(sword);
                p.getInventory().addItem(sword);
            }
        }
    }
    public static void addRunnable(BukkitRunnable runnable) {
        runnables.add(runnable);
    }
    public static void addZombieToRound(Mob zombie) {
        zombies.add(zombie);
        zombie.addScoreboardTag("pvz_zombie");
        zombie.setTarget(brain);
    }
    public static void removeZombieFromRound(Mob zombie) {
        zombies.remove(zombie);
    }
    public static Villager getBrain() {
        return brain;
    }
    public static Set<Mob> getZombies() {
        return zombies;
    }
    public static List<BukkitRunnable> getRunnables() {
        return runnables;
    }
    public static int getTotalWaves() {
        return totalWaves;
    }
    public static void setTotalWaves(int waves) {
        totalWaves = waves;
    }
    public static boolean isRunning() {
        return running;
    }
    public static boolean isWaveEnd() {
        return waveEnd;
    }

    public static void endWave() {
        waveEnd = true;
    }

    public static void endRound() {
        running = false;
        waveEnd = true;
        for(Player p: brain.getWorld().getPlayers()) {
            p.sendTitle("§a你的脑子保住了~","§b我们安全了，暂时...",10,70,20);
        }
        for(LivingEntity entity : brain.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if(entity.getScoreboardTags().contains("pvz_plant"))
                entity.setHealth(0);
        }
        brain.remove();
        for(BukkitRunnable r : runnables) {
            r.cancel();
        } runnables.clear();
    }

    public static void gameOver() {
        for(Player p: brain.getWorld().getPlayers()) {
            p.sendTitle("§c§l僵尸吃掉了你的脑子！","§cNOOOOOOOOOOOOOO",10,70,20);
        }
        for(Mob m:zombies) {
            m.remove();
        }
        for(LivingEntity entity : brain.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if(entity.getScoreboardTags().contains("pvz_plant"))
                entity.setHealth(0);
        }
        zombies.clear();
        brain = null;
        waveEnd = false;
        for(BukkitRunnable r : runnables) {
            r.cancel();
        } runnables.clear();
    }
}
