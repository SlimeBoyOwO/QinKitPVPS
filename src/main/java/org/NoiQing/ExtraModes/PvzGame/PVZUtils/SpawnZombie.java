package org.NoiQing.ExtraModes.PvzGame.PVZUtils;

import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class SpawnZombie {
    public static Mob spawnEntityByType(Location location, String type) {
        World world = location.getWorld();
        if(world == null) return null;
        switch (type) {
            case "普通僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                return z;
            }

            case "皮革僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.WOODEN_SWORD)
                        ,new ItemStack(Material.LEATHER_HELMET)
                        ,new ItemStack(Material.LEATHER_CHESTPLATE));
                return z;
            }

            case "路障僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30 * 3);
                z.setAdult();
                z.addScoreboardTag("pvz_armed");
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                PVZFunction.summonPlant(z,"路障",0,false);
                return z;
            }

            case "铁桶僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30 * 5);
                z.setAdult();
                z.addScoreboardTag("pvz_armed");
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                PVZFunction.summonPlant(z,"铁桶",0,false);
                return z;
            }

            case "火把僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                z.addScoreboardTag("torchZombie");
                PVZFunction.summonPlant(z,"火把",0,false);
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.19);
                return z;
            }

            case "僵尸精锐" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.IRON_SWORD)
                        ,new ItemStack(Material.IRON_HELMET)
                        ,new ItemStack(Material.IRON_CHESTPLATE));
                return z;
            }

            case "小鬼僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,12);
                z.setBaby();
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.21);
                return z;
            }

            case "撑杆跳僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,45);
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                PVZFunction.summonPlant(z,"撑杆跳僵尸",0,false);
                z.addScoreboardTag("PoleZombie");
                z.setAdult();
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.23);
                return z;
            }

            case "钻石僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,40);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.DIAMOND_SWORD)
                        ,new ItemStack(Material.DIAMOND_HELMET)
                        ,new ItemStack(Material.DIAMOND_CHESTPLATE));
                return z;
            }

            case "下界僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,50);
                z.setAdult();
                Function.setMobEquipment(z,new ItemStack(Material.NETHERITE_SWORD)
                        ,new ItemStack(Material.NETHERITE_HELMET)
                        ,new ItemStack(Material.NETHERITE_CHESTPLATE));
                return z;
            }

            case "读报僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                z.addScoreboardTag("paperZombie");
                PvzEntity.setZombieExtraHealth(z,20);
                PvzEntity.setZombieMaxExtraHealth(z,20);
                PVZFunction.summonExtra(z,"报纸",0);
                return z;
            }

            case "铁栅栏僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30);
                z.setAdult();
                z.addScoreboardTag("ironDoorZombie");
                PvzEntity.setZombieExtraHealth(z,30 * 4.07);
                PvzEntity.setZombieMaxExtraHealth(z,30 * 4.07);
                PVZFunction.summonExtra(z,"铁栅栏",0);
                return z;
            }

            case "死神僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,50);
                z.setAdult();
                z.addScoreboardTag("graveZombie");
                PvzEntity.setPlantAttackCD(z,-10 * 20);
                PVZFunction.summonPlant(z,"死神狗头",0,false);
                return z;
            }

            case "骷髅亡灵" -> {
                Skeleton z = spawnZombie(Skeleton.class,location);
                Function.setEntityHealth(z,60);
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.165);
                PvzEntity.setPlantAttackCD(z,-10 * 20);
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                return z;
            }

            case "亡灵法师" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,200);
                z.setAdult();
                z.addScoreboardTag("summonZombie");
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(1.1);
                PVZFunction.summonPlant(z,"亡灵法杖",0,false);
                return z;
            }

            case "橄榄球僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,30 * 5);
                z.setAdult();
                z.addScoreboardTag("pvz_armed");
                z.addScoreboardTag("rugbyZombie");
                ItemStack cloth = new ItemStack(Material.LEATHER_CHESTPLATE);
                Function.dyeCloth(cloth, Color.RED);
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.24);
                PVZFunction.summonPlant(z,"橄榄球僵尸头盔",0,false);
                Function.setMobEquipment(z,new ItemStack(Material.AIR),new ItemStack(Material.AIR),cloth);
                return z;
            }

            case "迪斯扣僵尸" -> {
                Zombie z = spawnZombie(Zombie.class,location);
                Function.setEntityHealth(z,50);
                z.setAdult();
                z.addScoreboardTag("discordZombie");
                Function.setMobEquipment(z,new ItemStack(Material.AIR));
                return z;
            }

            case "墓碑0" -> {
                Zombie z = spawnGrave(location);
                PVZFunction.summonPlant(z,"墓碑0",1.9f,false);
                return z;
            }

            case "墓碑1" -> {
                Zombie z = spawnGrave(location);
                PVZFunction.summonPlant(z,"墓碑1",1.9f,false);
                return z;
            }

            case "墓碑2" -> {
                Zombie z = spawnGrave(location);
                PVZFunction.summonPlant(z,"墓碑2",1.9f,false);
                return z;
            }
        }
        return null;
    }

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
    private static Zombie spawnGrave(Location location) {
        Zombie z = spawnZombie(Zombie.class,location);
        Function.setEntityHealth(z,150);
        z.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,9999999,5,true,false));
        z.setAdult();
        z.setAI(false);
        z.addScoreboardTag("pvz_grave");
        z.setSilent(true);
        Function.setMobEquipment(z,new ItemStack(Material.AIR));
        PvzRound.addZombieOffSet();
        return z;
    }
}
