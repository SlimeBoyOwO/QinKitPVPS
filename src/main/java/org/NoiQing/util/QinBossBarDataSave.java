package org.NoiQing.util;

import org.NoiQing.api.QinBossBar;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QinBossBarDataSave {
    private static final Map<String, QinBossBar> bossBarStorage = new HashMap<>();
    private static final Map<Player, Map<String, BossBar>> playerBossBar = new HashMap<>();
    private static final Map<Player, ArrayList<String>> playerBossBarTag = new HashMap<>();
    public static Map<String, QinBossBar> getBossBarCache() { return bossBarStorage; }
    public static Map<Player, Map<String, BossBar>> getPlayerBossBar() { return playerBossBar; }
    public static ArrayList<String> getPlayerBossBarTags(Player player) {
        if(playerBossBarTag.containsKey(player)) return playerBossBarTag.get(player);
        playerBossBarTag.put(player, new ArrayList<>());
        return playerBossBarTag.get(player);
    }
    public static void clearQinBossBarDataSave() {
        bossBarStorage.clear();
    }
}
