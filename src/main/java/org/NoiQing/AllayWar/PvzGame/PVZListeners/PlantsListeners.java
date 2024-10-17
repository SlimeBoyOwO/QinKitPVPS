package org.NoiQing.AllayWar.PvzGame.PVZListeners;

import org.NoiQing.AllayWar.PvzGame.PVZUtils.PVZFunction;
import org.NoiQing.AllayWar.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.AllayWar.PvzGame.PvzRunnable.PvzGameRunnable;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlantsListeners implements Listener {
    private PvzGameRunnable runnableCallback;
    public void setRunnable(PvzGameRunnable runnable) {
        runnableCallback = runnable;
    }

    //pvz_plant tag 用于标记植物
    @EventHandler
    public void onPlantDeath(EntityDeathEvent e) {
//        runnableCallback.onZombieDie(e);
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

    //怪物特定标记目标设定
    @EventHandler
    public void onAttackTowerOnlyEntityTarget(EntityTargetEvent e) {
        Entity entity = e.getEntity();
        if (entity.getScoreboardTags().contains("pvz_plant")) {
            Entity target = e.getTarget();
//            Bukkit.broadcastMessage(e.getReason().toString());

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


        }
    }

    @EventHandler
    public void onPlantBulletKnockBack(EntityKnockbackByEntityEvent e) {
        if(e.getSourceEntity().getScoreboardTags().contains("pvz_plant")) e.setCancelled(true);
    }

    @EventHandler
    public void onPlacePlant(PlayerInteractEvent e) {
        if(Function.cannotPlaceEntity(e)) return;
        assert e.getClickedBlock() != null;
        assert e.getItem() != null;

        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).startsWith("植物 - ")) return;
        String plantType = Function.getMainHandItemNameWithoutColor(p).substring(5);
        summonPlant(p,plantType,e.getClickedBlock().getLocation().clone().add(0,1,0));
        e.getItem().setAmount(e.getItem().getAmount() - 1);
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

    private void summonPlant(Player p, String plantType, Location loc) {
        switch (plantType) {
            case "豌豆射手" -> {
                Zombie z = spawnPlantCore(p, Zombie.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("peaShooter");
                z.setCustomName("豌豆射手");
                PVZFunction.summonPlant(z,"豌豆射手",1.3f,false);
            }
            case "向日葵" -> {

            }
            case "樱桃炸弹" -> {

            }
            case "坚果墙" -> {

            }
            case "土豆地雷" -> {

            }
            case "大嘴花" -> {

            }
            case "寒冰射手" -> {
                Zombie z = spawnPlantCore(p, Zombie.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("icePeaShooter");
                z.setCustomName("寒冰射手");
                PVZFunction.summonPlant(z,"寒冰射手",1.3f,false);
            }
            case "双发射手" -> {
                Zombie z = spawnPlantCore(p, Zombie.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("doublePeaShooter");
                z.setCustomName("双发射手");
                PVZFunction.summonPlant(z,"双发射手",1.3f,false);
            }
            case "卷心菜投手" -> {
                Zombie z = spawnPlantCore(p, Zombie.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cabbagePitcher");
                z.setCustomName("卷心菜投手");
                PVZFunction.summonPlant(z,"卷心菜投手",1.3f,false);
            }
        }
    }
}
