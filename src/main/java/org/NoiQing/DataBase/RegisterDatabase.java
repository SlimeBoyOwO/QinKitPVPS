package org.NoiQing.DataBase;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.DataBaseCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class RegisterDatabase implements Listener {
    private final QinKitPVPS qinKitPVPS;

    public RegisterDatabase(QinKitPVPS qinKitPVPS) {
        this.qinKitPVPS = qinKitPVPS;
    }

    @EventHandler
    public void inLeave(PlayerQuitEvent event) {
        DataBaseCache.removePlayerCache(event.getPlayer());
    }
}
