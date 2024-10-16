package org.NoiQing.BukkitRunnable;

import org.NoiQing.EventListener.System.WallJumpListener;
import org.NoiQing.util.Function;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WallJumpRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
            //回复体力
            if(player.getExp() < 1) player.setExp(player.getExp() + 0.0025F > 1 ? 1 : player.getExp() + 0.0025F);
            if(!player.getScoreboardTags().contains("GanYu")) player.setLevel((int) (player.getExp() * 100));

            //滑墙
            if(player.isSneaking() && WallJumpListener.isTouchingWallHere(player) && WallJumpListener.isOnGround(player) && !player.isClimbing() && !player.isFlying() && WallJumpListener.isBelowHaveBlock(player)){
                if(player.getScoreboardTags().contains("QinKit_Parkour")) return;
                if(Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"))).equals("梅雨"))
                    return;
                player.setVelocity(player.getVelocity().setY(-0.05).add(player.getVelocity().setY(0).multiply(0.2)));
                player.setFallDistance(0);
            }

            //炮台
            if(player.getLocation().clone().add(0,-0.2,0).getBlock().getType() == Material.MAGENTA_GLAZED_TERRACOTTA) {
                Block block = player.getLocation().add(0, -0.2, 0).getBlock();
                BlockFace blockFace = null;
                if (block.getBlockData() instanceof Directional directional) {
                    blockFace = directional.getFacing();
                }
                if (blockFace == null) return;
                switch (blockFace) {
                    case SOUTH -> player.setVelocity(player.getVelocity().add(new Vector(0, 1.2, -2)));
                    case NORTH -> player.setVelocity(player.getVelocity().add(new Vector(0, 1.2, 2)));
                    case EAST -> player.setVelocity(player.getVelocity().add(new Vector(-2, 1.2, 0)));
                    case WEST -> player.setVelocity(player.getVelocity().add(new Vector(2, 1.2, 0)));
                }
            }

            //绳索
            if(player.getLocation().clone().add(0,2.75,0).getBlock().getType() == Material.BAMBOO_FENCE){
                player.setVelocity(player.getVelocity().setY(0.1).add(player.getLocation().getDirection().multiply(0.05)));
            }

            //铁索
            if(player.getLocation().clone().getBlock().getType() == Material.CHAIN && player.isSneaking()){
                if(player.getVelocity().getY() <= 0.6 && player.getVelocity().getY() >= -0.6){
                    player.setVelocity(player.getVelocity().add(new Vector(0,player.getLocation().getDirection().getY() * 0.1, 0)));
                }else{
                    player.setVelocity(player.getVelocity());
                }
                player.setFallDistance(0);
            }
        }
    }
}
