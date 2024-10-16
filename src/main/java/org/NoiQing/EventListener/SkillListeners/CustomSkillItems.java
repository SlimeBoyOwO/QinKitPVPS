package org.NoiQing.EventListener.SkillListeners;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinSkill;
import org.NoiQing.mainGaming.Game;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomSkillItems implements Listener {
    private final QinKitPVPS plugin;
    private final Game game;
    public CustomSkillItems(QinKitPVPS plugin){
        this.plugin = plugin;
        this.game = plugin.getGame();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(!Function.isRightClicking(event)) return;
        Player player = event.getPlayer();
        if(Function.getMainHandItem(player).getType() != Material.AIR){
            QinSkill skill = game.getSkills().getSkillByName(Function.getMainHandItemNameWithoutColor(player));
            if(skill == null) return;
            if(Function.isHoldingSPItem(player, Function.getNameWithoutColor(skill.getActivatorName()))){
                if(PlayerDataSave.ifPlayerSkillPassCoolDownTime(player, skill.getActivatorName())){
                    Function.executeDelayCommands(player,skill.getCommands(),plugin);
                    Function.executeEffects(player,skill.getEffects());
                    Function.executeSounds(player,skill.getSoundType(),skill.getSoundPitch(),skill.getSoundVolume());
                    PlayerDataSave.setPlayerSkillCoolDownTime(player,skill.getActivatorName(), skill.getCoolDown());
                }
            }
            event.setCancelled(true);
        }
    }
}
