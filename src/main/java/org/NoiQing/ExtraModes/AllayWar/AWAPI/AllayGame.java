package org.NoiQing.ExtraModes.AllayWar.AWAPI;

import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWFunction;
import org.NoiQing.ExtraModes.MiniGames.MiniGameRound;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AllayGame extends MiniGameRound {
    private int chooseMapID;
    private boolean isDebug;
    String winTeam;
    private final Map<String, Set<Player>> teamPlayers;
    private final Map<String, Map<String,Integer>> teamLevels;
    private final Map<String, Integer> teamMoney;
    private final Map<String, String> tagToTeamMap = Map.of(
            "AW_ready_blue", "悦灵蓝",
            "AW_ready_red", "悦灵红",
            "AW_ready_yellow", "悦灵黄",
            "AW_ready_green", "悦灵绿"
    );

    public AllayGame() {
        super(-1);
        chooseMapID = 1;
        teamPlayers = new HashMap<>();
        teamLevels = new HashMap<>();
        teamMoney= new HashMap<>();
        winTeam = "无";
    }

    @Override
    protected void onStartGame() {
        initRound();
    }

    @Override
    protected void onPlayerReady() {

    }

    @Override
    protected void onEndGame() {
        String winTeamName = winTeam;    // TODO 获取胜利的队伍
        for (Player p : Objects.requireNonNull(Bukkit.getWorld("skyblock_copy")).getPlayers()) {
            Function.clearPlayerTeam(p);
            p.sendTitle(winTeamName + "队伍获胜！","",10,60,10);
            Function.sendPlayerSystemMessage(p,"游戏结束，游戏将会在8s后重启");
            new BukkitRunnable() {
                @Override
                public void run() {
                    gameBar.removeAll();
                    QinTeams.leaveTeam(p);
                    Function.clearPlayerTeam(p);
                    Function.playerReset(p);
                    Function.playerTpLobby(p);
                    p.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(QinKitPVPS.getPlugin(),5 * 20);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                reloadCopyWorld();
            }
        }.runTaskLater(QinKitPVPS.getPlugin(),6 * 20);
    }

    @Override
    protected boolean judgeEnd() {
        if(isDebug) return false;
        int teamAlive = 0;
        String winTeam = "无";
        for(Map.Entry<String, Set<Player>> entry : teamPlayers.entrySet()) {
            if(entry.getValue().size() > 0) {
                teamAlive++;
                winTeam = entry.getKey();
            }
            if(teamAlive > 1) return false;
        }
        this.winTeam = winTeam;
        return true;
    }

    @Override
    protected void onAddGameDuration() {
        gameBar.setTitle("§b§l悦灵战争 §3游戏时长：§b" + getTimeClock());
        if(judgeEnd()) endGame();
    }

    private void initRound() {
        World allayWorld = Bukkit.getWorld("skyblock_copy");
        if(allayWorld == null) {
            Function.broadcastSystemMessage("有人尝试在悦灵战争没启动的情况下启动！");
            return;
        }

        initTeamLevels();
        QinMap AWMap = QinKitPVPS.getPlugin().getKitGame().getMaps().getAllayQinMapByMapID(chooseMapID);
        initMap(AWMap);

        isDebug = false;
        winTeam = "无";

        gameBar.setProgress(1);
        gameBar.setTitle("§b§l悦灵战争 §3游戏时长：§b" + getTimeClock());

        for (String teamName : tagToTeamMap.values()) {
            teamPlayers.put(teamName, new HashSet<>());
        }

        allayWorld.setTime(18000);

        List<Player> players = allayWorld.getPlayers();
        if(players.size() == 1) {
            isDebug = true;
            Function.broadcastSystemMessage("检测到独自开启游戏！Debug模式启动！使用 /qinkit endAW 结束调试");
        }
        for (Player p : players) {

            PluginScoreboard.changeScoreboard(p);

            for (Map.Entry<String, String> entry : tagToTeamMap.entrySet()) {
                gameBar.addPlayer(p);
                if (p.getScoreboardTags().contains(entry.getKey())) {
                    Function.clearPlayerTeam(p);
                    QinTeam playerTeam = QinTeams.getQinTeamByName(entry.getValue());

                    if (playerTeam != null) {
                        playerTeam.addTeamEntities(p);
                    }
                    teamPlayers.get(entry.getValue()).add(p);

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

                    p.teleport(new Location(allayWorld, loc.getX() + 0.5,loc.getY() + 0.5,loc.getZ() + 0.5));

                }
            }

            if(!p.getScoreboardTags().contains("InAWGaming")) {
                p.setGameMode(GameMode.SPECTATOR);
                Location loc = AWMap.getCenterLocation();
                p.teleport(new Location(allayWorld, loc.getX() + 0.5,loc.getY() + 0.5,loc.getZ() + 0.5));
            }
        }
    }

    private void initTeamLevels() {
        for(QinTeam t : QinTeams.getTeams()) t.clearTeamEntities();
        for(Map.Entry<String, String> entry : tagToTeamMap.entrySet()) {
            teamMoney.put(entry.getValue(), 1000);
            teamLevels.put(entry.getValue(), new HashMap<>());
            teamLevels.get(entry.getValue()).put("Sharpness",0);
            teamLevels.get(entry.getValue()).put("Protection",0);
            teamLevels.get(entry.getValue()).put("MoneyLvl",0);
            teamLevels.get(entry.getValue()).put("GivenBase",0);
            teamLevels.get(entry.getValue()).put("HaveBase",0);
            teamLevels.get(entry.getValue()).put("HaveRaider",0);
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
    public static void reloadCopyWorld() {
        Function.copyWorld("skyblock","skyblock_copy");
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.createWorld(new WorldCreator("skyblock_copy"));
                if (world != null) {
                    world.setGameRule(GameRule.KEEP_INVENTORY,true);
                    world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);
                    world.setGameRule(GameRule.DO_MOB_LOOT,false);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
                    world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
                }
                Function.broadcastSystemMessage("塔防战争已经重置完毕~ (*^▽^*)");
            }
        }.runTaskLater(QinKitPVPS.getPlugin(),5 * 20);
    }

    public boolean upgradeTeamSharpnessLevel(Player p, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return false;
        if(noAllowTeamUpgrade(p, "Sharpness", level)) return false;

        getTeamLevels(entityTeam.getTeamName()).put("Sharpness",level);
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
    public boolean upgradeTeamProtectionLevel(Player p, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return false;
        if(noAllowTeamUpgrade(p, "Protection", level)) return false;


        getTeamLevels(entityTeam.getTeamName()).put("Protection",level);
        for(Player teamMembers : entityTeam.getTeamPlayers()) {
            Function.sendPlayerSystemMessage(teamMembers,"§7§l队伍保护等级升级到 §a§l"+level+" §7§l级");
            for(ItemStack item : teamMembers.getInventory().getArmorContents()) {
                if(item == null) continue;
                item.addUnsafeEnchantment(Enchantment.PROTECTION,level);
            }
        }

        return true;
    }
    public boolean upgradeTeamMoneyLevel(Player p, int level) {
        QinTeam entityTeam = QinTeams.getEntityTeam(p);
        if(entityTeam == null) return false;
        if(noAllowTeamUpgrade(p, "MoneyLvl", level)) return false;
        getTeamLevels(entityTeam.getTeamName()).put("MoneyLvl",level);
        for(Player teamMembers : entityTeam.getTeamPlayers()) {
            Function.sendPlayerSystemMessage(teamMembers,"§7§l队伍金钱等级升级到 §a§l"+level+" §7§l级");
        }
        return true;
    }
    private boolean noAllowTeamUpgrade(Player p, String subject, int level) {
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

    public void addPlayerToTeam(Player p, String teamName) {
        Set<Player> set = teamPlayers.getOrDefault(teamName, null);
        if(set == null) {
            Function.sendPlayerSystemMessage(p,"你加入了一个不存在的队伍！");
            return;
        }
        QinTeam qinTeam = QinTeams.getQinTeamByName(teamName);
        if(qinTeam != null && !qinTeam.getTeamPlayers().contains(p))
            qinTeam.addTeamEntities(p);
        set.add(p);
    }
    public void removePlayerFromTeam(Player p) {
        QinTeam qTeam = QinTeams.getEntityTeam(p);
        if (qTeam != null) qTeam.removeTeamEntities(p);

        for(Map.Entry<String, Set<Player>> entry : teamPlayers.entrySet()) {
            Set<Player> team = entry.getValue();
            team.remove(p);
        }
    }

    public void setChooseMapID(int mapID) {this.chooseMapID = mapID;}

    public int getChooseMapID() {return chooseMapID;}
    public Set<String> getAllayTeams() {
        Set<String> teams = new HashSet<>();
        for (Map.Entry<String, String> entry : tagToTeamMap.entrySet()) {
            String teamName = entry.getValue();
            teams.add(teamName);
        }
        return teams;
    }
    public Map<String, Integer> getTeamLevels(String teamName) {
        return teamLevels.get(teamName);
    }
    public String getPlayerTeam(Player player) {
        for(Map.Entry<String, Set<Player>> entry : teamPlayers.entrySet()) {
            Set<Player> team = entry.getValue();
            if(team.contains(player)) return entry.getKey();
        }
        return "无";
    }
    public String getMobTeam(Entity e) {
        QinTeam team = QinTeams.getEntityTeam(e);
        if(team != null) return team.getTeamName();
        return "无";
    }
    public int getPlayerTeamMoney(Player player) {
        String team = getPlayerTeam(player);
        if(team.equals("无")) return -1;
        return teamMoney.getOrDefault(team, -1);
    }
    public int getTeamMoney(String team) {
        return teamMoney.getOrDefault(team,-1);
    }

    public void setPlayerTeamMoney(Player player, int money) {
        String team = getPlayerTeam(player);
        if(team.equals("无")) return;
        teamMoney.put(team,money);
    }
    public void setTeamMoney(String team, int money) {
        teamMoney.put(team,money);
    }
}
