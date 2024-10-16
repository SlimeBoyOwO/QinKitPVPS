package org.NoiQing.EventListener.System;

import org.NoiQing.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.api.QinTeam;
import org.NoiQing.enums.FacingOffset;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class WallJumpListener implements Listener {

    private boolean hitNoExp(Player player, double exp) {
        if(player.getExp() <= exp){
            List<String> commands = new ArrayList<>();
            commands.add("console: title " + player.getName() + " actionbar [\"\\u00a76\\u00a7l✖体力不足✖\"]");
            Function.executeCommands(player, commands,"none","none");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO,1,1);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerSneakEvent(PlayerToggleSneakEvent e) {
        if(!e.isSneaking()) return;
        Player player = e.getPlayer();
        if(PlayerDataSave.isRushJump(player)) {
            if(player.getScoreboardTags().contains("raider_mode")) {
                if(player.getWorld().getName().equals("skyblock_copy")) {
                    QinTeam t = QinTeams.getEntityTeam(player);
                    if(t != null) {
                        for(Entity entity : t.getTeamEntities()) {
                            if(!entity.getScoreboardTags().contains("player_stand")) continue;
                            if(AWPlayer.getPlayerRaider(entity) != null && AWPlayer.getPlayerRaider(entity).equals(player)) {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.setAllowFlight(false);
                                player.getInventory().setContents(AWPlayer.getPlayerTempInventory(player));
                                player.setInvisible(false);
                                player.setInvulnerable(false);
                                player.teleport(entity.getLocation());
                                player.removeScoreboardTag("raider_mode");
                                AWPlayer.removePlayerRaider(entity);
                                entity.remove();
                            }
                        }
                    }
                }
                return;
            }

            double rushExp = 0.4;
            if(hitNoExp(player,rushExp)) return;
            if(!PlayerDataSave.ifPlayerPassiveSkillPassCoolDownTime(player,"战术冲刺")) return;

            Vector playerDirection = player.getLocation().getDirection().multiply(0.75).add(new Vector(0,0.2,0));
            Vector originalVector = player.getVelocity();
            originalVector.setY(originalVector.getY() * 0.3);
            player.setVelocity(originalVector.add(playerDirection));
            player.setSprinting(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,10,1));
            player.getWorld().spawnParticle(Particle.CLOUD,player.getLocation().clone().add(0,0.25,0),20,0.5,0.1,0.5,0.1);
            player.setExp((float) (player.getExp() - rushExp));
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GILDED_BLACKSTONE_BREAK,1,1.2f);
            PlayerDataSave.setPlayerPassiveSkillCoolDownTime(player, "战术冲刺", 1);
        }else PlayerDataSave.setRushJumpRecord(player);

    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if((isTouchingWallHere(player) || isTouchingWallAbove(player)) && isOnGround(player) && !event.isSneaking() && !player.isClimbing() && !player.isFlying()){

            //蹬墙跳发生条件
            if(player.getScoreboardTags().contains("QinKit_Parkour")) return;
            if(player.getScoreboardTags().contains("NoWallJump")) return;
            if(Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"))).equals("梅雨"))
                return;
            double wallJumpNeedExp = 0.1;
            if(hitNoExp(player, wallJumpNeedExp)) return;

            Location location;
            if(isTouchingWallHere(player)){
                location = player.getLocation();
            }else{
                Location tempLocation = player.getLocation();
                tempLocation.setY(tempLocation.getY()+1);
                location = tempLocation;
            }
            Vector playerDirection = new Vector(0,0,0).setY(0.5);
            playerDirection.add(player.getVelocity().clone().setY(0).multiply(0.45));

            FacingOffset facingNorth = FacingOffset.NORTH;
            Block blockNorth = location.clone().add(facingNorth.xOffset, facingNorth.yOffset, facingNorth.zOffset).getBlock();

            FacingOffset facingSouth = FacingOffset.SOUTH;
            Block blockSouth = location.clone().add(facingSouth.xOffset, facingSouth.yOffset, facingSouth.zOffset).getBlock();

            FacingOffset facingWest = FacingOffset.WEST;
            Block blockWest= location.clone().add(facingWest.xOffset, facingWest.yOffset, facingWest.zOffset).getBlock();

            FacingOffset facingEast = FacingOffset.EAST;
            Block blockEast = location.clone().add(facingEast.xOffset, facingEast.yOffset, facingEast.zOffset).getBlock();

            if(blockEast.getType().isSolid() && player.getFacing().equals(BlockFace.EAST)) {
                playerDirection.add(new Vector(-0.6, 0, 0));
                runWallJump(player, wallJumpNeedExp, playerDirection);
            }
            if(blockWest.getType().isSolid() && player.getFacing().equals(BlockFace.WEST)) {
                playerDirection.add(new Vector(0.6,0,0));
                runWallJump(player, wallJumpNeedExp, playerDirection);
            }
            if(blockNorth.getType().isSolid() && player.getFacing().equals(BlockFace.NORTH)) {
                playerDirection.add(new Vector(0,0,0.6));
                runWallJump(player, wallJumpNeedExp, playerDirection);
            }
            if(blockSouth.getType().isSolid() && player.getFacing().equals(BlockFace.SOUTH)) {
                playerDirection.add(new Vector(0,0,-0.6));
                runWallJump(player, wallJumpNeedExp, playerDirection);
            }


        }
    }

    private static void runWallJump(Player player, double wallJumpNeedExp, Vector playerDirection) {
        player.setVelocity(playerDirection);
        player.setExp((float) (player.getExp() - wallJumpNeedExp));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GILDED_BLACKSTONE_BREAK,1,1.2f);
        player.setSprinting(true);
    }

    @EventHandler
    public void onPlayerSwim(EntityToggleSwimEvent event){
        if(Function.getNameWithoutColor(WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"))).equals("梅雨"))
            return;
        if(event.getEntity() instanceof Player player && !event.isSwimming() && isAirAbove(player) && !isTouchingWallBelow(player)){
            if(player.getScoreboardTags().contains("QinKit_Parkour")) return;
            double waterJumpNeedExp = 0.4;
            if(player.getExp() < waterJumpNeedExp) return;
            Vector playerDirection = player.getLocation().getDirection().multiply(0.6).add(new Vector(0,0.3,0));
            player.setVelocity(playerDirection);
            player.setExp((float) (player.getExp() - waterJumpNeedExp));
        }
    }

    public static boolean isTouchingWallHere(Entity player){
        Location location = player.getLocation();
        return isLocationTouchingWall(location);
    }

    public static boolean isTouchingWallBelow(Player player){
        Location location = player.getLocation();
        location.setY(location.getY() - 1);
        return isLocationTouchingWall(location);
    }

    public static boolean isTouchingWallAbove(Player player){
        Location location = player.getLocation();
        location.setY(location.getY() + 1);
        return isLocationTouchingWall(location);
    }

    private static boolean isLocationTouchingWall(Location location) {

        if(location.getBlock().getType() == Material.CHAIN){
            return true;
        }

        FacingOffset facingNorth = FacingOffset.NORTH;
        Block blockNorth = location.clone().add(facingNorth.xOffset, facingNorth.yOffset, facingNorth.zOffset).getBlock();
        float distanceLimitNorth = facingNorth.distance;

        FacingOffset facingSouth = FacingOffset.SOUTH;
        Block blockSouth = location.clone().add(facingSouth.xOffset, facingSouth.yOffset, facingSouth.zOffset).getBlock();
        float distanceLimitSouth = facingSouth.distance;

        FacingOffset facingWest = FacingOffset.WEST;
        Block blockWest= location.clone().add(facingWest.xOffset, facingWest.yOffset, facingWest.zOffset).getBlock();
        float distanceLimitWest = facingWest.distance;

        FacingOffset facingEast = FacingOffset.EAST;
        Block blockEast = location.clone().add(facingEast.xOffset, facingEast.yOffset, facingEast.zOffset).getBlock();
        float distanceLimitEast = facingEast.distance;

        if(blockNorth.getType().isSolid() || blockSouth.getType().isSolid()) {
            return Math.abs(location.getZ() - blockNorth.getZ()) < distanceLimitNorth
                    || Math.abs(location.getZ() - blockSouth.getZ()) < distanceLimitSouth;
        }else if(blockEast.getType().isSolid() || blockWest.getType().isSolid()) {
            return Math.abs(location.getX() - blockEast.getX()) < distanceLimitEast
                    || Math.abs(location.getX() - blockWest.getX()) < distanceLimitWest;
        }

        return false;
    }

    public static boolean isAirAbove(Player player){
        return player.getLocation().clone().subtract(0,-2,0).getBlock().isPassable();
    }

    public static boolean isOnGround(Entity player) {
        return !player.getLocation().clone().subtract(0, 0.2, 0).getBlock().getType().isSolid();
    }

    public static boolean isBelowHaveBlock(Player player) {
        return !player.getLocation().clone().subtract(0, 1.2, 0).getBlock().getType().isSolid() && !player.getLocation().clone().subtract(0, 0.1, 0).getBlock().getType().isSolid();
    }
}
