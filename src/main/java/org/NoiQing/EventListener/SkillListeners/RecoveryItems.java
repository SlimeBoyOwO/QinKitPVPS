package org.NoiQing.EventListener.SkillListeners;

import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

public class RecoveryItems implements Listener {
    private int noRepeat = 0;

    @EventHandler
    public void onUseRecoveryItems(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(isRightClicked(event)){
            if(Function.isHoldingSPItem(player,"医疗包") && player.getHealth() < Function.getPlayerMaxHealth(player) && PlayerDataSave.ifPlayerSkillPassCoolDownTime(player,"医疗包")){
                double RecoveryHealth = 12.0;
                player.setHealth(Math.min(player.getHealth()+RecoveryHealth,Function.getPlayerMaxHealth(player)));
                PlayerDataSave.setPlayerSkillCoolDownTime(player,"医疗包",3);
                Function.useSPItem(player, "医疗包");
                player.getWorld().spawnParticle(Particle.FALLING_SPORE_BLOSSOM,player.getLocation().clone().add(0,1,0),50,0.4,0.5,0.4,0,null,true);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1.6F);
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        if(event.getItem().getType() == Material.COOKED_BEEF){
            if(player.getHealth() <= 0) return;
            double RecoveryHealth = 4;
            player.setHealth(Math.min(player.getHealth()+RecoveryHealth,Function.getPlayerMaxHealth(player)));
            Function.addPotionEffect(player, PotionEffectType.REGENERATION,4,1);
        }
    }

    private boolean isRightClicked(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_AIR){
            return true;
        }else if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            noRepeat += 1;
            if(noRepeat == 2){
                noRepeat = 0;
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
