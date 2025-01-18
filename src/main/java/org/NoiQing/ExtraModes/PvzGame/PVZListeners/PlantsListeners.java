package org.NoiQing.ExtraModes.PvzGame.PVZListeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.PVZFunction;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.SpawnPlant;
import org.NoiQing.QinKitPVPS;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PlantsListeners implements Listener {

    //pvz_plant tag 用于标记植物
    @EventHandler
    public void onPlantDeath(EntityDeathEvent e) {
        Entity plant = e.getEntity();
        if(plant.getScoreboardTags().contains("pvz_plant")) {
            plant.getWorld().playSound(plant.getLocation(), Sound.ENTITY_FOX_EAT,3,1);
            plant.getWorld().playSound(plant.getLocation(), Sound.ENTITY_DOLPHIN_EAT,3,1);
            PvzEntity.getPlantDisplays(plant).forEach(Entity::remove);
            PvzEntity.removePlantDisplays(plant);
            if(plant.getScoreboardTags().contains("dreamyRoom")) {
                final int max = 3;
                int times = 0;
                for(Entity zombie : plant.getNearbyEntities(2,1,2)) {
                    if(!zombie.getScoreboardTags().contains("pvz_zombie") || Function.hasTag(zombie,"pvz_dreamed")) continue;
                    PVZFunction.zombieDreamfiy(plant, zombie);
                    if(times++ >= max) break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInRoundDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!PvzRound.isRunning()) return;
        if(!(entity instanceof Player player)) return;
        if(!player.getWorld().equals(PvzRound.getBrain().getWorld())) return;
        //防止玩家直接死亡
        if(player.getHealth() - e.getFinalDamage() < 0) {
            e.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage("§e☠ §b>> §c§l " + player.getName() + " §7的脑子被吃掉了~");
            player.sendTitle("§c你被吃掉了脑子~","§a10s §b后复活",10,60,10);
            player.addScoreboardTag("pvz_dead");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!player.getScoreboardTags().contains("pvz_dead")) return;
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(PvzRound.getMap().getCenterLocation());
                    player.setHealth(Function.getPlayerMaxHealth(player));
                    PvzEntity.setPlayerMoney(player, (int) (PvzEntity.getPlayerMoney(player) * 0.25));
                    player.removeScoreboardTag("pvz_dead");
                }
            }.runTaskLater(QinKitPVPS.getPlugin(),10 * 20);
        }
    }
    @EventHandler
    public void onPlayerInRoundDamageByOthers(EntityDamageByEntityEvent e) {
        if(!PvzRound.isRunning()) return;
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if(entity instanceof Player player && damager instanceof Player damagerPlayer) {
            if(!player.getWorld().equals(PvzRound.getBrain().getWorld())) return;
            e.setCancelled(true);
            return;
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
            if(m.getScoreboardTags().contains("pvz_grave") || m.getScoreboardTags().contains("pvz_dreamed")) {
                PvzRound.minusZombieOffSet();
            }
            removeAllPlantDisplays(m);
            Player killer = m.getKiller();
            int chance = Function.createRandom(0,100);
            if(killer != null) chance = chance / 2;
            int money = PvzEntity.getPlayerMoney(killer);
            int addMoney = 0;
            if(chance < 1) addMoney = 100;
            else if(chance < 6) addMoney = 10;
            else if(chance < 47) addMoney = 1;
            int addedMoney = money + addMoney;

            if(addedMoney >= 0) {
                if(killer != null) {
                    PvzEntity.setPlayerMoney(killer, addedMoney);
                    killer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + addMoney + " 钱钱: " + addedMoney));
                } else {
                    if(m.getWorld().getPlayers().size() > 0) {
                        List<Player> moneyPlayers = new ArrayList<>();
                        for(Player checkPlayer : m.getWorld().getPlayers()) {
                            if(checkPlayer.getGameMode().equals(GameMode.ADVENTURE) || checkPlayer.getGameMode().equals(GameMode.ADVENTURE))
                                moneyPlayers.add(checkPlayer);
                        }
                        if(moneyPlayers.size() > 0) {
                            Player p = moneyPlayers.get(Function.createRandom(0,moneyPlayers.size()));
                            PvzEntity.setPlayerMoney(p, PvzEntity.getPlayerMoney(p) + addMoney);
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + addMoney + " 钱钱: " + addedMoney));
                        }
                    }
                }
            }
        }
        if(entity.getScoreboardTags().contains("pvz_brain")) {
            PvzRound.gameOver();
        }

    }

    private void removeAllPlantDisplays(Mob m) {
        PvzRound.removeZombieFromRound(m);
        PvzEntity.getPlantDisplays(m).forEach(Entity::remove);
        PvzEntity.removePlantDisplays(m);
        PvzEntity.getEffectDisplays(m).forEach(Entity::remove);
        PvzEntity.removeEffectDisplays(m);
        PvzEntity.getExtraDisplays(m).forEach(Entity::remove);
        PvzEntity.removeExtraDisplays(m);
    }


    @EventHandler
    public void onPvzZombieHurt(EntityDamageByEntityEvent e) {
        Entity zombie = e.getEntity();
        Entity damager = e.getDamager();
        if(zombie.getScoreboardTags().contains("pvz_zombie") && zombie instanceof Mob m) {

            if(damager.getScoreboardTags().contains("pvz_dreamed"))
                m.setTarget((LivingEntity) damager);
            if(damager.equals(zombie)) {
                e.setCancelled(true); return;
            }

            //血量设置
            String originalName = Function.getTopEntity(zombie).getCustomName();

            double damagedHealth = m.getHealth() - e.getFinalDamage();
            damagedHealth = damagedHealth < 0 ? 0 : damagedHealth;
            double healthBar = damagedHealth / Objects.requireNonNull(m.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            double extraHealth = PvzEntity.getZombieExtraHealth(m);
            double damagedExtraHealth = extraHealth - e.getFinalDamage();
            StringBuilder stringBuilder = new StringBuilder("§c§l||||||||||||||||||||");
            if(damagedExtraHealth > 0) {
                double extraHealthBar = damagedExtraHealth / PvzEntity.getZombieMaxExtraHealth(zombie);
                int extraInsertLoc = (int) (extraHealthBar * 10) + 20;
                int healthInsertLoc = (int) (healthBar * 10) + 4;
                stringBuilder = new StringBuilder("§c§l||||||||||§6§l||||||||||");
                if(originalName != null && originalName.startsWith("§b") || (m.getPotionEffect(PotionEffectType.SLOWNESS) != null && Objects.requireNonNull(m.getPotionEffect(PotionEffectType.SLOWNESS)).getAmplifier() < 200)) {
                    stringBuilder.replace(0,2,"§b");
                    stringBuilder.replace(14,16,"§3");
                }
                stringBuilder.insert(healthInsertLoc,"§7§l");
                stringBuilder.insert(extraInsertLoc,"§7§l");
            } else {
                int healthInsertLoc = (int) (healthBar * 20) + 4;
                if(originalName != null && originalName.startsWith("§b") || (m.getPotionEffect(PotionEffectType.SLOWNESS) != null && Objects.requireNonNull(m.getPotionEffect(PotionEffectType.SLOWNESS)).getAmplifier() < 200))
                    stringBuilder.replace(0,2,"§b");
                stringBuilder.insert(healthInsertLoc,"§7§l");
            }

            //有防具的僵尸的特殊处理
            if(damagedHealth <= 30 && m.getScoreboardTags().contains("pvz_armed")) {
                if(m.getScoreboardTags().contains("rugbyZombie"))
                    PVZFunction.changePlant(m,"橄榄球僵尸背心",0f);
                else {
                    PvzEntity.getPlantDisplays(m).forEach(Entity::remove);
                    PvzEntity.removePlantDisplays(m);
                }
            }

            // TODO 加上对具有二类防具僵尸的伤害判定
            // 这里表明僵尸拥有二类防具
            if(PvzEntity.getZombieMaxExtraHealth(zombie) > 1) {
                PvzEntity.setZombieExtraHealth(m,damagedExtraHealth);
                // 假如实体还没清除，则执行清除逻辑
                if(PvzEntity.getExtraDisplays(m).isEmpty()) return;
                if(damagedExtraHealth < 0) {
                    PvzEntity.getExtraDisplays(m).forEach(Entity::remove);
                    PvzEntity.removeExtraDisplays(m);
                    PvzEntity.setZombieMaxExtraHealth(zombie,0);
                } else {
                    if(damager.getScoreboardTags().contains("pvz_piercing") || damager.getScoreboardTags().contains("pvz_freeze")) {
                        int healthInsertLoc = (int) (healthBar * 10) + 4;
                        stringBuilder.insert(healthInsertLoc,"§7§l");
                    } else e.setCancelled(true);
                }
            }

            Entity topEntity = Function.getTopEntity(zombie);
            topEntity.setCustomName(stringBuilder.toString());
            topEntity.setCustomNameVisible(true);

            if(m.getScoreboardTags().contains("paperZombie") && !m.getScoreboardTags().contains("angry")) {
                if(damagedExtraHealth < 0) {
                    m.addScoreboardTag("angry");
                    m.setAI(false);
                    m.getWorld().playSound(m.getLocation(),Sound.ENTITY_VILLAGER_TRADE,3,1);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(m.isDead()) return;
                            m.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,999999,2));
                            Objects.requireNonNull(m.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.32);
                            m.getWorld().playSound(m.getLocation(),Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY,3,1);
                            m.setAI(true);
                        }
                    }.runTaskLater(QinKitPVPS.getPlugin(),20);
                }
            }
        }
    }

    @EventHandler
    public void onPotion(EntityPotionEffectEvent e) {
        Entity entity = e.getEntity();
        if(!entity.getScoreboardTags().contains("pvz_zombie")) return;
        if(!(entity instanceof LivingEntity zombie)) return;
        switch (e.getAction()) {
            case ADDED -> {
                if(e.getNewEffect() == null) return;
                if(!e.getNewEffect().getType().equals(PotionEffectType.SLOWNESS)) return;

                Entity topEntity = Function.getTopEntity(zombie);
                String healthBar = topEntity.getCustomName();
                if(healthBar != null) {
                    healthBar = healthBar.replaceAll("§c","§b");
                    topEntity.setCustomName(healthBar);
                }
            }

            case REMOVED -> {
                if(e.getOldEffect() == null) return;
                if(!e.getOldEffect().getType().equals(PotionEffectType.SLOWNESS)) return;

                Entity topEntity = Function.getTopEntity(zombie);
                String healthBar = topEntity.getCustomName();
                if(healthBar != null) {
                    healthBar = healthBar.replaceAll("§b","§c");
                    topEntity.setCustomName(healthBar);
                }
            }
        }
    }

    @EventHandler
    public void onPvzZombieHurtByPlant(EntityDamageByEntityEvent e) {
        Entity zombie = e.getEntity();
        Entity plant = e.getDamager();
        if(!plant.getScoreboardTags().contains("pvz_plant")) return;
        if(!(zombie instanceof LivingEntity lv)) return;

        //火炬僵尸特殊机制
        if(plant.getScoreboardTags().contains("icePeaShooter")) {
            if(zombie.getScoreboardTags().contains("torchZombie") && !zombie.getScoreboardTags().contains("torch_died")) {
                PVZFunction.changePlant(lv,"熄灭的火把",0);
                lv.addScoreboardTag("torch_died");
            }
        }

        //冰冻特殊机制
        if(plant.getScoreboardTags().contains("pvz_freeze")) {
            if (zombie.getScoreboardTags().contains("pvz_grave")) return;
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
    }

    @EventHandler
    public void onPvzZombieAttackPlant(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity plant = e.getEntity();
        if(!damager.getScoreboardTags().contains("pvz_zombie")) return;
        if(!(damager instanceof LivingEntity zombie)) return;

        if(Function.hasTag(damager,"pvz_dreamed")) e.setDamage(e.getFinalDamage() * 3);
        if(Function.hasTag(plant, "pvz_dreamed")) e.setDamage(e.getFinalDamage() * 3);
        //僵尸冷冻延迟攻击
        if(zombie.getPotionEffect(PotionEffectType.SLOWNESS) != null) {
            if(PvzEntity.isAttackFreeze(zombie)) e.setCancelled(true); return;
        }

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

        if ((preHitEntity != null && (preHitEntity.getScoreboardTags().contains("pvz_plant") ||
                preHitEntity.getScoreboardTags().contains("pvz_brain") ||
                preHitEntity.getScoreboardTags().contains("pvz_dreamed"))) ||
                preHitEntity instanceof Player) {
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
        } else if(bullet.getScoreboardTags().contains("puff_bullet")) {
            hitEntity.damage(4, shooter);
            bullet.getWorld().spawnParticle(Particle.ITEM,bullet.getLocation(),10,0.25,0.25,0.25,0.1,new ItemStack(Material.CRYING_OBSIDIAN),false);
        }
    }


    @EventHandler
    public void onPlayerPickUpSun(EntityPickupItemEvent e) {
        if((e.getItem().getScoreboardTags().contains("pvz_sun") || e.getItem().getScoreboardTags().contains("pvz_small_sun")) && e.getEntity() instanceof Player) {
            int sun = PvzRound.getTotalSun();
            int sunPrice = e.getItem().getScoreboardTags().contains("pvz_small_sun") ? 15 : 25;
            sun += sunPrice * e.getItem().getItemStack().getAmount();
            PvzRound.setTotalSun(sun);
            for(Player player : e.getItem().getWorld().getPlayers()) {
                player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + sunPrice + " 阳光: " + sun));
            }
            e.getItem().remove();
            e.setCancelled(true);
        }
    }

    //怪物特定标记目标设定
    @EventHandler
    public void onPVZTarget(EntityTargetEvent e) {
        Entity entity = e.getEntity();
        Entity target = e.getTarget();
        if (entity.getScoreboardTags().contains("pvz_plant")) {
            //筛选
            QinTeam team = QinTeams.getEntityTeam(entity);
            QinTeam targetTeam = QinTeams.getEntityTeam(target);
            Bukkit.broadcastMessage(e.getReason().toString() + Objects.requireNonNullElse(e.getTarget(),"无Entity").toString());
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

            if(target != null && entity instanceof Mob mob) {
                Entity lastTarget = PvzEntity.getMobTarget(mob);
                if(lastTarget != null
                        && lastTarget.getScoreboardTags().contains("pvz_nut")
                        && !lastTarget.isDead()
                        && !target.getScoreboardTags().contains("pvz_dreamed")) {
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
        if(e.getSourceEntity().getScoreboardTags().contains("pvz_plant"))
            e.setCancelled(true);
        if(e.getSourceEntity().getScoreboardTags().contains("pvz_zombie") && !(e.getEntity() instanceof Player))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlacePlant(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return;
        if(e.getItem() == null) return;
        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).startsWith("植物 - ")) return;
        //此处说明放置的是植物，取消事件
        e.setCancelled(true);

        String plantType = Function.getMainHandItemNameWithoutColor(p).substring(5);
        if(Function.getMainHandItemNameWithoutColor(p).equals("植物 - 墓碑吞噬者")) {
            return;
        }

        if(e.getClickedBlock() == null || e.getClickedBlock().isEmpty()) {
            Vector direction = p.getEyeLocation().getDirection();
            Predicate<Entity> predicate = x -> !x.getName().equals(p.getName());
            // 创建一条射线
            RayTraceResult result = p.getWorld().rayTrace(
                    p.getEyeLocation().clone(), // 起始点
                    direction,               // 方向向量
                    100,                     // 最大距离
                    FluidCollisionMode.NEVER, // 流体模式
                    true,                    // 忽略非可视方块
                    0.1,                     // 检测范围（宽度）
                    predicate              // 过滤器
            );
            if(result != null && result.getHitBlock() != null) {
                Location loc = result.getHitBlock().getLocation();
                if(cantPlantHere(loc,p)) return;
                SpawnPlant.summonPlant(p,plantType,loc.clone().add(0,1,0));
            }
        } else {
            if(cantPlantHere(e.getClickedBlock().getLocation(),p)) return;
            SpawnPlant.summonPlant(p,plantType,e.getClickedBlock().getLocation().clone().add(0,1,0));
        }
    }

    @EventHandler
    public void onPlacePlantOnOther(PlayerInteractEntityEvent e) {
        Entity clicked = e.getRightClicked();
        Player p = e.getPlayer();
        if(clicked.getScoreboardTags().contains("pvz_grave")) {
            if(!Function.getMainHandItemNameWithoutColor(p).equals("植物 - 墓碑吞噬者")) return;
            SpawnPlant.summonPlant(p,"墓碑吞噬者",clicked.getLocation().clone().add(-0.5,1.6,-0.5));
        }
    }

    private boolean cantPlantHere(Location loc, Player p) {
        if(p.getGameMode().equals(GameMode.CREATIVE)) return false;
        Location searchLoc = loc.clone().add(0,1,0);
        if(searchLoc.getWorld() == null) return false;
        for(Entity e : searchLoc.getWorld().getNearbyEntities(searchLoc,0.5,0.5,0.5)) {
            if(e.getScoreboardTags().contains("pvz_plant") && !PVZFunction.isBullet(e)) {
                Function.sendPlayerSystemMessage(p,"合理密植是不允许的o^o");
                return true;
            }
            if(e.getScoreboardTags().contains("pvz_grave")) {
                Function.sendPlayerSystemMessage(p,"植物不可以种植在墓碑上");
                return true;
            }
        }
        return false;
    }
}
