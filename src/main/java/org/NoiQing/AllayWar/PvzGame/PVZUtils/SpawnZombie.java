package org.NoiQing.AllayWar.PvzGame.PVZUtils;

import org.NoiQing.AllayWar.PvzGame.Game.PvzRound;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class SpawnZombie {
    public static <T extends Entity> T spawnZombie(Class<T> entityClass, Location loc) {
        T e = Objects.requireNonNull(loc.getWorld()).spawn(loc,entityClass);
        QinTeam zombieTeam = QinTeams.getQinTeamByName("僵尸");
        if(zombieTeam != null) {
            zombieTeam.addTeamEntities(e);
        }
        e.addScoreboardTag("pvz_zombie");
        Random random = new Random();
        if(e instanceof LivingEntity lv)
            Objects.requireNonNull(lv.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(random.nextDouble() * 0.005 + 0.157);
        if(e instanceof Mob m) PvzRound.addZombieToRound(m);
        return e;
    }

    public static void spawnEntityByType(Location location, String type) {
        World world = location.getWorld();
        if(world == null) return;
        switch (type) {
            case "普通僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
            }

            case "皮革僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.WOODEN_SWORD)
                        ,new ItemStack(Material.LEATHER_HELMET)
                        ,new ItemStack(Material.LEATHER_CHESTPLATE));
            }

            case "路障僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30 * 3);
                z.setAdult();
                z.addScoreboardTag("pvz_armed");
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                PVZFunction.summonPlant(z,"路障",0,false);
            }

            case "铁桶僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30 * 5);
                z.setAdult();
                z.addScoreboardTag("pvz_armed");
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                PVZFunction.summonPlant(z,"铁桶",0,false);
            }

            case "火把僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                z.addScoreboardTag("torchZombie");
                PVZFunction.summonPlant(z,"火把",0,false);
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.19);
            }

            case "僵尸精锐" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.IRON_SWORD)
                        ,new ItemStack(Material.IRON_HELMET)
                        ,new ItemStack(Material.IRON_CHESTPLATE));
            }

            case "小鬼僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,12);
                z.setBaby();
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.21);
            }

            case "撑杆跳僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,45);
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                PVZFunction.summonPlant(z,"撑杆跳僵尸",0,false);
                z.addScoreboardTag("PoleZombie");
                z.setAdult();
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.23);
            }

            case "钻石僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,40);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.DIAMOND_SWORD)
                        ,new ItemStack(Material.DIAMOND_HELMET)
                        ,new ItemStack(Material.DIAMOND_CHESTPLATE));
            }

            case "下界僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,50);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.NETHERITE_SWORD)
                        ,new ItemStack(Material.NETHERITE_HELMET)
                        ,new ItemStack(Material.NETHERITE_CHESTPLATE));
            }

            case "读报僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                z.addScoreboardTag("paperZombie");
                PvzEntity.setZombieExtraHealth(z,20);
                PvzEntity.setZombieMaxExtraHealth(z,20);
                PVZFunction.summonExtra(z,"报纸",0);
            }

            case "铁栅栏僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                z.addScoreboardTag("ironDoorZombie");
                PvzEntity.setZombieExtraHealth(z,30 * 4.07);
                PvzEntity.setZombieMaxExtraHealth(z,30 * 4.07);
                PVZFunction.summonExtra(z,"铁栅栏",0);
            }

            case "死神僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,50);
                z.setAdult();
                z.addScoreboardTag("graveZombie");
                PVZFunction.summonPlant(z,"死神狗头",0,false);
            }

            case "墓碑0" -> {
                Zombie z = spawnGrave(location);
                PVZFunction.summonPlant(z,"墓碑0",1.9f,false);
            }

            case "墓碑1" -> {
                Zombie z = spawnGrave(location);
                PVZFunction.summonPlant(z,"墓碑1",1.9f,false);
            }

            case "墓碑2" -> {
                Zombie z = spawnGrave(location);
                PVZFunction.summonPlant(z,"墓碑2",1.9f,false);
            }
        }
    }

    private static Zombie spawnGrave(Location location) {
        Zombie z = spawnZombie(Zombie.class,location);
        Function.setEntityHealth(z,300);
        z.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,9999999,5,true,false));
        z.setAdult();
        z.setAI(false);
        z.addScoreboardTag("pvz_grave");
        z.setSilent(true);
        z.setCollidable(false);
        Function.setMobEquipment(z,new ItemStack(Material.AIR));
        PvzRound.addZombieOffSet();
        return z;
    }
}
