package org.NoiQing.AllayWar.AWAPI;

import org.NoiQing.AllayWar.AWUtils.AWFunction;
import org.NoiQing.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AWRound {
    //以下是teamPlayers的注释
    private static int chooseMapID = 0;
    private static final Map<String, Set<Player>> teamPlayers = new HashMap<>();
    private static final Map<String, Map<String,Integer>> teamLevels = new HashMap<>();
    private static boolean isRunning = false;
    private static int time = 0;
    private static final Map<String, String> tagToTeamMap = Map.of(
            "AW_ready_blue", "悦灵蓝",
            "AW_ready_red", "悦灵红",
            "AW_ready_yellow", "悦灵黄",
            "AW_ready_green", "悦灵绿"
    );

    public static void initRound() {
        initTeamLevels();
        QinMap AWMap = QinKitPVPS.getPlugin().getGame().getMaps().getAllayQinMapByMapID(chooseMapID);
        initMap(AWMap);

        isRunning = true;
        time = 0;

        for (String teamName : tagToTeamMap.values()) {
            teamPlayers.put(teamName, new HashSet<>());
        }

        for (Player p : Objects.requireNonNull(Bukkit.getWorld("skyblock_copy")).getPlayers()) {

            PluginScoreboard.changeScoreboard(p);

            for (Map.Entry<String, String> entry : tagToTeamMap.entrySet()) {
                if (p.getScoreboardTags().contains(entry.getKey())) {
                    Function.clearPlayerTeam(p);
                    QinTeam playerTeam = QinTeams.getQinTeamByName(entry.getValue());

                    if (playerTeam != null) {
                        playerTeam.addTeamEntities(p);
                    }
                    teamPlayers.get(entry.getValue()).add(p);

                    AWPlayer.setPlayerAWMoney(p,1000);

                    p.getInventory().clear();
                    p.setInvulnerable(false);
                    p.setSaturation(5);
                    Function.setPlayerMaxHealth(p,40);
                    AWFunction.givePlayerRespawnItem(p);
                    p.addScoreboardTag("InAWGaming");
                    p.addScoreboardTag("AW_Team_" + entry.getValue().substring(2));
                    p.setGameMode(GameMode.SURVIVAL);
                    p.removeScoreboardTag(entry.getKey());


                    if (playerTeam != null && teamLevels.get(playerTeam.getTeamName()).get("GivenBase") < 1) {

                        ItemStack mine = new ItemStack(Material.RAW_GOLD_BLOCK);
                        ItemMeta meta1 = mine.getItemMeta();
                        if (meta1 != null) {
                            meta1.setDisplayName("§e§l建筑 - 矿场");
                            mine.setItemMeta(meta1);
                        }

                        ItemStack sniper = new ItemStack(Material.BONE_BLOCK);
                        ItemMeta meta2 = sniper.getItemMeta();
                        if (meta2 != null) {
                            meta2.setDisplayName("§e§l建筑 - 狙击塔");
                            sniper.setItemMeta(meta2);
                        }

                        ItemStack base = new ItemStack(Material.BEACON);
                        ItemMeta meta3 = base.getItemMeta();
                        if (meta3 != null) {
                            meta3.setDisplayName("§e§l建筑 - 生命核心");
                            base.setItemMeta(meta3);
                        }
                        p.getInventory().addItem(base, mine, sniper);
                        teamLevels.get(playerTeam.getTeamName()).put("GivenBase", 1);
                    }

                    p.sendTitle("§7꧁ꕥ §b游戏开始 §7ꕥ꧂","§e☘ 祝君好运 ☘",20,3*20,20);
                    p.playSound(p, Sound.ENTITY_ENDER_DRAGON_AMBIENT,1,1);

                    int teleportLocation = 0;
                    switch (entry.getKey()) {
                        case "AW_ready_yellow" -> teleportLocation = 1;
                        case "AW_ready_blue" -> teleportLocation = 2;
                        case "AW_ready_green" -> teleportLocation = 3;
                    }

                    Location loc = AWMap.getLocation().get(String.valueOf(teleportLocation));

                    p.teleport(new Location(Bukkit.getWorld("skyblock_copy"), loc.getX() + 0.5,loc.getY() + 0.5,loc.getZ() + 0.5));

                }
            }

            if(!p.getScoreboardTags().contains("InAWGaming")) {
                p.setGameMode(GameMode.SPECTATOR);
                Location loc = AWMap.getCenterLocation();
                p.teleport(new Location(Bukkit.getWorld("skyblock_copy"), loc.getX() + 0.5,loc.getY() + 0.5,loc.getZ() + 0.5));
            }
        }


    }

    private static void initMap(QinMap map) {
        double startX = map.getCenterLocation().getX() - map.getSpreadRadius().getX();
        double startY = map.getCenterLocation().getY() - map.getSpreadRadius().getY();
        double startZ = map.getCenterLocation().getZ() - map.getSpreadRadius().getZ();

        for(int i = 0; i < map.getSpreadRadius().getX() * 2; i++)
            for(int j = 0; j < map.getSpreadRadius().getY() * 2; j++)
                for(int l = 0; l < map.getSpreadRadius().getZ() * 2; l++) {
                    if(Bukkit.getWorld("skyblock_copy") == null) continue;
                    Block block = Objects.requireNonNull(Bukkit.getWorld("skyblock_copy")).getBlockAt(new Location(Bukkit.getWorld("skyblock_copy"), startX + i, startY + j, startZ + l));
                    if(block.getType() == Material.AIR || block.isPassable() || block.isLiquid()) continue;
                    if(Function.isBlockHasTag(block, "Allay_Tower") && Function.isBlockHasTag(block.getLocation().clone().add(0,-1,0).getBlock(), "Allay_Tower")) {
                        continue;
                    }

                    block.setMetadata("OriginalMap", new FixedMetadataValue(QinKitPVPS.getPlugin(), true));
                }
    }

    private static void judgeWin() {
        int zeroTeams = 0;
        String winTeam = "None";
        for (Map.Entry<String, String> entry : tagToTeamMap.entrySet())
            if(teamPlayers.getOrDefault(entry.getValue(),new HashSet<>()).size() == 0)
                zeroTeams++;
            else winTeam = entry.getValue();
        if(zeroTeams == tagToTeamMap.entrySet().size() - 1) {
            endRound(winTeam);
        } else if(zeroTeams == tagToTeamMap.entrySet().size()) {
            Bukkit.broadcastMessage("出错！重启游戏");
            endRound("None");
        }
    }

    public static void endRound(String teamName) {
        for (Player p : Objects.requireNonNull(Bukkit.getWorld("skyblock_copy")).getPlayers()) {
            Function.clearPlayerTeam(p);
            p.sendTitle(teamName + "队伍获胜！","",10,60,10);
            Function.sendPlayerSystemMessage(p,"游戏结束，游戏将会在8s后重启");
            new BukkitRunnable() {
                @Override
                public void run() {
                    QinTeams.leaveTeam(p);
                    Function.clearPlayerTeam(p);
                    Function.playerReset(p);
                    Function.playerTpLobby(p);
                    p.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(QinKitPVPS.getPlugin(),5 * 20);
        }

        isRunning = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                reloadCopyWorld();
            }
        }.runTaskLater(QinKitPVPS.getPlugin(),6 * 20);

    }

    private static int checkTeamRemainPlayersNum(String teamName) {
        return teamPlayers.getOrDefault(teamName, Collections.emptySet()).size();
    }

    public static void addTime() {
        time++;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static String getTimeClock() {
        int newTime = time;
        int hour = newTime / 20 / 60;
        int minute = (newTime - (hour * 20 * 60)) / 20;
        String hourPart;
        String minutePart;

        if (hour < 10) hourPart = "0" + hour;
        else hourPart = String.valueOf(hour);
        if (minute < 10) minutePart = "0" + minute;
        else minutePart = String.valueOf(minute);

        return hourPart + ":" + minutePart;
    }

    private static void initTeamLevels() {

        for(QinTeam t : QinTeams.getTeams()) t.clearTeamEntities();


        for(Map.Entry<String, String> entry : tagToTeamMap.entrySet()) {
            teamLevels.put(entry.getValue(), new HashMap<>());
            teamLevels.get(entry.getValue()).put("Sharpness",0);
            teamLevels.get(entry.getValue()).put("Protection",0);
            teamLevels.get(entry.getValue()).put("MoneyLvl",0);
            teamLevels.get(entry.getValue()).put("GivenBase",0);
            teamLevels.get(entry.getValue()).put("HaveBase",0);
            teamLevels.get(entry.getValue()).put("HaveRaider",0);
        }
    }

    public static void reloadCopyWorld() {
        Function.copyWorld("skyblock","skyblock_copy");
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.createWorld(new WorldCreator("skyblock_copy"));
                if (world != null) {
                    world.setGameRule(GameRule.KEEP_INVENTORY,true);
                    world.setGameRule(GameRule.DO_MOB_LOOT,false);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
                    world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
                }
                Bukkit.broadcastMessage("§b§lQinKitPVPS -> 塔防战争已经重置完毕~ (*^▽^*)");
            }
        }.runTaskLater(QinKitPVPS.getPlugin(),5 * 20);
    }

    public static Map<String, Integer> getTeamLevels(String teamName) {
        return teamLevels.get(teamName);
    }

    public static boolean upgradeTeamSharpnessLevel(Player p, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return false;
        if(noAllowTeamUpgrade(p, "Sharpness", level)) return false;

        AWRound.getTeamLevels(entityTeam.getTeamName()).put("Sharpness",level);
        for(Player teamMembers : entityTeam.getTeamPlayers()) {
            Function.sendPlayerSystemMessage(teamMembers,"§7§l队伍锋利等级升级到 §a§l"+level+" §7§l级");
            for(ItemStack item : teamMembers.getInventory().getContents()) {
                if(item == null) continue;
                if(item.getType().toString().endsWith("SWORD"))
                    item.addUnsafeEnchantment(Enchantment.SHARPNESS,level);
            }
        }

        return true;

    }

    public static boolean upgradeTeamProtectionLevel(Player p, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return false;
        if(noAllowTeamUpgrade(p, "Protection", level)) return false;


        AWRound.getTeamLevels(entityTeam.getTeamName()).put("Protection",level);
        for(Player teamMembers : entityTeam.getTeamPlayers()) {
            Function.sendPlayerSystemMessage(teamMembers,"§7§l队伍保护等级升级到 §a§l"+level+" §7§l级");
            for(ItemStack item : teamMembers.getInventory().getArmorContents()) {
                if(item == null) continue;
                item.addUnsafeEnchantment(Enchantment.PROTECTION,level);
            }
        }

        return true;
    }

    public static boolean upgradeTeamMoneyLevel(Player p, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return false;
        if(noAllowTeamUpgrade(p, "MoneyLvl", level)) return false;

        AWRound.getTeamLevels(entityTeam.getTeamName()).put("MoneyLvl",level);
        for(Player teamMembers : entityTeam.getTeamPlayers()) {
            Function.sendPlayerSystemMessage(teamMembers,"§7§l队伍金钱等级升级到 §a§l"+level+" §7§l级");
        }

        return true;
    }

    public static void setChooseMapID(int x) {
        chooseMapID = x;
    }

    public static int getChooseMapID() {
        return chooseMapID;
    }

    private static boolean noAllowTeamUpgrade(Player p, String subject, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return true;

        int levelNow = teamLevels.get(entityTeam.getTeamName()).get(subject);
        if(levelNow >= level) {
            Function.sendPlayerSystemMessage(p,"§c§l你的队伍已经达到该等级了");
            return true;
        }
        if(level - levelNow > 1) {
            Function.sendPlayerSystemMessage(p,"§c§l你还没有解锁前面的升级项");
            return true;
        }
        return false;
    }

    public static Map<String, Set<Player>> getTeamPlayers() {return teamPlayers;}
}
