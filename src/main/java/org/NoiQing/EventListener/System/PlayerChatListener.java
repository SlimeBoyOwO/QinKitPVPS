package org.NoiQing.EventListener.System;

import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

public class PlayerChatListener implements Listener {
    @EventHandler
    public void onPlayerSendMessage(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(PlayerDataSave.getPlayerTextDataRecord(player, "playerPrefix") != null && !Objects.equals(PlayerDataSave.getPlayerTextDataRecord(player, "playerPrefix"), "None"))
            event.setFormat("§7[" + Function.changeColorCharacters(PlayerDataSave.getPlayerTextDataRecord(player, "playerPrefix")) + "§7]" + " §f<" + player.getName() + "> " + event.getMessage());
    }
}
