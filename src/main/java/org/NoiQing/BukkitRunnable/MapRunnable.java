package org.NoiQing.BukkitRunnable;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.mainGaming.Game;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.MapDataSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MapRunnable extends BukkitRunnable {
    private final CreateFileConfig createFileConfig;
    private static Game game;
    private static int mapsCount;
    public MapRunnable(QinKitPVPS plugin){
        createFileConfig = plugin.getResource();
        game = plugin.getGame();
        mapsCount = createFileConfig.getMapFiles().size();
    }
    @Override
    public void run() {
        if(MapDataSave.getMapStorage().isEmpty() || !MapDataSave.getMapStorage().containsKey(Bukkit.getWorld("world"))) {
            MapDataSave.getMapStorage().put(Bukkit.getWorld("world"), -1);
        }
        int randomMapID = MapDataSave.getMapStorage().get(Bukkit.getWorld("world")) + 1;
        if (randomMapID >= createFileConfig.getMapFiles().size()) {
            randomMapID = 0;
        }
        mapChangeActions(randomMapID);
    }

    public static void changeMap(int randomMapID){
        mapChangeActions(randomMapID);
    }

    private static void mapChangeActions(int randomMapID) {
        MapDataSave.getMapStorage().remove((Bukkit.getWorld("world")));
        while(game.getMaps().getQinMapByMapID(randomMapID) == null){
            randomMapID++;
            if(randomMapID >= mapsCount)
                randomMapID = 0;
        }
        MapDataSave.getMapStorage().put(Bukkit.getWorld("world"),randomMapID);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int mapID = game.getMaps().getQinMapByMapID(randomMapID).getMapID();
            String mapName = game.getMaps().getQinMapByMapID(randomMapID).getMapName();
            // 判 断 玩 家 是 否 有 " I n M i x e d G a m e " 标 签
            if (onlinePlayer.getScoreboardTags().contains("InMixedGame")) {
                // 让 玩 家 重 新 传 送
                onlinePlayer.performCommand("qinkit tpspawnwithoutinfo");
            }
            onlinePlayer.sendMessage("§7> > §b§lQinKitPVPS §7--> §3地图已经轮换 §b" + mapID + " §3号：§7[ §b" + mapName + " §7]");
        }
    }
}
