package org.NoiQing.AllayWar.AWUtils;

import org.bukkit.entity.Allay;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AWBossBars {
    private static final Map<Player, Map<Allay, Long>> playerAttackAllayRecord = new HashMap<>();
    public static void updatePAAR(Player p, Allay a) {
        playerAttackAllayRecord.put(p,new HashMap<>(Map.of(a,System.currentTimeMillis())));
    }
    public static boolean ifPlayerPassBossBar(Player p, Allay a) {
        if(playerAttackAllayRecord.get(p) == null) return true;
        if(playerAttackAllayRecord.get(p).get(a) == null) return true;
        if(System.currentTimeMillis() - playerAttackAllayRecord.get(p).get(a) >= 4000) {
            playerAttackAllayRecord.remove(p);
            return true;
        }else return false;
    }
}
