package org.NoiQing.AllayWar.AllayRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.AllayWar.AWUtils.AWAllay;
import org.NoiQing.AllayWar.AWUtils.AWFunction;
import org.NoiQing.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.Objects;

public class TowerAttackRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for(World w : Bukkit.getWorlds()) {
            for(Allay a : w.getEntitiesByClass(Allay.class)) {
                if(getAllayTowerType(a).isEmpty()) continue;
                AWAllay.setAllayAttackCD(a,AWAllay.getAllayAttackCD(a) + 1);
                switch (getAllayTowerType(a)) {
                    case "Sniper" -> {
                        if(AWAllay.getAllayAttackCD(a) >= 40) {
                            // 查找周围20米以内的生物
                            LivingEntity lv = findNearestEntity(a,18);
                            if(lv == null) continue;
                            shootArrowAtTarget(a, lv,4.5);
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }

                    case "Machine" -> {
                        if(AWAllay.getAllayAttackCD(a) >= 5) {
                            // 查找周围20米以内的生物
                            LivingEntity lv = findNearestEntity(a,15);
                            if(lv == null) continue;
                            shootArrowAtTarget(a, lv,1.8);
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }

                    case "Boomer" -> {
                        if(AWAllay.getAllayAttackCD(a) >= 80) {
                            LivingEntity lv = findNearestEntity(a, 20);
                            if (lv == null) continue;
                            launchTNT(a,lv);
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }

                    case "Magic" -> {
                        if(AWAllay.getAllayAttackCD(a) >= 40) {
                            LivingEntity lv = findNearestEntity(a, 12);
                            if (lv == null) continue;
                            if(!Function.isShootAble(a,lv)) return;
                            shootMagic(a,lv);
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }

                    case "Mine" -> {
                        if(AWAllay.getAllayAttackCD(a) >= 15 && AWRound.isRunning() && AWFunction.isEntityInTeam(a)) {
                            if(hasMinerNearBy(a,8) || AWAllay.getAllayAttackCD(a) >= 60) {
                                for(int i = 0; i < 5; i++) {
                                    Item item = a.getWorld().spawn(a.getLocation(), Item.class);
                                    int x = Function.createRandom(0,10);
                                    QinTeam t = QinTeams.getEntityTeam(a);
                                    if (t != null) x -= AWRound.getTeamLevels(t.getTeamName()).get("MoneyLvl");
                                    if(x < 2) item.setItemStack(new ItemStack(Material.GOLD_INGOT));
                                    else item.setItemStack(new ItemStack(Material.GOLD_NUGGET));
                                    item.addScoreboardTag("allay_gold");
                                    item.setPickupDelay(20);
                                    AWAllay.setAllayAttackCD(a,0);
                                }
                            }
                        }
                    }

                    case "Hospital" -> {
                        //每20ticks回复周围生物血量
                        if(AWAllay.getAllayAttackCD(a) >= 60) {
                            for(Entity e : a.getNearbyEntities(6,6,6)) {
                                if(e instanceof LivingEntity lv) {
                                    if (e.equals(a)) continue;
                                    if (e instanceof Allay) continue;
                                    double maxHealth = Objects.requireNonNull(lv.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
                                    double health = lv.getHealth();
                                    if(health <= 0 || e.isDead()) continue;
                                    if(health == maxHealth) continue;
                                    lv.setHealth(Math.min(health + 5, maxHealth));
                                    lv.getWorld().spawnParticle(Particle.HEART, lv.getLocation(), 5,1,1,1,0,null,true);
                                }
                            }
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }

                    case "Resource" -> {
                        if (AWAllay.getAllayAttackCD(a) >= 30 && AWRound.isRunning() && AWFunction.isEntityInTeam(a)) {
                            int totalAddMoney = 0;
                            for (Entity e : a.getNearbyEntities(6, 6, 6)) {
                                if (e instanceof Item gold && gold.getScoreboardTags().contains("allay_gold")) {
                                    switch (gold.getItemStack().getType()) {
                                        case GOLD_INGOT -> totalAddMoney += 6;
                                        case GOLD_NUGGET -> totalAddMoney += 1;
                                    }
                                    e.remove();
                                }
                            }
                            QinTeam team = QinTeams.getEntityTeam(a);
                            totalAddMoney = (int) (totalAddMoney * 0.8);
                            int addMoney;
                            if (team == null) continue;
                            addMoney = totalAddMoney / team.getTeamPlayers().size();
                            addPlayerMoney(a, team, addMoney);
                        }
                    }

                    case "Oil" -> {
                        if (AWAllay.getAllayAttackCD(a) >= 20 && AWRound.isRunning() && AWFunction.isEntityInTeam(a)) {
                            int totalAddMoney = 10;
                            QinTeam team = QinTeams.getEntityTeam(a);
                            if (team == null) continue;
                            int addMoney = totalAddMoney / team.getTeamPlayers().size();
                            addPlayerMoney(a, team, addMoney);
                        }
                    }

                    case "Cannon" -> {
                        if(AWAllay.getAllayAttackCD(a) >= 100) {
                            LivingEntity lv = AWFunction.findNearestTower(a, 40);
                            if (lv == null) continue;
                            shootCannon(a,lv);
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }
                }
            }
            for(Entity e : w.getEntities()) {
                if(AWFunction.isTankArmy(e)) {
                    if(AWAllay.getTankDisplays(e) == null) continue;
                    for(Display d : AWAllay.getTankDisplays(e)) {
                        if(d.getScoreboardTags().contains("Tank_Head")) {
                            d.setRotation(e.getLocation().getYaw() - 180,0);
                        } else {
                            Location lastLoc = AWAllay.getTankLastPos(e);
                            if (lastLoc != null) {
                                //坦克身体算法
                                float yaw = Function.calculateYaw(lastLoc, e.getLocation());
                                d.setRotation(yaw - 180, 0);
                            } else {
                                d.setRotation(e.getLocation().getYaw() - 180, 0);
                                AWAllay.setTankLastPos(e, e.getLocation());
                            }
                        }
                    }
                    if(AWAllay.getTankLastPos(e).distance(e.getLocation()) >= 0.1) {
                        AWAllay.setTankLastPos(e, e.getLocation());
                    }

                }

                //以下是军队设定
                if(!e.getScoreboardTags().contains("allay_army")) continue;
                if(e instanceof Mob mob) {
                    LivingEntity target = mob.getTarget();

                    //AWMove索敌
                    //生物自动寻找敌人
                    if (mob.getScoreboardTags().contains("engineer"))
                        target = findNearestFriendlyTower(mob, 100);
                    else
                        target = findNearestEnemy(mob, 100);
                    if(target != null) mob.setTarget(target);

                    if(target == null) {
                        mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1, 255, true));
                        continue;
                    }

                    //步枪兵机制
                    if(mob.getScoreboardTags().contains("rifle")) {
                        if(target.getScoreboardTags().contains("move_tag")) continue;
                        if(target.getLocation().distance(mob.getLocation()) <= 8) {
                            AWAllay.setAllayAttackCD(mob,AWAllay.getAllayAttackCD(mob) + 1);
                            if(Function.isShootAble(mob,target))
                                mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,5,50,true));
                            else continue;
                            if(AWAllay.getAllayAttackCD(mob) >= 30) {
                                shootGun(mob,target);
                                AWAllay.setAllayAttackCD(mob,0);
                            }
                        }
                    } else if(mob.getScoreboardTags().contains("laser_tank")) {
                        //激光坦克设定
                        if(target.getScoreboardTags().contains("move_tag")) continue;
                        if(target.getLocation().distance(mob.getLocation()) <= 20) {
                            AWAllay.setAllayAttackCD(mob,AWAllay.getAllayAttackCD(mob) + 1);
                            mob.setRotation(Function.calculateYaw(mob.getLocation(),target.getLocation()),0);
                            if(Function.isShootAble(mob,target,mob.getLocation().clone().add(0,2.5,0)))
                                mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,5,50,true));
                            else continue;
                            if(AWAllay.getAllayAttackCD(mob) >= 40) {
                                shootLaser(mob,target);
                                AWAllay.setAllayAttackCD(mob,0);
                            }
                        }
                    } else if(mob.getScoreboardTags().contains("normal_tank")) {
                        //灰熊坦克设定
                        if(target.getScoreboardTags().contains("move_tag")) continue;
                        if(target.getLocation().distance(mob.getLocation()) <= 10) {
                            AWAllay.setAllayAttackCD(mob,AWAllay.getAllayAttackCD(mob) + 1);
                            mob.setRotation(Function.calculateYaw(mob.getLocation(),target.getLocation()),0);
                            if(Function.isShootAble(mob,target,mob.getLocation().clone().add(0,2,0)))
                                mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,5,50,true));
                            else continue;
                            if(AWAllay.getAllayAttackCD(mob) >= 30) {
                                shootTank(mob,target);
                                AWAllay.setAllayAttackCD(mob,0);
                            }
                        }
                    }
                }
            }
        }
    }
    private void addPlayerMoney(Allay a, QinTeam team, int addMoney) {
        for(Player p : team.getTeamPlayers()) {
            AWPlayer.setPlayerAWMoney(p, AWPlayer.getPlayerAWMoney(p) + addMoney);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + addMoney + " 总资产: " + AWPlayer.getPlayerAWMoney(p)));
            p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
            p.addScoreboardTag("money_told");
        }
        AWAllay.setAllayAttackCD(a,0);
    }

    private void shootLaser(Mob mob, LivingEntity target) {
        target.damage(20,mob);
        QinTeam at = QinTeams.getEntityTeam(mob);
        for(Entity e : target.getNearbyEntities(3,3,3)) {
            if(e instanceof LivingEntity lv) {
                if(lv.equals(target)) continue;
                if(at == null || !at.equals(QinTeams.getEntityTeam(lv))) {
                    lv.damage(4,mob);
                    lv.setNoDamageTicks(0);
                }
            }
        }

        if(mob.getLocation().getWorld() == null) return;
        mob.getLocation().getWorld().playSound(mob.getLocation(), Sound.BLOCK_GLASS_BREAK,1,1.2f);

        AWFunction.showMagicParticle(mob.getLocation().clone().add(0,2.7,0),target.getLocation().clone().add(0,1.2,0), Particle.TRIAL_SPAWNER_DETECTION_OMINOUS);
        AWFunction.showMagicParticle(mob.getLocation().clone().add(0,2.6,0),target.getLocation().clone().add(0,1.1,0), Particle.OMINOUS_SPAWNING);
        AWFunction.showMagicParticle(mob.getLocation().clone().add(0,2.5,0),target.getLocation().clone().add(0,1,0), Particle.OMINOUS_SPAWNING);
        AWFunction.showMagicParticle(mob.getLocation().clone().add(0,2.4,0),target.getLocation().clone().add(0,0.9,0), Particle.OMINOUS_SPAWNING);
        AWFunction.showMagicParticle(mob.getLocation().clone().add(0,2.3,0),target.getLocation().clone().add(0,0.8,0), Particle.OMINOUS_SPAWNING);
    }

    private boolean hasMinerNearBy(Allay a, int i) {
        QinTeam allayTeam = QinTeams.getEntityTeam(a);
        if(allayTeam == null) return false;
        for(Entity e : a.getNearbyEntities(i,i,i)) {
            if(e instanceof Player) {
                return true;
            } else if(e instanceof Allay allay) {
                if (allay.getScoreboardTags().contains("Tower_Resource")) return true;
            }
        }
        return false;
    }

    private void shootCannon(Allay allay, LivingEntity target) {
        Location location = allay.getLocation();
        Location targetLocation = target.getLocation();

        // 生成并发射TNT
        TNTPrimed tnt = (TNTPrimed) allay.getWorld().spawnEntity(location.add(0, 1, 0), EntityType.TNT);
        tnt.setFuseTicks(50); // 设置TNT的爆炸延迟（1.5秒）
        tnt.setYield(3F);

        // 计算抛物线的速度矢量
        Vector velocity = Function.calculateVelocity(location.toVector(), targetLocation.toVector(), 6,0.115);
        tnt.setVelocity(velocity);

        tnt.addScoreboardTag("cannon_tnt");

        QinTeam team = QinTeams.getEntityTeam(allay);
        if(team != null) team.addTeamEntities(tnt);
    }

    private void shootMagic(Allay a, LivingEntity target) {
        target.damage(4);
        target.damage(10,a);
        QinTeam at = QinTeams.getEntityTeam(a);
        for(Entity e : target.getNearbyEntities(0.75,0.75,0.75)) {
            if(e instanceof LivingEntity lv) {
                if(lv.equals(target)) continue;
                if(at == null || !at.equals(QinTeams.getEntityTeam(lv))) {
                    lv.damage(4,a);
                    lv.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,40,2));
                    lv.setNoDamageTicks(0);
                }
            }
        }
        AWFunction.showMagicParticle(a.getLocation().clone().add(0,1,0),target.getLocation().clone().add(0,1,0), Particle.WITCH);
    }

    private void shootGun(Mob mob, LivingEntity target) {
        target.damage(6,mob);
        AWFunction.showMagicParticle(mob.getLocation().clone().add(0,1,0),target.getLocation().clone().add(0,1,0), Particle.CRIT);
    }

    private String getAllayTowerType(Allay a) {
        for(String s : a.getScoreboardTags()) {
            if(s.startsWith("Tower_")) return s.substring(6);
        }
        return "";
    }

    private void launchTNT(Allay allay, LivingEntity target) {
        Location location = allay.getLocation();
        Location targetLocation = target.getLocation();

        // 生成并发射TNT
        TNTPrimed tnt = (TNTPrimed) allay.getWorld().spawnEntity(location.add(0, 1, 0), EntityType.TNT);
        tnt.setFuseTicks(30); // 设置TNT的爆炸延迟（1.5秒）
        tnt.setYield(1.75F);

        // 计算抛物线的速度矢量
        Vector velocity = Function.calculateVelocity(location.toVector(), targetLocation.toVector(), 2,0.115);
        tnt.setVelocity(velocity);
        tnt.addScoreboardTag("allay_damage");

        QinTeam team = QinTeams.getEntityTeam(allay);
        if(team != null) team.addTeamEntities(tnt);
    }

    private LivingEntity findNearestEntity(Allay allay, double findDistance) {

        QinTeam allayTeam = QinTeams.getEntityTeam(allay);

        double nearestEntity = findDistance;
        LivingEntity closestEntity = null;
        Location allayLocation = allay.getLocation();

        for (Entity entity : allay.getNearbyEntities(findDistance, findDistance, findDistance)) {
            QinTeam entityTeam = QinTeams.getEntityTeam(entity);
            if(entity.getScoreboardTags().contains("move_tag")) continue;
            if (entity instanceof Player p) {
                if (allayTeam == null) continue;
                if ((p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE
                        || p.isInvulnerable() || allayTeam.equals(entityTeam))) continue;
            }
            if (entity.getType() != EntityType.VILLAGER && entity instanceof LivingEntity livingEntity) {
                if(entity.getScoreboardTags().contains("move_tag")) continue;
                if (ignoreSomeEntities(allayTeam, entity, entityTeam, livingEntity.equals(allay), livingEntity.isDead()))
                    continue;

                double distance = entity.getLocation().distance(allayLocation);

                if (distance < nearestEntity) {
                    nearestEntity = distance;
                    closestEntity = livingEntity;
                }
            }
        }

        return closestEntity;
    }

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
        if(dead) return true;

        return false;
    }

    private LivingEntity findNearestFriendlyTower(Mob mob, double findDistance) {
        QinTeam mobTeam = QinTeams.getEntityTeam(mob);
        if(mobTeam == null) return null;

        double nearestTower = findDistance;
        LivingEntity closestTower = null;
        Location mobLocation = mob.getLocation();

        for (Entity entity : mob.getNearbyEntities(findDistance, findDistance, findDistance)) {
            if(!AWFunction.isAllayTower(entity)) continue;
            QinTeam entityTeam = QinTeams.getEntityTeam(entity);
            if(mobTeam.equals(entityTeam)) continue;
            String towerName = Function.getNameWithoutColor(entity.getCustomName()).replaceAll("[^\\p{IsHan}]", "");
            if(towerName.endsWith("塔") || towerName.endsWith("炮") || towerName.equals("生命核心")) continue;
            double distance = entity.getLocation().distance(mobLocation);

            if (distance < nearestTower) {
                nearestTower = distance;
                closestTower = (LivingEntity) entity;
            }
        }
        return closestTower;
    }
    private LivingEntity findNearestEnemy(Mob mob, double findDistance) {

        QinTeam mobTeam = QinTeams.getEntityTeam(mob);

        double nearestEnemy = findDistance;
        LivingEntity closestEnemy = null;
        Location mobLocation = mob.getLocation();

        for (Entity entity : mob.getNearbyEntities(findDistance, findDistance, findDistance)) {
            QinTeam entityTeam = QinTeams.getEntityTeam(entity);
            //无视生物移动标记
            if(entity.getScoreboardTags().contains("move_tag")) continue;
            //无视无敌的玩家
            if (entity instanceof Player p)
                if(p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE
                        || p.isInvulnerable() || Objects.equals(entityTeam, mobTeam)) continue;

            //如果是一些特殊的实体，无视
            if (ignoreSomeEntities(mobTeam, entity, entityTeam, entity.equals(mob), entity.isDead()))
                continue;

            if(!(entity instanceof LivingEntity)) continue;
            if(entity instanceof Villager) continue;
            double distance = entity.getLocation().distance(mobLocation);

            if (distance < nearestEnemy) {
                nearestEnemy = distance;
                closestEnemy = (LivingEntity) entity;
            }
        }

        return closestEnemy;
    }

    private void shootArrowAtTarget(Allay allay, LivingEntity target, double damage) {
        Location allayLocation = allay.getLocation();
        Location targetLocation = target.getBoundingBox().getCenter().toLocation(target.getWorld());
        if(target instanceof Allay a) {
            targetLocation = a.getLocation().add(0,0.4,0);
        }

        Vector direction = targetLocation.toVector().subtract(allayLocation.toVector()).normalize();

        Arrow arrow = allay.getWorld().spawnArrow(allayLocation.add(0, 0.7, 0), direction, 1.5f, 12);
        arrow.setVelocity(direction.multiply(2.5));
        arrow.setShooter(allay);
        arrow.setDamage(damage);
        arrow.addScoreboardTag("allay_damage");

        QinTeam allayTeam = QinTeams.getEntityTeam(allay);
        if(allayTeam != null) allayTeam.addTeamEntities(arrow);
    }

    private void shootTank(Mob mob, LivingEntity target) {
        Location allayLocation = mob.getLocation().clone().add(0, 2, 0);
        Location targetLocation = target.getBoundingBox().getCenter().toLocation(target.getWorld());
        Vector direction = targetLocation.toVector().subtract(allayLocation.toVector()).normalize();

        LargeFireball fireball = mob.getWorld().spawn(allayLocation, LargeFireball.class);
        fireball.setVelocity(direction.multiply(1.5));
        fireball.setShooter(mob);
        fireball.addScoreboardTag("allay_damage");

        QinTeam allayTeam = QinTeams.getEntityTeam(mob);
        if(allayTeam != null) allayTeam.addTeamEntities(fireball);
    }


    private void rotateBlockDisplay(BlockDisplay blockDisplay, float yaw) {
        Transformation t = blockDisplay.getTransformation();
        // 获取当前 BlockDisplay 实体的世界坐标
        double x = t.getTranslation().x + blockDisplay.getLocation().getX();
        double y = t.getTranslation().y;
        double z = t.getTranslation().z + blockDisplay.getLocation().getZ();

        //获取中心点
        double cx =  blockDisplay.getLocation().getX();
        double cz =  blockDisplay.getLocation().getZ();

        double rotationY = t.getLeftRotation().y * 180;

        // 计算新坐标
        double newX = cx + (x - cx) * Math.cos(yaw) - (z - cz) * Math.sin(yaw);
        double newZ = cz + (x - cx) * Math.sin(yaw) + (z - cz) * Math.cos(yaw);

        // 计算物体当前的旋转角度（将初始 rotation 从 -1~1 映射到角度值 -180~180）

        // 新的旋转角度 = 当前角度 + 本次旋转的角度
        double newAngle = (double) yaw - blockDisplay.getLocation().getYaw() + rotationY;

        // 将新的角度归一化到 -180 到 180 度范围内
        if (newAngle > 180) {
            newAngle -= 360;
        } else if (newAngle < -180) {
            newAngle += 360;
        }

        // 将角度归一化为 -1 到 1 的 rotation 值
        double newRotation = newAngle / 180.0;

        // 确保 rotation 值在 -1 和 1 之间
        if (newRotation > 1) {
            newRotation = 1;
        } else if (newRotation < -1) {
            newRotation = -1;
        }

        t.getTranslation().set(newX - blockDisplay.getLocation().getX(),y,newZ - blockDisplay.getLocation().getZ());
        t.getLeftRotation().set(t.getLeftRotation().x, (float) newRotation, t.getLeftRotation().z,t.getLeftRotation().w);

        blockDisplay.setTransformation(t);
        blockDisplay.setRotation(yaw,0);
    }




}
