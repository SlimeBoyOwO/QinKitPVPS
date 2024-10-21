package org.NoiQing.AllayWar.PvzGame.Game;

import org.NoiQing.AllayWar.PvzGame.PVZAPI.PvzMap;
import org.NoiQing.itemFunction.ItemsFunction;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PvzRound {
    private static Villager brain = null;
    private static final Set<Mob> zombies = new HashSet<>();
    private static final List<BukkitRunnable> runnables = new ArrayList<>();
    private static boolean running = false;
    private static boolean waveEnd = true;
    private static int totalWaves = 0;
    private static int totalSun = 0;
    private static BossBar process = null;
    private static int totalTime = 0;
    private static int currentTime = 0;
    public static void initRound(Villager v, PvzMap.LevelData level) {
        brain = v;
        running = true;
        waveEnd = false;
        if(process == null) {
            process = Bukkit.createBossBar("§e§l关卡: " + level.getId(), BarColor.GREEN, BarStyle.SOLID);
        }
        totalTime = level.getTotalTime();
        currentTime = 0;
        process.setTitle("§e§l关卡: " + level.getId());
        process.setProgress(0);
        for(Player p : brain.getWorld().getPlayers()) process.addPlayer(p);

        setTotalSun(50);
        for(Player p: v.getWorld().getPlayers()) {
            if(p.getGameMode().equals(GameMode.ADVENTURE)) {
                p.setInvulnerable(false);
                ItemStack sword = new ItemStack(Material.IRON_SWORD);
                ItemsFunction.setUnbreakable(sword);
                p.getInventory().addItem(sword);
            }
        }
    }
    public static int getTotalTime() {return totalTime;}
    public static int getCurrentTime() {return currentTime;}
    public static void addCurrentTime() {if(currentTime < totalTime) currentTime++;}
    public static BossBar getProcessBar() {return process;}
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
    public static int getTotalSun() { return totalSun; }
    public static void setTotalSun(int sun) { totalSun = sun; }
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
            process.removeAll();
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
            process.removeAll();
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
