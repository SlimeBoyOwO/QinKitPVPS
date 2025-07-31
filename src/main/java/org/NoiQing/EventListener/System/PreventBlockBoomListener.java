package org.NoiQing.EventListener.System;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreventBlockBoomListener implements Listener {
    private final QinKitPVPS plugin;
    public PreventBlockBoomListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }
    @EventHandler
    void onBlockExplode(EntityExplodeEvent event){
        World world = event.getLocation().getWorld();
        if(world == null) return;
        if(!world.getName().equals("skyblock_copy"))
            event.blockList().clear();
        else {
            // 检查方块是否有"protected"标签
            event.blockList().removeIf(this::checkBlockUnbreakableForAllayWar);
        }
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

    public boolean checkBlockUnbreakableForAllayWar(Block block) {
        return Function.isBlockHasTag(block, "OriginalMap")
                || Function.isBlockHasTag(block, "Unbreakable")
                || Function.isBlockHasTag(block, "Allay_Tower")
                || block.getType().toString().contains("GLASS");
    }
}
