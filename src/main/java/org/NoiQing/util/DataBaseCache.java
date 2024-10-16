package org.NoiQing.util;

import org.NoiQing.DataBase.MySQLDataBase;
import org.NoiQing.QinKitPVPS;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseCache {
    private static final Map<Player, List<String>> playerOwnKits = new HashMap<>();
    private static final Map<Player, List<String>> playerOwnKillEffects = new HashMap<>();
    private static final Map<Player, Integer> playerOwnKeys = new HashMap<>();
    private static final Map<Player, Map<String,String>> playerEquip = new HashMap<>();
    private static final Map<Player, List<String>> playerOwnKillSounds = new HashMap<>();
    /**name-职业名字  price-职业价格  rare-职业稀有度  available-是否可用**/
    private static Map<String, Map<String, String>> kitColumns = new HashMap<>();
    /**name-特效名称  price-特效价格  rare-特效稀有度**/
    private static Map<String, Map<String, String>> killEffectColumns = new HashMap<>();
    /**name-特效名称  price-特效价格  rare-特效稀有度**/
    private static Map<String, Map<String, String>> killSoundColumns = new HashMap<>();

    public static void initCache() {
        try {
            allKits = QinKitPVPS.getPlugin().getMySQLDataBase().getAllKits();
            kitColumns = QinKitPVPS.getPlugin().getMySQLDataBase().getKitInfo();
            killEffectColumns = QinKitPVPS.getPlugin().getMySQLDataBase().getKillEffectInfo();
            killSoundColumns = QinKitPVPS.getPlugin().getMySQLDataBase().getKillSoundInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, Map<String, String>> getKitColumns() {return kitColumns;}
    public static Map<String, Map<String, String>> getKillEffectColumns() {return killEffectColumns;}
    public static Map<String, Map<String, String>> getKillSoundColumns() {return killSoundColumns;}
    private static List<String> allKits = new ArrayList<>();
    public static List<String> getAllKits() {return allKits;}
    public static void addPlayerOwnKit(Player player, String kitName) throws SQLException {
        QinKitPVPS.getPlugin().getMySQLDataBase().givePlayerKit(player, kitName);
        playerOwnKits.get(player).add(kitName);
    }
    public static void addPlayerOwnKillSound(Player player, String soundID) throws SQLException {
        QinKitPVPS.getPlugin().getMySQLDataBase().givePlayerKillSound(player, soundID);
        playerOwnKillSounds.get(player).add(soundID);
    }
    public static List<String> getPlayerOwnKits(Player player) {
        return playerOwnKits.get(player);
    }
    public static List<String> getPlayerOwnKillEffects(Player player) { return playerOwnKillEffects.get(player); }
    public static List<String> getPlayerNotOwnKits(Player player) {
        List<String> notOwnKits = new ArrayList<>();
        if(allKits.isEmpty()) player.sendMessage("出错了，所有职业显示为空");
        for(String kitName : allKits)
            if(!playerOwnKits.get(player).contains(kitName)) {
                notOwnKits.add(kitName);
            }

        return notOwnKits;
    }
    public static int getPlayerKeys(Player player){ return playerOwnKeys.get(player); }
    public static List<String> getPlayerOwnKillSounds(Player player) {return playerOwnKillSounds.get(player);}
    public static String getPlayerEquipKillEffect(Player player) {return playerEquip.get(player).get("killEffect"); }
    public static void setPlayerEquipKillEffect(Player player, String killEffectID) {
        try {
            QinKitPVPS.getPlugin().getMySQLDataBase().setPlayerEquipKillEffect(player, killEffectID);
            playerEquip.get(player).put("killEffect", killEffectID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getPlayerEquipKillSound(Player player) {return playerEquip.get(player).get("killSound"); }

    public static void setPlayerEquipKillSound(Player player, String killSoundID) {
        try {
            QinKitPVPS.getPlugin().getMySQLDataBase().setPlayerEquipKillSound(player, killSoundID);
            playerEquip.get(player).put("killSound", killSoundID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getPlayerEquipTitle(Player player) {return playerEquip.get(player).get("title");}

    public static void setPlayerEquipTitle(Player player, String titleID) {
        try {
            QinKitPVPS.getPlugin().getMySQLDataBase().setPlayerEquipTitle(player, titleID);
            playerEquip.get(player).put("title", titleID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int getPlayerOwnKeys(Player player) {
        return playerOwnKeys.get(player);
    }
    public static void setPlayerOwnKeys(Player player, int keys) throws SQLException {
        QinKitPVPS.getPlugin().getMySQLDataBase().setPlayerKey(player,keys);
        playerOwnKeys.put(player, keys);

    }
    public static void initPlayerCache(Player player) {
        try {
            MySQLDataBase db = QinKitPVPS.getPlugin().getMySQLDataBase();
            playerOwnKits.put(player, db.getPlayerAllKits(player)) ;
            playerOwnKillEffects.put(player,db.getPlayerAllKillEffects(player));
            playerOwnKillSounds.put(player,db.getPlayerAllKillSounds(player));
            playerOwnKeys.put(player,db.getPlayerKey(player));
            playerEquip.put(player,new HashMap<>());
            playerEquip.get(player).put("killEffect",db.getPlayerEquipKillEffect(player));
            playerEquip.get(player).put("killSound",db.getPlayerEquipKillSound(player));
            playerEquip.get(player).put("title",db.getPlayerEquipTitle(player));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void removePlayerCache(Player player) {
        playerOwnKits.remove(player);
        playerOwnKillEffects.remove(player);
        playerOwnKeys.remove(player);
        playerEquip.remove(player);
    }
}
