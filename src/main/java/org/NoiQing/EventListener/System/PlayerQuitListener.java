package org.NoiQing.EventListener.System;

import org.NoiQing.ExtraModes.AllayWar.AWAPI.AllayGame;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("InAWGaming")) {
            QinTeam team = QinTeams.getEntityTeam(player);
            AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();
            if(team != null) {
                player.addScoreboardTag("TeamName_" + team.getTeamName());
                allayGame.removePlayerFromTeam(player);
            }

            return;
        }
        if(!player.getScoreboardTags().isEmpty()){
            player.damage(999999, PlayerDataSave.getLastAttackPlayerRecord(player));
        }
        PlayerDataSave.clearPlayerDataSave(player);
        List<String> commands = new ArrayList<>();
        commands.add("console: team leave %player%");
        Function.executeCommands(player,commands,"none","none");
    }
}
