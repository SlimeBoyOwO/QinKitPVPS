package org.NoiQing.EventListener.SkillListeners;

import org.NoiQing.EventListener.System.PlayerJoinListener;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class SkillItems implements Listener {
    private final QinKitPVPS plugin;
    private final Scoreboard scoreboard;
    public SkillItems(QinKitPVPS plugin){
        this.plugin = plugin;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        this.scoreboard = scoreboardManager.getMainScoreboard();
    }
    @EventHandler
    public void onInteract (PlayerInteractEvent event){
        if(!Function.isRightClicking(event)) return;

        Player player = event.getPlayer();

        if(Function.isHoldingSPItem(player,"起飞")){
            player.setVelocity(new Vector(0, 2, 0));
            Function.useSPItem(player,"起飞");
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player, "起飞装置")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player, "起飞装置")){
                player.setVelocity(new Vector(0,3,0));
                player.setFallDistance(-30);
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"起飞装置", 20);
            }
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player, "炮弹")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player, "炮弹")){
                Location handLocation = player.getLocation();
                handLocation.setY(handLocation.getY() + 1.0);
                Vector direction = handLocation.getDirection();

                Explosive entity = player.getWorld().spawn(handLocation, LargeFireball.class);
                entity.addScoreboardTag("Tank_Fireball");
                entity.setVelocity(direction.multiply(2.0));
                entity.setYield(2.5f);

                PlayerDataSave.setPlayerSkillCoolDownTime(player,"炮弹", 20);
            }
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player, "遁影模式")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player, "遁影模式"))
                player.setArrowsInBody(0);
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"手榴弹")){
            Location handLocation = player.getLocation();
            handLocation.setY(handLocation.getY() + 1.0);
            Vector direction = handLocation.getDirection();

            TNTPrimed entity = player.getWorld().spawn(handLocation, TNTPrimed.class);

            if(player.isSneaking()){
                entity.addScoreboardTag("Short_TNT");
                entity.setFuseTicks(15);
                entity.setYield(2.5f);
            }
            else {
                entity.setFuseTicks(60);
                entity.setGlowing(true);
            }

            entity.setVelocity(direction.multiply(1.5));
            entity.setCustomName(player.getName());

            Function.useSPItem(player,"手榴弹");
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"抽奖宝箱")) {
            player.performCommand("qinkit rollKit");
            Function.useSPItem(player,"抽奖宝箱");
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"黎明闪耀")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player, "黎明闪耀")){
                useGunSkill(player,17);
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"破甲", 15L);
                player.damage(1,player);
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"黎明闪耀", 25);
                Function.addPotionEffect(player, PotionEffectType.DARKNESS,3*20,1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GLOW_SQUID_SQUIRT,1,1);
            }
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"黑暗咆哮")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"黑暗咆哮")){
                Function.addPotionEffect(player,PotionEffectType.SLOWNESS,50,3);
                Function.addPotionEffect(player,PotionEffectType.WEAKNESS,50,3);
                player.getWorld().spawnParticle(Particle.GLOW,player.getEyeLocation(),100,1,1,1,0.3);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        useGunSkill(player,5);
                        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WARDEN_SONIC_BOOM,1,1);
                        player.getWorld().spawnParticle(Particle.GLOW,player.getEyeLocation(),100,1,1,1,0.3);
                    }
                }.runTaskLater(plugin,50);
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"黑暗咆哮",30);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WARDEN_SONIC_CHARGE,1,1);

            }
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"土豆地雷")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"土豆地雷")){
                Location newLocation = player.getLocation().clone();
                newLocation.setY(newLocation.getY() - 0.3);
                Slime slime = player.getWorld().spawn(newLocation,Slime.class);
                slime.setSize(0);
                slime.setAI(false);
                slime.setInvisible(true);
                slime.addScoreboardTag("Slime_Mine");
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"土豆地雷", 12);
            }
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"兴奋剂")){
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"兴奋剂")){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,8*20,3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,8*20,1));
                player.damage(player.getHealth() > 6 ? 6 : (player.getHealth() - 1 > 0 ? player.getHealth() - 1 : 0));
                player.addScoreboardTag("Octane_Running");
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF, 1F, 1.6F);
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"兴奋剂", 7);
            }
            event.setCancelled(true);
        }

        if(playerUseSPItem(player,"返回大厅",0.1)) {
            player.getInventory().clear();
            /* 传 送 玩 家 到 大 厅 */
            Function.playerTpLobby(player);
            PlayerJoinListener.refreshPlayer(player,plugin);
            event.setCancelled(true);
        }
        if(playerUseSPItem(player,"跳板",30)){
            Location handLocation = player.getLocation();
            handLocation.setY(handLocation.getY() + 1.0);
            Vector direction = handLocation.getDirection();

            Slime slime = player.getWorld().spawn(handLocation,Slime.class);
            slime.setVelocity(direction.multiply(0.8).setY(0.35).add(player.getVelocity()));
            slime.setSize(2);
            Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(0);
            Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
            slime.setHealth(20);
            slime.addScoreboardTag("JumpPad");
            event.setCancelled(true);
        }
        if(playerUseSPItem(player,"时间停止",30)) {
            player.addScoreboardTag("pre_THE_WORLD");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20,3,false,false));
            player.getWorld().playSound(player.getLocation(),Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!player.getScoreboardTags().contains("pre_THE_WORLD")) return;
                    player.removeScoreboardTag("pre_THE_WORLD");
                    player.addScoreboardTag("TWorld");
                    player.addScoreboardTag("TWorld_S");
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 1F);
                    for(Entity entity : player.getNearbyEntities(8,8,8)) {
                        if(entity instanceof LivingEntity livingEntity) {
                            if(entity.equals(player)) continue;
                            ArmorStand stand = livingEntity.getWorld().spawn(livingEntity.getLocation(), ArmorStand.class);
                            stand.setInvisible(true);
                            stand.setMarker(true);
                            stand.addScoreboardTag("TWorld_A");
                            stand.addScoreboardTag("Owner_" + player.getName());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    stand.remove();
                                }
                            }.runTaskLater(plugin,20 * 5);
                        }
                    }
                }
            }.runTaskLater(QinKitPVPS.getPlugin(),20);
            event.setCancelled(true);
        }
        if(playerUseSPItem(player,"战争突刺",15)){
            player.setVelocity(player.getLocation().getDirection().multiply(1.5).add(new Vector(0,0.2,0)));
            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BLAZE_SHOOT,1,1);
            event.setCancelled(true);
        }
        if(Function.isHoldingSPItem(player,"战争践踏")){
            event.setCancelled(true);
            if(!PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"战争践踏")) return;
            if(player.getVelocity().getY() > -0.4){
                player.sendMessage("§b你不在下落的途中！");
                return;
            }
            player.setVelocity(player.getVelocity().add(new Vector(0,-1.25,0)));
            player.setFallDistance(7.5f);
            player.addScoreboardTag("Reaper_Falling");
            player.getWorld().playSound(player.getLocation(),Sound.BLOCK_ANVIL_PLACE,1,1);
            PlayerDataSave.setPlayerSkillCoolDownTime(player,"战争践踏", 30);
        }
        if(playerUseSPItem(player,"冰冻莲花",1)){
            event.setCancelled(true);
            long yuanSu = PlayerDataSave.getPlayerPassiveSkillRecords(player,"元素蓄力");
            if(yuanSu < 30)
                player.sendMessage("§b§l元素能量不足");
            else{
                player.setVelocity(player.getLocation().getDirection().multiply(-1.0));
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"元素蓄力", (yuanSu - 30));
                player.setLevel((int) (yuanSu - 30));
                BlockDisplay block = player.getWorld().spawn(player.getLocation().clone().add(new Vector(-0.5,1,-0.5)), BlockDisplay.class);
                Zombie zombie = player.getWorld().spawn(player.getLocation().clone().add(new Vector(0,1,0)), Zombie.class);
                zombie.setBaby();zombie.setSilent(true);zombie.setInvisible(true);zombie.setAI(false);zombie.addScoreboardTag("Ice_LianHua");
                block.setBlock(Material.BLUE_ORCHID.createBlockData());
                block.setGlowing(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!zombie.isDead()){
                            zombie.setHealth(0);
                            zombie.getWorld().spawnParticle(Particle.DUST,zombie.getLocation(),200,4,0.1,4,0,new Particle.DustOptions(Color.fromRGB(0,255,255), 1F),true);
                            zombie.getWorld().spawnParticle(Particle.SNOWFLAKE,zombie.getLocation().clone().add(new Vector(0,1,0)),80,0,0,0,1,null,true);
                            for(Entity entity : zombie.getNearbyEntities(4,1,4)){
                                if(entity instanceof LivingEntity livingEntity && !livingEntity.getScoreboardTags().contains("GanYu")){
                                    livingEntity.damage(12);
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,40,1));
                                }
                            }
                        }
                        block.remove();
                    }
                }.runTaskLater(plugin,60);
            }
        }
        if(playerUseSPItem(player,"毒之狂潮",10)){
            event.setCancelled(true);
            for(Entity entity : player.getNearbyEntities(5,5,5)){
                LivingEntity effectedEntity = (LivingEntity) entity;
                effectedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON,80,1));
            }
            for(Entity entity : player.getNearbyEntities(8,8,8)){
                LivingEntity effectedEntity = (LivingEntity) entity;
                effectedEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON,40,0));
                effectedEntity.removePotionEffect(PotionEffectType.ABSORPTION);
                effectedEntity.addScoreboardTag("Poison_Mark");
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        effectedEntity.removeScoreboardTag("Poison_Marker");
                    }
                }.runTaskLater(plugin,600);
                PotionEffect effect = effectedEntity.getPotionEffect(PotionEffectType.INVISIBILITY);
                if(effect != null && effect.getDuration() < 10000)
                    effectedEntity.removePotionEffect(PotionEffectType.INVISIBILITY);
                Function.addSkillPower(player,"毒素充能",1800,40);
            }
        }
        if(Function.isHoldingSPItem(player,"蛇腹")){
            event.setCancelled(true);
            if(!PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"蛇腹")) return;
            if(!Function.isHasPower(player,"毒素充能",1000,"§a毒素能量不足！")) return;
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.addScoreboardTag("Snake_DuSu");
            armorStand.setSmall(true);
            armorStand.setInvisible(true);
            Function.reducePlayerPower(player,"毒素充能",1000);
            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_AMBIENT,4,1);
            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_AMBIENT,4,2);
            player.sendTitle("§a不要妨碍我！","",20,40,20);
            for(Entity entity : player.getNearbyEntities(6,3,6)){
                if(entity instanceof Player player1){
                    if(player1.equals(player)) return;
                    player.sendTitle("§a爬过来受死！","",20,40,20);
                }
                if(entity instanceof LivingEntity effectedEntity){
                    effectedEntity.addScoreboardTag("Poison_Mark");
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            effectedEntity.removeScoreboardTag("Poison_Marker");
                        }
                    }.runTaskLater(plugin,600);
                }
            }
            PlayerDataSave.setPlayerSkillCoolDownTime(player,"蛇腹",10);
        }
        if(playerUseSPItem(player,"毒爆",60)){
            event.setCancelled(true);
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            Function.executeCommand(player,"console: effect %player% clear");
            player.removeScoreboardTag("Poison_Resist");
            for(Entity entity : player.getNearbyEntities(10,10,10)){
                if(entity.equals(player)) return;
                LivingEntity effectedEntity = (LivingEntity) entity;
                if(effectedEntity.getScoreboardTags().contains("Poison_Marker")){
                    effectedEntity.damage(16,player);
                    effectedEntity.removeScoreboardTag("Poison_Marker");
                }
                for(Entity anotherEntity : player.getNearbyEntities(10,10,10)) {
                    if(anotherEntity.equals(entity) || anotherEntity.equals(player)) return;
                    LivingEntity anotherEffectedEntity = (LivingEntity) entity;
                    anotherEffectedEntity.damage(4,player);
                }
            }
            Function.addSkillPower(player,"毒素充能", 1800,200);
        }
        if(playerUseSPItem(player,"冰雨",1)){
            event.setCancelled(true);
            int skillCost = 100;
            long yuanSu = PlayerDataSave.getPlayerPassiveSkillRecords(player,"元素蓄力");
            if(yuanSu < skillCost){
                player.sendMessage("§b§l元素能量不足");
            }
            else{
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"元素蓄力", (yuanSu - skillCost));
                player.setLevel((int) (yuanSu - skillCost));
                BlockDisplay block = player.getWorld().spawn(player.getLocation().clone().add(new Vector(-0.5,1,-0.5)), BlockDisplay.class);
                WitherSkeleton skeleton = player.getWorld().spawn(player.getLocation().clone().add(new Vector(0,1,0)), WitherSkeleton.class);
                skeleton.setSilent(true);skeleton.setInvisible(true);skeleton.setAI(false);skeleton.addScoreboardTag("Ice_Big");
                Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(60);
                skeleton.setHealth(60);
                Objects.requireNonNull(skeleton.getEquipment()).clear();
                block.setBlock(Material.ICE.createBlockData());
                block.setGlowing(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!skeleton.isDead()){
                            skeleton.setHealth(0);
                        }
                        block.remove();
                    }
                }.runTaskLater(plugin,220);
            }
        }
        if(Function.isHoldingSPItem(player,"尸体派对")){
            event.setCancelled(true);
            if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"尸体派对")){
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ZOMBIE_DEATH,10,1);
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,player.getLocation(),50,1,1,1,1,null,true);
                Team zombieTeam;
                boolean isExtraTeam = false;
                if(scoreboard.getEntryTeam(player.getName()) == null){
                    String teamName = "Team_" + player.getName();
                    scoreboard.registerNewTeam(teamName);
                    zombieTeam = scoreboard.getTeam(teamName);
                    if (zombieTeam != null) {
                        zombieTeam.setAllowFriendlyFire(false);
                    }
                    Objects.requireNonNull(zombieTeam).addEntry(player.getName());
                    isExtraTeam = true;
                }else{
                    zombieTeam = scoreboard.getEntryTeam(player.getName());
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,10*20,0));
                for(int i = 0; i < 8; i++){
                    Zombie zombie = player.getWorld().spawn(player.getLocation(),Zombie.class);
                    Objects.requireNonNull(zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(15);
                    Objects.requireNonNull(zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(3);
                    Objects.requireNonNull(zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.4);
                    zombie.addScoreboardTag("Summoned_Zombie");
                    Objects.requireNonNull(zombieTeam).addEntry(String.valueOf(zombie.getUniqueId()));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            zombie.setHealth(0);
                        }
                    }.runTaskLater(plugin,200);
                }

                boolean finalIsExtraTeam = isExtraTeam;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(finalIsExtraTeam) zombieTeam.unregister();
                    }
                }.runTaskLater(plugin,200);

                PlayerDataSave.setPlayerSkillCoolDownTime(player,"尸体派对",40);
            }
        }

        if(playerUseSPItem(player,"道士之剑",15)) {
            String skillName = Function.getItemNameWithoutColor(player.getInventory().getItemInOffHand());
            switch (skillName) {
                case "风 - 追杀" -> {
                    Predicate<Entity> predicate = x -> !x.equals(player);

                    // 发射射线
                    RayTraceResult result = player.getWorld().rayTrace(
                            player.getEyeLocation(), // 起始点
                            player.getLocation().getDirection(),               // 方向向量
                            40,                     // 最大距离
                            FluidCollisionMode.NEVER, // 流体模式
                            true,                    // 忽略非可视方块
                            0.3,                     // 检测范围（宽度）
                            predicate              // 过滤器
                    );

                    // 检查是否有实体被射线命中
                    if (result != null && result.getHitEntity() != null) {
                        player.setVelocity(Function.calculateVelocity(player.getLocation().toVector(),result.getHitEntity().getLocation().toVector(),0.8,0.315));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,60,1,true,true));
                        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
                        player.getWorld().spawnParticle(Particle.CLOUD,player.getLocation().clone().add(0,0.2,0),50,0.2,0,0.2,0.15);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,2 * 20,0,true,true));
                        PlayerDataSave.setPlayerSkillCoolDownTime(player,"道士之剑",10);
                    } else {
                        Function.sendPlayerSystemMessage(player,"你没有指向一个目标");
                        PlayerDataSave.setPlayerSkillCoolDownTime(player,"道士之剑",0);
                    }
                }
                case "鬼 - 吸血" -> {
                    player.setExp(0);
                    player.addScoreboardTag("Taoist_attack_regeneration");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,3 * 20,1,true,true));
                    player.getWorld().spawnParticle(Particle.CHERRY_LEAVES,player.getLocation().clone().add(0,1,0),50,1,1,1);
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BAT_DEATH,1,1);
                }
                case "禁 - 狼嚎" -> {
                    player.setExp(1);
                    player.setHealth(player.getHealth() / 2);
                    player.damage(1);
                    player.addScoreboardTag("Taoist_Wolf");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,10 * 20,2,true,true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,10 * 20,0,true,true));
                    player.getWorld().spawnParticle(Particle.RAID_OMEN,player.getLocation().clone().add(0,1,0),50,0.5,1,0.5,0.2);
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WOLF_HOWL,1,1);
                    PlayerDataSave.setPlayerSkillCoolDownTime(player,"道士之剑",30);
                }
                case "剑 - 锐利" -> {
                    PlayerDataSave.setPlayerSkillCoolDownTime(player,"道士之剑",5);
                    player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,player.getLocation().clone().add(0,1,0),50,1,1,1);
                    for(int i = 0; i < 5; i++) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,1);
                            }
                        }.runTaskLater(QinKitPVPS.getPlugin(),i);
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,2 * 20,0,true,true));
                    for (Entity entity : player.getNearbyEntities(6,3,6)) {
                        if(entity.equals(player)) continue;
                        if(entity instanceof LivingEntity lv) {
                            lv.damage(8, player);
                            lv.setVelocity(entity.getVelocity().add(player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize()).multiply(-1.6).setY(0.6));
                        }
                    }
                    for (int k = 0; k < 20; k++) {
                        int finalK = k;
                        // 获取玩家的朝向角度
                        float yaw = player.getLocation().getYaw() - 60; // 获取偏航角（单位：度）
                        double playerAngle = Math.toRadians(yaw);  // 转换为弧度，用于三角函数计算
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Location center = player.getLocation().clone().add(0, 1, 0);  // 圆心的位置
                                double radius = 3;  // 设置圆的半径
                                int segments = 20;  // 10个小段来完成一个完整的圆形

                                // 计算当前段的起始角度和结束角度
                                double startAngle = playerAngle + (2 * Math.PI * finalK / segments); // 起始角度
                                double endAngle = playerAngle + (2 * Math.PI * (finalK + 1) / segments); // 结束角度

                                // 生成当前段的粒子
                                for (double angle = startAngle; angle <= endAngle; angle += (endAngle - startAngle) / segments) {
                                    double x = radius * Math.cos(angle);  // x坐标
                                    double z = radius * Math.sin(angle);  // z坐标

                                    // 在圆上创建粒子
                                    Location particleLocation = center.clone().add(x, 0, z);
                                    player.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 1.35f), true);
                                }
                            }
                        }.runTaskLater(QinKitPVPS.getPlugin(), finalK / 3);  // 延迟2 * k个ticks
                    }
                }
            }
        }
        if(playerUseSPItem(player,"死神之镰",4)) {
            event.setCancelled(true);
            player.setVelocity(player.getLocation().getDirection().multiply(1.2).add(new Vector(0,0.2,0)));
            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_SHOOT,1,1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<LivingEntity> effectEntities = new ArrayList<>();
                    for(int i = 0; i < 30; i++){
                        double pai = 3.14159265 * 2;
                        double x = (pai / 30) * i;
                        double cosX = Math.cos(x);
                        double sinX = Math.sin(x);
                        double rate = 2;
                        Location loc = player.getLocation().clone().add(0,1,0);
                        Location aimLoc = loc.clone().add(player.getLocation().getDirection().multiply(2));
                        for(Entity entity : Objects.requireNonNull(aimLoc.getWorld()).getNearbyEntities(aimLoc,2,2,2)) {
                            if(entity instanceof LivingEntity le) {
                                if(effectEntities.contains(le)) continue;
                                if(le.equals(player)) continue;
                                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,3 * 20,2));
                                le.damage(10,player);
                                effectEntities.add(le);
                            }
                        }
                        if(loc.clone().add(sinX * rate,player.getLocation().getDirection().multiply(2).getY(), cosX * rate).distance(aimLoc) >= 2) continue;
                        if(i % 2 == 0) player.getWorld().spawnParticle(Particle.DUST,loc.clone().add(sinX * rate,player.getLocation().getDirection().multiply(2).getY(), cosX * rate),1,0,0,0,0,new Particle.DustOptions(Color.fromRGB(87, 66, 102), 2F));
                        else player.getWorld().spawnParticle(Particle.DUST,loc.clone().add(sinX * rate,player.getLocation().getDirection().multiply(2).getY(), cosX * rate),1,0,0,0,0,new Particle.DustOptions(Color.fromRGB(176, 164, 227), 2F));
                    }
                    if(effectEntities.isEmpty()) PlayerDataSave.setPlayerSkillCoolDownTime(player,"死神之镰",10);
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,1);
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_BREAK_BLOCK,1,1);
                }
            }.runTaskLater(plugin,12);
        }

        if(playerUseSPItem(player,"失重核心",40)) {
            event.setCancelled(true);
            Location handLocation = player.getLocation();
            handLocation.setY(handLocation.getY() + 1.0);

            List<Entity> list = player.getNearbyEntities(10,10,10);
            for(int i = 0; i < 15; i++){
                double pai = 3.14159265 * 2;
                double x = (pai / 15) * i;
                double cosX = Math.cos(x);
                double sinX = Math.sin(x);
                double rate = 3;
                ShulkerBullet entity = player.getWorld().spawn(handLocation.clone().add(sinX * rate,0, cosX * rate), ShulkerBullet.class);
                entity.setCustomName(player.getName());
                entity.addScoreboardTag("Astronaut_Bullet");
                if(list.isEmpty()) continue;
                int newI = i % list.size();
                if(!list.get(newI).equals(player))
                    entity.setTarget(list.get(newI));
            }

        }
        if(playerUseSPItem(player,"失重引擎",5)) {
            event.setCancelled(true);
            if(player.getPotionEffect(PotionEffectType.SLOW_FALLING) == null || player.getPotionEffect(PotionEffectType.JUMP_BOOST) == null){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,20*99999,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,20*99999,4));
                player.sendTitle("","§7失重模式开启",20,40,20);
            } else if (player.getPotionEffect(PotionEffectType.SLOW_FALLING) != null && player.getPotionEffect(PotionEffectType.JUMP_BOOST) != null) {
                player.removePotionEffect(PotionEffectType.SLOW_FALLING);
                player.removePotionEffect(PotionEffectType.JUMP_BOOST);
                player.sendTitle("","§7失重模式关闭",20,40,20);
            }
        }

        if(playerUseSPItem(player, "风驱",25)) {
            event.setCancelled(true);
            Location handLocation = player.getLocation();
            handLocation.setY(handLocation.getY() + 1.0);

            for(int i = 0; i < 20; i++){
                double pai = 3.14159265 * 2;
                double x = (pai / 20) * i;
                double cosX = Math.cos(x);
                double sinX = Math.sin(x);
                double rate = 0.5;

                WindCharge wind = player.getWorld().spawn(handLocation.clone().add(sinX * rate,2, cosX * rate), WindCharge.class);
                wind.setVelocity(wind.getLocation().subtract(handLocation).toVector().normalize().setY(-0.6));

            }
        }

    }
    private void useGunSkill(Player player, double damage){
        // 获取玩家的视线方向
        Vector direction = player.getEyeLocation().getDirection();
        Predicate<Entity> predicate = x -> !x.getName().equals(player.getName());

        // 创建一条射线
        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation().clone().add(direction.multiply(2)), // 起始点
                direction,               // 方向向量
                100,                     // 最大距离
                1,                     // 检测范围（宽度）
                predicate                    // 过滤器
        );


        if (result != null && result.getHitEntity() instanceof LivingEntity target) {
            // 检测到生物，对其造成伤害
            target.damage(damage,player); // 造成8点伤害
            target.setNoDamageTicks(0);
            if(damage == 5){
                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,5*20,1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,5*20,1));
                TNTPrimed tnt = target.getWorld().spawn(target.getLocation(),TNTPrimed.class);
                tnt.setFuseTicks(0);
                tnt.setYield(1.0f);
            }
        }

        if(result != null && (result.getHitBlock() != null || result.getHitEntity() != null)){
            Vector rayStart = player.getEyeLocation().toVector();
            Vector rayEnd = result.getHitPosition();
            if(damage == 17){
                ShowGunParticle1(player, rayStart, rayEnd);
            }else{
                ShowGunParticle2(player,rayStart,rayEnd);
            }

        }else{
            Vector rayStart = player.getEyeLocation().toVector();
            Vector rayEnd = player.getEyeLocation().add(direction.multiply(300)).toVector();
            if(damage == 17){
                ShowGunParticle1(player, rayStart, rayEnd);
            }else{
                ShowGunParticle2(player,rayStart,rayEnd);
            }
        }
    }
    private void ShowGunParticle1(Player player, Vector rayStart, Vector rayEnd) {
        double distance = rayStart.distance(rayEnd);
        Vector directionNormalized = rayEnd.subtract(rayStart).normalize();

        for (double i = 0; i < distance; i += 1.5) {
            Vector particleLocation = rayStart.clone().add(directionNormalized.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.END_ROD, particleLocation.toLocation(player.getWorld()), 10,1,1,1,0.3);
        }
    }
    private void ShowGunParticle2(Player player, Vector rayStart, Vector rayEnd){
        double distance = rayStart.distance(rayEnd);
        Vector directionNormalized = rayEnd.subtract(rayStart).normalize();

        for (double i = 0; i < distance; i += 1.5) {
            Vector particleLocation = rayStart.clone().add(directionNormalized.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, particleLocation.toLocation(player.getWorld()), 1,0.5,0.5,0.5,0.5);
        }
    }
    private boolean playerUseSPItem(Player player, String skillName, double coolDown){
        boolean ifYes = Function.isHoldingSPItem(player,skillName);
        if(ifYes){
            ifYes = PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,skillName);
            if(ifYes){
                PlayerDataSave.setPlayerSkillCoolDownTime(player, skillName, coolDown);
            }
        }

        return ifYes;
    }
}
