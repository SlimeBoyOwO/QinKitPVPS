package org.NoiQing.AllayWar.PvzGame.PvzRunnable;

//import com.github.davidmoten.rtree.Entry;
//import com.github.davidmoten.rtree.RTree;
//import com.github.davidmoten.rtree.geometry.Geometries;
//import com.github.davidmoten.rtree.geometry.Point;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.Entity2DTree;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PVZFunction;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.EventListener.System.WallJumpListener;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;


public class PvzGameRunnable extends BukkitRunnable {
    private static final Logger log = LoggerFactory.getLogger(PvzGameRunnable.class);
    private int pause = 0;
    Entity2DTree EnemyTree = new Entity2DTree();
    int updateInterval = 5;

//    private RTree<Entity, Point> rTree = RTree.create();
//    private ConcurrentHashMap<UUID, Long> lastUpdated = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<UUID, Location> lastLocations = new ConcurrentHashMap<>();

//    public void onZombieDie(EntityDeathEvent event) {
//        Entity e = event.getEntity();
//        if(e instanceof Zombie z && !e.getScoreboardTags().contains("pvz_plant")) {
//            removeZombie(z);
//        }
//    }

//    private RTree<Entity, Point> rTree = RTree.create();
//    private ConcurrentHashMap<UUID, Long> lastUpdated = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<UUID, Location> lastLocations = new ConcurrentHashMap<>();

//    public void onZombieDie(EntityDeathEvent event) {
//        Entity e = event.getEntity();
//        if(e instanceof Zombie z && !e.getScoreboardTags().contains("pvz_plant")) {
//            removeZombie(z);
//        }
//    }

    @Override
    public void run() {
        pause++;
        List<Entity> ZombieList = new ArrayList<>();
        int updateInterval = 5;
        for(World w : Bukkit.getWorlds()) {
            for(Entity e: w.getEntities()) {
//                if(e instanceof Zombie && !e.getScoreboardTags().contains("pvz_plant")) {
//                    UUID entityId = e.getUniqueId();
//                    Long lastUpdate = lastUpdated.get(entityId);
//                    long currentTime = System.currentTimeMillis();
//                    if (lastUpdate == null || (currentTime - lastUpdate) > 500) {
//                        lastUpdated.put(entityId, currentTime);
//                        updateZombieInRTree((Zombie) e);
//                    }
//                }
                //以下代码针对僵尸逻辑
                if(pause % updateInterval == 0) { //每5tick更新一次
                    if (e instanceof Zombie && !e.getScoreboardTags().contains("pvz_plant")) {
                        ZombieList.add(e);
                        continue;
                    }
                }

                if(!e.getScoreboardTags().contains("pvz_plant")) continue;

                //以下代码针对植物逻辑
                if(PvzEntity.getPlantDisplays(e) == null) continue;
                for(Display d : PvzEntity.getPlantDisplays(e)) {
                    d.setRotation(e.getLocation().getYaw() + 90,0);
                }

                //以下代码针对子弹逻辑
                if(PVZFunction.isBullet(e)) {
                    if(e.hasGravity()) {
                        Vector vec = PvzEntity.getBulletVector(e);
                        e.setVelocity(new Vector(vec.getX(),e.getVelocity().getY(),vec.getZ()));
                    } else e.setVelocity(PvzEntity.getBulletVector(e));

                    LivingEntity hitEntity = findHitEntity(e,e.hasGravity() ? 0.6 : 0.3);
                    Entity shooter = PvzEntity.getBulletOwner(e);
                    if(WallJumpListener.isTouchingWallHere(e) || hitEntity != null) {
                        PvzEntity.getPlantDisplays(e).forEach(Entity::remove);
                        PvzEntity.removePlantDisplays(e);
                        e.remove();
                    }

                    if(hitEntity == null) continue;
                    if (hitEntity.getScoreboardTags().contains("pvz_plant")) continue;

                    if(e.getScoreboardTags().contains("pea_bullet")) {
                        hitEntity.damage(4, Objects.requireNonNullElse(shooter, e));
                        e.getWorld().spawnParticle(Particle.ITEM_SLIME,e.getLocation(),10,0,0,0,0,null,true);
                    } else if(e.getScoreboardTags().contains("ice_pea_bullet")) {
                        hitEntity.damage(4, Objects.requireNonNullElse(shooter, e));
                        e.getWorld().spawnParticle(Particle.ITEM_SNOWBALL,e.getLocation(),10,0,0,0,0,null,true);
                        hitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,10*20,1,true,false));
                    } else if(e.getScoreboardTags().contains("cabbage_bullet")) {
                        hitEntity.damage(8, Objects.requireNonNullElse(shooter, e));
                        e.getWorld().spawnParticle(Particle.ITEM_SLIME,e.getLocation(),10,0,0,0,0,null,true);
                        hitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,10*20,1,true,false));
                    }
                }

                //豌豆射手逻辑
                if(e instanceof Mob mob) {
                    LivingEntity target = mob.getTarget();

                    if (target != null && (target.isDead())) {
                        mob.setTarget(null);
                        target = null;
                    }

                    //如果设定了pause 则会导致每次getTarget都是null
                    if(pause == 20) {
                        LivingEntity newTarget = findNearestEnemy(mob,30);
                        if(newTarget != null && !newTarget.equals(target)) {
                            mob.setTarget(newTarget);
                            target = newTarget;
                        }
                    }

                    if(target == null) continue;
                    mob.setRotation(Function.calculateYaw(mob.getLocation(),target.getLocation()),0);
                    if(mob.getScoreboardTags().contains("peaShooter")) {
                        PvzEntity.setPlantAttackCD(mob,PvzEntity.getPlantAttackCD(mob) + 1);
                        if(!Function.isShootAble(mob,target))
                            continue;
                        if(PvzEntity.getPlantAttackCD(mob) >= 28) {
                            shootPea(mob,target);
                            PvzEntity.setPlantAttackCD(mob,0);
                        }
                    } else if(mob.getScoreboardTags().contains("icePeaShooter")) {
                        PvzEntity.setPlantAttackCD(mob,PvzEntity.getPlantAttackCD(mob) + 1);
                        if(!Function.isShootAble(mob,target))
                            continue;
                        if(PvzEntity.getPlantAttackCD(mob) >= 28) {
                            shootIcePea(mob,target);
                            PvzEntity.setPlantAttackCD(mob,0);
                        }
                    } else if(mob.getScoreboardTags().contains("doublePeaShooter")) {
                        PvzEntity.setPlantAttackCD(mob,PvzEntity.getPlantAttackCD(mob) + 1);
                        if(!Function.isShootAble(mob,target))
                            continue;
                        if(PvzEntity.getPlantAttackCD(mob) == 5) shootPea(mob,target);
                        if(PvzEntity.getPlantAttackCD(mob) >= 28) {
                            shootPea(mob,target);
                            PvzEntity.setPlantAttackCD(mob,0);
                        }
                    } else if(mob.getScoreboardTags().contains("cabbagePitcher")) {
                        PvzEntity.setPlantAttackCD(mob,PvzEntity.getPlantAttackCD(mob) + 1);
                        if(PvzEntity.getPlantAttackCD(mob) == 54) {
                            PvzEntity.setEntityLastLoc(target,target.getLocation());
                        }
                        if(PvzEntity.getPlantAttackCD(mob) >= 56) {
                            shootCabbage(mob,target);
                            PvzEntity.setPlantAttackCD(mob,0);
                        }
                    }
                }
            }
        }

        if(pause % updateInterval == 0){
            EnemyTree.clear();
            EnemyTree.addEntities(ZombieList);
        }

        if(pause == 20) {
            pause = 0;
            // 根据ZombieList的大小动态决定kd树的更新频率
            if(ZombieList.size() > 400) {
                updateInterval = 20;
            } else if(ZombieList.size() > 200) {
                updateInterval = 10;
            } else if(ZombieList.size() > 80) {
                updateInterval = 4;
            } else {
                updateInterval = 2;
            }
        }
    }

//    private LivingEntity findEnemyViaRay(Mob mob, int distance) {
//        Predicate<Entity> predicate = x -> !isEnemy(mob,x);
//        Location start = mob.getLocation().clone().add(0,1,0);
//        // 创建一条射线
//        RayTraceResult result = mob.getWorld().rayTrace(
//                start, // 起始点
//                mob.getLocation().getDirection(),               // 方向向量
//                distance,                     // 最大距离
//                FluidCollisionMode.NEVER, // 流体模式
//                true,                    // 忽略非可视方块
//                0.1,                     // 检测范围（宽度）
//                predicate              // 过滤器
//        );
//
//        if (result != null) {
//            return (LivingEntity) result.getHitEntity();
//        }
//        return null;
//    }

//    private void updateZombieInRTree(Zombie zombie) {
//        Location lastLocation = lastLocations.get(zombie.getUniqueId());
//        Location location = zombie.getLocation();;
//        if(lastLocation == location){
//            return;
//        }
//
//        Point point = Geometries.point(location.getX(), location.getZ());
//        if(lastLocation != null) {
//            Point old_point = Geometries.point(lastLocation.getX(), lastLocation.getZ());
//            rTree = rTree.delete(zombie, old_point);
//        }
//
//        // 插入新的实体位置
//        rTree = rTree.add(zombie, point);
//        lastLocations.put(zombie.getUniqueId(), location);
//    }
//
//    public void removeZombie(Zombie zombie) {
////        Bukkit.broadcastMessage("Size: " + rTree.size());
////        Bukkit.broadcastMessage("Trying to remove zombie from rTree");
//        UUID entityId = zombie.getUniqueId();
//        Location location = lastLocations.get(entityId);
//        Point point = Geometries.point(location.getX(), location.getZ());
//
//        rTree = rTree.delete(zombie, point);
////        Bukkit.broadcastMessage("Removed zombie from rTree");
////        Bukkit.broadcastMessage("NEW Size: " + rTree.size());
//        lastUpdated.remove(entityId);
//    }
//
//    public Zombie findNearestZombie(Location location,double maxDistance) {
//        if(!rTree.isEmpty()){
//            Point point = Geometries.point(location.getX(), location.getZ());
//            Entry<Entity, Point> nearest = rTree.nearest(point, maxDistance, 1).toBlocking().single();
//
//            if(nearest != null) {
//                return (Zombie) nearest.value();
//            }
//        }
//        return null;
//    }

    private boolean isEnemy(Entity e, Entity entity) {
        QinTeam mobTeam = QinTeams.getEntityTeam(e);
        QinTeam entityTeam = QinTeams.getEntityTeam(entity);
        //无视生物移动标记
        if(entity.getScoreboardTags().contains("move_tag")) return false;
        if(PVZFunction.isBullet(entity)) return false;
        if(entity instanceof Display) return false;
        //无视无敌的玩家
        if (entity instanceof Player p)
            if(p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE
                    || p.isInvulnerable() || Objects.equals(entityTeam, mobTeam)) return false;

        if (mobTeam != null && mobTeam.getTeamName().equals("植物") && entity instanceof Player) return false;

        //如果是一些特殊的实体，无视
        if (ignoreSomeEntities(mobTeam, entity, entityTeam, entity.equals(e), entity.isDead()))
            return false;

        if(!(entity instanceof LivingEntity)) return false;
        return !(entity instanceof Villager);
    }

    private void shootCabbage(Mob mob, LivingEntity target) {
        Entity cabbage = generateBullet(mob,target,false);
        cabbage.addScoreboardTag("cabbage_bullet");
        PVZFunction.summonPlant(cabbage,"卷心菜",0.4f,true);
    }

    private void shootPea(Mob mob, LivingEntity target) {
        Entity pea = generateBullet(mob,target,true);
        pea.addScoreboardTag("pea_bullet");
        PVZFunction.summonPlant(pea,"豌豆子弹",0.4f,true);
    }
    private void shootIcePea(Mob mob, LivingEntity target) {
        Entity pea = generateBullet(mob,target,true);
        pea.addScoreboardTag("ice_pea_bullet");
        PVZFunction.summonPlant(pea,"寒冰豌豆子弹",0.4f,true);
    }

    private Entity generateBullet(Mob mob, LivingEntity target, Boolean isPea) {
        Location fireLocation = mob.getLocation().clone().add(0, 1.25, 0);
        Location targetLocation = target.getBoundingBox().getCenter().toLocation(target.getWorld());
        Vector direction = targetLocation.toVector().subtract(fireLocation.toVector()).normalize();
        QinTeam mobTeam = QinTeams.getEntityTeam(mob);
        Silverfish pea = mob.getWorld().spawn(fireLocation, Silverfish.class);
        Objects.requireNonNull(pea.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(0.2);
        pea.addScoreboardTag("plant_damage");
        pea.addScoreboardTag("plant_bullet");
        pea.setHealth(1);
        if(mobTeam != null) mobTeam.addTeamEntities(pea);
        PvzEntity.setBulletOwner(pea,mob);
        pea.addScoreboardTag("pvz_plant");
        pea.setSilent(true);
        pea.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,999,5,true,false));
        QinTeam allayTeam = QinTeams.getEntityTeam(mob);
        if(allayTeam != null) allayTeam.addTeamEntities();
        Objects.requireNonNull(fireLocation.getWorld()).playSound(fireLocation, Sound.ENTITY_SNOWBALL_THROW,1,1);

        if(isPea) {
            pea.setGravity(false);
            pea.setVelocity(direction.multiply(1.1));
            PvzEntity.setBulletVector(pea,direction.multiply(1.1));
        } else {
            pea.setGravity(true);
            Vector vec = Function.calculateVelocity(fireLocation.toVector(),calculateFutureLocation(target,20).toVector(),6,0.16);
            pea.setVelocity(vec);
            PvzEntity.setBulletVector(pea,vec);
        }

        return pea;
    }

    private LivingEntity findHitEntity(Entity bullet, double findDistance) {
        QinTeam plantTeam = QinTeams.getEntityTeam(PvzEntity.getBulletOwner(bullet));
        //一般情况不可能没有队伍
        if(plantTeam == null) return null;
        LivingEntity hitEntity;

        Predicate<Entity> predicate = x -> isEnemy(bullet, x) && !bullet.equals(x);
        // 计算射线方向的角度

        double angle = Math.atan2(PvzEntity.getBulletVector(bullet).getZ(), PvzEntity.getBulletVector(bullet).getX());

        // 基于角度计算射线的目标方向
        Vector direction = new Vector(Math.cos(angle), 0, Math.sin(angle));

        // 发射射线
        RayTraceResult result = bullet.getWorld().rayTrace(
                bullet.getLocation(), // 起始点
                direction,               // 方向向量
                findDistance,                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                0.3,                     // 检测范围（宽度）
                predicate              // 过滤器
        );

        // 检查是否有实体被射线命中
        if (result != null && result.getHitEntity() != null) {
            hitEntity = (LivingEntity) result.getHitEntity();
            return hitEntity;
        }

        return null;
    }

    private LivingEntity findNearestEnemy(Entity mob,double maxDistance) {
        LivingEntity foundEntity =  (LivingEntity) EnemyTree.findNearest(mob);
        if(foundEntity != null) {
            if(mob.getLocation().distanceSquared(foundEntity.getLocation()) > maxDistance * maxDistance) {
                foundEntity = null;
            }
        }
        return foundEntity;
    }

//    private LivingEntity findNearestEnemy(Entity mob,double maxDistance) {
//        LivingEntity foundEntity =  (LivingEntity) findNearestZombie(mob.getLocation(), maxDistance);
////        if(foundEntity != null) {
////            if(mob.getLocation().distanceSquared(foundEntity.getLocation()) > maxDistance * maxDistance) {
////                foundEntity = null;
////            }
////        }
//        return foundEntity;
//    }

    private boolean ignoreSomeEntities(QinTeam allayTeam, Entity entity, QinTeam entityTeam, boolean equals, boolean dead) {
        //是自己的话，排除
        if(equals) return true;
        //是中立油井的话，排除
        if(entityTeam == null && entity.getCustomName() != null && entity.getCustomName().contains("油井")) return true;
        //是盔甲架排除
        if(entity instanceof ArmorStand) return true;
        //如果两个队伍相同，排除
        if(allayTeam != null && Objects.equals(entityTeam, allayTeam))
            return true;
        //死了的话，排除
        return dead;
    }

    private Location calculateFutureLocation(Entity target, int ticks) {
        ticks = ticks / 2;
        Location previousLocation = PvzEntity.getEntityLastLoc(target);
        PvzEntity.clearEntityLastLoc(target);
        // 获取实体的当前位置
        Location currentLocation = target.getLocation();

        // 计算实体每tick的移动距离
        double deltaX = currentLocation.getX() - previousLocation.getX();
        double deltaZ = currentLocation.getZ() - previousLocation.getZ();

        // 预测未来位置，基于当前移动的速度和给定的ticks数
        double futureX = currentLocation.getX() + (deltaX * ticks);
        double futureY = currentLocation.getY();
        double futureZ = currentLocation.getZ() + (deltaZ * ticks);

        // 创建新的Location对象预测未来位置
        return new Location(currentLocation.getWorld(), futureX, futureY, futureZ, currentLocation.getYaw(), currentLocation.getPitch());
    }

}
