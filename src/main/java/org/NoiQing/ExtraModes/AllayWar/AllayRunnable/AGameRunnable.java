package org.NoiQing.ExtraModes.AllayWar.AllayRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.ExtraModes.AllayWar.AWAPI.AllayGame;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.NoiQing.QinKitPVPS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class AGameRunnable extends BukkitRunnable {

    private static int refresh = 0;

    @Override
    public void run() {
        AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();
        if(!allayGame.isRunning()) return;

        boolean sbRefresh = false;
        allayGame.addGameDuration(); refresh++;
        if(refresh >= 20) {
            refresh = 0;
            sbRefresh = true;
        }
        if(Bukkit.getWorld("skyblock_copy") == null) {
            Bukkit.broadcastMessage("空岛复制世界无法获取！！！！");
        }
        else {
            for (Player p : Objects.requireNonNull(Bukkit.getWorld("skyblock_copy")).getPlayers()) {
                if(p.getScoreboardTags().contains("money_told")) p.removeScoreboardTag("money_told");
                else p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e总资产: " + AWPlayer.getPlayerAWMoney(p)));
                if(sbRefresh) PluginScoreboard.changeScoreboard(p);
            }
        }

    }

}
