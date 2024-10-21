package org.NoiQing.AllayWar.PvzGame.PVZAPI;

import org.NoiQing.AllayWar.PvzGame.Game.PvzRound;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.util.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PvzMap extends QinMap {
    // 怪物刷新区域（矩形），使用两个Location表示区域的对角点
    private Location monsterSpawnCorner1;
    private Location monsterSpawnCorner2;
    private Location villagerArea;
    // 关卡信息，使用一个 Map 存储各个关卡的 Wave 数据
    private final Map<String, LevelData> levels = new HashMap<>();

    public void setMonsterSpawnArea(Location corner1, Location corner2) {
        this.monsterSpawnCorner1 = corner1;
        this.monsterSpawnCorner2 = corner2;
    }
    public Location getVillagerArea() {
        return villagerArea;
    }
    public void setVillagerArea(Location villagerArea) {
        this.villagerArea = villagerArea;
    }

    // 获取怪物刷新区域的第一个对角点
    public Location getMonsterSpawnCorner1() {
        return monsterSpawnCorner1;
    }
    // 获取怪物刷新区域的第二个对角点
    public Location getMonsterSpawnCorner2() {
        return monsterSpawnCorner2;
    }
    // 添加关卡数据
    public void addLevelData(String levelName, LevelData levelData) {
        this.levels.put(levelName, levelData);
    }
    // 获取关卡数据
    public Map<String, LevelData> getLevels() {
        return levels;
    }
    // 关卡数据类
    public static class LevelData {
        private final int totalTime;
        private final Map<Integer, WaveData> waves = new HashMap<>();
        private final String name;

        public LevelData(String name,int totalTime) {
            this.name = name;
            this.totalTime = totalTime;
        }

        public void addWaveData(int waveTime, WaveData waveData) {
            waves.put(waveTime, waveData);
        }

        public int getTotalTime() {
            return totalTime;
        }
        public String getId() {return name;}

        public Map<Integer, WaveData> getWaves() {
            return waves;
        }
    }

    // 波次数据类
    public static class WaveData {
        private String type;
        private int amount;

        public WaveData(String type, int amount) {
            this.type = type;
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }
    }

    public void startLevel(String level) {
        LevelData levelData = levels.get(level);
        int lastWaveKey = Collections.max(levelData.getWaves().keySet());
        int firstWaveKey = Collections.min(levelData.getWaves().keySet());
        World world = this.getMonsterSpawnCorner1().getWorld();

        if(world==null) return;
        Villager villager = world.spawn(villagerArea, Villager.class);
        villager.setAI(false);
        villager.setHealth(1);
        villager.addScoreboardTag("pvz_brain");
        villager.setGlowing(true);
        PvzRound.initRound(villager, levelData);

        for(Map.Entry<Integer,WaveData> wave : levelData.getWaves().entrySet()) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    WaveData waveData = wave.getValue();
                    if (wave.getKey() == firstWaveKey) {
                        for (Player player : world.getPlayers()) {
                            player.sendTitle("§c§lI'm zombie, I'm coming~", "", 10, 70, 20); // 显示标题
                        }
                    } else if (wave.getKey() == lastWaveKey) {
                        PvzRound.endWave();
                        for (Player player : world.getPlayers()) {
                            player.sendTitle("§c§l最后一波！", "", 10, 70, 20); // 显示标题
                        }
                    }

                    for(int i = 0; i < waveData.amount; i++) {
                        Location randomLocation = generateRandomLocation();
                        spawnEntityByType(randomLocation,waveData.type);
                    }

                    PvzRound.getRunnables().remove(this);

                }
            };
            runnable.runTaskLater(QinKitPVPS.getPlugin(),wave.getKey());
            PvzRound.addRunnable(runnable);
        }
        PvzRound.setTotalWaves(PvzRound.getRunnables().size());
    }

    private void spawnEntityByType(Location randomLocation, String type) {
        World world = randomLocation.getWorld();
        if(world == null) return;
        switch (type) {
            case "普通僵尸" -> {
                Zombie z = world.spawn(randomLocation,Zombie.class);
                Function.setEntityHealth(z,30);
                z.setAdult();
                PvzRound.addZombieToRound(z);
            }

            case "皮革僵尸" -> {
                Zombie z = world.spawn(randomLocation,Zombie.class);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.WOODEN_SWORD)
                        ,new ItemStack(Material.LEATHER_HELMET)
                        ,new ItemStack(Material.LEATHER_CHESTPLATE)
                        ,new ItemStack(Material.LEATHER_LEGGINGS)
                        ,new ItemStack(Material.LEATHER_BOOTS));
                PvzRound.addZombieToRound(z);
            }
        }
    }

    private Location generateRandomLocation() {
        // 获取 X, Y, Z 范围的最小和最大值
        double minX = Math.min(monsterSpawnCorner1.getX(), monsterSpawnCorner2.getX());
        double maxX = Math.max(monsterSpawnCorner1.getX(), monsterSpawnCorner2.getX());

        double minY = Math.min(monsterSpawnCorner1.getY(), monsterSpawnCorner2.getY());
        double maxY = Math.max(monsterSpawnCorner1.getY(), monsterSpawnCorner2.getY());

        double minZ = Math.min(monsterSpawnCorner1.getZ(), monsterSpawnCorner2.getZ());
        double maxZ = Math.max(monsterSpawnCorner1.getZ(), monsterSpawnCorner2.getZ());

        // 生成随机的 X, Y, Z 坐标
        double randomX = minX + (Math.random() * (maxX - minX));
        double randomY = minY + (Math.random() * (maxY - minY));
        double randomZ = minZ + (Math.random() * (maxZ - minZ));

        // 创建并返回随机位置
        return new Location(monsterSpawnCorner1.getWorld(), randomX, randomY, randomZ);
    }
}
