package org.NoiQing.EventListener.SkillListeners;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import org.bukkit.util.Vector;

import java.util.*;

public class PassiveSkillsListener implements Listener {
    private final QinKitPVPS plugin;
    private final Objective activeSkill;
    public PassiveSkillsListener(QinKitPVPS plugin){
        this.plugin = plugin;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        this.activeSkill = scoreboardManager.getMainScoreboard().getObjective("reload");

    }

    //综合玩家抵消1.8攻击被动技能
    @EventHandler
    public void DecreaseDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            if(!WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world")).equals("§b春雨") && !Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"))).equals("梅雨")){
                if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                    event.setDamage(event.getDamage() * 0.75);
                else if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
                    event.setDamage(event.getDamage() * 0.85);
            }
        }
    }

    @EventHandler
    public void addParticle(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player player){
            player.getWorld().spawnParticle(Particle.ENCHANTED_HIT,player.getLocation().clone().add(0,1,0),25,0.5,1,0.5,0.25,null,false);
        }
    }

    //综合削弱TNT和火球爆炸伤害
    @EventHandler
    public void DamageCutSystem(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Fireball f) {
            if(f.getScoreboardTags().contains("Tank_Fireball")) {
                e.setDamage(e.getDamage() * 0.8);
            }
        } else if (e.getDamager() instanceof TNTPrimed t) {
            if(t.getScoreboardTags().contains("Short_TNT")) {
                e.setDamage(e.getDamage() * 0.8);
            }
        }
    }

    //战 士 被 动 技 能
    @EventHandler
    public void FighterPassiveSkill(PlayerDeathEvent event){
        Player player = event.getEntity();
        Player killer = PlayerDataSave.getLastAttackPlayerRecord(player);
        if(killer != null && killer.getScoreboardTags().contains("Fighter")){
            Function.addPotionEffect(killer,PotionEffectType.ABSORPTION,900000*20,1);
            Function.addPotionEffect(killer,PotionEffectType.RESISTANCE,5*20,1);
            Function.addPotionEffect(killer,PotionEffectType.REGENERATION,5*20,1);
            killer.getWorld().playSound(killer.getLocation(),Sound.ENTITY_HORSE_DEATH,0.2f,0.5F);
        }
    }

    //弓 箭 手 被 动 技 能
    @EventHandler
    public void ArcherPassiveSkill(EntityDamageByEntityEvent event){

        if(event.getDamager() instanceof Arrow arrow){
            if(arrow.getShooter() instanceof Player damager){
                if (damager.getScoreboardTags().contains("Archer")) {
                    addPotionEffect(damager,PotionEffectType.REGENERATION,2*20,0);
                    damager.setHealth(Math.min(damager.getHealth()+1.5,Function.getPlayerMaxHealth(damager)));
                }
            }
        }
    }

    @EventHandler
    public void ArcherPassiveSkill2(EntityShootBowEvent event){
        if(event.getEntity().getScoreboardTags().contains("Archer") && event.getEntity() instanceof Player){
            if(event.getProjectile() instanceof Arrow arrow){
                arrow.setDamage(3.05);
            }
        }
    }

    //快 刀 手 被 动 技 能
    @EventHandler
    public void FastSwordPassiveSkillFix(EntityDamageEvent event){
        if(event.getEntity() instanceof Player player){
            if(player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                player.setMaximumNoDamageTicks(20);
            }
        }
    }
    @EventHandler
    public void FastSwordPassiveSkill(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof LivingEntity lv){
            if(event.getDamager() instanceof Player damager
                    && damager.getScoreboardTags().contains("FastSword")
                    && !damager.getScoreboardTags().contains("FastSword_S")){
                if(Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"))).equals("月蚀")){
                    lv.setMaximumNoDamageTicks(14);
                }else{
                    lv.setMaximumNoDamageTicks(7);
                }
                lv.setNoDamageTicks(0);
            }else if((event.getDamager() instanceof Player damager && damager.getScoreboardTags().contains("FastSword_S")) ||
                    event.getDamager().getScoreboardTags().contains("TWorld_Arrow") ||
                    event.getDamager().getScoreboardTags().contains("allay_damage") ||
                    event.getDamager().getScoreboardTags().contains("pvz_plant") ||
                    event.getDamager().getScoreboardTags().contains("pvz_zombie")){
                lv.setMaximumNoDamageTicks(0);
                lv.setNoDamageTicks(0);
            }else if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                if(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world")) == null){
                    lv.setMaximumNoDamageTicks(10);
                    return;
                }
                if(Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"))).equals("月蚀") || Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(lv.getWorld())).equals("春雨") || Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(lv.getWorld())).equals("梅雨")){
                    lv.setMaximumNoDamageTicks(19);
                }else
                    lv.setMaximumNoDamageTicks(10);
            }
        }
    }

    //夺 命 丘 比 特 被 动 技 能
    @EventHandler
    public void SniperPassiveSkill(EntityShootBowEvent event){
        if(event.getEntity().getScoreboardTags().contains("Sniper") && event.getEntity() instanceof Player player){
            if(event.getProjectile() instanceof Arrow arrow && player.getInventory().getItemInMainHand().getEnchantments().get(Enchantment.POWER) >= 8){
                arrow.addScoreboardTag("Sniper_Ammo");
                arrow.setCritical(true);
                arrow.setGravity(false);
                addPotionEffect(player,PotionEffectType.SPEED,2*20,1);
                if(event.getEntity().getScoreboardTags().contains("Love_Sniper"))  {
                    arrow.removeScoreboardTag("Sniper_Ammo");
                    arrow.addScoreboardTag("Love_Arrow");
                }
            }
        }
    }

    @EventHandler
    public void onLoveArrowHit(EntityDamageByEntityEvent e) {
        if(e.getDamager().getScoreboardTags().contains("Love_Arrow")) {
            e.setDamage(1);
            if(e.getEntity() instanceof LivingEntity le) {
                le.damage(15);
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,100,3));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,40,10));
                le.addScoreboardTag("Loved_Sniper");
            }
        }
    }

    //坦 克 被 动 技 能
    @EventHandler
    public void TankPassiveSkill(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getScoreboardTags().contains("Tank")) {
                Random random = new Random();
                int randomNumber = random.nextInt(100);
                if (randomNumber <= 2) {
                    addPotionEffect(player, PotionEffectType.RESISTANCE, 2 * 20, 0);
                }
            }
        }
    }

    //忍 者 被 动 技 能
    @EventHandler
    public void NinjaPassiveKill(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player player && player.getScoreboardTags().contains("ninja") && player.getScoreboardTags().contains("INVIS")){
            player.removePotionEffect(PotionEffectType.STRENGTH);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            if(event.getEntity() instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20,2));
            }
        }
    }

    private static void addPotionEffect(Player player, PotionEffectType type, int duration, int amplifier){
        List<PotionEffect> potionEffectList = new ArrayList<>();
        PotionEffect effect1 = new PotionEffect(type, duration, amplifier);
        potionEffectList.add(effect1);
        Function.executeEffects(player, potionEffectList);
    }

    //影魔被动技能
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if(event.getPlayer().getScoreboardTags().contains("shadowF2") && event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            event.setCancelled(true);
        }
    }
    //末 影 人 被 动 技 能
    @EventHandler
    public void EnderManPassiveSkill(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL){
                if(player.getScoreboardTags().contains("Teleporter")){
                    if(player.getTargetBlockExact(75)!=null){
                        Vector vector = player.getLocation().getDirection();
                        Location location = player.getLocation();
                        Particle particle = Particle.PORTAL;
                        player.getWorld().spawnParticle(particle,location.add(0,1,0),50,0.5,1,0.5);
                        Location blockLocation = Objects.requireNonNull(player.getTargetBlockExact(100)).getLocation();
                        blockLocation.setDirection(vector);
                        blockLocation.add(vector.multiply(-1));
                        blockLocation.add(0,1,0);
                        player.teleport(blockLocation);
                        player.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                        player.getWorld().playSound(blockLocation, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                        player.getWorld().spawnParticle(particle,blockLocation,100,0.25,0.75,0.25);
                        player.setFallDistance(0);
                        Function.addPotionEffect(player,PotionEffectType.SLOWNESS,3*20,2);
                        Function.addPotionEffect(player,PotionEffectType.BLINDNESS,20,1);
                        event.getItem().setAmount(event.getItem().getAmount()-1);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    //丛 林 射 手 被 动 技 能
    @EventHandler
    public void CLLSPassiveSkill(ProjectileLaunchEvent event){
        if(event.getEntity().getShooter() instanceof Player player && event.getEntity() instanceof Arrow arrow
                && player.getScoreboardTags().contains("Clls")){
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    //鬼 魂 被 动 技 能
    @EventHandler
    public void GhostPassiveSkill(ProjectileHitEvent event){
        if(event.getHitEntity() != null && event.getHitEntity() instanceof Player player && player.getScoreboardTags().contains("Ghost")){
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.addScoreboardTag("Ghost_Shot");
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VEX_DEATH,1,1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20,4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,100,0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,100,1));
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(!player.getScoreboardTags().contains("Ghost_Shot")){
                        player.setArrowsInBody(0);
                    }
                }
            }.runTaskLater(plugin,20*5 + 1);
            activeSkill.getScore(player.getName()).setScore(0);
        }
    }
    @EventHandler
    public void GhostPassiveSkill3(EntityDamageEvent event){
        if(event.getEntity().getScoreboardTags().contains("Ghost") && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            event.setDamage(event.getDamage()*1.5);
        }
    }

    //花海被动技能
    @EventHandler
    public void HuaHaiPassiveSkill(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        if(!player.getScoreboardTags().contains("HuaHai")) return;
        player.getInventory().addItem(flowerArrow(item.getItemStack().getType()));
        event.getItemDrop().remove();
    }
    @EventHandler
    public void onHuaHaiShootIceBow(EntityShootBowEvent event){
        if(event.getEntity().getScoreboardTags().contains("HuaHai") && event.getEntity() instanceof Player player){
            if(event.getConsumable() != null && event.getConsumable().getType().equals(Material.TIPPED_ARROW)){
                if(Function.getNameWithoutColor(event.getConsumable().getItemMeta().getDisplayName()).equals("Ice")){
                    player.sendMessage("awa");
                }
            }
        }
    }
    //黄昏使者被动技能
    @EventHandler
    public void MinerPassiveSkill(EntityDamageEvent event){
        if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("Miner")){
            if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
            long attackedTimes = PlayerDataSave.getPlayerPassiveSkillRecords(player,"破甲");
            long currentAttackedTimes = attackedTimes + 1;
            if(!player.getScoreboardTags().contains("Miner_Broke")){
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"破甲",currentAttackedTimes);
            }
            if(currentAttackedTimes >= 15){
                ItemStack previousHelmet = player.getInventory().getHelmet();
                ItemStack previousChestplate = player.getInventory().getChestplate();
                ItemStack previousLeggings = player.getInventory().getLeggings();
                ItemStack previousBoots = player.getInventory().getBoots();
                player.getWorld().spawnParticle(Particle.FALLING_HONEY,player.getLocation().add(0,1,0),100,0.5,0.5,0.5);
                player.getInventory().setArmorContents(null);
                player.getWorld().playSound(player.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                player.removePotionEffect(PotionEffectType.SPEED);
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"破甲",0L);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(player.getScoreboardTags().contains("Miner") && player.getScoreboardTags().contains("Miner_Broke")){
                            player.getInventory().setHelmet(previousHelmet);
                            player.getInventory().setChestplate(previousChestplate);
                            player.getInventory().setLeggings(previousLeggings);
                            player.getInventory().setBoots(previousBoots);
                            Function.addPotionEffect(player,PotionEffectType.SPEED,99999999,1);
                            player.getWorld().playSound(player.getLocation(),Sound.ITEM_ARMOR_EQUIP_LEATHER,1,1);
                            player.removeScoreboardTag("Miner_Broke");
                        }
                    }
                }.runTaskLater(plugin, 8 * 20);
                player.addScoreboardTag("Miner_Broke");
            }
        }
    }

    //火焰豌豆 被 动 技 能
    @EventHandler
    public void FireShooterPassiveSkill(ProjectileLaunchEvent event){
        if(event.getEntity().getShooter() instanceof Player player && event.getEntity() instanceof Arrow arrow
                && player.getScoreboardTags().contains("FireMan")){
            long arrowShootTimes = PlayerDataSave.getPlayerPassiveSkillRecords(player,"火焰豌豆");
            long currentArrowShootTimes = arrowShootTimes + 1;
            if(currentArrowShootTimes == 4){
                arrow.addScoreboardTag("FirePea_Fire");
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BLAZE_AMBIENT,1,1);
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"火焰豌豆",currentArrowShootTimes);
            }else if(currentArrowShootTimes >= 5 || player.getScoreboardTags().contains("FireMan_S")){
                arrow.addScoreboardTag("FirePea");
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BLAZE_SHOOT,1,1);
                PlayerDataSave.clearPlayerPassiveSkillRecords(player);
            }else{
                arrow.addScoreboardTag("FirePea_Fire");
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"火焰豌豆",currentArrowShootTimes);
            }
        }
    }
    @EventHandler
    public void FireShooterPassiveSkill2(EntityDamageByEntityEvent event){
        Entity victim = event.getEntity();
        if(event.getDamager() instanceof Arrow arrow){
            if(arrow.getScoreboardTags().contains("FirePea")){
                TNTPrimed entity = victim.getWorld().spawn(victim.getLocation().clone().add(0,1.5,0), TNTPrimed.class);
                entity.setCustomName(victim.getName());
                entity.setYield(2f);
                entity.setFuseTicks(0);
                entity.addScoreboardTag("FirePea_TNT");
                victim.setFireTicks(80);
            }else if(arrow.getScoreboardTags().contains("FirePea_Fire")){
                victim.setFireTicks(80);
            }
        }
    }
    @EventHandler
    public void FireShooterPassiveSkill3(EntityDamageByEntityEvent event) {
        if(event.getDamager().getScoreboardTags().contains("FirePea_TNT"))
            event.setDamage(event.getDamage() * 0.75);
    }

    //空袭弩手被动技能
    @EventHandler
    public void SkyPassiveSkill(EntityShootBowEvent event){
        if(event.getEntity().getScoreboardTags().contains("Sky") && event.getEntity() instanceof Player){
            if(event.getProjectile() instanceof Arrow arrow){
                arrow.addScoreboardTag("Sky");
                arrow.setDamage(arrow.getDamage() * 0.6);
            }else if(event.getProjectile() instanceof SpectralArrow spectralArrow){
                spectralArrow.addScoreboardTag("Sky");
            }
        }
    }

    @EventHandler
    public void SkyPassiveSkill2(EntityDamageByEntityEvent e) {
        if(!e.getDamager().getScoreboardTags().contains("Sky")) return;
        if(e.getDamager() instanceof Arrow a) {
            Player p = (Player) a.getShooter();
            e.setCancelled(true);
            if(e.getEntity() instanceof LivingEntity entity) {
                entity.damage(e.getDamage());
                if(e.getEntity() instanceof Player en) {
                    PlayerDataSave.setLastAttackPlayerRecord(en,p);
                    en.setSprinting(true);
                }
            }
        }

    }

    //雪人被动技能
    @EventHandler
    public void IceManPassiveSkill2(EntityShootBowEvent event){
        if(event.getEntity().getScoreboardTags().contains("IceMan_D") && event.getEntity() instanceof Player){
            if(event.getProjectile() instanceof Arrow arrow){
                arrow.addScoreboardTag("IceMan_Arrow");
            }
        }
    }
    @EventHandler
    public void IceManPassiveSkill3(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player && event.getDamager() instanceof Arrow arrow && arrow.getScoreboardTags().contains("IceMan_Arrow")){
            player.setFreezeTicks(player.getFreezeTicks() + 70);
            if(event.getEntity().isFrozen()){
                event.setDamage(event.getDamage() * 2);
                event.getEntity().getWorld().spawnParticle(Particle.ITEM_SNOWBALL,event.getEntity().getLocation().add(0,1,0),100,0.5,0.5,0.5);
            }
        }
    }
    @EventHandler
    public void onIceManSkilled(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Bat bat && bat.getScoreboardTags().contains("LauchBat")){
            Location hitLocation = event.getEntity().getLocation().add(0,1,0);
            event.getEntity().setFreezeTicks(140);
            generateIceBlocks(hitLocation);
        }
    }

    //狂战士被动技能
    @EventHandler
    public void warriorPassiveSkill(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player player && player.getScoreboardTags().contains("WarFight_Extra")){
            if(event.getEntity() instanceof Player victim && victim.getHealth() <= Function.getPlayerMaxHealth(player)/2.0){
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,2 * 20,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,2*20,0));
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WOLF_GROWL,1,1);
            }
        }
    }
    //渔夫被动技能
    @EventHandler
    public void onFishManHurt(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("FishMan")){
            if(event.getDamager() instanceof PufferFish)
                event.setCancelled(true);
            if(Function.getMainHandItem(player).getType().equals(Material.FISHING_ROD)){
                if(player.getInventory().getItem(0) != null && Objects.requireNonNull(player.getInventory().getItem(0)).getType().equals(Material.FISHING_ROD))
                    player.getInventory().setHeldItemSlot(1);
                else player.getInventory().setHeldItemSlot(0);
            }
        }
        if(event.getDamager().getScoreboardTags().contains("FishMan_Shoot") && event.getEntity() instanceof Player player){
            Function.addPotionEffect(player,PotionEffectType.SLOWNESS,40,1);
        }
    }
    @EventHandler
    public void onThrowRod(PlayerFishEvent event){
        Entity caught = event.getCaught();
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("FishMan") && PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"钓人")){
            if (caught != null) {
                caught.teleport(player.getLocation().add(player.getEyeLocation().getDirection().multiply(0.4).setY(0)));
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"钓人",1);
                event.getHook().remove();
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onFishManShoot(ProjectileLaunchEvent event){
        if(event.getEntity().getShooter() instanceof Player player && player.getScoreboardTags().contains("FishMan")){
            event.getEntity().addScoreboardTag("FishMan_Shoot");
        }
    }

    //医生被动技能
    @EventHandler
    public void onDoctorGetHurt(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("Doctor") && !player.getScoreboardTags().contains("Doctor_Hurt")){
            player.addScoreboardTag("Doctor_Hurt");
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.removeScoreboardTag("Doctor_Hurt");
                }
            }.runTaskLater(plugin,10*20);
        }
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){
        if(event.getEntity().getScoreboardTags().contains("Doctor_A")){
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getEntity().remove();
                }
            }.runTaskLater(plugin,8*20);
        }
    }

    //时停者
    @EventHandler
    public void onDioUseArrow(PlayerInteractEvent event) {
        if(!Function.isRightClicking(event)) return;
        Player player = event.getPlayer();
        if(!player.getScoreboardTags().contains("DIO")) return;
        if(event.getItem() != null && event.getItem().getType().equals(Material.ARROW)) {
            if(!player.getScoreboardTags().contains("TWorld")){
                if(!PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player,"飞刀")) return;
                PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player, "飞刀", 0.8);
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
            Arrow arrow = player.getWorld().spawn(player.getEyeLocation(),Arrow.class);
            arrow.setShooter(player);
            arrow.setCritical(true);
            if(!player.getScoreboardTags().contains("TWorld")) arrow.setDamage(arrow.getDamage() * 0.50);
            else arrow.setDamage(arrow.getDamage() * 0.80);
            arrow.setVelocity(player.getLocation().getDirection().multiply(3));
            if(!player.getScoreboardTags().contains("TWorld")) return;
            int reload = activeSkill.getScore(player.getName()).getScore();
            reload += 4;
            activeSkill.getScore(player.getName()).setScore(reload);
            Vector vector = arrow.getVelocity();
            new BukkitRunnable(){
                @Override
                public void run(){
                    arrow.setCustomName(player.getName());
                    arrow.setGravity(false);
                    arrow.setVelocity(new Vector(0.0,0.0,0.0));
                    arrow.addScoreboardTag("TWorld_Arrow");
                    arrow.addScoreboardTag("Vector_" + vector.getX() +"_" + vector.getY() + "_" + vector.getZ());
                    arrow.addScoreboardTag("Owner_"+ player.getName());
                }
            }.runTaskLater(plugin,1);
            refreshTimeStopArrow(player, reload);
        }
    }

    @EventHandler
    public void onDioAttackPlayer(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player player) {
            if(!player.getScoreboardTags().contains("TWorld")) return;
            int reload = activeSkill.getScore(player.getName()).getScore();
            reload += 10;
            activeSkill.getScore(player.getName()).setScore(reload);
            refreshTimeStopArrow(player,reload);
        }
    }

    @EventHandler
    public void onDioArrowShootPlayer(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Arrow arrow && arrow.getScoreboardTags().contains("StoppedArrow")) {
            if(event.getEntity() instanceof LivingEntity le)
                le.setNoDamageTicks(0);
        }
    }

    //魅魔和溺尸被动技能
    @EventHandler
    public void onMaidShootByArrow(ProjectileHitEvent event){
        if(event.getHitEntity() != null && event.getHitEntity().getScoreboardTags().contains("Maid_Skill")){
            if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof LivingEntity shooter && event.getHitEntity() instanceof LivingEntity maid) {
                MaidSkill(shooter, maid);
                event.setCancelled(true);
            }
        }
        if(event.getEntity() instanceof Trident && event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof LivingEntity shooter && shooter.getScoreboardTags().contains("Drowned")){
            Random random = new Random();
            if(random.nextInt(9) < 5){
                event.getEntity().getWorld().spawn(event.getEntity().getLocation(), LightningStrike.class);
            }
        }
    }

    //战争狂人技能取消衰落
    @EventHandler
    public void onReaperFall(EntityDamageEvent event){
        if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("Reaper_Falling")){
            if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                event.setCancelled(true);
        }else if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("Reaper")){
            if(!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;
            if(event.getDamage() >= 4){
                player.getWorld().playSound(player.getLocation(),Sound.BLOCK_ANVIL_LAND,0.7f,0.7f);
                player.getWorld().spawnParticle(Particle.EXPLOSION,player.getLocation().clone().add(0,1,0),50,4,1,4,0);
                for(Entity entity : player.getNearbyEntities(4,3,4)) {
                    if (entity.equals(player)) return;
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.damage(event.getFinalDamage() * 1.75, player);
                        livingEntity.setNoDamageTicks(0);
                        livingEntity.setVelocity(entity.getVelocity().add(player.getLocation().toVector().subtract(livingEntity.getLocation().toVector()).normalize()).multiply(-1.5).setY(0.5));
                    }
                }
            }

        }
    }

    @EventHandler
    public void onMaidGetAttackedWhileSkilled(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("Maid_Skill")){
            Entity damager8 = event.getDamager();
            if(damager8 instanceof LivingEntity damager){
                MaidSkill(damager, player);
                event.setCancelled(true);
            }
        }
    }

    //宇航员被动技能
    @EventHandler
    public void onAttackedByBullet(EntityDamageByEntityEvent event){
        if(!event.getEntity().getScoreboardTags().contains("Astronaut")) return;
        if(event.getDamager().getCustomName() != null && event.getDamager().getCustomName().equals(event.getEntity().getName()))
            event.setCancelled(true);
    }
    @EventHandler
    public void onAttackFloat(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.getPotionEffect(PotionEffectType.LEVITATION) == null) return;
            if(event.getDamager().getScoreboardTags().contains("Astronaut")) event.setDamage(event.getDamage() * 1.25);
            if(event.getDamager() instanceof Arrow arrow && arrow.getShooter()instanceof Player player &&player.getScoreboardTags().contains("Astronaut"))
                event.setDamage(event.getDamage() * 1.25);
        }
    }

    //土豆人被动技能
    @EventHandler
    public void onSlimeGetTNTExplosion(EntityDamageEvent event){
        if(event.getEntity().getScoreboardTags().contains("Slime_Mine") && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))){
            event.setCancelled(true);
        }
    }

    //爱之魅魔被动技能
    @EventHandler
    public void onLoveDemoAttacked(EntityDamageByEntityEvent event){
        if(!event.getDamager().getScoreboardTags().contains("Fallen_Love")) return;
        if(event.getEntity().getScoreboardTags().contains("LoveDemo_D") && event.getEntity() instanceof Player player){
            Random random = new Random();
            if(random.nextInt(8) == 0){
                Function.recoverHealth(player, 8);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_CAT_AMBIENT, 1F, 1.2F);
                player.getWorld().spawnParticle(Particle.HEART,player.getLocation().clone().add(0,1,0),8,1,1,1);
            }
        }
    }

    @EventHandler
    public void onAttackedByFireDragonFire(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof ArmorStand stand && stand.getScoreboardTags().contains("Firing")){
            event.getEntity().setFireTicks(60);
        }
    }

    @EventHandler
    public void onOctaneAttackedByArrow(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow && event.getEntity().getScoreboardTags().contains("Octane_Running")){
            event.setDamage(event.getDamage() * 1.2);
        }
    }

    @EventHandler
    public void onSlimeSpread(SlimeSplitEvent event){
        if(event.getEntity().getScoreboardTags().contains("JumpPad")){
            event.setCancelled(true);
        }
    }

    //甘雨
    @EventHandler
    public void onGanYuUseBow(PlayerInteractEvent event){
        if(event.getItem() != null){
            if(Objects.requireNonNull(event.getItem()).getType().equals(Material.BOW) && event.getPlayer().getScoreboardTags().contains("GanYu") && !event.getPlayer().getScoreboardTags().contains("GanYu_Charging")){
                event.getPlayer().addScoreboardTag("GanYu_Charging");
            }
        }
    }

    @EventHandler
    public void onGanYuNotUseBow(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("GanYu_Charging")){
            if(player.getInventory().getItem(event.getNewSlot()) != null){
                if(!Objects.requireNonNull(player.getInventory().getItem(event.getNewSlot())).getType().equals(Material.BOW)){
                    player.removeScoreboardTag("GanYu_Charging");
                    activeSkill.getScore(player.getName()).setScore(0);
                }
            }else{
                player.removeScoreboardTag("GanYu_Charging");
                activeSkill.getScore(player.getName()).setScore(0);
            }
        }
    }

    @EventHandler
    public void onGanYuShootBow(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player player && player.getScoreboardTags().contains("GanYu_Charging")){
            if(activeSkill.getScore(player.getName()).getScore() >= 30){
                event.getProjectile().addScoreboardTag("GanYu_IceArrow");
                player.getWorld().playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,1,2);
            }
            player.removeScoreboardTag("GanYu_Charging");
            activeSkill.getScore(player.getName()).setScore(0);
        }
    }

    @EventHandler
    public void OnGanYuArrowShoot(ProjectileHitEvent event){
        if(event.getEntity().getScoreboardTags().contains("GanYu_IceArrow")){
            if(event.getEntity().getShooter() instanceof Player player) {
                if(event.getHitEntity() !=null){
                    event.getHitEntity().getWorld().spawnParticle(Particle.DUST,event.getHitEntity().getLocation(),100,2,0.1,2,0,new Particle.DustOptions(Color.fromRGB(0,255,255), 1F),true);
                    event.getHitEntity().getWorld().spawnParticle(Particle.DUST,event.getHitEntity().getLocation().clone().add(new Vector(0,1,0)),50,1,0.5,1,0,new Particle.DustOptions(Color.fromRGB(0,255,255), 1F),true);
                    for (Entity entity : event.getHitEntity().getNearbyEntities(3, 2, 3)) {
                        if (entity instanceof LivingEntity livingEntity && !livingEntity.getScoreboardTags().contains("GanYu")) {
                            livingEntity.damage(8, player);
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 0));
                            addGanYuXuLi(player,100,5);
                        }
                    }
                    addGanYuXuLi(player,100,20);
                }
                if(event.getHitBlock() != null){
                    event.getHitBlock().getWorld().spawnParticle(Particle.DUST, event.getHitBlock().getLocation().clone().add(new Vector(0,1,0)),100,2,0.1,2,0,new Particle.DustOptions(Color.fromRGB(0,255,255), 1F),true);
                    for (Entity entity : event.getHitBlock().getWorld().getNearbyEntities(event.getHitBlock().getLocation().clone().add(0,1,0),3,2,3)) {
                        if (entity instanceof LivingEntity livingEntity && !livingEntity.getScoreboardTags().contains("GanYu")) {
                            livingEntity.damage(8, player);
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 0));
                            addGanYuXuLi(player,100,10);
                        }
                    }
                }
            }
        }else{
            if(event.getEntity().getShooter() instanceof Player player && player.getScoreboardTags().contains("GanYu")){
                if(event.getHitEntity() == null) return;
                addGanYuXuLi(player,100,5);
            }
        }
    }

    @EventHandler
    public static void onShuraigShootBow(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() instanceof Player player) {
            if(!player.getScoreboardTags().contains("SLG")) return;
            if(player.getScoreboardTags().contains("LMJS")) {
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_LIGHTNING_BOLT_IMPACT,1,1);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_LIGHTNING_BOLT_IMPACT,2,1);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_LIGHTNING_BOLT_IMPACT,0.5f,1);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_SHOOT,1,1);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,1);
                player.getWorld().spawnParticle(Particle.FLAME,player.getLocation().clone().add(0,1,0),20,1,1,1);
                event.getEntity().addScoreboardTag("LMJSDY");
            }else if(player.getScoreboardTags().contains("GGHL")) {
                event.getEntity().addScoreboardTag("GGHLDY");
            }
        }
    }
    public static void addGanYuXuLi(Player player,long max, long add){
        long record = PlayerDataSave.getPlayerPassiveSkillRecords(player,"元素蓄力");
        if (record <= max - add) {
            record += add;
            PlayerDataSave.setPlayerPassiveSkillRecords(player, "元素蓄力", record);
            player.setLevel((int)record);
        }
    }

    @EventHandler
    public void onEntityDead(EntityDeathEvent event){
        if (event.getEntity() instanceof Zombie zombie && zombie.getScoreboardTags().contains("Ice_LianHua")){
            for(Entity entity : zombie.getNearbyEntities(1,1,1)){
                if(entity instanceof BlockDisplay display){
                    display.remove();
                }
            }
        }else if(event.getEntity() instanceof WitherSkeleton skeleton && skeleton.getScoreboardTags().contains("Ice_Big")){
            for(Entity entity : skeleton.getNearbyEntities(1,1,1)){
                if(entity instanceof BlockDisplay display){
                    display.remove();
                }
            }
        }
    }



    private void generateIceBlocks(Location centerLocation) {
        int radiusX = 1; // 3x3 area has a radius of 1 (total size 3 blocks)
        int radiusY = 1; // 2 blocks high (total height 2 blocks)
        int radiusZ = 1;

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY - 1; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    Location blockLocation = centerLocation.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();

                    // Check if the block is air or a replaceable block
                    if (block.getType() == Material.AIR || block.getType().isAir()) {
                        if(!((x == 0 && z == 0) && (y == 0 || y == -1))){
                            block.setType(Material.ICE);

                            // Schedule the block to be melted after 10 seconds
                            scheduleMeltTask(blockLocation);
                        }
                    }
                }
            }
        }
    }
    private void scheduleMeltTask(Location blockLocation) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Block block = blockLocation.getBlock();
                if (block.getType() == Material.ICE) {
                    block.breakNaturally();
                    blockLocation.getBlock().setType(Material.AIR);
                }
            }
        }.runTaskLater(plugin, 80L); // 10 seconds (20 ticks per second)
    }
    private void MaidSkill(LivingEntity shooter, LivingEntity maid) {
        Location teleportLocation = shooter.getLocation().clone();
        double y = teleportLocation.getY();
        teleportLocation.add(shooter.getLocation().getDirection().normalize().multiply(-1.35));
        teleportLocation.setY(y);
        maid.teleport(teleportLocation);
        shooter.getWorld().playSound(teleportLocation, Sound.ENTITY_PILLAGER_CELEBRATE,1,1);
        maid.removeScoreboardTag("Maid_Skill");
        maid.removePotionEffect(PotionEffectType.SLOWNESS);
        maid.removePotionEffect(PotionEffectType.WEAKNESS);
        maid.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*3,2));
        maid.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,20*3,1));
        shooter.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20,0));
        activeSkill.getScore(maid.getName()).setScore(0);
    }

    private ItemStack flowerArrow(Material material){
        ItemStack specialArrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta potionMeta = (PotionMeta) specialArrow.getItemMeta();
        assert potionMeta != null;
        if(material.equals(Material.WITHER_ROSE)){
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 60, 2), true); // 3秒钟的凋零 III 效果
        }else if(material.equals(Material.BLUE_ORCHID)){
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0), true); // 3秒钟的凋零 III 效果
        }else if(material.equals(Material.ALLIUM)){
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION,60,2),true);
        }else if(material.equals(Material.CORNFLOWER)){
            potionMeta.setDisplayName("§b冲击矢车菊");
        }else if(material.equals(Material.LILY_OF_THE_VALLEY)){
            potionMeta.setDisplayName("垂头铃兰箭");
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS,60,2),true);
        }else if(material.equals(Material.DANDELION)){
            potionMeta.setDisplayName("火焰蒲公箭");
        }else if(material.equals(Material.POPPY)){
            potionMeta.setDisplayName("爱情玫瑰箭");
        }else if(material.equals(Material.AZURE_BLUET)){
            potionMeta.setDisplayName("分裂美耳草");
        }else if(material.equals(Material.CHORUS_FLOWER)){
            potionMeta.setDisplayName("传送紫菘花");
        }else if(material.equals(Material.GLOW_BERRIES)){
            potionMeta.setDisplayName("发光眩晕果");
        }else if(material.equals(Material.PITCHER_CROP)){
            potionMeta.setDisplayName("爆裂瓶子草");
        }else if(material.equals(Material.PINK_PETALS)){
            potionMeta.setDisplayName("活力樱花箭");
        }else{
            return null;
        }

        specialArrow.setItemMeta(potionMeta);
        return specialArrow;
    }

    private void refreshTimeStopArrow(Player player, int reload) {
        for(Entity entity : player.getWorld().getEntities()) {
            if(entity.getType().equals(EntityType.ARROW)) {
                Arrow stopArrow = (Arrow) entity;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!stopArrow.getScoreboardTags().contains("TWorld_Arrow")) return;
                        boolean playerOwn = false;

                        if(ifPlayerNotOwns(player, stopArrow.getScoreboardTags())) return;
                        for(String s : stopArrow.getScoreboardTags()) {
                            if(!s.startsWith("Vector")) continue;
                            String[] info = s.split("_");
                            Vector vector = new Vector(Double.parseDouble(info[1]), Double.parseDouble(info[2]), Double.parseDouble(info[3]));
                            stopArrow.setVelocity(vector);
                        }
                        stopArrow.setGravity(true);
                        stopArrow.removeScoreboardTag("TWorld_Arrow");
                        stopArrow.addScoreboardTag("StoppedArrow");
                    }
                }.runTaskLater(plugin, Math.max(100 - reload, 0));
            }
        }
        refreshTimeStopArmorStand(player, reload);
    }

    private void refreshTimeStopArmorStand(Player player, int reload) {
        for(Entity entity : player.getWorld().getEntities()) {
            if(entity.getType().equals(EntityType.ARMOR_STAND)) {
                ArmorStand stand = (ArmorStand) entity;
                if(!stand.getScoreboardTags().contains("TWorld_A")) continue;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(ifPlayerNotOwns(player, stand.getScoreboardTags())) return;
                        stand.remove();
                    }
                }.runTaskLater(plugin, Math.max(100 - reload, 0));
            }
        }
    }

    private boolean ifPlayerNotOwns(Player player, Set<String> tags) {
        boolean playerOwn = false;
        for(String s : tags) {
            if(!s.startsWith("Owner")) continue;
            s = s.replaceFirst("_","-");
            String[] info = s.split("-");
            if(info[1].equals(player.getName()))
                playerOwn = true;
        }
        return !playerOwn;
    }
}
