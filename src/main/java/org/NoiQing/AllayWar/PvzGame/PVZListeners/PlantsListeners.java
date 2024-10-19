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
            plant.getWorld().playSound(plant.getLocation(), Sound.ENTITY_GENERIC_EAT,3,1);
        }
    }

    @EventHandler
    public void onPvzZombieDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if(entity.getScoreboardTags().contains("pvz_zombie") && entity instanceof Mob m) {
            PvzRound.removeZombieFromRound(m);
        }
        if(entity.getScoreboardTags().contains("pvz_brain")) {
            PvzRound.gameOver();
        }

    }

    @EventHandler
    public void onPvzZombieHurt(EntityDamageEvent e) {
        Entity zombie = e.getEntity();
        if(zombie.getScoreboardTags().contains("pvz_zombie") && zombie instanceof Mob m) {
            double healthBar = (m.getHealth() - e.getFinalDamage()) / Objects.requireNonNull(m.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            int insertLoc = (int) (healthBar * 20) + 2;
            StringBuilder stringBuilder = new StringBuilder("§c||||||||||||||||||||");
            stringBuilder.insert(insertLoc,"§7");
            zombie.setCustomName(stringBuilder.toString());
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
        if(e.getItem().getScoreboardTags().contains("pvz_sun") && e.getEntity() instanceof Player p) {
            int sun = PvzEntity.getPlayerSun(p);
            sun += 25 * e.getItem().getItemStack().getAmount();
            PvzEntity.setPlayerSun(p,sun);
            p.playSound(p,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + "25" + " 阳光: " + sun));
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
        } else {
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
    private boolean notHaveEnoughSun(Player p, int sunCost) {
        if(p.getGameMode().equals(GameMode.CREATIVE)) return false;
        int playerSun = PvzEntity.getPlayerSun(p);
        if(playerSun < sunCost) {
            Function.sendPlayerSystemMessage(p,"你没有足够的阳光！");
            return true;
        }
        PvzEntity.setPlayerSun(p,playerSun - sunCost);
        return false;
    }
    private void summonPlant(Player p, String plantType, Location loc) {
        switch (plantType) {
            case "豌豆射手" -> {
                if(notHaveEnoughSun(p,100)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("peaShooter");
                z.setCustomName("豌豆射手");
                PVZFunction.summonPlant(z,"豌豆射手",0.54f,false);
            }
            case "向日葵" -> {
                if(notHaveEnoughSun(p,50)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("sunFlower");
                z.setCustomName("向日葵");
                PVZFunction.summonPlant(z,"向日葵",0.54f,false);
                PvzEntity.setPlantAttackCD(z,16);
            }
            case "樱桃炸弹" -> {
                if(notHaveEnoughSun(p,150)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cherryBoom");
                z.setCustomName("樱桃炸弹");
                PVZFunction.summonPlant(z,"樱桃炸弹",0.54f,false);
            }
            case "坚果墙" -> {
                if(notHaveEnoughSun(p,50)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("wallNut");
                z.addScoreboardTag("pvz_nut");
                z.setCustomName("坚果墙");
                Function.setEntityHealth(z,267);
                PVZFunction.summonPlant(z,"坚果墙",0.54f,false);
            }
            case "高坚果" -> {
                if(notHaveEnoughSun(p,125)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("tallWallNut");
                z.addScoreboardTag("pvz_nut");
                z.setCustomName("高坚果");
                Function.setEntityHealth(z,267 * 2);
                PVZFunction.summonPlant(z,"高坚果",0.54f,false);
            }
            case "土豆地雷" -> {
                if(notHaveEnoughSun(p,25)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("potatoMine");
                z.setCustomName("土豆地雷");
                PVZFunction.summonPlant(z,"土豆地雷",0.54f,false);
            }
            case "大嘴花" -> {

            }
            case "寒冰射手" -> {
                if(notHaveEnoughSun(p,175)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("icePeaShooter");
                z.setCustomName("寒冰射手");
                PVZFunction.summonPlant(z,"寒冰射手",0.54f,false);
            }
            case "双发射手" -> {
                if(notHaveEnoughSun(p,200)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("doublePeaShooter");
                z.setCustomName("双发射手");
                PVZFunction.summonPlant(z,"双发射手",0.54f,false);
            }
            case "卷心菜投手" -> {
                if(notHaveEnoughSun(p,100)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cabbagePitcher");
                z.setCustomName("卷心菜投手");
                PVZFunction.summonPlant(z,"卷心菜投手",0.54f,false);
            }

            case "机枪射手" -> {
                if(notHaveEnoughSun(p,450)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("machinePeaShooter");
                z.setCustomName("机枪射手");
                PVZFunction.summonPlant(z,"机枪射手",0.54f,false);
            }
            case "玉米投手" -> {
                if(notHaveEnoughSun(p,100)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cornPitcher");
                z.setCustomName("玉米投手");
                PVZFunction.summonPlant(z,"玉米投手",0.54f,false);
            }
            case "西瓜投手" -> {
                if(notHaveEnoughSun(p,300)) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("melonPitcher");
                z.setCustomName("西瓜投手");
                PVZFunction.summonPlant(z,"西瓜投手",0.54f,false);
            }

        }
    }
}
