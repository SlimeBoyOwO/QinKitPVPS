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
    public void inJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        if (!e.getPlayer().hasPlayedBefore()){
            qinKitPVPS.getSQLiteDatabase().addPlayer(e.getPlayer());
            qinKitPVPS.getMySQLDataBase().addPlayer(e.getPlayer());
            qinKitPVPS.getMySQLDataBase().givePlayerKit(p,"Fighter");
            qinKitPVPS.getMySQLDataBase().givePlayerKit(p,"Archer");
            qinKitPVPS.getMySQLDataBase().givePlayerKit(p,"Tank");
            qinKitPVPS.getMySQLDataBase().setPlayerKey(p,3);
        }
    }

    @EventHandler
    public void inLeave(PlayerQuitEvent event) {
        DataBaseCache.removePlayerCache(event.getPlayer());
    }
}
