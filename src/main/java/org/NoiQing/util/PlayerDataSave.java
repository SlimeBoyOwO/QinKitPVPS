package org.NoiQing.util;

import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.NoiQing.QinKitPVPS;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDataSave {

    //主动技能冷却
    private static final Map<Player, Map<String, Long>> SkillCoolDowns = new HashMap<>();
    //玩家连杀记录
    private static final Map<Player, Map<String, Long>> PlayerKillStreaks = new HashMap<>();
    //最近一次攻击玩家记录
    private static final Map<Player, Player> LastAttackPlayerRecord = new HashMap<>();
    //最近一次攻击玩家时间记录
    private static final Map<Player, Map<Player, Long>> LastAttackPlayerTimeRecord = new HashMap<>();
    private static final Map<Player, Long> LastShiftTimeRecord = new HashMap<>();
    //玩家所持金钱记录
    private static final Map<Player, Integer> PlayerMoneyRecord = new HashMap<>();
    //玩家所持经验记录
    private static final Map<Player, Integer> PlayerExpRecord = new HashMap<>();
    //玩家所选择职业记录
    private static final Map<Player, String> PlayerKitRecord = new HashMap<>();
    //玩家文字数组数据库记录
    private static final Map<Player, Map<String, String>> PlayerTextDataRecord = new HashMap<>();
    //玩家被动技能冷却时间记录
    private static final Map<Player, Map<String, Long>> PassiveSkillCoolDowns = new HashMap<>();
    //玩家被动技能记录
    private static final Map<Player, Map<String, Long>> PassiveSkillRecords = new HashMap<>();

    /* 玩 家 冷 却 时 间 记 录 */
    public static boolean ifPlayerSkillPassCoolDownTime(Player player, String skill){
        skillCoolDownInitialization(player);
        long currentTime = System.currentTimeMillis();
        if(SkillCoolDowns.get(player).get(skill) == null){
            return true;
        }else if(System.currentTimeMillis() < SkillCoolDowns.get(player).get(skill)){
            int CoolDownTimeLeft = (int) (SkillCoolDowns.get(player).get(skill) - currentTime) /1000 + 1;
            List<String> commands = new ArrayList<>();
            commands.add("console: title " + player.getName() + " actionbar [\"\\u00a73\\u00a7l冷却剩余 \\u00a7b" + CoolDownTimeLeft + " \\u00a73\\u00a7l秒\"]");
            Function.executeCommands(player, commands,"none","none");
            return false;
        }
        return true;
    }
    public static long getPlayerSkillCoolDownTime(Player player, String skill){
        skillCoolDownInitialization(player);
        return SkillCoolDowns.get(player).get(skill);
    }
    public static void setPlayerSkillCoolDownTime(Player player, String skill, double coolDownTime){
        long passCoolDownTime = (long) (System.currentTimeMillis() + coolDownTime* 1000L);

        skillCoolDownInitialization(player);
        SkillCoolDowns.get(player).remove(skill);
        SkillCoolDowns.get(player).put(skill, passCoolDownTime);
        player.setCooldown(player.getInventory().getItemInMainHand().getType(), (int) coolDownTime * 20);
    }
    public static void clearPlayerSkillCoolDownTime(Player player){
        SkillCoolDowns.remove(player);
    }
    public static void skillCoolDownInitialization(Player player){
        if (!SkillCoolDowns.containsKey(player)) {
            SkillCoolDowns.put(player, new HashMap<>());
        }
    }

    /*被动技能冷却记录*/
    public static boolean ifPlayerPassiveSkillPassCoolDownTime(Player player, String skill){
        passiveSkillCoolDownInitialization(player);
        long currentTime = System.currentTimeMillis();
        if(PassiveSkillCoolDowns.get(player).get(skill) == null){
            return true;
        }else return currentTime >= PassiveSkillCoolDowns.get(player).get(skill);
    }
    public static void setPlayerPassiveSkillCoolDownTime(Player player, String skill, double coolDownTime){
        //计算秒数
        long passCoolDownTime = (long) (System.currentTimeMillis() + coolDownTime* 1000L);

        passiveSkillCoolDownInitialization(player);
        PassiveSkillCoolDowns.get(player).remove(skill);
        PassiveSkillCoolDowns.get(player).put(skill, passCoolDownTime);
    }
    public static void passiveSkillCoolDownInitialization(Player player){
        if (!PassiveSkillCoolDowns.containsKey(player)) {
            PassiveSkillCoolDowns.put(player, new HashMap<>());
        }
    }
    /*被动技能记录*/
    public static long getPlayerPassiveSkillRecords(Player player, String passiveRecord){
        passiveRecordInitialization(player, passiveRecord);
        return PassiveSkillRecords.get(player).get(passiveRecord);
    }
    public static void setPlayerPassiveSkillRecords(Player player, String passiveRecord, Long record){
        passiveRecordInitialization(player,passiveRecord);
        PassiveSkillRecords.get(player).remove(passiveRecord);
        PassiveSkillRecords.get(player).put(passiveRecord,record);
    }
    public static void setPlayerPassiveSkillRecords(Player player, String passiveRecord, Long record, boolean level){
        setPlayerPassiveSkillRecords(player,passiveRecord,record);
        if(level)
            player.setLevel(Math.toIntExact(record));
    }
    public static void clearPlayerPassiveSkillRecords(Player player){
        PassiveSkillRecords.remove(player);
    }
    private static void passiveRecordInitialization(Player player, String passiveRecord) {
        if(!PassiveSkillRecords.containsKey(player)){
            PassiveSkillRecords.put(player,new HashMap<>());
            PassiveSkillRecords.get(player).put(passiveRecord, 0L);
        }
        if(!PassiveSkillRecords.get(player).containsKey(passiveRecord)){
            PassiveSkillRecords.get(player).put(passiveRecord, 0L);
        }
    }
    /* 玩 家 连 杀 个 数 记 录 */
    public static void playerKillStreaksInitialization(Player player){
        if (!PlayerKillStreaks.containsKey(player)) {
            PlayerKillStreaks.put(player, new HashMap<>());
            PlayerKillStreaks.get(player).put("KillStreaks",0L);
        }
    }
    public static void setPlayerKillStreaks(Player player, String instantKills, long kills){
        playerKillStreaksInitialization(player);
        PlayerKillStreaks.get(player).remove(instantKills);
        PlayerKillStreaks.get(player).put(instantKills, kills);
        PluginScoreboard.changeScoreboard(player);
    }
    public static long getPlayerKillStreaks(Player player, String instantKills){
        playerKillStreaksInitialization(player);
        return PlayerKillStreaks.get(player).get(instantKills);
    }
    /* 玩 家 击 杀 榜 记 录 */
    public static void setLastAttackPlayerRecord(Player player, Player damager){
        LastAttackPlayerRecord.remove(player);
        LastAttackPlayerTimeRecord.remove(player);
        LastAttackPlayerRecord.put(player,damager);
        LastAttackPlayerTimeRecord.put(player,new HashMap<>());
        LastAttackPlayerTimeRecord.get(player).put(damager,System.currentTimeMillis());
    }
    public static void clearLastAttackPlayerRecord(Player player){
        LastAttackPlayerTimeRecord.remove(player);
        LastAttackPlayerRecord.remove(player);
    }
    public static Player getLastAttackPlayerRecord(Player player){
        if(LastAttackPlayerTimeRecord.get(player) == null || LastAttackPlayerTimeRecord.get(player).get(LastAttackPlayerRecord.get(player)) == null){
            return null;
        }
        else if(System.currentTimeMillis() - LastAttackPlayerTimeRecord.get(player).get(LastAttackPlayerRecord.get(player)) <= 10000){
            return LastAttackPlayerRecord.getOrDefault(player, null);
        }else{
            return null;
        }
    }
    /*玩家职业选择记录*/
    public static void clearPlayerKitRecord(Player player) {PlayerKitRecord.remove(player);}
    public static void setPlayerKitRecord(Player player, String kitName) {
        PlayerKitRecord.remove(player);
        PlayerKitRecord.put(player,kitName);
    }
    public static String getPlayerKitRecord(Player player){
        return PlayerKitRecord.getOrDefault(player, "无");
    }

    /* 玩 家 经 济 钱 数 记 录 */
    public static void updatePlayerMoneyRecord(Player player, int money) {
        PlayerMoneyRecord.remove(player);
        PlayerMoneyRecord.put(player,money);
        try{
            QinKitPVPS.getPlugin().getMySQLDataBase().updatePlayerMoney(player, money);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        PluginScoreboard.changeScoreboard(player);
    }
    public static Integer getPlayerMoneyRecord(Player player){
        if(!PlayerMoneyRecord.containsKey(player)){
            PlayerMoneyRecord.put(player, 0);
        }
        return PlayerMoneyRecord.get(player);
    }
    public static void clearPlayerDataSave(){
        PlayerKillStreaks.clear();
        SkillCoolDowns.clear();
    }
    /*玩家文字数据库记录*/
    public static void setPlayerTextDataRecord(Player player, String column, String data) {
        if(!PlayerTextDataRecord.containsKey(player)){
            PlayerTextDataRecord.put(player, new HashMap<>());
            PlayerTextDataRecord.get(player).put(column, data);
            return;
        }
        if(!PlayerTextDataRecord.get(player).containsKey(column)){
            PlayerTextDataRecord.get(player).put(column,data);
            return;
        }
        PlayerTextDataRecord.get(player).remove(column);
        PlayerTextDataRecord.get(player).put(column,data);
    }
    public static String getPlayerTextDataRecord(Player player, String column) {
        if(!PlayerTextDataRecord.containsKey(player) || !PlayerTextDataRecord.get(player).containsKey(column))
            return null;
        return PlayerTextDataRecord.get(player).get(column);
    }

    /*玩家段位系统*/
    public static void updatePlayerExpRecord(Player player, int newExp) {
        int oldExp = PlayerExpRecord.getOrDefault(player, newExp);
        if(oldExp == 0) oldExp = newExp;

        PlayerExpRecord.remove(player);
        PlayerExpRecord.put(player,newExp);
        try{
            QinKitPVPS.getPlugin().getMySQLDataBase().updatePlayerExp(player, newExp);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        PluginScoreboard.changeScoreboard(player);

        if(isLevelUp(oldExp,newExp)){
            try{
                int keyAmount = QinKitPVPS.getPlugin().getMySQLDataBase().getPlayerKey(player);
                QinKitPVPS.getPlugin().getMySQLDataBase().setPlayerKey(player,keyAmount+1);
                DataBaseCache.setPlayerOwnKeys(player,keyAmount+1);
                Function.sendPlayerSystemMessage(player,"&3获得了一个抽奖宝箱~ &b(*^▽^*)");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /*玩家是否出发冲刺*/
    public static boolean isRushJump(Player player) {
        if(!LastShiftTimeRecord.containsKey(player)) return false;
        if(System.currentTimeMillis() - LastShiftTimeRecord.get(player) < 250) {
            LastShiftTimeRecord.remove(player);
            return true;
        }
        return false;
    }

    public static void setRushJumpRecord(Player player) {
        LastShiftTimeRecord.put(player, System.currentTimeMillis());
    }

    private static boolean isLevelUp(int oldExp, int newExp) {
        if(stringLevelToInt(levelFind(newExp)) % 2 != 0 && stringLevelToInt(levelFind(newExp)) >= 10) return false;
        if(oldExp < 2000) {
            return oldExp / 200 != newExp / 200;
        } else if(oldExp < 4500) {
            oldExp -= 2000;
            newExp -= 2000;
            return oldExp / 250 != newExp / 250;
        } else if(oldExp < 8500) {
            oldExp -= 4500;
            newExp -= 4500;
            return oldExp / 400 != newExp / 400;
        } else {
            oldExp -= 8500;
            newExp -= 8500;
            return oldExp / 500 != newExp / 500;
        }
    }

    public static Integer getPlayerExpRecord(Player player){
        if(!PlayerExpRecord.containsKey(player)){
            PlayerExpRecord.put(player, 0);
        }
        return PlayerExpRecord.get(player);
    }
    public static Integer getPlayerRankExp(Player player){
        int exp =  getPlayerExpRecord(player);
        if(exp < 2000) return exp % 200;
        else if (exp < 4500) return  (exp-2000) % 250;
        else if (exp < 8500) return (exp - 4500) % 400;
        else if (exp < 18500) return (exp - 8500) % 500;
        else return (exp - 18500) % 500;
    }

    public static Integer getPlayerMaxRankExp(Player player) {
        int exp =  getPlayerExpRecord(player);
        if(exp < 2000) return 200;
        else if (exp < 4500) return 250;
        else if (exp < 8500) return 400;
        else return 500;
    }
    public static String getPlayerRank(Player player){
        if(!PlayerExpRecord.containsKey(player)){
            PlayerExpRecord.put(player, 0);
        }
        Integer exp = PlayerExpRecord.get(player);
        return levelFind(exp);
    }
    public static String levelFind(int exp){
        int level = 0;
        if(exp <= 2000) {
            level += exp / 200;
            return "§7" + level + "§r";
        } else if (exp <= 4500) {
            level += (exp-2000) / 250 + 10;
            return "§r" + level + "§r";
        } else if (exp <= 8500) {
            level += (exp - 4500) / 400 + 20;
            return "§b" + level + "§r";
        } else if (exp <= 18500) {
            level += (exp - 8500) / 500 + 30;
            return "§a" + level + "§r";
        } else {
            level += (exp - 18500) / 500 + 40;
            int rank = level / 10;

            switch (rank) {
                case 4 -> {
                    return "§6" + level + "§r";
                }
                case 5 -> {
                    return "§e" + level + "§r";
                }
                case 6 -> {
                    return "§4" + level + "§r";
                }
                case 7 -> {
                    return "§c" + level + "§r";
                }
                case 8 -> {
                    return "§2" + level + "§r";
                }
                case 9 -> {
                    return "§9" + level + "§r";
                }
                case 10 -> {
                    return "§1" + level + "§r";
                }
            }
            if(rank > 11) {
                return "§3" + level + "§r";
            }
        }
        return "ERROR";
    }

    private static int stringLevelToInt(String level) {
        if(level.equals("ERROR")) return -1;
        return Integer.parseInt(level.substring(2,level.length() - 2));
    }

    public static void clearPlayerDataSave(Player player) {
        SkillCoolDowns.remove(player);
        PlayerKillStreaks.remove(player);
        LastAttackPlayerRecord.remove(player);
        LastAttackPlayerTimeRecord.remove(player);
        PlayerMoneyRecord.remove(player);
        PlayerExpRecord.remove(player);
        PlayerKitRecord.remove(player);
        PlayerTextDataRecord.remove(player);
        PassiveSkillCoolDowns.remove(player);
        PassiveSkillRecords.remove(player);
    }
}
