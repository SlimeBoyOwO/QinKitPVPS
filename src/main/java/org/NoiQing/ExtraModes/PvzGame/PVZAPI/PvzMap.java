package org.NoiQing.ExtraModes.PvzGame.PVZAPI;

import org.NoiQing.ExtraModes.PvzGame.PVZUtils.SpawnZombie;
import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PvzMap extends QinMap {
    // 怪物刷新区域（矩形），使用两个Location表示区域的对角点
    private Location monsterSpawnCorner1;
    private Location monsterSpawnCorner2;
    private Location plantCorner1;
    private Location plantCorner2;
    private Location graveCorner1;
    private Location graveCorner2;
    private final List<Location> plantLocations = new ArrayList<>();
    private final List<Location> graveLocations = new ArrayList<>();
    private Location villagerArea;
    // 关卡信息，使用一个 Map 存储各个关卡的 Wave 数据
    private final Map<String, LevelData> levels = new HashMap<>();

    public void setMonsterSpawnArea(Location corner1, Location corner2) {
        this.monsterSpawnCorner1 = corner1;
        this.monsterSpawnCorner2 = corner2;
    }
    public void setPlantCorner(Location corner1, Location corner2) {
        this.plantCorner1 = corner1;
        this.plantCorner2 = corner2;
        initPlantLocations();
    }
    public void setGraveCorner(Location corner1, Location corner2) {
        this.graveCorner1 = corner1;
        this.graveCorner2 = corner2;
        initGraveLocations();
    }

    public List<Location> getGraveLocations() {return graveLocations;}
    public List<Location> getPlantLocations() {return plantLocations;}

    private void initGraveLocations() {
        initLocations(graveCorner1,graveCorner2,graveLocations);
    }

    private void initPlantLocations() {
        initLocations(plantCorner1,plantCorner2,plantLocations);
    }

    private void initLocations(Location corner1, Location corner2, List<Location> locations) {
        double cornerX1 = corner1.getX();
        double cornerX2 = corner2.getX();
        double cornerZ1 = corner1.getZ();
        double cornerZ2 = corner2.getZ();
        World w = corner1.getWorld();
        double y = corner1.getY();

        for(int i = 1; i < Math.abs(cornerX1-cornerX2); i+=3) {
            for(int j = 1; j < Math.abs(cornerZ1-cornerZ2); j+=3) {
                locations.add(new Location(w,Math.min(cornerX1,cornerX2) + i,y,Math.min(cornerZ1,cornerZ2) + j));
            }
        }
    }


    public void showPlantLocations() {
        World particleWorld = plantLocations.get(0).getWorld();
        if(particleWorld == null) {
            Bukkit.broadcastMessage("世界不存在");
            return;
        }

        for(Location loc : plantLocations) {
            particleWorld.spawnParticle(Particle.END_ROD, loc.clone().add(0,1,0), 5, 0, 0, 0,0);
        }
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
        private final List<Integer> waveTimes;
        private final Map<Integer, WaveData> waves = new HashMap<>();
        private final String name;
        private final int graveAmount;

        public LevelData(String name,List<Integer> totalTime, int graveAmount) {
            this.name = name;
            this.waveTimes = totalTime;
            this.graveAmount = graveAmount;
        }

        public void addWaveData(int waveTime, WaveData waveData) {
            waves.put(waveTime, waveData);
        }

        public List<Integer> getWaveTimes() {
            return waveTimes;
        }
        public String getId() {return name;}
        public int getGraveAmount() {return graveAmount;}
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

    public void startLevel(String level, int difficultly) {
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
        villager.setCustomName("§5你的脑子");
        villager.setCustomNameVisible(true);

        //召唤坟墓
        Random random = new Random();

        // 选择x个不同的随机元素
        Set<Location> randomElements = new HashSet<>();
        while (randomElements.size() < levelData.graveAmount) {
            int index = random.nextInt(graveLocations.size());
            randomElements.add(graveLocations.get(index));
        }

        PvzRound.initRound(villager, levelData, this, difficultly);
        for(Location spawnGraveLoc : randomElements) {
            int chance = Function.createRandom(0,3);
            String graveName = "墓碑" + chance;
            SpawnZombie.spawnEntityByType(spawnGraveLoc.clone().add(0.5,1,0.5),graveName);
        }

        int lastWaveTime = 0;
        for(Map.Entry<Integer,WaveData> wave : levelData.getWaves().entrySet()) {

            if (levelData.getWaveTimes().contains(wave.getKey())) {
                BukkitRunnable waveRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : world.getPlayers()) {
                            player.sendTitle("§c§l一大波僵尸即将来袭！", "", 10, 120, 20); // 显示标题
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Player player : world.getPlayers()) {
                                    player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_2,1,1); // 发出声音
                                }
                            }
                        }.runTaskLater(QinKitPVPS.getPlugin(),150);
                    }
                };
                waveRunnable.runTaskLater(QinKitPVPS.getPlugin(),wave.getKey() - 150);
                PvzRound.addSubRunnable(waveRunnable);
            }

            int finalLastWaveTime = lastWaveTime;
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    WaveData waveData = wave.getValue();
                    if (wave.getKey() == firstWaveKey) {
                        for (Player player : world.getPlayers()) {
                            player.sendTitle("§c§lI'm zombie, I'm coming~", "", 10, 70, 20); // 显示标题
                            player.playSound(player,Sound.ENTITY_ZOMBIE_AMBIENT,1,1);
                        }
                    } else if (wave.getKey() == lastWaveKey) {
                        PvzRound.endWave();
                        for (Player player : world.getPlayers()) {
                            player.sendTitle("§c§l最后一波！", "", 10, 70, 20); // 显示标题
                        }
                    }
                    if (levelData.getWaveTimes().contains(wave.getKey()))
                        PvzRound.addCurrentWave();
                    if(wave.getKey() - finalLastWaveTime > 25) {
                        PvzRound.addCurrentSmallWave();
                    }

                    int spawnAmount = waveData.amount;
                    if(difficultly == 0) spawnAmount = spawnAmount / 2 + 1;
                    else if(difficultly == 2) spawnAmount = (int) (spawnAmount * 1.5);
                    else if(difficultly == 3) spawnAmount = (int) (spawnAmount * 2);

                    for(int i = 0; i < spawnAmount; i++) {
                        Location randomLocation = generateRandomLocation();
                        SpawnZombie.spawnEntityByType(randomLocation,waveData.type);
                    }

                    PvzRound.getRunnables().remove(this);

                }
            };
            runnable.runTaskLater(QinKitPVPS.getPlugin(),wave.getKey());
            if(wave.getKey() - lastWaveTime <= 25) {
                PvzRound.addSubRunnable(runnable);
            } else PvzRound.addRunnable(runnable);
            lastWaveTime = wave.getKey();
        }

        PvzRound.setTotalSmallWaves(PvzRound.getRunnables().size() - PvzRound.getWaveOffset());
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
