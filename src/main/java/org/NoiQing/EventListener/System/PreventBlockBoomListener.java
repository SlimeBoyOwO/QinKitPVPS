package org.NoiQing.EventListener.System;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class PreventBlockBoomListener implements Listener {
    private final QinKitPVPS plugin;
    private boolean isRunned = false;
    final int rollMoney = 500;
    public PreventBlockBoomListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }
    @EventHandler
    void onBlockExplode(EntityExplodeEvent event){
        event.blockList().clear();
    }

    @EventHandler
    void onPlayerRightClickCampfire(PlayerInteractEvent event){
        if(event.getClickedBlock() != null){
            Block block = event.getClickedBlock();
            if(block.getBlockData() instanceof Campfire campfire){
                campfire.setSignalFire(false);
                block.setBlockData(campfire);
            }
        }
    }

    @EventHandler
    void onShanHuDie(BlockFadeEvent e) {
        if(e.getBlock().getType().toString().endsWith("_coral")) e.setCancelled(true);
        if(e.getBlock().getType().toString().endsWith("_coral_fan")) e.setCancelled(true);
    }

    @EventHandler
    void onRoll(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if (block != null && block.getType() == Material.END_PORTAL_FRAME && block.getLocation().equals(new Location(Bukkit.getWorld("world"), 942, 3, 998))) {
                block.setMetadata("Unbreakable", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                int money = PlayerDataSave.getPlayerMoneyRecord(player);
                if(money < rollMoney && !player.getScoreboardTags().contains("Pre_Rolling")){
                    player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3你的小钱钱不够惹 §b(=^=)");
                    player.addScoreboardTag("Pre_Rolling");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.removeScoreboardTag("Pre_Rolling");
                        }
                    }.runTaskLater(plugin, 1);
                }else{
                    if(!player.getScoreboardTags().contains("Rolling")){
                        try{
                            if(plugin.getSQLiteDatabase().getPlayerMoney(player) < rollMoney){
                                return;
                            }
                            int newMoney = plugin.getSQLiteDatabase().getPlayerMoney(player) - rollMoney;
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_STRAY_AMBIENT, 10, 1.2F);
                            if(!isRunned){
                                isRunned = true;
                                new BukkitRunnable() {
                                    @Override
                                    public void run(){
                                        if(isHaveRollingPlayer(player)){
                                            player.getWorld().spawnParticle(Particle.DUST,new Location(player.getWorld(),942.5,5,998.5),10,0.5,1,0.5,1,new Particle.DustOptions(Color.fromRGB(255,255,255), 0.5F),true);
                                        }
                                    }

                                    private boolean isHaveRollingPlayer(Player player) {
                                        for(Entity entity :player.getWorld().getNearbyEntities(new Location(player.getWorld(),942.5,3,998.5),30,30,30)){
                                            if(entity.getScoreboardTags().contains("Rolling")){
                                                return true;
                                            }
                                        }
                                        return false;
                                    }
                                }.runTaskTimer(plugin,0,1);
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);
                                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);
                                    player.getWorld().spawnParticle(Particle.FIREWORK,new Location(player.getWorld(),942.5,3,998.5),100,0,0,0,1,null,true);
                                    player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,new Location(player.getWorld(),942.5,3,998.5),1,0,0,0,0,null,true);
                                    player.getWorld().spawn(new Location(player.getWorld(),942.5,4,998.5), LightningStrike.class);
                                    player.performCommand("qinkit rollkit");
                                    player.removeScoreboardTag("Rolling");
                                    player.removeScoreboardTag("Pre_Rolling");
                                }
                            }.runTaskLater(plugin, 60);
                            plugin.getSQLiteDatabase().updatePlayerMoney(player,newMoney);
                            PlayerDataSave.updatePlayerMoneyRecord(player,newMoney);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    player.addScoreboardTag("Rolling");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(event.getPlayer().getScoreboardTags().contains("Kitted"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // 检查方块是否有"protected"标签
        if (Function.isBlockHasTag(block, "Unbreakable")) {
            event.setCancelled(true);
            player.sendMessage("你只能破坏玩家放置的方块！");
        }
    }
}
