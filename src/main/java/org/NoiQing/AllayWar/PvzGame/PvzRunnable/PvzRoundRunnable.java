package org.NoiQing.AllayWar.PvzGame.PvzRunnable;

import org.NoiQing.AllayWar.PvzGame.Game.PvzRound;
import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PvzRoundRunnable extends BukkitRunnable {
    private int sbRefresh = 0;
    @Override
    public void run() {
        if(!PvzRound.isRunning()) return;
        if(Bukkit.getWorld("skyblock_copy") == null) return;
        sbRefresh++;
        if(sbRefresh >= 20) {
            for (Player p : Objects.requireNonNull(Bukkit.getWorld("skyblock_copy")).getPlayers())
                PluginScoreboard.changeScoreboard(p);

            sbRefresh = 0;
        }
    }
}
