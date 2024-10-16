package org.NoiQing.BukkitRunnable;

import org.NoiQing.api.QinBossBar;
import org.NoiQing.util.QinBossBarDataSave;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.Objects;

public class EffectRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(String s : player.getScoreboardTags()) {
                QinBossBar qinBossBar = QinBossBarDataSave.getBossBarCache().get(s);
                if(qinBossBar == null) continue;
                Objective activeSkill = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective(qinBossBar.getScoreboard());
                if(activeSkill == null) continue;

                //新建boss栏逻辑
                if(QinBossBarDataSave.getPlayerBossBar().get(player) == null || QinBossBarDataSave.getPlayerBossBar().get(player).get(s) == null) {
                    QinBossBarDataSave.getPlayerBossBar().computeIfAbsent(player, k -> new HashMap<>());
                    QinBossBarDataSave.getPlayerBossBar().get(player).put(s,Bukkit.createBossBar(qinBossBar.getBossBar().getTitle(),qinBossBar.getBossBar().getColor(),qinBossBar.getBossBar().getStyle()));
                    QinBossBarDataSave.getPlayerBossBarTags(player).add(qinBossBar.getTag());
                }

                //刷新boss栏逻辑
                BossBar playerBar = QinBossBarDataSave.getPlayerBossBar().get(player).get(s);
                double ascOrder = Math.max((double) activeSkill.getScore(player.getName()).getScore() / qinBossBar.getMax(),0);
                double dascOrder = Math.max((double) (qinBossBar.getMax() - activeSkill.getScore(player.getName()).getScore()) / qinBossBar.getMax(),0);
                if(qinBossBar.getASC()) playerBar.setProgress(ascOrder);
                else playerBar.setProgress(dascOrder);
                playerBar.addPlayer(player);
            }

            //移除boss栏逻辑
            if(!QinBossBarDataSave.getPlayerBossBarTags(player).isEmpty()) {
                for(int i = 0; i < QinBossBarDataSave.getPlayerBossBarTags(player).size(); i++) {
                    String tag = QinBossBarDataSave.getPlayerBossBarTags(player).get(i);
                    if(!player.getScoreboardTags().contains(tag)) {
                        QinBossBarDataSave.getPlayerBossBar().get(player).get(tag).removePlayer(player);
                        QinBossBarDataSave.getPlayerBossBar().get(player).get(tag).removeAll();
                        QinBossBarDataSave.getPlayerBossBar().get(player).remove(tag);
                        QinBossBarDataSave.getPlayerBossBarTags(player).remove(i--);
                    }
                }
            }
        }
    }
}
