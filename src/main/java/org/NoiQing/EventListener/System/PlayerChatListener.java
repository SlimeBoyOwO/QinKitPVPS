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
    public void onPlayerSendMessage (AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String rec = PlayerDataSave.getPlayerTextDataRecord(p, "playerPrefix");

        /* 替换那些有头衔的玩家 */
        if (rec != null && ! Objects.equals(rec, "None"))
            event.setFormat("§e[" + Function.changeColorCharacters(rec) + "§e] §f§l%s§f: §r%s");
        else event.setFormat("§f§l%s §f: §r%s");
    }
}