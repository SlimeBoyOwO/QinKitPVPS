package org.NoiQing.ExtraModes.PvzGame.PvzRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.ExtraModes.MiniGames.MiniGameUtils.MiniFunction;
import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.PVZFunction;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.SpawnZombie;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.Entity2DTree;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.EventListener.System.WallJumpListener;
import org.NoiQing.QinKitPVPS;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
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
            for(Player player : w.getPlayers()) {
                if(PvzEntity.getPlantDisplays(player) == null) continue;
                for(Display d : PvzEntity.getPlantDisplays(player)) {
                    d.setRotation(player.getLocation().getYaw(),player.getLocation().getPitch());
                }
            }
            for(Monster zombie: w.getEntitiesByClass(Monster.class)) {
                if (!zombie.getScoreboardTags().contains("pvz_plant") && !zombie.isDead() && !zombie.getScoreboardTags().contains("pvz_dreamed")) {
                    zombieList.add(zombie);
                }
            }
            for(Entity e: w.getEntitiesByClass(LivingEntity.class)) {

                int effectDuration = PvzEntity.getEffectDuration(e);
                if(effectDuration >= 0) {
                    PvzEntity.setEffectDuration(e,--effectDuration);
                    if(effectDuration == 0) {
                        if(e instanceof Mob mob && !mob.getScoreboardTags().contains("pvz_grave")) mob.setAI(true);
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
                            mob.setTarget(null);
                            target = null;
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
                            case "puffRoom" ->
                                    handlePuffShot(mob, target, attackCD, 28, this::shootPuff);
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
                                if(attackCD % 20 == 0) allowPlantToBeEat(mob);
                            }
                            case "sunRoom" -> {
                                if(attackCD % (23 * 20) == 0) {
                                    mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,40,1,true,false));
                                    Item sun = mob.getWorld().spawn(mob.getLocation().clone().add(0,1,0), Item.class);
                                    sun.setItemStack(new ItemStack(Material.SUNFLOWER));
                                    if(!mob.getScoreboardTags().contains("bigSunRoom")) {
                                        sun.addScoreboardTag("pvz_small_sun");
                                    } else {
                                        sun.addScoreboardTag("pvz_sun");
                                        PvzEntity.setPlantAttackCD(mob,0);
                                    }
                                }
                                if(attackCD >= 120 * 20) {
                                    mob.addScoreboardTag("bigSunRoom");
                                    PVZFunction.changePlant(mob,"大阳光菇",0.54f);
                                    PvzEntity.setPlantAttackCD(mob,0);
                                }
                                if(attackCD % 20 == 0) allowPlantToBeEat(mob);
                            }
                            case "pvz_nut" -> {
                                if(attackCD >= 20) {
                                    if(mob.getScoreboardTags().contains("deliciousRoom")) {
                                        allowPlantToBeEat(mob,8,2,8);
                                    }else {
                                        allowPlantToBeEat(mob);
                                    }
                                    PvzEntity.setPlantAttackCD(mob,0);
                                }
                            }
                            case "cherryBoom" -> {
                                if(attackCD >= 24) {
                                    for(Entity entity: mob.getNearbyEntities(8,6,8)) {
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
                                        for(Entity entity: mob.getNearbyEntities(2.5,2.5,2.5)) {
                                            if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                                enemy.damage(4*90,mob);
                                            }
                                        }

                                        mob.getWorld().spawnParticle(Particle.EXPLOSION,mob.getLocation(),30,2.5,2.5,2.5,0);
                                        mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);
                                        mob.setHealth(0);
                                    } else {
                                        if(mob.getScoreboardTags().contains("ready_boom")) continue;
                                        List<Entity> entities = new ArrayList<>();
                                        for(Entity entity: mob.getNearbyEntities(1.5,0.25,1.5)) {
                                            if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                                entities.add(enemy);
                                            }
                                        }
                                        allowPlantToBeEat(mob);
                                        if(entities.size() > 0) mob.addScoreboardTag("ready_boom");
                                        else PvzEntity.setPlantAttackCD(mob,0);
                                    }
                                }
                            }
                            case "icebergLettuce" -> {
                                if(attackCD >= 20) {
                                    for(Entity entity: mob.getNearbyEntities(3,3,3)) {
                                        if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                            enemy.damage(2,mob);
                                        }
                                    }
                                    mob.getWorld().spawnParticle(Particle.SNOWFLAKE,mob.getLocation(),50,3,3,3,0);
                                    mob.getWorld().playSound(mob.getLocation(),Sound.BLOCK_SNOW_BREAK,3,1);
                                    mob.setHealth(0);
                                } else {
                                    if(mob.getScoreboardTags().contains("ready_boom")) continue;
                                    List<Entity> entities = new ArrayList<>();
                                    for(Entity entity: mob.getNearbyEntities(1.5,0.25,1.5)) {
                                        if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                            entities.add(enemy);
                                        }
                                    }
                                    if(entities.size() > 0) mob.addScoreboardTag("ready_boom");
                                    else PvzEntity.setPlantAttackCD(mob,0);
                                }
                            }
                            case "iceRoom" -> {
                                if(attackCD < 20) continue;
                                for(Entity entity: mob.getNearbyEntities(60,20,60)) {
                                    if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                        enemy.damage(2,mob);
                                    }
                                }
                                mob.getWorld().spawnParticle(Particle.SNOWFLAKE,mob.getLocation(),300,15,4,15,0);
                                mob.getWorld().playSound(mob.getLocation(),Sound.BLOCK_SNOW_BREAK,3,1);
                                mob.setHealth(0);
                            }
                            case "doomRoom" -> {
                                if(attackCD < 24) continue;
                                for(Entity entity: mob.getNearbyEntities(12,1,12)) {
                                    if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                                        enemy.damage(4*90,mob);
                                    }
                                }
                                mob.getWorld().spawnParticle(Particle.EXPLOSION,mob.getLocation(),200,15,6,15,0);
                                mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,2,1);
                                mob.setHealth(0);
                            }
                            case "afraidRoom" -> {
                                if(attackCD % 20 == 0) {
                                    Entity nearestZombie = findNearestEnemy2(mob,6);
                                    if(nearestZombie != null && !mob.getScoreboardTags().contains("pvz_afraid")) {
                                        PVZFunction.changePlant(mob,"害怕的胆小菇",0.54f);
                                        mob.addScoreboardTag("pvz_afraid");
                                        continue;
                                    } else if(mob.getScoreboardTags().contains("pvz_afraid") && nearestZombie == null) {
                                        mob.removeScoreboardTag("pvz_afraid");
                                        PVZFunction.changePlant(mob,"胆小菇",0.54f);
                                    }
                                }
                                if(mob.getScoreboardTags().contains("pvz_afraid")) continue;
                                handleAfraidPuffShot(mob, target, attackCD, 28, this::shootPuff);
                            }
                            case "eaterFlower" -> {
                                if(!mob.getScoreboardTags().contains("Chewing")) {
                                    if(attackCD < 20) continue;
                                    LivingEntity nearestEnemy = findNearestEnemy2(mob,4.5);
                                    LivingEntity pointedEnemy = findNearestEnemy2(mob,20);
                                    mob.setTarget(pointedEnemy);
                                    PvzEntity.setPlantAttackCD(mob,0);

                                    if(nearestEnemy == null) continue;
                                    if(nearestEnemy.getScoreboardTags().contains("PoleZombie") && !nearestEnemy.getScoreboardTags().contains("Pole_Jumped"))
                                        continue;
                                    nearestEnemy.setHealth(0);
                                    mob.addScoreboardTag("Chewing");
                                    PVZFunction.changePlant(mob,"咀嚼的大嘴花",0.54f);
                                    allowPlantToBeEat(mob);
                                    mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,2,1.6f);
                                } else {
                                    if(attackCD % 20 == 0) {
                                        allowPlantToBeEat(mob);
                                        LivingEntity pointedEnemy = findNearestEnemy2(mob,20);
                                        mob.setTarget(pointedEnemy);
                                    }
                                    if(attackCD < 40 * 20) continue;
                                    PvzEntity.setPlantAttackCD(mob,0);
                                    PVZFunction.changePlant(mob,"大嘴花",0.54f);
                                    mob.removeScoreboardTag("Chewing");
                                }
                            }
                            case "bigPuffRoom" -> {
                                if(attackCD < 28) continue;
                                LivingEntity nearestEnemy = findNearestEnemy2(mob,16);
                                mob.setTarget(nearestEnemy);
                                boolean isShooting = false;
                                if(nearestEnemy != null) {
                                    for(Entity zombie : zombieList) {
                                        if(!(zombie instanceof LivingEntity enemy)) continue;
                                        if(Function.isEntityNearLine(mob.getLocation(),Function.getLocationInFront(mob,20),zombie,1)) {
                                            isShooting = true;
                                            enemy.damage(4,mob);
                                        }
                                    }
                                }
                                if(isShooting) {
                                    for(int i = 0; i < 5; i++) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                mob.getWorld().playSound(mob.getLocation(),Sound.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA,1,1.4f);
                                            }
                                        }.runTaskLater(QinKitPVPS.getPlugin(),i);
                                    }
                                    Function.showMagicParticle(mob.getLocation().clone().add(0,0.8,0),
                                            Function.getLocationInFront(mob,20).clone().add(0,0.8,0),
                                            Particle.WITCH, 5, 0.25f, 0.25f, 0.25f, 0.05f);
                                }
                                PvzEntity.setPlantAttackCD(mob,0);
                            }
                            case "graveEater" -> {
                                if(attackCD % 20 == 0) {
                                    allowPlantToBeEat(mob);
                                    mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_GENERIC_EAT,1.5f,0.6f);
                                    mob.getWorld().playSound(mob.getLocation(),Sound.BLOCK_STONE_BREAK,1.5f,1.6f);
                                }
                                if(attackCD < 90) continue;
                                for(Entity grave : mob.getNearbyEntities(1.5,3,1.5)) {
                                    if(grave.getScoreboardTags().contains("pvz_grave") && grave instanceof LivingEntity lv)
                                        lv.setHealth(0);
                                }
                                mob.setHealth(0);
                            }
                            case "dreamyRoom" -> {
                                if(attackCD % 20 == 0) allowPlantToBeEat(mob);
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
                if(!item.getScoreboardTags().contains("pvz_sun") && !item.getScoreboardTags().contains("pvz_small_sun")) continue;
                int cd = PvzEntity.getPlantAttackCD(item);
                PvzEntity.setPlantAttackCD(item,++cd);

                int sunPrice = 25;
                if(item.getScoreboardTags().contains("pvz_small_sun")) sunPrice = 15;
                if(cd >= 60) {
                    PvzRound.setTotalSun(PvzRound.getTotalSun() + item.getItemStack().getAmount() * sunPrice);
                    for(Player player : item.getWorld().getPlayers()) {
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + sunPrice + " 阳光: " + PvzRound.getTotalSun()));
                    }
                    item.remove();
                }
            }
        }


        //此处处理Pvz_Zombie的逻辑
        Set<Mob> roundZombies = new HashSet<>(PvzRound.getZombies());
        for(Mob m : roundZombies) {
            //僵尸实体逻辑处理
            if(PvzEntity.getPlantDisplays(m) != null) {
                for(Display d : PvzEntity.getPlantDisplays(m)) {
                    d.setRotation(m.getLocation().getYaw() + 90,0);
                }
            }
            //防止处理墓碑逻辑
            if(m.getScoreboardTags().contains("pvz_grave")) continue;
            //保证魅惑僵尸攻击其他僵尸
            if(m.getScoreboardTags().contains("pvz_dreamed")) setPlantTarget(m,null,60);
            //保证僵尸向着脑子攻击
            if(m.getTarget() == null || m.getTarget().isDead()) {
                //防止魅惑僵尸攻击
                if(!m.getScoreboardTags().contains("pvz_dreamed"))
                    m.setTarget(PvzRound.getBrain());
            }
            //以下代码针对僵尸逻辑
            processZombieLogic(m);
        }
        if(PvzRound.isRunning()) {
            if(PvzRound.allowDropSun() && ++sunFall == 20 * 7) {
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
        if(PvzRound.isRunning() && PvzRound.isWaveEnd() && PvzRound.getRemainZombies() == 0)
            PvzRound.endRound();

        // 达到一定刷新间隔之后重新构建敌人八叉树
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

    private void shootPuff(Mob mob, LivingEntity target) {
        if(target == null) return;
        Entity pea = generateBullet(mob,target,true,0.6);
        mob.getWorld().playSound(mob.getLocation(),Sound.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA,1,1.2f);
        pea.addScoreboardTag("puff_bullet");
        PVZFunction.summonPlant(pea,"蘑菇孢子",0.4f,true);
    }

    private void allowPlantToBeEat(Mob mob) {
        allowPlantToBeEat(mob,1.5,0.15,1.5);
    }
    private void allowPlantToBeEat(Mob mob, double rangeX, double rangeY, double rangeZ) {
        int attackCD = PvzEntity.getPlantAttackCD(mob);
        if(attackCD % 20 == 0) {
            for(Entity entity: mob.getNearbyEntities(rangeX,rangeY,rangeZ)) {
                if(isEnemy(mob,entity) && entity instanceof Mob enemy) {
                    Entity lastTarget = PvzEntity.getMobTarget(mob);
                    if(lastTarget == null || !lastTarget.getScoreboardTags().contains("pvz_nut")) {
                        if(enemy.getScoreboardTags().contains("pvz_dreamed")) continue;
                        enemy.setTarget(mob);
                        PvzEntity.setMobTarget(enemy,mob);
                    }
                }
            }
        }
    }

    private boolean isSupportedShooter(String tag) {
        return tag.equals("peaShooter") || tag.equals("icePeaShooter") ||
                tag.equals("doublePeaShooter") || tag.equals("machinePeaShooter") ||
                tag.equals("cabbagePitcher") || tag.equals("cornPitcher") ||
                tag.equals("melonPitcher") || tag.equals("sunFlower") ||
                tag.equals("pvz_nut") || tag.equals("potatoMine") ||
                tag.equals("cherryBoom") || tag.equals("icebergLettuce") ||
                tag.equals("eaterFlower") || tag.equals("puffRoom") ||
                tag.equals("sunRoom") || tag.equals("bigPuffRoom") ||
                tag.equals("iceRoom") || tag.equals("doomRoom") ||
                tag.equals("afraidRoom") || tag.equals("graveEater") ||
                tag.equals("deliciousRoom") || tag.equals("dreamyRoom");
    }

    private LivingEntity setPlantTarget(Mob mob,LivingEntity target, double distance) {
        LivingEntity newTarget = null;
        newTarget = findNearestEnemy2(mob,distance);
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
        if (MiniFunction.ignoreSomeEntities(mobTeam, target, entityTeam, target.equals(e), target.isDead()))
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

    private Entity generateBullet(Mob mob, LivingEntity target, Boolean isPea, double... height) {
        double bulletHeight = 1.25;
        if(height.length == 1) {
            bulletHeight = height[0];
        }
        Location fireLocation = mob.getLocation().clone().add(0, bulletHeight, 0);
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
            Vector vec = Function.calculateVelocity(fireLocation.toVector(),calculateFutureLocation(mob,target,20).toVector(),6,0.115);
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

    private Entity generateBullet(Mob mob, Location targetLocation, Boolean isPea, double... height) {
        double bulletHeight = 1.25;
        if(height.length == 1) {
            bulletHeight = height[0];
        }
        Location fireLocation = mob.getLocation().clone().add(0, bulletHeight, 0);
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
            Vector vec = Function.calculateVelocity(fireLocation.toVector(),targetLocation.toVector(),6,0.115);
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

    private Set<Entity> findZombiesNearLine(Entity mob, double maxDistance) {
        return enemyTree.findZombiesNearLine(mob.getLocation().clone().add(0,0.5,0),Function.getLocationInFront(mob,maxDistance));
    }

    private Location calculateFutureLocation(Entity plant, Entity target, int ticks) {
        ticks = ticks / 2;
        Location previousLocation = PvzEntity.getEntityLastLocation(target,plant);
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
            target = setPlantTarget(mob, target,56);
            shootAction.accept(mob, target);

            PvzEntity.setPlantAttackCD(mob, 0);
        }
    }

    private void handlePuffShot(Mob mob, LivingEntity target, int attackCD, int cooldown, BiConsumer<Mob, LivingEntity> shootAction) {
        if (attackCD >= cooldown) {
            target = setPlantTarget(mob, target,12);
            shootAction.accept(mob, target);

            PvzEntity.setPlantAttackCD(mob, 0);
        }
    }

    private void handleAfraidPuffShot(Mob mob, LivingEntity target, int attackCD, int cooldown, BiConsumer<Mob, LivingEntity> shootAction) {
        if (attackCD >= cooldown) {
            target = setPlantTarget(mob, target,56);
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
            target = setPlantTarget(mob, target,56);
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

    private boolean cantPlantHere(Location loc) {
        Location searchLoc = loc.clone().add(0,1,0);
        if(searchLoc.getWorld() == null) return false;
        for(Entity e : searchLoc.getWorld().getNearbyEntities(searchLoc,1.5,1.5,1.5)) {
            if(e.getScoreboardTags().contains("pvz_plant") && !PVZFunction.isBullet(e)) {
                return true;
            }
            if(e.getScoreboardTags().contains("pvz_grave")) {
                return true;
            }
        }
        return false;
    }

    private void processZombieLogic(Mob m) {
        //撑杆跳僵尸
        if(m.getScoreboardTags().contains("PoleZombie") && !m.getScoreboardTags().contains("Pole_Jumped")) {
            for(Entity e : m.getNearbyEntities(4,2,4)) {
                if(!e.getScoreboardTags().contains("pvz_plant") || PVZFunction.isBullet(e)) continue;

                m.setVelocity(new Vector(0,1,0).add(m.getLocation().getDirection().multiply(1.4).setY(0)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        m.addScoreboardTag("refresh_walk");
                    }
                }.runTaskLater(QinKitPVPS.getPlugin(),28);

                m.addScoreboardTag("Pole_Jumped");
                Objects.requireNonNull(m.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.135);
                PVZFunction.changePlant(m,"撑杆跳僵尸头发",0);
                break;
            }
        }
        //亡灵法师（召唤僵尸）僵尸
        if(m.getScoreboardTags().contains("summonZombie")) {
            int cd = PvzEntity.getPlantAttackCD(m);
            if (cd >= 16 * 20) {
                if(!PvzRound.isRunning()) return;
                PvzEntity.setPlantAttackCD(m,0);
                m.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,2 * 20,244,true,false));
                m.getWorld().playSound(m.getLocation(),Sound.ENTITY_EVOKER_PREPARE_SUMMON,5,1);
                for(Entity e : m.getNearbyEntities(8,3,8)) {
                    if(!e.getScoreboardTags().contains("pvz_grave")) continue;
                    for(int i = 0; i < Function.createRandom(1,6); i++) {
                        int chance = Function.createRandom(0,5);
                        Mob z;
                        if(chance < 2) z = SpawnZombie.spawnEntityByType(e.getLocation(), "骷髅亡灵");
                        else z = SpawnZombie.spawnEntityByType(e.getLocation(), "普通僵尸");
                        if(m.getScoreboardTags().contains("pvz_dreamed") && z != null) PVZFunction.zombieDreamfiy(m,z);
                    }
                    e.getWorld().spawnParticle(Particle.SOUL,e.getLocation().clone().add(0,1,0),40,1,1,1,0,null,true);
                }
            } else PvzEntity.setPlantAttackCD(m,++cd);
        }
        //死神（召唤坟墓）僵尸
        if(m.getScoreboardTags().contains("graveZombie")) {
            if(m.getScoreboardTags().contains("pvz_dreamed")) return;
            int cd = PvzEntity.getPlantAttackCD(m);
            if(cd >= 16 * 20) {
                PvzEntity.setPlantAttackCD(m,0);
                m.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,5 * 20,244,true,false));
                for(int i = 0; i < 2; i++) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(m.isDead()) return;
                            m.getWorld().playSound(m.getLocation(),Sound.ENTITY_SHULKER_AMBIENT,5f,1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(m.isDead()) return;
                                    int index = Function.createRandom(0,PvzRound.getMap().getPlantLocations().size());
                                    Location selection = PvzRound.getMap().getPlantLocations().get(index).clone().add(0.5,1,0.5);
                                    // 召唤骨头投掷物
                                    Entity bone = generateBullet(m, selection,false,1.15);
                                    PVZFunction.summonPlant(bone,"死神骨头",0.4f,false);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            World graveWorld = selection.getWorld();
                                            if(graveWorld == null) return;
                                            if(cantPlantHere(selection)) return;
                                            if(!PvzRound.isRunning()) return;
                                            int next = Function.createRandom(0,3);
                                            SpawnZombie.spawnEntityByType(selection,"墓碑" + next);
                                        }
                                    }.runTaskLater(QinKitPVPS.getPlugin(),35);
                                }
                            }.runTaskLater(QinKitPVPS.getPlugin(),20);
                        }
                    }.runTaskLater(QinKitPVPS.getPlugin(),50*i);
                }
            } else PvzEntity.setPlantAttackCD(m,++cd);
        }
    }
}
