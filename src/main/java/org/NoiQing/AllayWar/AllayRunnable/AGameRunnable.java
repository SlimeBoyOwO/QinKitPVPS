package org.NoiQing.AllayWar.AllayRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class AGameRunnable extends BukkitRunnable {

    private static int refresh = 0;

    @Override
    public void run() {
        if(!AWRound.isRunning()) return;

        boolean sbRefresh = false;
        AWRound.addTime(); refresh++;
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
