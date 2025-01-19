package org.NoiQing.ExtraModes.AllayWar.AllayRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.ExtraModes.AllayWar.AWAPI.AllayGame;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWAllay;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWFunction;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.Entity2DTree;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TowerAttackRunnable extends BukkitRunnable {

    private int pause = 0;
    Entity2DTree enemyTree = new Entity2DTree();
    private int updateInterval = 5;

    @Override
    public void run() {
        AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();
        List<Entity> entityList = new ArrayList<>();
        for(World w : Bukkit.getWorlds()) {
            if(!w.getName().equals("skyblock_copy")) continue;
            for(LivingEntity livingEntity: w.getEntitiesByClass(LivingEntity.class)) {
                if(!livingEntity.isDead()
                        && (AWFunction.isAllayTower(livingEntity)
                        || livingEntity.getScoreboardTags().contains("allay_army")
                        || livingEntity instanceof Player))
                    entityList.add(livingEntity);
            }
            // 达到一定刷新间隔之后重新构建敌人KD树
            if(pause % updateInterval == 0){
                enemyTree.clear();
                enemyTree.addEntities(entityList);
            }
            if(pause == 20) {
                pause = 0;
                // 根据ZombieList的大小动态决定kd树的更新频率
                if(entityList.size() > 400) {
                    updateInterval = 20;
                } else if(entityList.size() > 200) {
                    updateInterval = 10;
                } else if(entityList.size() > 80) {
                    updateInterval = 4;
                } else {
                    updateInterval = 2;
                }
            }
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
                        if(AWAllay.getAllayAttackCD(a) >= 12 && allayGame.isRunning() && AWFunction.isEntityInTeam(a)) {
                            if(hasTooManyMinesNearBy(a,5)) {
                                if(AWAllay.getAllayAttackCD(a) >= 90) {
                                    a.getWorld().spawnParticle(Particle.SOUL,a.getLocation(),5,0.5,0.5,0.5,0,null,true);
                                    produceMoney(allayGame,a);
                                }
                            }
                            else {
                                if(hasResourceNearBy(a,8)) {
                                    a.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS,a.getLocation(),5,0.5,0.5,0.5,0,null,true);
                                    produceMoney(allayGame, a);
                                }
                                else if (hasPlayerNearBy(a,8) && AWAllay.getAllayAttackCD(a) >= 18) {
                                    a.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION,a.getLocation(),5,0.5,0.5,0.5,0,null,true);
                                    produceMoney(allayGame,a);
                                } else {
                                    if (AWAllay.getAllayAttackCD(a) >= 54) {
                                        produceMoney(allayGame,a);
                                    }
                                }
                            }
                        }
                    }

                    case "Hospital" -> {
                        //每20ticks回复周围生物血量
                        if(AWAllay.getAllayAttackCD(a) >= 40) {
                            for(Entity e : a.getNearbyEntities(6,6,6)) {
                                if(e instanceof LivingEntity lv) {
                                    if (e.equals(a)) continue;
                                    if (e instanceof Allay) continue;
                                    double maxHealth = Objects.requireNonNull(lv.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
                                    double health = lv.getHealth();
                                    if(health <= 0 || e.isDead()) continue;
                                    if(health == maxHealth) continue;
                                    lv.setHealth(Math.min(health + 8, maxHealth));
                                    lv.getWorld().spawnParticle(Particle.HEART, lv.getLocation(), 5,1,1,1,0,null,true);
                                }
                            }
                            AWAllay.setAllayAttackCD(a,0);
                        }
                    }

                    case "Resource" -> {
                        if (AWAllay.getAllayAttackCD(a) >= 30 && allayGame.isRunning() && AWFunction.isEntityInTeam(a)) {
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
                            if (team == null) continue;
                            String teamName = team.getTeamName();
                            allayGame.setTeamMoney(teamName,allayGame.getTeamMoney(teamName) + totalAddMoney);
                        }
                    }

                    case "Oil" -> {
                        if (AWAllay.getAllayAttackCD(a) >= 20 && allayGame.isRunning() && AWFunction.isEntityInTeam(a)) {
                            int totalAddMoney = 10;
                            QinTeam team = QinTeams.getEntityTeam(a);
                            if (team == null) continue;
                            informTeamMoney(team,totalAddMoney);
                            allayGame.setTeamMoney(team.getTeamName(), allayGame.getTeamMoney(team.getTeamName()) + totalAddMoney);
                            AWAllay.setAllayAttackCD(a,0);
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
            for(Entity e : w.getEntitiesByClass(LivingEntity.class)) {
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
                    LivingEntity target = null;
                    LivingEntity originalTarget = mob.getTarget();

                    //AWMove索敌
                    if(originalTarget == null || !originalTarget.getScoreboardTags().contains("move_tag")) {
                        //生物自动寻找敌人
                        if (mob.getScoreboardTags().contains("engineer"))
                            target = findNearestFriendlyTower(mob, 100);
                        else
                            target = findNearestEnemy(mob, 100);
                        if(target != null) mob.setTarget(target);
                    }

                    if(originalTarget == null && target == null) {
                        mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3, 50, true));
                        continue;
                    }
                    if(target == null) continue;
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

    private void produceMoney(AllayGame allayGame, Allay a) {
        for(int i = 0; i < 5; i++) {
            int x = Function.createRandom(0,10);
            int addMoney = 1;
            QinTeam t = QinTeams.getEntityTeam(a);
            if (t != null) x -= allayGame.getTeamLevels(t.getTeamName()).get("MoneyLvl");
            if(x < 2) addMoney = 6;
            String team = allayGame.getMobTeam(a);
            informTeamMoney(QinTeams.getQinTeamByName(team),addMoney);
            allayGame.setTeamMoney(team, allayGame.getTeamMoney(team) + addMoney);
            AWAllay.setAllayAttackCD(a,0);
        }
    }

    private boolean hasTooManyMinesNearBy(Entity e, int i) {
        Set<Entity> entities = enemyTree.findEntitiesWithinRadius(e,12, Entity2DTree.FilterMode.NONE);
        int minerNum = 0;
        for(Entity entity : entities) {
            if(entity instanceof LivingEntity lv) {
                if(getAllayTowerType(lv).equals("Mine")) minerNum++;
                if(minerNum >= i) return true;
            }
        }
        return false;
    }

    private void informTeamMoney(QinTeam team, int addMoney) {
        if(team == null) return;
        for(Player p : team.getTeamPlayers()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + addMoney + " 总资产: " + AWPlayer.getPlayerAWMoney(p)));
            p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.2f,1.5f);
            p.addScoreboardTag("money_told");
        }
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
    private boolean hasResourceNearBy(Allay a, int i) {
        QinTeam allayTeam = QinTeams.getEntityTeam(a);
        if(allayTeam == null) return false;
        for(Entity e : a.getNearbyEntities(i,i,i)) {
            if(e instanceof Allay allay) {
                if (allay.getScoreboardTags().contains("Tower_Resource")) return true;
            }
        }
        return false;
    }
    private boolean hasPlayerNearBy(Allay a, int i) {
        for(Entity e : a.getNearbyEntities(i,i,i)) {
            if(e instanceof Player) {
                return true;
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
    private String getAllayTowerType(LivingEntity a) {
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

    private LivingEntity findNearestEntity(Allay allay, double maxDistance) {
        Set<Entity> inRangeEntities = enemyTree.findEntitiesWithinRadius(allay,maxDistance, Entity2DTree.FilterMode.TOWER);
        return getNearestInSet(allay, inRangeEntities, maxDistance);
    }
    private LivingEntity findNearestFriendlyTower(Mob mob, double maxDistance) {
        Set<Entity> inRangeEntities = enemyTree.findEntitiesWithinRadius(mob,maxDistance, Entity2DTree.FilterMode.FRIENDLY_TOWER);
        return getNearestInSet(mob, inRangeEntities, maxDistance);
    }
    private LivingEntity findNearestEnemy(Mob mob, double maxDistance) {
        Set<Entity> inRangeEntities = enemyTree.findEntitiesWithinRadius(mob,maxDistance, Entity2DTree.FilterMode.ARMY);
        return getNearestInSet(mob, inRangeEntities, maxDistance);
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
        arrow.addScoreboardTag("allay_sniper_arrow");

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
        fireball.setYield(fireball.getYield() * 1.2f);

        QinTeam allayTeam = QinTeams.getEntityTeam(mob);
        if(allayTeam != null) allayTeam.addTeamEntities(fireball);
    }

    private static LivingEntity getNearestInSet(Entity entity, Set<Entity> inRangeEntities, double maxDistance) {
        if(inRangeEntities.isEmpty()) return null;
        LivingEntity shortestEntity = null;
        double shortest = -1;
        for(Entity e : inRangeEntities) {
            double distance = entity.getLocation().distanceSquared(e.getLocation());
            if(distance > maxDistance * maxDistance) continue;
            if(distance < shortest || shortest == -1) {
                shortestEntity = (LivingEntity) e;
                shortest = distance;
            }
        }
        return shortestEntity;
    }
}
