package org.NoiQing.AllayWar.AWListeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.AllayWar.AWUtils.AWAllay;
import org.NoiQing.AllayWar.AWUtils.AWBossBars;
import org.NoiQing.AllayWar.AWUtils.AWFunction;
import org.NoiQing.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinConstant;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class TowerListener implements Listener {
    //召唤防御塔设定
    @EventHandler
    public void onBuildTower(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return;
        if(e.getItem() == null) return;
        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).startsWith("建筑 - ")) return;
        if(e.getClickedBlock() == null || e.getClickedBlock().isEmpty()) {
            e.setCancelled(true);
            return;
        }

        QinTeam t = QinTeams.getEntityTeam(p);

        String buildingName = Function.getMainHandItemNameWithoutColor(p).substring(5);
        if(!buildingName.equals("生命核心") && !hasTowerNearBy(e.getClickedBlock().getLocation(), p,12, true) && t != null) {
            Function.sendPlayerSystemMessage(p, "附近没有己方的防御塔，无法部署");
            e.setCancelled(true);
            return;
        }
        if(summonAllayAndTower(p,buildingName,e.getClickedBlock().getLocation().clone().add(0,1,0))) {
            e.getItem().setAmount(e.getItem().getAmount() - 1);
        } else {
            e.setCancelled(true);
        }

    }

    //建筑核心防止机制和防止玩家建筑在塔附近监听器
    @EventHandler
    public void onBuildingCorePlaced(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if(item.getItemMeta() == null) return;

        if(Function.isBlockHasTag(e.getBlock(),"AllayTower")) {
            e.setCancelled(true);
            Function.sendPlayerSystemMessage(e.getPlayer(), "防御塔附近不能放置方块！");
        }
    }

    private boolean hasTowerNearBy(Location loc, Player p, int radius, boolean selfTeam) {
        QinTeam team = QinTeams.getEntityTeam(p);
        if(team == null) return false;
        if(loc.getWorld() == null) return false;
        for(Entity e : loc.getWorld().getNearbyEntities(loc,  radius, radius, radius)) {
            if(QinTeams.getEntityTeam(e) == null) continue;
            if(!isAllayTower(e)) continue;
            if(e.getScoreboardTags().contains("allay_army")) continue;
            QinTeam enemyTeam = QinTeams.getEntityTeam(e);
            boolean isSelfTeam = team.equals(enemyTeam);
            if(selfTeam && isSelfTeam) return true;
            if(!selfTeam && !isSelfTeam) return true;
        }
        return false;
    }

    //召唤部队设定
    @EventHandler
    public void onSummonArmy(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return;
        if(e.getClickedBlock() == null || e.getClickedBlock().isEmpty()) return;
        if(e.getItem() == null) return;
        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).startsWith("部队 - ")) return;
        if(hasTowerNearBy(e.getClickedBlock().getLocation(), e.getPlayer(),12, false)) {
            if(!hasTowerNearBy(e.getClickedBlock().getLocation(),e.getPlayer(),12,true)) {
                e.setCancelled(true);
                Function.sendPlayerSystemMessage(e.getPlayer(),"兵种不能放于敌人的防御塔附近");
                return;
            }
        }

        String armyName = Function.getMainHandItemNameWithoutColor(p).substring(5);
        summonArmy(p,armyName,e.getClickedBlock().getLocation().clone().add(0,1,0));
        e.getItem().setAmount(e.getItem().getAmount() - 1);

    }

    //防止玩家夺取悦灵物品
    @EventHandler
    public void onPlayerInteractAllay(PlayerInteractEntityEvent e) {
        if(e.getRightClicked() instanceof Allay a) {
            for(String s : a.getScoreboardTags()) {
                if(s.contains("Tower")) e.setCancelled(true);
            }
        }
    }

    //防止防御塔被人工破坏
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // 检查方块是否有"Tower"标签
        if (Function.isBlockHasTag(block, "AllayTower") && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            Function.sendPlayerSystemMessage(player,"你无法直接破坏防御塔方块！");
            return;
        }

        // 检查方块是否有"OriginalMap"标签
        if (Function.isBlockHasTag(block, "OriginalMap") && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            Function.sendPlayerSystemMessage(player,"你只能破坏玩家放置的方块！");
        }
    }

    //塔受伤的判定
    @EventHandler
    public void onAllayDamaged(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity murder = e.getDamager();
        if(e.getEntity() instanceof Allay a && isAllayTower(a)) {
            //攻城机器额外攻击机制
            if(e.getDamager() instanceof LivingEntity lv) {
                if(lv.getEquipment() != null && lv.getEquipment().getItemInMainHand().getType().toString().endsWith("PICKAXE")) {
                    e.setDamage(e.getDamage() * 2.5);
                }
            }

            //生命核心不允许被弹射物攻击
            if(Function.getNameWithoutColor(a.getCustomName()).equals("۞ 生命核心 ۞") && e.getDamager() instanceof Projectile projectile) {
                if(projectile.getShooter() instanceof Player) {
                    e.setCancelled(true);
                    return;
                }
            }

            if(e.getDamager() instanceof LargeFireball fireball) {
                e.setDamage(e.getDamage() * 1.5);
            }



            //被工程师占领判定
            if(e.getDamager().getScoreboardTags().contains("engineer")) {
                QinTeam allayTeam = QinTeams.getEntityTeam(a);
                QinTeam engineerTeam = QinTeams.getEntityTeam(e.getDamager());
                if(engineerTeam == null) return;
                if(allayTeam != null)
                    allayTeam.removeTeamEntities(a);
                engineerTeam.addTeamEntities(a);
                AWFunction.setNameByTeam(a,"§l" + Function.getNameWithoutColor(a.getCustomName()));
                engineerTeam.removeTeamEntities(e.getDamager());
                e.getDamager().remove();
            }

            //血条显示
            double maxHealth = Objects.requireNonNull(a.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
            double health = a.getHealth() - e.getFinalDamage();
            NamespacedKey key = new NamespacedKey(QinKitPVPS.getPlugin(),"allay_tower" + a.getUniqueId());
            BossBar allayBossBar = Bukkit.getBossBar(key);
            if(allayBossBar == null) allayBossBar = Bukkit.createBossBar(key, a.getCustomName() + "§7 (" + health + "/" + maxHealth + ")", BarColor.GREEN, BarStyle.SEGMENTED_6);
            else allayBossBar.setTitle(a.getCustomName() + "§7 (" + health + "/" + maxHealth + ")");
            allayBossBar.setProgress(health <= 0 ? 0 : health / maxHealth);

            //取消悦灵自身攻击
            QinTeam t = QinTeams.getEntityTeam(a);
            if(e.getDamager().getScoreboardTags().contains("allay_damage")) {
                if(t != null && t.equals(QinTeams.getEntityTeam(e.getDamager())))
                    e.setCancelled(true);
            }

            //增加悦灵血条显示
            if(e.getDamager() instanceof Player p) {
                if(!allayBossBar.getPlayers().contains(p)) allayBossBar.addPlayer(p);
                AWBossBars.updatePAAR(p,a);
                BossBar finalAllayBossBar = allayBossBar;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(AWBossBars.ifPlayerPassBossBar(p,a)) {
                            finalAllayBossBar.removePlayer(p);
                        }
                    }
                }.runTaskLater(QinKitPVPS.getPlugin(),5*20);
            }
        }
        if(entity.getScoreboardTags().contains("allay_army")) {
            if(entity instanceof Mob m) {
                if(AWAllay.getMobMove(m) != null) m.addScoreboardTag("attacked");
            }
        }
        if(e.getDamager() instanceof TNTPrimed tnt) {
            //TNT防止炸自家队友设定
            QinTeam team = QinTeams.getEntityTeam(tnt);
            if(team != null && team.equals(QinTeams.getEntityTeam(e.getEntity()))) {
                e.setCancelled(true);
                return;
            }
            if(tnt.getScoreboardTags().contains("cannon_tnt")) {
                if(e.getEntity() instanceof LivingEntity lv) {
                    if(isAllayTower(lv))
                        e.setDamage(25);
                    else e.setCancelled(true);
                }
            }
        }

        if(e.getDamager().getScoreboardTags().contains("rifle")) {
            e.setCancelled(true);
            if(e.getEntity() instanceof LivingEntity lv) {
                lv.damage(e.getFinalDamage());
            }
        }

        if(murder instanceof Mob mob) {
            //移动机制
            if(entity.getScoreboardTags().contains("move_tag")) {
                e.setCancelled(true);
                AWAllay.removeMobMove(mob);
                mob.setTarget(null);
            }
        }


    }

    //防止悦灵回复生命值
    @EventHandler
    public void onAllayHealth(EntityRegainHealthEvent e) {
        if(e.getEntity() instanceof Allay a && isAllayTower(a)) e.setCancelled(true);
    }

    //坦克收到坦克双倍攻击
    @EventHandler
    public void onTankDamaged(EntityDamageByEntityEvent e) {
        if(AWFunction.isTank(e.getEntity()) && e.getDamager() instanceof LargeFireball)
            e.setDamage(e.getDamage() * 1.5);
    }

    //防御塔死亡设定和队伍自动排出设定
    @EventHandler
    public void onTowerDead(EntityDeathEvent e) {
        if(e.getEntity() instanceof Allay a && isAllayTower(a)) {
            QinTeam aTeam = QinTeams.getEntityTeam(a);
            if(Function.getNameWithoutColor(a.getCustomName()).equals("۞ 生命核心 ۞")) {
                if(aTeam != null && AWRound.getTeamLevels(aTeam.getTeamName()) != null) {
                    AWRound.getTeamLevels(aTeam.getTeamName()).put("HaveBase",0);
                    Bukkit.broadcastMessage("§c" + aTeam.getTeamName() + " §7的 §b生命核心 §7被§c摧毁§7了！");
                }

            } else if (Function.getNameWithoutColor(a.getCustomName()).equals("📡 雷达 📡")) {
                if(aTeam != null && AWRound.getTeamLevels(aTeam.getTeamName()) != null) {
                    AWRound.getTeamLevels(aTeam.getTeamName()).put("HaveRaider",0);
                    for(Entity entity : aTeam.getTeamEntities()) {
                        if(entity.getScoreboardTags().contains("player_stand")) {
                            Player p = AWPlayer.getPlayerRaider(entity);
                            if(p == null) continue;
                            p.setGameMode(GameMode.SURVIVAL);
                            p.setAllowFlight(false);
                            p.getInventory().setContents(AWPlayer.getPlayerTempInventory(p));
                            p.setInvisible(false);
                            p.setInvulnerable(false);
                            p.removeScoreboardTag("raider_mode");
                            p.teleport(entity.getLocation());
                            AWPlayer.removePlayerRaider(entity);
                            entity.remove();
                        }
                    }
                }
            }

            Configuration config = QinKitPVPS.getPlugin().getResource().getData();
            String towerName = Function.getNameWithoutColor(a.getCustomName()).replaceAll("[^\\p{IsHan}]", "");
            int size = config.contains("TowerData." + towerName + ".size") ? config.getInt("TowerData." + towerName + ".size") : 3;

            if (size == 3) Function.summonTower(a.getLocation().add(0,-1.1,0),"荒地");
            else Function.summonTower(a.getLocation().add(0,-1.1,0),"大荒地");

            e.getDrops().clear();
            NamespacedKey key = new NamespacedKey(QinKitPVPS.getPlugin(),"allay_tower" + a.getUniqueId());
            BossBar allayBossBar = Bukkit.getBossBar(key);
            if(allayBossBar != null) {
                allayBossBar.removeAll();
                Bukkit.removeBossBar(key);
            }
        }

        if(e.getEntity().getScoreboardTags().contains("allay_army") || isAllayTower(e.getEntity())) {
            QinTeam team = QinTeams.getEntityTeam(e.getEntity());
            if(team != null) team.removeTeamEntities(e.getEntity());
            e.getDrops().clear();
        }

        if(AWFunction.isTankArmy(e.getEntity())) {
            Entity tank = e.getEntity();
            AWAllay.getTankDisplays(tank).forEach(Entity::remove);
            AWAllay.removeTankDisplays(tank);
        }
    }

    //矿工获取资金设定
    @EventHandler
    public void onPlayerPickUpGold(EntityPickupItemEvent e) {
        if(!e.getItem().getScoreboardTags().contains("allay_gold")) return;
        if(e.getEntity() instanceof Player p) {
            if(p.getScoreboardTags().contains("raider_mode")) {
                e.setCancelled(true);
                return;
            }
            int addMoney = 0;
            switch (e.getItem().getItemStack().getType()) {
                case GOLD_INGOT -> addMoney = 6;
                case GOLD_NUGGET -> addMoney = 1;
            }
            addMoney = addMoney * e.getItem().getItemStack().getAmount();
            AWPlayer.setPlayerAWMoney(p,AWPlayer.getPlayerAWMoney(p) + addMoney);
            p.playSound(p,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1.5f);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e+ " + addMoney + " 总资产: " + AWPlayer.getPlayerAWMoney(p)));
            e.setCancelled(true);
            e.getItem().remove();
        } else e.setCancelled(true);
    }

    //怪物特定标记目标设定
    @EventHandler
    public void onAttackTowerOnlyEntityTarget(EntityTargetEvent e) {
        Entity entity = e.getEntity();
        if(entity.getScoreboardTags().contains("allay_army")) {
            Entity target = e.getTarget();
            Entity moveTarget;

            //筛选
            if(target instanceof Villager) {
                e.setCancelled(true);  return;
            }
            if(target instanceof Player p && p.isInvulnerable()) {
                e.setCancelled(true);  return;
            }
            QinTeam team = QinTeams.getEntityTeam(entity);
            QinTeam targetTeam = QinTeams.getEntityTeam(target);
            if(team == null) return;
            if(team.equals(targetTeam)) {
                e.setCancelled(true);
                return;
            }

            if(entity instanceof Mob mob) {
                moveTarget = AWAllay.getMobMove(mob);
                if(moveTarget != null) {
                    //生物强制移动到对应地点
                    if (target != null && !moveTarget.equals(target) && !target.getScoreboardTags().contains("move_tag")) {
                        if(mob.getScoreboardTags().contains("attacked")) {
                            AWAllay.removeMobMove(mob);
                        } else {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }

        }

        if(e.getEntity().getScoreboardTags().contains("engineer")) {
            if(e.getTarget() instanceof Allay a) {
                if(!isAllayTower(a)) e.setCancelled(true);
                if(AWFunction.isNotFriendlyTower(a)) e.setCancelled(true);
            }
        }

        //以下是生物只锁定塔的设定
        if(!e.getEntity().getScoreboardTags().contains(QinConstant.ATTACK_TOWER_ONLY_TAG)) return;
        if(e.getEntity() instanceof Mob mob) {
            if(!(e.getTarget() instanceof Allay)) {
                e.setCancelled(true);
                e.setTarget(AWFunction.findNearestTower(mob,100));
            }
        }
    }

    //玩家进入雷达判定
    @EventHandler
    public void onPlayerEnterRadar(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return;
        if(e.getItem() == null) return;
        if(e.getItem().getItemMeta() == null) return;
        if(!Function.getNameWithoutColor(e.getItem().getItemMeta().getDisplayName()).equals("雷达视野")) return;
        Player p = e.getPlayer();
        QinTeam t = QinTeams.getEntityTeam(p);
        if(t == null) return;
        if(AWRound.getTeamLevels(t.getTeamName()).get("HaveRaider") == 0) {
            Function.sendPlayerSystemMessage(p,"队伍雷达信号已经丢失！");
            e.setCancelled(true);
            return;
        }
        Skeleton s = p.getWorld().spawn(p.getLocation(),Skeleton.class);
        AWFunction.setMobEquipment(s,new ItemStack(Material.AIR));
        t.addTeamEntities(s);
        s.addScoreboardTag("player_stand");
        Objects.requireNonNull(s.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(0);
        Objects.requireNonNull(s.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        AWFunction.setNameByTeam(s,p.getName());

        AWPlayer.setPlayerRaider(s,p);
        AWPlayer.setPlayerTempInventory(p,p.getInventory().getContents());
        p.getInventory().clear();
        ItemStack commandStick = new ItemStack(Material.STICK);
        ItemMeta commandStickMeta = commandStick.getItemMeta();
        if (commandStickMeta != null) {
            commandStickMeta.setDisplayName("指挥棒 - 选中");
        }
        commandStick.setItemMeta(commandStickMeta);
        p.getInventory().setItem(4,commandStick);
        p.setInvisible(true);
        p.setGameMode(GameMode.ADVENTURE);
        p.setAllowFlight(true);
        p.setInvulnerable(true);
        p.setFlying(true);

        p.addScoreboardTag("raider_mode");
        p.sendTitle("§7已进入雷达模式","双击shift退出视野",10,20,10);
        e.setCancelled(true);
    }

    //玩家的雷达被攻击事件
    @EventHandler
    public void onPlayerRadarAttacked(EntityDamageByEntityEvent e) {
        if(e.getEntity().getScoreboardTags().contains("player_stand")) {
            Player p = AWPlayer.getPlayerRaider(e.getEntity());
            if(p == null) return;
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(false);
            p.getInventory().setContents(AWPlayer.getPlayerTempInventory(p));
            p.setInvisible(false);
            p.setInvulnerable(false);
            p.teleport(e.getEntity().getLocation());
            p.damage(e.getDamage(), e.getDamager());
            p.removeScoreboardTag("raider_mode");
            AWPlayer.removePlayerRaider(e.getEntity());
            e.getEntity().remove();
        }

        if(e.getDamager().getScoreboardTags().contains("raider_mode")) {
            e.setCancelled(true);
        }
    }

    //雷达禁止传送事件
    @EventHandler
    public void onPlayerRadarTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if(p.getScoreboardTags().contains("raider_mode")) {
            if(e.getTo() == null || e.getTo().getWorld() == null) return;
            if(!e.getTo().getWorld().equals(e.getFrom().getWorld()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTankDamage(EntityDamageEvent e) {
        if(AWFunction.isTankArmy(e.getEntity())) {
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(),Sound.ENTITY_IRON_GOLEM_HURT,1,1.85f);
        }
    }

    @EventHandler
    public void onEntityTransform(EntityTransformEvent e) {
        if(e.getEntity().getScoreboardTags().contains("hell_pig")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChangeCommandStick(PlayerToggleSneakEvent e) {
        if(e.isSneaking()) return;
        Player p = e.getPlayer();
        ItemStack stick = p.getInventory().getItemInMainHand();
        ItemMeta meta = stick.getItemMeta();
        if(meta == null) return;
        String itemName = Function.getMainHandItemNameWithoutColor(p);
        if(!itemName.startsWith("指挥棒")) return;

        switch (itemName) {
            case "指挥棒 - 选中" -> meta.setDisplayName("指挥棒 - 指令");
            case "指挥棒 - 指令" -> meta.setDisplayName("指挥棒 - 选中");
        }
        p.playSound(p,Sound.ENTITY_BAT_TAKEOFF,1,1.8f);
        stick.setItemMeta(meta);
    }

    @EventHandler
    public void onPlayerSelect(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return;
        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).equals("指挥棒 - 选中")) return;

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

        if(result == null) {
            Function.sendPlayerSystemMessage(p,"你选择了一个无效区域");
            return;
        }

        Location selectLocation = result.getHitPosition().toLocation(p.getWorld());

        AWFunction.showMagicParticle(selectLocation.clone().add(0,0.5,0),p.getEyeLocation().clone(),Particle.GLOW);

        Location first = AWPlayer.getPlayerSelectLocation(p);
        if(first == null) {
            Function.sendPlayerSystemMessage(p,"第一点已选择");
            AWPlayer.setPlayerSelectLocation(p,selectLocation.clone().add(0,0.5,0));
        } else {
            Set<Mob> entities = getSelectedEntities(first,selectLocation,p);
            if(entities == null) {
                Function.sendPlayerSystemMessage(p,"你的选中出现了错误");
                AWPlayer.removePlayerSelectLocation(p);
                return;
            }
            Function.sendPlayerSystemMessage(p,"选中了 " + entities.size() + " 个单位");
            for(Mob m : entities) {
                m.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 6, 0, true, false));
            }
            AWPlayer.setPlayerSelectedEntities(p,entities);
            AWPlayer.removePlayerSelectLocation(p);
        }
    }

    @EventHandler
    public void onPlayerCommandMove(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return;
        Player p = e.getPlayer();
        if(!Function.getMainHandItemNameWithoutColor(p).equals("指挥棒 - 指令")) return;

        Set<Mob> mobs = AWPlayer.getPlayerSelectedEntities(p);
        if(mobs.isEmpty()) {
            Function.sendPlayerSystemMessage(p,"你还没有选择任何单位");
            return;
        }

        LivingEntity lv = getPlayerSelectEntity(p);
        if(lv != null) {
            Function.sendPlayerSystemMessage(p,"指定了生物攻击");
            for(Mob m : mobs) {
                AWAllay.removeMobMove(m);
                m.setTarget(lv);
            }
        } else {
            Location selectLoc = getPlayerSelectLoc(p);
            if(selectLoc == null) {
                Function.sendPlayerSystemMessage(p,"你选择了一个无效区域");
                return;
            }

            Chicken c = p.getWorld().spawn(selectLoc, Chicken.class);
            QinTeam t = QinTeams.getQinTeamByName("默认白");
            if(t != null)
                t.addTeamEntities(c);
            c.setInvisible(true);
            c.setSilent(true);
            c.setAI(false);
            c.setInvulnerable(false);
            Function.setEntityHealth(c,100);
            c.addScoreboardTag("move_tag");
            Function.sendPlayerSystemMessage(p,"指定了生物移动");

            for(Mob m : mobs) {
                m.addScoreboardTag("mob_moving");
                AWAllay.setMobMove(m,c);
            }
        }
    }


    public static boolean isAllayTower(Entity a) {
        for(String s : a.getScoreboardTags()) {
            if(s.contains(QinConstant.ALLAY_TOWER_TAG)) return true;
        }
        return false;
    }

    private <T extends Entity> T summonAllayArmy(Player p, Class<T> entityClass, Location loc) {
        T e = p.getWorld().spawn(loc,entityClass);
        QinTeam playerTeam = QinTeams.getEntityTeam(p);
        if(playerTeam != null) {
            playerTeam.addTeamEntities(e);
        }
        e.addScoreboardTag("allay_army");
        e.setCustomNameVisible(true);
        e.getScoreboardTags();
        e.setPersistent(true);

        return e;
    }
    private static Location randomNearbyLocation(Location loc, double radius) {
        Random r = new Random();
        double rangeX = r.nextDouble() * radius;
        double rangeZ = r.nextDouble() * radius;
        if(r.nextBoolean()) rangeX = -rangeX;
        if(r.nextBoolean()) rangeZ = -rangeZ;
        return loc.clone().add(rangeX,0,rangeZ);
    }

    private void summonArmy(Player p, String armyName, Location loc) {
        switch (armyName) {
            case "僵尸精锐" -> {
                for(int i = 0; i < 3; i++) {
                    Zombie z = summonAllayArmy(p, Zombie.class, randomNearbyLocation(loc,1));
                    AWFunction.setNameByTeam(z, "§l精锐士兵");
                    z.setAdult();
                    Objects.requireNonNull(z.getEquipment()).setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                    z.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                    z.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    z.getEquipment().setLeggings(new ItemStack(Material.AIR));
                    z.getEquipment().setBoots(new ItemStack(Material.AIR));
                }
            }

            case "骷髅军团" -> {
                for(int i = 0; i < 6; i++) {
                    Skeleton z = summonAllayArmy(p, Skeleton.class, randomNearbyLocation(loc,2));
                    AWFunction.setNameByTeam(z, "§l杂鱼");
                    Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(10);
                    z.setHealth(10);
                    Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.25);
                    Objects.requireNonNull(z.getEquipment()).setItemInMainHand(new ItemStack(Material.AIR));
                    z.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                    z.getEquipment().setChestplate(new ItemStack(Material.AIR));
                    z.getEquipment().setLeggings(new ItemStack(Material.AIR));
                    z.getEquipment().setBoots(new ItemStack(Material.AIR));
                }
            }

            case "丧尸巨人" -> {
                Zombie z = summonAllayArmy(p, Zombie.class, randomNearbyLocation(loc,1));
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(100);
                z.setHealth(100);
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(1.5);
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(0.5);
                z.addScoreboardTag("Attack_Tower_Only");
                AWFunction.setNameByTeam(z, "§l丧尸巨人");
                z.setAdult();
                AWFunction.setMobEquipment(z,new ItemStack(Material.AIR), new ItemStack(Material.DIAMOND_HELMET));
            }

            case "步枪士兵" -> {
                for(int i = 0; i < 3; i++) {
                    Zombie z = summonAllayArmy(p, Zombie.class, randomNearbyLocation(loc,1));
                    AWFunction.setNameByTeam(z, "§l枪兵");
                    z.addScoreboardTag("rifle");
                    z.setAdult();
                    z.setHealth(20);
                    AWFunction.setMobEquipment(z,new ItemStack(Material.IRON_HOE),new ItemStack(Material.IRON_HELMET));
                }
            }

            case "工程师" -> {
                Zombie z = summonAllayArmy(p, Zombie.class, randomNearbyLocation(loc,1));
                AWFunction.setNameByTeam(z, "§l工程师");
                z.addScoreboardTag("engineer");
                z.setAdult();
                z.setHealth(20);
                AWFunction.setMobEquipment(z,new ItemStack(Material.ORANGE_CONCRETE),new ItemStack(Material.IRON_HELMET));
            }

            case "激光坦克" -> {
                Ravager z = summonAllayArmy(p, Ravager.class, randomNearbyLocation(loc,1));

                z.addScoreboardTag("laser_tank");
                summonTankCore(z);
                z.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 999999, 2, false, false, true));
                AWFunction.setNameByTeam(z,"§l激光坦克");
                Function.setEntityHealth(z,40);
                AWFunction.summonTank(z,"激光坦克",(float) z.getHeight() + 0.6f);
            }

            case "普通坦克" -> {
                Ravager z = summonAllayArmy(p, Ravager.class, randomNearbyLocation(loc,1));

                z.addScoreboardTag("normal_tank");
                summonTankCore(z);
                z.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 999999, 1, false, false, true));
                AWFunction.setNameByTeam(z,"§l普通坦克");
                Function.setEntityHealth(z,75);
                AWFunction.summonTank(z,"普通坦克",(float) z.getHeight() + 0.6f);
            }

            case "拆塔步兵" -> {
                for(int i = 0; i < 2; i++) {
                    Zombie z = summonAllayArmy(p, Zombie.class, randomNearbyLocation(loc,1));
                    AWFunction.setNameByTeam(z, "§l拆塔兵");
                    z.addScoreboardTag("demolition");
                    z.setAdult();
                    z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false, true));
                    z.setHealth(20);
                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                    ItemMeta meta = helmet.getItemMeta();
                    if(meta instanceof LeatherArmorMeta lm) {
                        lm.setColor(Color.RED);
                        helmet.setItemMeta(lm);
                    }
                    AWFunction.setMobEquipment(z,new ItemStack(Material.IRON_PICKAXE),helmet);
                }
            }

            case "地狱蛮兵" -> {
                for(int i = 0; i < 2; i++) {
                    Zombie z = summonAllayArmy(p, Zombie.class, randomNearbyLocation(loc,1));
                    AWFunction.setNameByTeam(z, "§l地狱火尸");
                    Function.setEntityHealth(z,50);
                    z.addScoreboardTag("hell_pig");
                    //添加速度
                    z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false, true));
                    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
                    item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT,2);
                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    ItemMeta meta = helmet.getItemMeta();
                    if(meta instanceof LeatherArmorMeta lm) {
                        lm.setColor(Color.RED);
                        helmet.setItemMeta(lm);
                        chestplate.setItemMeta(lm);
                        leggings.setItemMeta(lm);
                        boots.setItemMeta(lm);
                    }
                    AWFunction.setMobEquipment(z,item,helmet,chestplate,leggings,boots);
                }
            }
        }
    }

    private void summonTankCore(Ravager z) {
        Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(1.3);
        Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(0);
        Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1000);
        z.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false, true));
        z.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 50, false, false, true));
        z.setSilent(true);
    }

    private boolean summonAllayAndTower(Player p, String towerType, Location loc) {

        if(Function.noAllowBuildTower(p ,towerType, loc)) return false;

        Allay allay = Objects.requireNonNull(loc.getWorld()).spawn(loc.clone().add(0.5,1.6,0.5), Allay.class);
        Objects.requireNonNull(allay.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(1.5);

        allay.setAI(false);
        allay.setCustomNameVisible(true);
        allay.setInvulnerable(false);
        allay.setGravity(false);
        Function.setEntityHealth(allay, 100);

        QinTeam team = QinTeams.getEntityTeam(p);
        if(team != null) team.addTeamEntities(allay);

        switch (towerType) {
            case "狙击塔" -> {
                Function.summonTower(p,"狙击塔", loc);
                allay.addScoreboardTag("Tower_Sniper");
                AWFunction.setNameByTeam(allay,"§l\uD83C\uDFF9 狙击塔 \uD83C\uDFF9");
                Function.setEntityHealth(allay, 80);
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.BOW));
            }

            case "荒地" -> {
                Function.summonTower(p,"荒地", loc);
                allay.remove();
            }

            case "医院" -> {
                Function.summonTower(p,"医院", loc);
                allay.addScoreboardTag("Tower_Hospital");
                AWFunction.setNameByTeam(allay,"§l❤ 医院 ❤");
                Function.setEntityHealth(allay, 150);
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.BEETROOT_SOUP));
            }

            case "魔法防御塔" -> {
                Function.summonTower(p,"魔法防御塔", loc);
                allay.addScoreboardTag("Tower_Magic");
                AWFunction.setNameByTeam(allay,"§l♜ 魔法防御塔 ♜");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.AMETHYST_CLUSTER));
            }

            case "矿场" -> {
                Function.summonTower(p,"矿场", loc);
                allay.addScoreboardTag("Tower_Mine");
                AWFunction.setNameByTeam(allay,"§l⛏ 矿场 ⛏");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.GOLD_INGOT));
            }

            case "兵营" -> {
                Function.summonTower(p,"兵营", loc);
                allay.addScoreboardTag("Tower_Army");
                AWFunction.setNameByTeam(allay,"§l⚔ 兵营 ⚔");
                Function.setEntityHealth(allay, 50);
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
            }

            case "生命核心" -> {
                Function.summonTower(p,"生命核心", loc);
                allay.addScoreboardTag("Tower_LifeCore");
                AWFunction.setNameByTeam(allay,"§l۞ 生命核心 ۞");
                if (team != null) AWRound.getTeamLevels(team.getTeamName()).put("HaveBase",1);
                Function.setEntityHealth(allay, 600);
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.REDSTONE_BLOCK));
            }

            case "实验书楼" -> {
                Function.summonTower(p,"实验书楼", loc);
                allay.addScoreboardTag("Tower_Lab");
                AWFunction.setNameByTeam(allay,"§l◈ 实验书楼 ◈");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.BOOK));
            }

            case "炸弹塔" -> {
                Function.summonTower(p,"炸弹塔", loc);
                allay.addScoreboardTag("Tower_Boomer");
                AWFunction.setNameByTeam(allay,"§l\uD83D\uDCA3 炸弹塔 \uD83D\uDCA3");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.TNT));
            }

            case "机枪塔" -> {
                Function.summonTower(p,"机枪塔", loc);
                allay.addScoreboardTag("Tower_Machine");
                AWFunction.setNameByTeam(allay,"§l\uD83D\uDD2B 机枪塔 \uD83D\uDD2B");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.WHITE_DYE));
            }

            case "资源收集器" -> {
                Function.summonTower(p,"资源收集器", loc);
                allay.addScoreboardTag("Tower_Resource");
                AWFunction.setNameByTeam(allay,"§l⛏ 资源收集器 ⛏");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.YELLOW_STAINED_GLASS));
            }

            case "迫击炮" -> {
                Function.summonTower(p,"迫击炮", loc);
                allay.addScoreboardTag("Tower_Cannon");
                AWFunction.setNameByTeam(allay,"§l\uD83E\uDDE8 迫击炮 \uD83E\uDDE8");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.NETHER_WART_BLOCK));
            }

            case "造车工坊" -> {
                Function.summonTower(p,"造车工坊", loc);
                allay.addScoreboardTag("Tower_Workshop");
                AWFunction.setNameByTeam(allay,"§l🚗 造车工坊 🚗");
                Function.setEntityHealth(allay,150);
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.IRON_BLOCK));
            }

            case "雷达" -> {
                Function.summonTower(p,"雷达", loc);
                allay.addScoreboardTag("Tower_Radar");
                AWFunction.setNameByTeam(allay,"§l📡 雷达 📡");
                if (team != null) {
                    AWRound.getTeamLevels(team.getTeamName()).put("HaveRaider",1);
                    ItemStack item = new ItemStack(Material.SPYGLASS);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName("§7§l雷达视野");
                    }
                    item.setItemMeta(meta);

                    for(Player player : team.getTeamPlayers())
                        player.getInventory().addItem(item);

                }
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.IRON_BLOCK));
            }

            case "油井" -> {
                Function.summonTower(p,"油井", loc);
                allay.addScoreboardTag("Tower_Oil");
                AWFunction.setNameByTeam(allay,"§l🛢 油井 🛢");
                Objects.requireNonNull(allay.getEquipment()).setItemInMainHand(new ItemStack(Material.BLACK_CONCRETE_POWDER));
            }

            case "" -> allay.remove();

            default -> {
                allay.remove();
                Function.sendPlayerSystemMessage(p,"不存在名为 " + towerType + " 的建筑");
                return false;
            }
        }
        return true;
    }

    

    public static Set<Mob> getSelectedEntities(Location loc1, Location loc2, Player p) {
        // 获取两个Location的最小和最大边界
        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        Set<Mob> selectedMobs = new HashSet<>();
        QinTeam playerTeam = QinTeams.getEntityTeam(p);
        // 获取两个Location的世界
        World world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            return null;
        }

        // 查找loc1位置附近的实体，先用一个较大的半径来查找
        double radius = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)) / 2;
        Location center = loc1.clone().add(loc2).multiply(0.5); // 中心点

        // 查找指定范围内的所有实体
        for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
            // 判断实体是否为LivingEntity（即生物对象），并且位于给定的立方体区域内
            if (entity instanceof Mob mob && mob.getScoreboardTags().contains("allay_army") && !mob.isDead()) {

                QinTeam mobTeam = QinTeams.getEntityTeam(mob);
                if(mobTeam == null || !mobTeam.equals(playerTeam)) continue;

                Location entityLocation = entity.getLocation();
                if (entityLocation.getX() >= minX && entityLocation.getX() <= maxX
                        && entityLocation.getY() >= minY-1 && entityLocation.getY() <= maxY+1
                        && entityLocation.getZ() >= minZ && entityLocation.getZ() <= maxZ) {
                    selectedMobs.add(mob);
                }
            }
        }

        return selectedMobs;
    }

    private Location getPlayerSelectLoc(Player p) {
        RayTraceResult result = getPlayerSelectResult(p);

        if(result == null) {
            return null;
        }

        Location selectLocation = result.getHitPosition().toLocation(p.getWorld());

        AWFunction.showMagicParticle(selectLocation.clone().add(0,0.5,0),p.getEyeLocation().clone(),Particle.GLOW);
        return selectLocation;
    }

    private LivingEntity getPlayerSelectEntity(Player p) {
        RayTraceResult result = getPlayerSelectResult(p);
        Entity entity = result.getHitEntity();
        if (entity == null) return null;
        if(entity instanceof LivingEntity livingEntity) {
            QinTeam lvTeam = QinTeams.getEntityTeam(livingEntity);
            QinTeam playerTeam = QinTeams.getEntityTeam(p);
            if(playerTeam == null || playerTeam.equals(lvTeam)) return null;
            AWFunction.showMagicParticle(entity.getLocation().clone().add(0,0.5,0),p.getEyeLocation().clone(),Particle.LAVA);
            return livingEntity;
        }
        return null;
    }

    private RayTraceResult getPlayerSelectResult(Player p) {
        Vector direction = p.getEyeLocation().getDirection();
        Predicate<Entity> predicate = x -> !x.getName().equals(p.getName());
        // 创建一条射线

        return p.getWorld().rayTrace(
                p.getEyeLocation().clone(), // 起始点
                direction,               // 方向向量
                100,                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                0.1,                     // 检测范围（宽度）
                predicate              // 过滤器
        );
    }
}
