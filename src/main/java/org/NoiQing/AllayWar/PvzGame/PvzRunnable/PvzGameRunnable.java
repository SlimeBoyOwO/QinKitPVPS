package org.NoiQing.AllayWar.PvzGame.PvzRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.AllayWar.PvzGame.Game.PvzRound;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.Entity2DTree;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PVZFunction;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.EventListener.System.WallJumpListener;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;


public class PvzGameRunnable extends BukkitRunnable {
    private int pause = 0;
    private int sunFall = 0;
    Entity2DTree enemyTree = new Entity2DTree();
    int updateInterval = 5;
    @Override
    public void run() {
        pause++;
        List<Entity> zombieList = new ArrayList<>();
        for(World w : Bukkit.getWorlds()) {
            if(!w.getName().equals("skyblock_copy")) continue;
            for(Monster zombie: w.getEntitiesByClass(Monster.class)) {
                if(pause % updateInterval == 0) { //每5tick更新一次
                    if (!zombie.getScoreboardTags().contains("pvz_plant")) {
                        zombieList.add(zombie);
                    }
                }
            }
            for(Entity e: w.getEntitiesByClass(LivingEntity.class)) {

                int effectDuration = PvzEntity.getEffectDuration(e);
                if(effectDuration >= 0) {
                    PvzEntity.setEffectDuration(e,--effectDuration);
                    if(effectDuration == 0) {
                        if(e instanceof Mob mob) mob.setAI(true);
                        List<Display> effectDisplays = PvzEntity.getEffectDisplays(e);
                        if(!effectDisplays.isEmpty()) {
                            for(Display display : effectDisplays)
                                e.removePassenger(display);
                        }
                        PvzEntity.getEffectDisplays(e).forEach(Entity::remove);
                        PvzEntity.removeEffectDisplays(e);
                        List<Display> displays = PvzEntity.getPlantDisplays(e);
                        if(!displays.isEmpty()) {
                            for(Display display : displays)
                                e.addPassenger(display);
                        }
                    }
                }

                if(!e.getScoreboardTags().contains("pvz_plant")) continue;
                //以下代码针对植物逻辑
                if(PvzEntity.getPlantDisplays(e) == null) continue;
                for(Display d : PvzEntity.getPlantDisplays(e)) {
                    d.setRotation(e.getLocation().getYaw() + 90,0);
                }

                //植物逻辑处理
                if(e instanceof Mob mob) {
                    LivingEntity target = mob.getTarget();

                    if (target != null) {
                        mob.setRotation(Function.calculateYaw(mob.getLocation(), target.getLocation()), 0);
                        if(target.isDead()) {
                            LivingEntity newTarget = findNearestEnemy2(mob,50);
                            mob.setTarget(newTarget);
                            target = newTarget;
                        }
                    }

                    int attackCD = PvzEntity.getPlantAttackCD(mob);
                    PvzEntity.setPlantAttackCD(mob, ++attackCD);
                    String mobTag = mob.getScoreboardTags().stream()
                            .filter(this::isSupportedShooter)
                            .findFirst()
                            .orElse(null);
                    if (mobTag != null) {
                        switch (mobTag) {
                            case "peaShooter" ->
                                    handleSingleShot(mob, target, attackCD, 28, this::shootPea);
                            case "icePeaShooter" ->
                                    handleSingleShot(mob, target, attackCD, 28, this::shootIcePea);
                            case "doublePeaShooter" ->
                                    handleDoubleShot(mob, target, attackCD);
                            case "machinePeaShooter" ->
                                    handleMultiShot(mob, target, attackCD, new int[]{4, 8, 12}, 28, this::shootPea);
                            case "cabbagePitcher" ->
                                    handlePitcherShot(mob, target, attackCD, 54, 56, this::shootCabbage);
                            case "cornPitcher" ->
                                    handlePitcherShot(mob, target, attackCD, 54, 56, this::shootCorn);
                            case "melonPitcher" ->
                                    handlePitcherShot(mob, target, attackCD, 54, 56, this::shootMelon);
                            case "sunFlower" -> {
                                if(attackCD >= 23 * 20) {
                                    mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,40,1,true,false));
                                    Item sun = mob.getWorld().spawn(mob.getLocation().clone().add(0,1,0), Item.class);
                                    sun.setItemStack(new ItemStack(Material.SUNFLOWER));
                                    sun.addScoreboardTag("pvz_sun");
                                    PvzEntity.setPlantAttackCD(mob,0);
                                }
                            }
                            case "pvz_nut" -> {
                                if(attackCD >= 20) {
                                    for(Entity entity: mob.getNearbyEntities(2,2,2)) {
                                        if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                            Entity lastTarget = PvzEntity.getMobTarget(mob);
                                            if(lastTarget == null || !lastTarget.getScoreboardTags().contains("pvz_nut")) {
                                                enemy.setTarget(mob);
                                                PvzEntity.setMobTarget(enemy,mob);
                                            }
                                        }
                                    }
                                    PvzEntity.setPlantAttackCD(mob,0);
                                }
                            }
                            case "cherryBoom" -> {
                                if(attackCD >= 24) {
                                    for(Entity entity: mob.getNearbyEntities(6,6,6)) {
                                        if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                            enemy.damage(4*90,mob);
                                        }
                                    }
                                    mob.getWorld().spawnParticle(Particle.EXPLOSION,mob.getLocation(),100,6,6,6,0);
                                    mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,2,1);
                                    mob.setHealth(0);
                                }
                            }
                            case "potatoMine" -> {
                                if(!mob.getScoreboardTags().contains("mine_ready")) {
                                    if(attackCD >= 20 * 20) {
                                        mob.addScoreboardTag("mine_ready");
                                        PvzEntity.setPlantAttackCD(mob,0);
                                    }
                                } else {
                                    if(attackCD >= 20) {
                                        for(Entity entity: mob.getNearbyEntities(2,2,2)) {
                                            if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                                enemy.damage(4*90,mob);
                                            }
                                        }

                                        mob.getWorld().spawnParticle(Particle.EXPLOSION,mob.getLocation(),30,2,2,2,0);
                                        mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);
                                        mob.setHealth(0);
                                    } else {
                                        if(mob.getScoreboardTags().contains("ready_boom")) continue;
                                        List<Entity> entities = new ArrayList<>();
                                        for(Entity entity: mob.getNearbyEntities(2,2,2)) {
                                            if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                                entities.add(enemy);
                                            }
                                        }
                                        if(entities.size() > 0) mob.addScoreboardTag("ready_boom");
                                        else PvzEntity.setPlantAttackCD(mob,0);
                                    }
                                }
                            }
                            case "icebergLettuce" -> {
                                if(attackCD >= 20) {
                                    for(Entity entity: mob.getNearbyEntities(2,2,2)) {
                                        if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                            enemy.damage(2,mob);
                                            break;
                                        }
                                    }
                                    mob.getWorld().spawnParticle(Particle.SNOWFLAKE,mob.getLocation(),30,2,2,2,0);
                                    mob.getWorld().playSound(mob.getLocation(),Sound.BLOCK_GRASS_BREAK,3,1);
                                    mob.setHealth(0);
                                } else {
                                    if(mob.getScoreboardTags().contains("ready_boom")) continue;
                                    List<Entity> entities = new ArrayList<>();
                                    for(Entity entity: mob.getNearbyEntities(2,2,2)) {
                                        if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                            entities.add(enemy);
                                        }
                                    }
                                    if(entities.size() > 0) mob.addScoreboardTag("ready_boom");
                                    else PvzEntity.setPlantAttackCD(mob,0);
                                }
                            }
                        }
                    }
                }
            }
            for(Entity e: w.getEntitiesByClass(ThrownPotion.class)) {
                //以下代码针对子弹逻辑
                if(PVZFunction.isBullet(e)) {
                    if(WallJumpListener.isTouchingWallHere(e) || isPotionOnGround(e)) {
                        PvzEntity.getPlantDisplays(e).forEach(Entity::remove);
                        PvzEntity.removePlantDisplays(e);
                        e.remove();
                    }
                }
            }
            for(Item item: w.getEntitiesByClass(Item.class)) {
                if(!item.getScoreboardTags().contains("pvz_sun")) return;
                int cd = PvzEntity.getPlantAttackCD(item);
                PvzEntity.setPlantAttackCD(item,++cd);
                if(cd >= 60) {
                    PvzRound.setTotalSun(PvzRound.getTotalSun() + item.getItemStack().getAmount() * 25);
                    for(Player player : item.getWorld().getPlayers()) {
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + "25" + " 阳光: " + PvzRound.getTotalSun()));
                    }
                    item.remove();
                }
            }
        }


        //保证僵尸向脑子前进
        for(Mob m : PvzRound.getZombies()) {
            if(m.getTarget() == null || m.getTarget().isDead()) {
                m.setTarget(PvzRound.getBrain());
            }
            //以下代码针对植物逻辑
            if(PvzEntity.getPlantDisplays(m) == null) continue;
            for(Display d : PvzEntity.getPlantDisplays(m)) {
                d.setRotation(m.getLocation().getYaw() + 90,0);
            }
        }
        if(PvzRound.isRunning()) {
            if(++sunFall == 20 * 7) {
                sunFall = 0;
                PvzRound.setTotalSun(PvzRound.getTotalSun() + 25);
                if(PvzRound.getBrain() == null) return;
                for(Player player : PvzRound.getBrain().getWorld().getPlayers()) {
                    player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + "25" + " 阳光: " + PvzRound.getTotalSun()));
                }
            }
            PvzRound.addCurrentTime();
            PvzRound.getProcessBar().setProgress((double) PvzRound.getCurrentTime() / PvzRound.getTotalTime());
        }
        if(PvzRound.isRunning() && PvzRound.isWaveEnd() && PvzRound.getZombies().size() == 0)
            PvzRound.endRound();

        if(pause % updateInterval == 0){
            enemyTree.clear();
            enemyTree.addEntities(zombieList);
        }


        if(pause == 20) {
            pause = 0;
            // 根据ZombieList的大小动态决定kd树的更新频率
            if(zombieList.size() > 400) {
                updateInterval = 20;
            } else if(zombieList.size() > 200) {
                updateInterval = 10;
            } else if(zombieList.size() > 80) {
                updateInterval = 4;
            } else {
                updateInterval = 2;
            }
        }
    }

    private boolean isSupportedShooter(String tag) {
        return tag.equals("peaShooter") || tag.equals("icePeaShooter") ||
                tag.equals("doublePeaShooter") || tag.equals("machinePeaShooter") ||
                tag.equals("cabbagePitcher") || tag.equals("cornPitcher") ||
                tag.equals("melonPitcher") || tag.equals("sunFlower") ||
                tag.equals("pvz_nut") || tag.equals("potatoMine") ||
                tag.equals("cherryBoom") || tag.equals("icebergLettuce");
    }

    private LivingEntity setPlantTarget(Mob mob,LivingEntity target) {
        LivingEntity newTarget = null;
        newTarget = findNearestEnemy2(mob,52);
        if(newTarget != null && !newTarget.equals(target)) {
            mob.setTarget(newTarget);
            target = newTarget;
        }

        return target;
    }

    public static boolean isEnemy(Entity e, Entity target) {
        if(target instanceof Display) return false;
        QinTeam mobTeam = QinTeams.getEntityTeam(e);
        QinTeam entityTeam = QinTeams.getEntityTeam(target);
        //无视生物移动标记
        if(target.getScoreboardTags().contains("move_tag")) return false;
        if(PVZFunction.isBullet(target)) return false;
        //无视无敌的玩家
        if (target instanceof Player p)
            if(p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE
                    || p.isInvulnerable() || Objects.equals(entityTeam, mobTeam)) return false;

        if (mobTeam != null && mobTeam.getTeamName().equals("植物") && target instanceof Player) return false;

        //如果是一些特殊的实体，无视
        if (ignoreSomeEntities(mobTeam, target, entityTeam, target.equals(e), target.isDead()))
            return false;

        if(!(target instanceof LivingEntity)) return false;
        return !(target instanceof Villager);
    }

    private void shootCabbage(Mob mob, LivingEntity target) {
        if(target == null) return;
        Entity cabbage = generateBullet(mob,target,false);
        cabbage.addScoreboardTag("cabbage_bullet");
        PVZFunction.summonPlant(cabbage,"卷心菜",0.4f,true);
    }

    private void shootCorn(Mob mob, LivingEntity target) {
        if(target == null) return;
        Entity corn = generateBullet(mob,target,false);
        corn.addScoreboardTag("corn_bullet");
        PVZFunction.summonPlant(corn,"玉米粒投掷物",0.4f,true);
    }
    private void shootMelon(Mob mob, LivingEntity target) {
        if(target == null) return;
        Entity melon = generateBullet(mob,target,false);
        melon.addScoreboardTag("melon_bullet");
        PVZFunction.summonPlant(melon,"西瓜投掷物",0.4f,true);
    }

    private void shootPea(Mob mob, LivingEntity target) {
        if(target == null) return;
        Entity pea = generateBullet(mob,target,true);
        pea.addScoreboardTag("pea_bullet");
        PVZFunction.summonPlant(pea,"豌豆子弹",0.4f,true);
    }
    private void shootIcePea(Mob mob, LivingEntity target) {
        if(target == null) return;
        Entity pea = generateBullet(mob,target,true);
        pea.addScoreboardTag("ice_pea_bullet");
        PVZFunction.summonPlant(pea,"寒冰豌豆子弹",0.4f,true);
    }

    private Entity generateBullet(Mob mob, LivingEntity target, Boolean isPea) {
        Location fireLocation = mob.getLocation().clone().add(0, 1.25, 0);
        Location targetLocation = target.getBoundingBox().getCenter().toLocation(target.getWorld());
        Vector direction = targetLocation.toVector().subtract(fireLocation.toVector()).normalize();
        QinTeam mobTeam = QinTeams.getEntityTeam(mob);
        ThrownPotion pea = mob.getWorld().spawn(fireLocation, ThrownPotion.class);
        pea.addScoreboardTag("plant_damage");
        pea.addScoreboardTag("plant_bullet");
        if(mobTeam != null) mobTeam.addTeamEntities(pea);
        PvzEntity.setBulletOwner(pea,mob);
        pea.addScoreboardTag("pvz_plant");
        pea.setSilent(true);
        QinTeam allayTeam = QinTeams.getEntityTeam(mob);
        if(allayTeam != null) allayTeam.addTeamEntities();
        Objects.requireNonNull(fireLocation.getWorld()).playSound(fireLocation, Sound.ENTITY_SNOWBALL_THROW,1,1);

        if(isPea) {
            pea.setGravity(false);
            pea.setVelocity(direction.multiply(1.5));
            // PvzEntity.setBulletVector(pea,direction.multiply(1.1));
        } else {
            pea.setGravity(true);
            Vector vec = Function.calculateVelocity(fireLocation.toVector(),calculateFutureLocation(target,20).toVector(),6,0.115);
            if (vec.getX() > -100 && vec.getX() < 100 && vec.getY() > -3 && vec.getY() < 100 && vec.getZ() > -100 && vec.getZ() < 100)
                pea.setVelocity(vec);
            else {
                vec = new Vector(0,1.5,0);
                pea.setVelocity(vec);
            }
            // PvzEntity.setBulletVector(pea,vec);
        }

        return pea;
    }

    /*
    private LivingEntity findHitEntity(Entity bullet, double findDistance) {
        QinTeam plantTeam = QinTeams.getEntityTeam(PvzEntity.getBulletOwner(bullet));
        //一般情况不可能没有队伍
        if(plantTeam == null) return null;
        LivingEntity hitEntity = null;

        Predicate<Entity> predicate = x -> isEnemy(bullet, x);
        // 计算射线方向的角度

        double angle = Math.atan2(PvzEntity.getBulletVector(bullet).getZ(), PvzEntity.getBulletVector(bullet).getX());;

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

    private LivingEntity findNearestEnemy1(Entity mob, double findDistance, boolean once) {
        double nearestEnemy = findDistance * findDistance;
        LivingEntity closestEnemy = null;
        Location mobLocation = mob.getLocation().clone().add(0,4.7,0);

        // 射线数量 (这里设为30条，360度范围内)
        int rayCount = 40;
        double angleIncrement = 360.0 / rayCount;

        Predicate<Entity> predicate = x -> isEnemy(mob, x);

        {
            // 发射射线
            RayTraceResult result = mob.getWorld().rayTrace(
                    mobLocation, // 起始点
                    mob.getLocation().getDirection(),               // 方向向量
                    findDistance,                     // 最大距离
                    FluidCollisionMode.NEVER, // 流体模式
                    true,                    // 忽略非可视方块
                    3.5,                     // 检测范围（宽度）
                    predicate              // 过滤器
            );
            if (result != null && result.getHitEntity() != null) {
                return (LivingEntity) result.getHitEntity();
            }
        }


        // 执行到这里，则说明第一次射线没有成功，则追加360度扫描
        if(!once) {
            for (int i = 0; i < rayCount; i++) {
                // 计算射线方向的角度
                double angle = Math.toRadians(i * angleIncrement);

                // 基于角度计算射线的目标方向
                Vector direction = new Vector(Math.cos(angle), 0, Math.sin(angle));

                // 发射射线
                RayTraceResult result = mob.getWorld().rayTrace(
                        mobLocation, // 起始点
                        direction,               // 方向向量
                        findDistance,                     // 最大距离
                        FluidCollisionMode.NEVER, // 流体模式
                        true,                    // 忽略非可视方块
                        3.5,                     // 检测范围（宽度）
                        predicate              // 过滤器
                );

                // 检查是否有实体被射线命中
                if (result != null && result.getHitEntity() != null) {
                    LivingEntity hitEntity = (LivingEntity) result.getHitEntity();
                    if(hitEntity.equals(closestEnemy)) continue;
                    if(closestEnemy == null) {
                        closestEnemy = hitEntity;
                        continue;
                    }

                    // 计算命中实体与mob的距离
                    double distance = Math.pow(hitEntity.getLocation().getX() - closestEnemy.getLocation().getX(),2) + Math.pow(hitEntity.getLocation().getZ() - closestEnemy.getLocation().getZ(),2);


                    // 更新最近的敌人
                    if (distance < nearestEnemy) {
                        nearestEnemy = distance;
                        closestEnemy = hitEntity;
                    }
                }
            }
        }

        return closestEnemy;
    }

     */
    private LivingEntity findNearestEnemy2(Entity mob,double maxDistance) {
        LivingEntity foundEntity =  (LivingEntity) enemyTree.findNearest(mob);
        if(foundEntity != null) {
            if(mob.getLocation().distanceSquared(foundEntity.getLocation()) > maxDistance * maxDistance) {
                foundEntity = null;
            }
        }
        return foundEntity;
    }


    public static boolean ignoreSomeEntities(QinTeam allayTeam, Entity entity, QinTeam entityTeam, boolean equals, boolean dead) {
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

    private void handleSingleShot(Mob mob, LivingEntity target, int attackCD, int cooldown, BiConsumer<Mob, LivingEntity> shootAction) {
        if (attackCD >= cooldown) {
            target = setPlantTarget(mob, target);
            shootAction.accept(mob, target);

            PvzEntity.setPlantAttackCD(mob, 0);
        }
    }

    private void handleDoubleShot(Mob mob, LivingEntity target, int attackCD) {
        if (attackCD == 4) {
            shootPea(mob, target);
        }
        handleSingleShot(mob, target, attackCD, 28, this::shootPea);
    }

    private void handleMultiShot(Mob mob, LivingEntity target, int attackCD, int[] shotIntervals, int cooldown, BiConsumer<Mob, LivingEntity> shootAction) {
        for (int interval : shotIntervals) {
            if (attackCD == interval) {
                shootAction.accept(mob, target);
            }
        }
        handleSingleShot(mob, target, attackCD, cooldown, shootAction);
    }

    private void handlePitcherShot(Mob mob, LivingEntity target, int attackCD, int preShootTime, int shootTime, BiConsumer<Mob, LivingEntity> shootAction) {
        if (attackCD == preShootTime) {
            target = setPlantTarget(mob, target);
            if (target != null) {
                PvzEntity.setEntityLastLocation(target,mob);
            }
        }
        if (attackCD >= shootTime) {
            shootAction.accept(mob, target);

            PvzEntity.setPlantAttackCD(mob, 0);
        }
    }

    public static boolean isPotionOnGround(Entity player) {
        return player.getLocation().clone().subtract(0, 0.4, 0).getBlock().getType().isSolid();
    }

    public static boolean isShootAble(LivingEntity attacker, LivingEntity target, Location start) {
        if(target==null) return false;
        Predicate<Entity> predicate = x -> isEnemy(attacker,target);
        Location end = target.getLocation().clone().add(0,1,0);
        // 创建一条射线
        RayTraceResult result = Objects.requireNonNull(start.getWorld()).rayTrace(
                start, // 起始点
                end.subtract(start).toVector(),               // 方向向量
                30,                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                0.3,                     // 检测范围（宽度）
                predicate              // 过滤器
        );
        if(result != null && result.getHitEntity() instanceof LivingEntity lv) {
            Bukkit.broadcastMessage(lv.getName());
            return true;
        }
        return false;

    }
}
