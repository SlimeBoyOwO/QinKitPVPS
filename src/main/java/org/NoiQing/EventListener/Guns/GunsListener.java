package org.NoiQing.EventListener.Guns;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public class GunsListener implements Listener {
    private final QinKitPVPS plugin;
    public GunsListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getItem() != null && Function.isHoldingSPItem(player,"滋崩") && Function.isRightClicking(event)){
            if(Function.isRightClicking(event)){
                long useTimes = PlayerDataSave.getPlayerPassiveSkillRecords(player,"滋崩使用次数");
                if(!player.getScoreboardTags().contains("ZiBooming_Tag") && useTimes < 4 && PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"滋崩")){
                    useTimes++;
                    player.addScoreboardTag("ZiBooming");
                    player.addScoreboardTag("ZiBooming_Tag");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,14,4));
                    PlayerDataSave.setPlayerPassiveSkillRecords(player,"滋崩使用次数",useTimes);
                    if(useTimes >= 4){
                        PlayerDataSave.setPlayerSkillCoolDownTime(player,"滋崩",15);
                        PlayerDataSave.setPlayerPassiveSkillRecords(player,"滋崩使用次数",0L);
                    }
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            player.removeScoreboardTag("ZiBooming");
                            ZiBoomBoom(player);
                        }
                    }.runTaskLater(plugin,14);
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            player.removeScoreboardTag("ZiBooming_Tag");
                        }
                    }.runTaskLater(plugin,25);
                }
            }else if(Function.isLeftClicking(event)){
                long useTimes = PlayerDataSave.getPlayerPassiveSkillRecords(player,"滋崩使用次数");
                if(useTimes == 0) return;
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"滋崩",15);
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"滋崩使用次数",0L);
            }
        }

        if (event.getItem() != null && Function.isHoldingSPItem(player,"普通手枪") && Function.isRightClicking(event)) {

            //玩家换蛋没有完成，直接返回
            if(!PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"手枪换弹") || player.getScoreboardTags().contains("Pistol_Shooting")) return;

            //玩家射出最后一发子弹，给予换弹冷却
            else if(PlayerDataSave.getPlayerPassiveSkillRecords(player,"手枪子弹") == 1){
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"手枪换弹",2);
                playReloadSound(player);
            }

            //玩家换蛋已经完成，而且将要射击
            else if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"手枪换弹") && PlayerDataSave.getPlayerPassiveSkillRecords(player,"手枪子弹") == 0){
                PlayerDataSave.setPlayerPassiveSkillRecords(player,"手枪子弹", 13L);
            }

            //削减玩家子弹，并显示玩家剩余子弹数，给予射击冷却
            PlayerDataSave.setPlayerPassiveSkillRecords(player,"手枪子弹",PlayerDataSave.getPlayerPassiveSkillRecords(player,"手枪子弹")-1);
            player.addScoreboardTag("Pistol_Shooting");
            new BukkitRunnable(){
                @Override
                public void run(){
                    player.removeScoreboardTag("Pistol_Shooting");
                }
            }.runTaskLater(plugin,5);
            player.setLevel((int) PlayerDataSave.getPlayerPassiveSkillRecords(player,"手枪子弹"));


            // 获取玩家的视线方向
            Vector direction = player.getEyeLocation().getDirection();

            int gunRange = 100;
            double gunWide = 0.1;
            double damage = 2.0;
            int recoil = 2;

            useGun(player, direction, gunRange, gunWide, damage, recoil);

        }
    }

    public static void useGun(Player player, Vector direction, int gunRange, double gunWide, double damage, int recoil) {
        Predicate<Entity> predicate = x -> !x.getName().equals(player.getName());
        // 创建一条射线
        RayTraceResult result = player.getWorld().rayTrace(
                player.getEyeLocation().clone().add(direction), // 起始点
                direction,               // 方向向量
                gunRange,                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                gunWide,                     // 检测范围（宽度）
                predicate              // 过滤器
        );

        if (result != null && result.getHitEntity() instanceof LivingEntity target) {
            // 检测到生物，对其造成伤害
            target.damage(damage); // 造成2点伤害
            target.setNoDamageTicks(0);
            playHitGunSound(player);
        }

        playGunSound(player);           //播放枪械音效

        if(result != null && (result.getHitBlock() != null || result.getHitEntity() != null)){
            showGunParticle(player,result);
        }else{
            Vector rayStart = player.getEyeLocation().toVector();
            Vector rayEnd = player.getEyeLocation().add(direction.multiply(300)).toVector();
            GunParticle(player, rayStart, rayEnd);      //播放枪械粒子
        }

        if(recoil != 0){
            player.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch()- recoil);
        }
    }
    private static void ZiBoomBoom(Player player){
        Vector direction = player.getEyeLocation().getDirection();
        Predicate<Entity> predicate = x -> !x.getName().equals(player.getName());
        // 创建一条射线
        RayTraceResult result = player.getWorld().rayTrace(
                player.getEyeLocation().clone().add(direction), // 起始点
                direction,               // 方向向量
                100,                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                0.1,                     // 检测范围（宽度）
                predicate              // 过滤器
        );
        if (result != null && result.getHitEntity() instanceof LivingEntity target) {
            // 检测到生物，对其造成伤害
            target.damage(12,player); // 造成2点伤害
            target.getWorld().playSound(target.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,8,1);
            target.getWorld().spawnParticle(Particle.LAVA,target.getLocation().clone().add(0,1,0),30,0.5,0.5,0.5,0,null,true);

        }else if (result != null && result.getHitBlock() != null) {
           Block block = result.getHitBlock();
            block.getWorld().spawnParticle(Particle.LAVA,block.getLocation(),30,0.5,0.5,0.5,0,null,true);
            block.getWorld().playSound(block.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,8,1);
        }
        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);
    }

    private static void GunParticle(Player player, Vector rayStart, Vector rayEnd) {
        double distance = rayStart.distance(rayEnd);
        Vector directionNormalized = rayEnd.subtract(rayStart).normalize();

        for (double i = 0; i < distance; i += 1.5) {
            Vector particleLocation = rayStart.clone().add(directionNormalized.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.CRIT, particleLocation.toLocation(player.getWorld()), 1,0,0,0,0);
        }
    }

    private static void showGunParticle(Player player, RayTraceResult result){
        // 显示射线上的粒子效果
        Vector rayStart = player.getEyeLocation().toVector();
        Vector rayEnd = result.getHitPosition();
        GunParticle(player, rayStart, rayEnd);
    }

    private static void playGunSound(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BASALT_BREAK,1,2);
    }

    private static void playHitGunSound(Player player){
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,2);
    }

    private static void playReloadSound(Player player){
        player.getWorld().playSound(player.getLocation(),Sound.BLOCK_PISTON_CONTRACT,1,1);
    }
}
