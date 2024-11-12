package org.NoiQing.AllayWar.PvzGame.Game;

import org.NoiQing.AllayWar.PvzGame.PVZAPI.PvzMap;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.itemFunction.ItemsFunction;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PvzRound {
    private static Villager brain = null;
    private static final Set<Mob> zombies = new HashSet<>();
    private static final List<BukkitRunnable> runnables = new ArrayList<>();
    private static int waveOffset = 0;                  //记录的runnables里显示的波数的偏差值
    private static String levelName = "";               //当前关卡名字
    private static boolean running = false;             //记录当前是否在进行关卡
    private static boolean waveEnd = true;              //记录关卡的波数是否已经完全完成
    private static int totalFlag = 0;               //记录一共有多少个旗帜
    private static int currentFlag = 0;                 //记录当前旗帜
    private static int totalSmallWaves = 0;             //记录总波数-由runnables产生
    private static int currentSmallWave = 0;
    private static int totalSun = 0;                    //记录玩家全体阳光数
    private static BossBar process = null;              //记录bossBar对象
    private static int totalTime = 0;                   //记录当前进度条总时间
    private static int currentTime = 0;                 //记录当前游戏进度
    private static boolean allowSunDrop = true;
    public static void initRound(Villager v, PvzMap.LevelData level) {
        brain = v;
        running = true;
        waveEnd = false;
        if(process == null) {
            process = Bukkit.createBossBar("§e§l关卡: " + level.getId(), BarColor.GREEN, BarStyle.SOLID);
        }
        totalTime = level.getWaveTimes().getLast();
        totalFlag = level.getWaveTimes().size();
        currentFlag = 0;
        currentTime = 0;
        currentSmallWave = 0;
        waveOffset = 0;
        levelName = level.getId();
        allowSunDrop = !levelName.startsWith("2");
        PvzEntity.resetPlayersMoney();
        process.setTitle("§e§l关卡: " + levelName + " §3§l旗帜 §7[ §b" + currentFlag + "§7/§b " + totalFlag + " §7]");
        process.setProgress(0);
        v.getWorld().setGameRule(GameRule.NATURAL_REGENERATION,false);
        for(Player p : brain.getWorld().getPlayers()) process.addPlayer(p);

        setTotalSun(50);
        for(Player p: v.getWorld().getPlayers()) {
            if(p.getGameMode().equals(GameMode.ADVENTURE)) {
                p.setInvulnerable(false);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,99999,2,true,false,false));
                ItemStack sword = new ItemStack(Material.IRON_SWORD);
                ItemsFunction.setUnbreakable(sword);
                p.getInventory().addItem(sword);
            }
        }
    }
    public static int getTotalTime() {return totalTime;}
    public static int getCurrentTime() {return currentTime;}
    public static void addCurrentTime() {if(currentTime < totalTime) currentTime++;}
    public static void addCurrentSmallWave() {currentSmallWave++;}
    public static int getCurrentSmallWave() {return currentSmallWave;}
    public static BossBar getProcessBar() {return process;}
    public static void addRunnable(BukkitRunnable runnable) {
        runnables.add(runnable);
    }
    public static void addSubRunnable(BukkitRunnable runnable) {
        runnables.add(runnable);
        waveOffset++;
    }
    public static void addZombieToRound(Mob zombie) {
        zombies.add(zombie);
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
    public static int getTotalSmallWaves() {
        return totalSmallWaves;
    }
    public static void setTotalSmallWaves(int waves) {
        totalSmallWaves = waves;
    }
    public static int getWaveOffset() { return waveOffset; }
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
        brain.getWorld().setGameRule(GameRule.NATURAL_REGENERATION,true);
        for(Player p: brain.getWorld().getPlayers()) {
            p.sendTitle("§a你的脑子保住了~","§b我们安全了，暂时...",10,70,20);
            p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE,1,1);
            p.removePotionEffect(PotionEffectType.SATURATION);
            process.removeAll();
        }
        for(LivingEntity entity : brain.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if(entity.getScoreboardTags().contains("pvz_plant"))
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.setHealth(0);
                    }
                }.runTaskLater(QinKitPVPS.getPlugin(),60);
        }
        brain.remove();
        for(BukkitRunnable r : runnables) {
            r.cancel();
        } runnables.clear();

        totalSun = 0;
    }

    public static void gameOver() {
        for(Player p: brain.getWorld().getPlayers()) {
            p.sendTitle("§c§l僵尸吃掉了你的脑子！","§cNOOOOOOOOOOOOOO",10,70,20);
            p.playSound(p, Sound.ENTITY_ENDERMAN_DEATH,1,0.1f);
            p.removePotionEffect(PotionEffectType.SATURATION);
            process.removeAll();
        }
        for(Mob m:zombies) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    m.setHealth(0);
                }
            }.runTaskLater(QinKitPVPS.getPlugin(),60);
        }
        for(LivingEntity entity : brain.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if(entity.getScoreboardTags().contains("pvz_plant"))
                entity.setHealth(0);
        }
        zombies.clear();
        brain.getWorld().setGameRule(GameRule.NATURAL_REGENERATION,true);
        brain = null;
        waveEnd = false;
        running = false;
        for(BukkitRunnable r : runnables) {
            r.cancel();
        } runnables.clear();
    }
    public static void addCurrentWave() {
        currentFlag++;
        process.setTitle("§e§l关卡: " + levelName + " §3§l旗帜 §7[ §b" + currentFlag + "§7/§b " + totalFlag + " §7]");
    }

    public static int getRemainZombies() {
        return zombies.size();
    }

    public static boolean allowDropSun() {
        return allowSunDrop;
    }
}
