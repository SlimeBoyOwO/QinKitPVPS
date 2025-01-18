package org.NoiQing.BukkitRunnable;

import org.NoiQing.EventListener.Guns.GunsListener;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PassiveSkillRunnable extends BukkitRunnable {
    private final Objective activeSkill = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective("reload");
    private final QinKitPVPS plugin;
    public PassiveSkillRunnable(QinKitPVPS plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {

        //丘比特被动技能
        for(Player player : Bukkit.getOnlinePlayers()){

            //鬼魂被动叫声
            if (player.getScoreboardTags().contains("Ghost") && PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player, "鬼叫")) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VEX_AMBIENT,1,1);
                PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"鬼叫",4);
            }

            //末影人被动技能
            if(player.getScoreboardTags().contains("Teleporter")){
                if(Function.getPlayerItemAmount(player, Material.ENDER_PEARL) < 2){
                    if(PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player,"末影补给")){
                        ItemStack item = new ItemStack(Material.ENDER_PEARL);
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName("§d末影珍珠");
                            meta.setUnbreakable(true);
                            item.setItemMeta(meta);
                        }
                        player.getInventory().addItem(item);
                        PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"末影补给",30);
                    }
                }else if(Function.getPlayerItemAmount(player, Material.ENDER_PEARL) > 2){
                    Function.removeExtraItems(player,"末影珍珠",2);
                }else{
                    PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"末影补给",30);
                }
            }

            //自爆兵被动技能
            if(player.getScoreboardTags().contains("Boom")){
                if(Function.getPlayerItemAmount(player, Material.TNT) < 5){
                    if(PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player,"手榴弹补给")){
                        ItemStack item = new ItemStack(Material.TNT);
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName("§c手榴弹");
                            meta.setUnbreakable(true);
                            item.setItemMeta(meta);
                        }
                        player.getInventory().addItem(item);
                        PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"手榴弹补给",8);
                    }
                }else if(Function.getPlayerItemAmount(player, Material.TNT) > 5){
                    Function.removeExtraItems(player,"手榴弹",5);
                }else{
                    PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"手榴弹补给",5);
                }
            }

            //宇航员被动技能
            if(player.getScoreboardTags().contains("Astronaut")){
                if(Function.getPlayerItemAmount(player, Material.TIPPED_ARROW) < 3){
                    if(PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player,"浮空箭补给")){
                        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName("§b浮空箭");
                            meta.setUnbreakable(true);
                            item.setItemMeta(meta);
                        }
                        if (meta instanceof PotionMeta pm) {
                            pm.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION,5 * 20,0),true);
                            item.setItemMeta(pm);
                        }
                        if(player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
                            player.getInventory().setItemInOffHand(item);
                        } else if(player.getInventory().getItemInOffHand().getType().equals(Material.TIPPED_ARROW)) {
                            player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount() + 1);
                        } else {
                            player.getInventory().addItem(item);
                        }
                        PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"浮空箭补给",12);
                    }
                }else if(Function.getPlayerItemAmount(player, Material.TIPPED_ARROW) > 3){
                    Function.removeExtraItems(player,"浮空箭",3);
                }else{
                    PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"浮空箭补给",8);
                }
            }

            //医生被动技能
            if(player.getScoreboardTags().contains("Doctor") && !player.getScoreboardTags().contains("Doctor_Hurt") && player.isSneaking()){
                Function.addPotionEffect(player,PotionEffectType.REGENERATION,40,1);
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,player.getLocation(),3,1,1,1,0.5);
            }

            //动力小子跳板机制
            for(Entity entity : player.getNearbyEntities(0.5,0.1,0.5)){
                if(entity.getScoreboardTags().contains("JumpPad") && player.getVelocity().getY() < 1.0 && entity instanceof LivingEntity livingEntity && !livingEntity.hasAI()){
                    player.setFallDistance(0);
                    player.setVelocity(player.getLocation().getDirection().multiply(1.5).add(player.getVelocity().multiply(3.0)).setY(0).add(new Vector(0,1.3,0)));
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
                }
            }

            //时停者机制

            //空袭弩手，森林蟒蛇上限
            if(player.getScoreboardTags().contains("Sky")){
                Function.removeExtraItemsAuto(player,Material.ARROW,48);
                Function.removeExtraItemsAuto(player,Material.TIPPED_ARROW,3);
                Function.removeExtraItemsAuto(player,Material.FIREWORK_ROCKET,3);
            }else if(player.getScoreboardTags().contains("clls")){
                Function.removeExtraItemsAuto(player,Material.TIPPED_ARROW,64);
            }

            //蟒蛇回复能量
            if(player.getScoreboardTags().contains("Snake"))
                Function.addSkillPower(player,"毒素充能",1800,1);

            //战争狂人掉落伤害技能
            if(player.getScoreboardTags().contains("Reaper_Falling")){
                if(player.getFallDistance() == 0){
                    player.removeScoreboardTag("Reaper_Falling");
                    player.getWorld().playSound(player.getLocation(),Sound.BLOCK_ANVIL_LAND,1,1);
                    player.getWorld().spawnParticle(Particle.EXPLOSION,player.getLocation().clone().add(0,1,0),100,8,1,8,0);
                    for(Entity entity : player.getNearbyEntities(8,3,8)){
                        if(entity.equals(player)) return;
                        if(entity instanceof LivingEntity livingEntity){
                            livingEntity.damage(6,player);
                            livingEntity.setNoDamageTicks(0);
                            livingEntity.setVelocity(entity.getVelocity().add(player.getLocation().toVector().subtract(livingEntity.getLocation().toVector()).normalize()).multiply(-2.25).setY(1.00));
                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    livingEntity.setNoDamageTicks(0);
                                    livingEntity.damage(8);
                                }
                            }.runTaskLater(plugin,0);
                        }
                    }
                }
            }

            //火龙被动技能
            if(player.getScoreboardTags().contains("FireDragon_Firing")){
                for(int i = 0; i < 5; i++){
                    Vector v = getRandomSpreadX(player);
                    player.getWorld().spawnParticle(Particle.FLAME,player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(0.4)),0,v.getX(),v.getY(),v.getZ(),0.8,null,true);
                }
            }
            //滋崩
            if(player.getScoreboardTags().contains("ZiBooming")){
                GunsListener.useGun(player,player.getEyeLocation().getDirection(),100,0.1,0.3,0);
            }
            //动力小子音效
            if(player.getScoreboardTags().contains("Octane_Running")){
                if(Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective("reload")).getScore(player.getName()).getScore() % 10 == 0){
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WARDEN_HEARTBEAT,1,1);
                }
            }

            //狙击手弓箭控制
            if(player.getScoreboardTags().contains("Sniper")){
                if(Function.getPlayerItemAmountWithName(player, "勾魂弓") < 1){
                    if(PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player,"狙击弹药补给")){
                        ItemStack item2 = new ItemStack(Material.BOW);
                        ItemStack item3 = new ItemStack(Material.ARROW);
                        item3.setAmount(1);
                        Objects.requireNonNull(item3.getItemMeta()).setUnbreakable(true);
                        ItemMeta meta2 = item2.getItemMeta();
                        if (meta2 != null){
                            meta2.setDisplayName("§c勾魂弓");
                            if(meta2 instanceof Damageable){
                                ((Damageable)meta2).setDamage(383);
                            }
                            item2.setItemMeta(meta2);
                            item2.addUnsafeEnchantment(Enchantment.POWER,10);
                        }
                        player.getInventory().addItem(item2);
                        player.getInventory().addItem(item3);
                        player.getWorld().playSound(player.getLocation(),Sound.BLOCK_PISTON_CONTRACT,1,1);
                        PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"狙击弹药补给",10);
                    }
                }else if(Function.getPlayerItemAmountWithName(player, "勾魂弓") > 1){
                    Function.removeExtraItems(player,"勾魂弓",1);
                } else{
                    PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player,"狙击弹药补给",10);
                }
            }
        }

        //生物类被动技能
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            // 遍历每个世界中的所有实体
            for (Entity entity : world.getEntities()) {
                // 检查实体是否是生物
                if (entity instanceof LivingEntity livingEntity) {
                    if(livingEntity.getScoreboardTags().contains("Doctor_A")){
                        Location handLocation = livingEntity.getLocation().add(0,1,0);
                        for(int i = 1; i <= 50 ; i++){
                            double pai = 3.14159265 * 2;
                            double x = (pai / 50) * i;
                            double rate = 3;
                            double cosX = Math.cos(x);
                            double sinX = Math.sin(x);
                            Location particleLocation = handLocation.clone().add(sinX*rate,0,cosX*rate);
                            Objects.requireNonNull(livingEntity.getLocation().getWorld()).spawnParticle(Particle.HEART,particleLocation,0,10,0,0,0);
                        }
                        for(Entity entity1 : livingEntity.getNearbyEntities(5,5,5)){
                            if(entity1 instanceof Player player){
                                Function.addPotionEffect(player,PotionEffectType.REGENERATION,40,2);
                            }
                        }
                    }else if (activeSkill != null && livingEntity.getScoreboardTags().contains("Ice_Big") && activeSkill.getScore(livingEntity.getUniqueId().toString()).getScore() % 2 == 0) {
                        Location newLocation = generateRandomLocation(livingEntity.getLocation().clone());
                        Arrow arrow = livingEntity.getWorld().spawn(newLocation, Arrow.class);
                        arrow.setVelocity(new Vector(0,-2,0));
                        arrow.setCritical(true);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                        arrow.addScoreboardTag("Snow_Arrow");
                        arrow.setDamage(3.2);
                        if(activeSkill.getScore(livingEntity.getUniqueId().toString()).getScore() % 20 == 0){
                            for(Entity entities : livingEntity.getNearbyEntities(8,8,8)){
                                if(entities instanceof Player && !entities.getScoreboardTags().contains("GanYu")){
                                    Arrow Earrow = livingEntity.getWorld().spawn(entities.getLocation().add(0,6,0), Arrow.class);
                                    Earrow.setVelocity(new Vector(0,-2,0));
                                    Earrow.setCritical(true);
                                    Earrow.addScoreboardTag("Snow_Arrow");
                                    Earrow.setDamage(3.2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Vector getRandomSpreadX(Player player) {
        Random random = new Random();
        final double spread = 0.8;
        double x = random.nextDouble(-spread,spread);
        double y = random.nextDouble(-spread,spread);
        double z = random.nextDouble(-spread,spread);
        Vector vec = player.getLocation().getDirection().normalize();
        vec.setX(vec.clone().multiply(1 + x).getX());
        vec.setY(vec.clone().multiply(1 + y).getY());
        vec.setZ(vec.clone().multiply(1 + z).getZ());
        return vec;
    }
    private Location generateRandomLocation(Location playerLocation) {
        Random random = new Random();
        playerLocation.add(random.nextBoolean() ? random.nextDouble() * 8 : random.nextDouble() * -8,8,random.nextBoolean() ? random.nextDouble() * 8 : random.nextDouble() * -8);
        return playerLocation;
    }

    private void addHuaHaiFlowers(Player player){
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ItemStack arrow1 = new ItemStack(Material.WITHER_ROSE);
        arrow1.getItemMeta().setDisplayName("凋零玫瑰花");
        ItemStack arrow2 = new ItemStack(Material.BLUE_ORCHID);
        arrow2.getItemMeta().setDisplayName("寒冰兰花箭");
        ItemStack arrow3 = new ItemStack(Material.ALLIUM);
        arrow3.getItemMeta().setDisplayName("");
        ItemStack arrow4 = new ItemStack(Material.CORNFLOWER);
        ItemStack arrow5 = new ItemStack(Material.LILY_OF_THE_VALLEY);
        ItemStack arrow6 = new ItemStack(Material.DANDELION);
        ItemStack arrow7 = new ItemStack(Material.POPPY);
        ItemStack arrow8 = new ItemStack(Material.AZURE_BLUET);
        ItemStack arrow9 = new ItemStack(Material.CHORUS_FLOWER);
        ItemStack arrow10 = new ItemStack(Material.GLOW_BERRIES);
        ItemStack arrow11 = new ItemStack(Material.PITCHER_CROP);
        ItemStack arrow12 = new ItemStack(Material.PINK_PETALS);

        stacks.add(arrow1);stacks.add(arrow2);stacks.add(arrow3);stacks.add(arrow4);stacks.add(arrow5);
        stacks.add(arrow6);stacks.add(arrow7);stacks.add(arrow8);stacks.add(arrow9);stacks.add(arrow10);
        stacks.add(arrow11);stacks.add(arrow12);
    }
}
