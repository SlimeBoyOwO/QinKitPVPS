package org.NoiQing.AllayWar.PvzGame.PVZListeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.AllayWar.PvzGame.Game.PvzRound;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PVZFunction;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlantsListeners implements Listener {

    //pvz_plant tag 用于标记植物
    @EventHandler
    public void onPlantDeath(EntityDeathEvent e) {
        if(e.getEntity().getScoreboardTags().contains("pvz_plant")) {
            Entity plant = e.getEntity();
            plant.getWorld().playSound(plant.getLocation(), Sound.ENTITY_FOX_EAT,3,1);
            plant.getWorld().playSound(plant.getLocation(), Sound.ENTITY_DOLPHIN_EAT,3,1);
            PvzEntity.getPlantDisplays(plant).forEach(Entity::remove);
            PvzEntity.removePlantDisplays(plant);

        }
    }
    @EventHandler
    public void onPlantDamaged(EntityDamageEvent e) {
        if(e.getEntity().getScoreboardTags().contains("pvz_plant")) {
            Entity plant = e.getEntity();
            if(e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
                e.setCancelled(true);
                return;
            }
            plant.getWorld().playSound(plant.getLocation(), Sound.ENTITY_GENERIC_EAT,3,1);
        }
    }

    @EventHandler
    public void onPvzZombieDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if(entity.getScoreboardTags().contains("pvz_zombie") && entity instanceof Mob m) {
            PvzRound.removeZombieFromRound(m);
            PvzEntity.getPlantDisplays(m).forEach(Entity::remove);
            PvzEntity.removePlantDisplays(m);
            PvzEntity.getEffectDisplays(m).forEach(Entity::remove);
            PvzEntity.removeEffectDisplays(m);
        }
        if(entity.getScoreboardTags().contains("pvz_brain")) {
            PvzRound.gameOver();
        }

    }


    @EventHandler
    public void onPvzZombieHurt(EntityDamageEvent e) {
        Entity zombie = e.getEntity();
        if(zombie.getScoreboardTags().contains("pvz_zombie") && zombie instanceof Mob m) {
            double damagedHealth = m.getHealth() - e.getFinalDamage();
            damagedHealth = damagedHealth < 0 ? 0 : damagedHealth;
            double healthBar = damagedHealth / Objects.requireNonNull(m.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            int insertLoc = (int) (healthBar * 20) + 4;
            StringBuilder stringBuilder = new StringBuilder("§c§l||||||||||||||||||||");
            stringBuilder.insert(insertLoc,"§7§l");
            Entity topEntity;
            if(zombie.getPassengers().isEmpty()) topEntity = zombie;
            else topEntity = zombie.getPassengers().getLast();
            topEntity.setCustomName(stringBuilder.toString());
            topEntity.setCustomNameVisible(true);

            if(damagedHealth <= 30 && m.getScoreboardTags().contains("pvz_armed")) {
                PvzEntity.getPlantDisplays(m).forEach(Entity::remove);
                PvzEntity.removePlantDisplays(m);
            }
        }
    }

    @EventHandler
    public void onPvzZombieHurtByPlant(EntityDamageByEntityEvent e) {
        Entity zombie = e.getEntity();
        Entity plant = e.getDamager();
        if(!plant.getScoreboardTags().contains("pvz_plant")) return;
        if(!(zombie instanceof LivingEntity lv)) return;

        if(plant.getScoreboardTags().contains("icebergLettuce")) {
            lv.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 18 * 20, 2, true, false));
            lv.addScoreboardTag("pvz_frozen");
            lv.setAI(false);
            PVZFunction.summonEffect(zombie,"冰冻",1.8f);
            PvzEntity.setEffectDuration(zombie,8*20);
            if(lv.getScoreboardTags().contains("torchZombie")) {
                PVZFunction.changePlant(lv,"熄灭的火把",0);
                lv.addScoreboardTag("torch_died");
            }
        }

        if(plant.getScoreboardTags().contains("icePeaShooter")) {
            if(zombie.getScoreboardTags().contains("torchZombie") && !zombie.getScoreboardTags().contains("torch_died")) {
                PVZFunction.changePlant(lv,"熄灭的火把",0);
                lv.addScoreboardTag("torch_died");
            }
        }
    }

    @EventHandler
    public void onPvzZombieAttackPlant(EntityDamageByEntityEvent e) {
        Entity zombie = e.getDamager();
        Entity plant = e.getEntity();
        if(!zombie.getScoreboardTags().contains("pvz_zombie")) return;

        if(zombie.getScoreboardTags().contains("torchZombie") && !zombie.getScoreboardTags().contains("torch_died")) {
            if(!plant.getScoreboardTags().contains("pvz_plant")) return;
            if(plant instanceof LivingEntity lv) {
                lv.setHealth(0);
                lv.getWorld().spawnParticle(Particle.FLAME,lv.getLocation().add(0,0.5,0),50,0.2,0.2,0.2,0.05);
            }
        }
    }

    @EventHandler
    public void onPvzZombieFire(EntityCombustEvent e) {
        Entity zombie = e.getEntity();
        if(zombie.getScoreboardTags().contains("pvz_zombie") && zombie instanceof Mob m) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onAllayHealth(EntityRegainHealthEvent e) {
        if(e.getEntity() instanceof Allay a && a.getScoreboardTags().contains("pvz_plant")) e.setCancelled(true);
    }


    @EventHandler
    public void onPlantBulletHit(ProjectileHitEvent e) {
        Entity bullet = e.getEntity();
        Entity preHitEntity = e.getHitEntity();

        if(!PVZFunction.isBullet(bullet)) return;
        if(e.getHitBlock() != null) {
            e.setCancelled(true);
            PvzEntity.getPlantDisplays(bullet).forEach(Entity::remove);
            PvzEntity.removePlantDisplays(bullet);
            bullet.remove();
            return;
        }

        if ((preHitEntity != null && (preHitEntity.getScoreboardTags().contains("pvz_plant") || preHitEntity.getScoreboardTags().contains("pvz_brain"))) || preHitEntity instanceof Player) {
            e.setCancelled(true);
            return;
        }

        //子弹碰撞则处理消失逻辑
        PvzEntity.getPlantDisplays(bullet).forEach(Entity::remove);
        PvzEntity.removePlantDisplays(bullet);
        e.setCancelled(true);
        bullet.remove();

        if (!(preHitEntity instanceof LivingEntity hitEntity)) return;

        Entity shooter = PvzEntity.getBulletOwner(bullet);
        if(bullet.getScoreboardTags().contains("pea_bullet")) {
            hitEntity.damage(4, shooter);
            bullet.getWorld().spawnParticle(Particle.ITEM_SLIME,bullet.getLocation(),10,0,0,0,0,null,false);
        } else if(bullet.getScoreboardTags().contains("ice_pea_bullet")) {
            hitEntity.damage(4, shooter);
            bullet.getWorld().spawnParticle(Particle.ITEM_SNOWBALL,bullet.getLocation(),10,0,0,0,0,null,false);
            hitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,10*20,1,true,false));
        } else if(bullet.getScoreboardTags().contains("cabbage_bullet")) {
            hitEntity.damage(8, shooter);
            bullet.getWorld().spawnParticle(Particle.ITEM_SLIME,bullet.getLocation(),10,0,0,0,0,null,false);
        } else if(bullet.getScoreboardTags().contains("corn_bullet")) {
            hitEntity.damage(4, shooter);
            bullet.getWorld().spawnParticle(Particle.ITEM,bullet.getLocation(),10,0.5,0,0.5,0,new ItemStack(Material.GOLD_INGOT),false);
        } else if(bullet.getScoreboardTags().contains("melon_bullet")) {
            hitEntity.damage(16, shooter);
            bullet.getWorld().spawnParticle(Particle.ITEM,bullet.getLocation(),20,0.5,0.25,0.5,0,new ItemStack(Material.MELON_SLICE),false);
        }
    }


    @EventHandler
    public void onPlayerPickUpSun(EntityPickupItemEvent e) {
        if(e.getItem().getScoreboardTags().contains("pvz_sun") && e.getEntity() instanceof Player) {
            int sun = PvzRound.getTotalSun();
            sun += 25 * e.getItem().getItemStack().getAmount();
            PvzRound.setTotalSun(sun);
            for(Player player : e.getItem().getWorld().getPlayers()) {
                player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + "25" + " 阳光: " + sun));
            }
            e.getItem().remove();
            e.setCancelled(true);
        }
    }

    //怪物特定标记目标设定
    @EventHandler
    public void onAttackTowerOnlyEntityTarget(EntityTargetEvent e) {
        Entity entity = e.getEntity();
        Entity target = e.getTarget();
        if (entity.getScoreboardTags().contains("pvz_plant")) {
            //筛选
            QinTeam team = QinTeams.getEntityTeam(entity);
            QinTeam targetTeam = QinTeams.getEntityTeam(target);
            if (target instanceof Player p && p.isInvulnerable()) {
                e.setCancelled(true);
                return;
            }
            if (team == null) return;
            if (team.getTeamName().equals("植物") && target instanceof Player) return;
            if (team.equals(targetTeam)) {
                e.setCancelled(true);
            }
        } else if(entity.getScoreboardTags().contains("pvz_zombie")){
            if(e.getReason().equals(EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY) ||
                    e.getReason().equals(EntityTargetEvent.TargetReason.CLOSEST_PLAYER) ||
                    e.getReason().equals(EntityTargetEvent.TargetReason.FORGOT_TARGET)) {
                e.setCancelled(true);
                return;
            }

            if(entity.getScoreboardTags().contains("PoleZombie")) {
                if(e.getTarget() != null && !e.getTarget().equals(PvzRound.getBrain())) {
                    e.setCancelled(true); return;
                }
            }
            if(target != null && entity instanceof Mob mob) {
                Entity lastTarget = PvzEntity.getMobTarget(mob);
                if(lastTarget != null && lastTarget.getScoreboardTags().contains("pvz_nut") && !lastTarget.isDead()) {
                    e.setCancelled(true); return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAllay(PlayerInteractEntityEvent e) {
        if(e.getRightClicked().getScoreboardTags().contains("pvz_plant"))
            e.setCancelled(true);
    }


    @EventHandler
    public void onPlantBulletKnockBack(EntityKnockbackByEntityEvent e) {
        if(e.getSourceEntity().getScoreboardTags().contains("pvz_plant")) e.setCancelled(true);
    }

    /*
    @EventHandler
    public void onPlantBulletKnockBack(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity victim = e.getEntity();
        if(damager.getScoreboardTags().contains("pvz_plant")) {
            e.setCancelled(true);
            if(e.getDamage() < 0.2) return;
            if(victim instanceof LivingEntity lv)
                lv.damage(e.getFinalDamage());
        }
    }
     */


    @EventHandler
    public void onPlacePlant(PlayerInteractEvent e) {
        if(Function.cannotPlaceEntity(e)) return;

        assert e.getClickedBlock() != null;
        assert e.getItem() != null;

        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).startsWith("植物 - ")) return;
        String plantType = Function.getMainHandItemNameWithoutColor(p).substring(5);
        summonPlant(p,plantType,e.getClickedBlock().getLocation().clone().add(0,1,0));
        e.setCancelled(true);
    }

    private <T extends Entity> T spawnPlantCore(Player p, Class<T> entityClass, Location loc) {
        T e = p.getWorld().spawn(loc,entityClass);
        QinTeam playerTeam = QinTeams.getEntityTeam(p);
        if(playerTeam != null) {
            playerTeam.addTeamEntities(e);
        } else {
            QinTeam defaultPlantTeam = QinTeams.getQinTeamByName("植物");
            if(defaultPlantTeam != null) defaultPlantTeam.addTeamEntities(e);
        }
        e.addScoreboardTag("pvz_plant");

        return e;
    }
    private boolean notHaveEnoughSun(Player p, int sunCost, String plant) {
        if(p.getGameMode().equals(GameMode.CREATIVE)) {
            p.getWorld().playSound(p.getLocation(),Sound.BLOCK_GRASS_PLACE,1,1);
            return false;
        }
        if(!PvzEntity.ifPlayerPlantPassCoolDownTime(p,plant)) return true;
        int playerSun = PvzRound.getTotalSun();
        if(playerSun < sunCost) {
            Function.sendPlayerSystemMessage(p,"你没有足够的阳光！");
            return true;
        }
        playerSun -= sunCost;
        PvzRound.setTotalSun(playerSun);
        for(Player player :p.getWorld().getPlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e阳光: " + playerSun));
        }
        p.getWorld().playSound(p.getLocation(),Sound.BLOCK_GRASS_PLACE,1,1);
        return false;
    }
    private void summonPlant(Player p, String plantType, Location loc) {
        if(!p.getGameMode().equals(GameMode.CREATIVE)) {
            if(!loc.clone().add(0,-1,0).getBlock().getType().equals(Material.GREEN_CONCRETE)) {
                Function.sendPlayerSystemMessage(p,"请把植物放在指定地点！");
                return;
            }
        }
        switch (plantType) {
            case "豌豆射手" -> {
                if(notHaveEnoughSun(p,100,"豌豆射手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("peaShooter");
                z.setCustomName("豌豆射手");
                PVZFunction.summonPlant(z,"豌豆射手",0.54f,false);
                setAllPlayerCD(p,"豌豆射手",7.5);
            }
            case "向日葵" -> {
                if(notHaveEnoughSun(p,50,"向日葵")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("sunFlower");
                z.setCustomName("向日葵");
                PVZFunction.summonPlant(z,"向日葵",0.54f,false);
                PvzEntity.setPlantAttackCD(z,12 * 20);
                setAllPlayerCD(p,"向日葵",7.5);
            }
            case "樱桃炸弹" -> {
                if(notHaveEnoughSun(p,150,"樱桃炸弹")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cherryBoom");
                z.setCustomName("樱桃炸弹");
                PVZFunction.summonPlant(z,"樱桃炸弹",0.54f,false);
                setAllPlayerCD(p,"樱桃炸弹",50);
            }
            case "坚果墙" -> {
                if(notHaveEnoughSun(p,50,"坚果墙")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("wallNut");
                z.addScoreboardTag("pvz_nut");
                z.setCustomName("坚果墙");
                Function.setEntityHealth(z,267);
                PVZFunction.summonPlant(z,"坚果墙",0.54f,false);
                setAllPlayerCD(p,"坚果墙",30);
            }
            case "高坚果" -> {
                if(notHaveEnoughSun(p,125,"高坚果")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("tallWallNut");
                z.addScoreboardTag("pvz_nut");
                z.setCustomName("高坚果");
                Function.setEntityHealth(z,267 * 2);
                PVZFunction.summonPlant(z,"高坚果",0.54f,false);
                setAllPlayerCD(p,"高坚果",30);
            }
            case "土豆地雷" -> {
                if(notHaveEnoughSun(p,25,"土豆地雷")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,-0.55,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("potatoMine");
                z.setCustomName("土豆地雷");
                PVZFunction.summonPlant(z,"土豆地雷",0.54f,false);
                setAllPlayerCD(p,"土豆地雷",30);
            }
            case "大嘴花" -> {

            }
            case "寒冰射手" -> {
                if(notHaveEnoughSun(p,175,"寒冰射手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("icePeaShooter");
                z.setCustomName("寒冰射手");
                PVZFunction.summonPlant(z,"寒冰射手",0.54f,false);
                setAllPlayerCD(p,"寒冰射手",7.5);
            }
            case "双发射手" -> {
                if(notHaveEnoughSun(p,200,"双发射手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("doublePeaShooter");
                z.setCustomName("双发射手");
                PVZFunction.summonPlant(z,"双发射手",0.54f,false);
                setAllPlayerCD(p,"双发射手",7.5);
            }
            case "卷心菜投手" -> {
                if(notHaveEnoughSun(p,100,"卷心菜投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cabbagePitcher");
                z.setCustomName("卷心菜投手");
                PVZFunction.summonPlant(z,"卷心菜投手",0.54f,false);
                setAllPlayerCD(p,"卷心菜投手",7.5);
            }

            case "机枪射手" -> {
                if(notHaveEnoughSun(p,450,"机枪投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("machinePeaShooter");
                z.setCustomName("机枪射手");
                PVZFunction.summonPlant(z,"机枪射手",0.54f,false);
                setAllPlayerCD(p,"机枪射手",50);
            }
            case "玉米投手" -> {
                if(notHaveEnoughSun(p,100,"玉米投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cornPitcher");
                z.setCustomName("玉米投手");
                PVZFunction.summonPlant(z,"玉米投手",0.54f,false);
                setAllPlayerCD(p,"玉米投手",7.5);
            }
            case "西瓜投手" -> {
                if(notHaveEnoughSun(p,300,"西瓜投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("melonPitcher");
                z.setCustomName("西瓜投手");
                PVZFunction.summonPlant(z,"西瓜投手",0.54f,false);
                setAllPlayerCD(p,"西瓜投手",7.5);
            }
            case "冰冻生菜" -> {
                if(notHaveEnoughSun(p,0,"冰冻生菜")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("icebergLettuce");
                z.setCustomName("冰冻生菜");
                PVZFunction.summonPlant(z,"冰冻生菜",0.54f,false);
                setAllPlayerCD(p,"冰冻生菜",22.5);
            }
        }
    }

    private void setAllPlayerCD(Player p, String plant, double cooldownTime) {
        Material type = Function.getMainHandItem(p).getType();
        for(Player player : p.getWorld().getPlayers()) {
            if(!player.getGameMode().equals(GameMode.CREATIVE))
                PvzEntity.setPlayerPlantCoolDownTime(player,plant,cooldownTime,type);
        }
    }
}
