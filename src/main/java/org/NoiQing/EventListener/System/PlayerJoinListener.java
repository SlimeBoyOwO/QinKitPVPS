package org.NoiQing.EventListener.System;

import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.NoiQing.BukkitRunnable.WeatherRunnable;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.DataBaseCache;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.NoiQing.util.QinConstant;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;

public class PlayerJoinListener implements Listener {
    private final QinKitPVPS plugin;
    public PlayerJoinListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void playerOnJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.getLocation().getWorld() != null) {
            if(player.getScoreboardTags().contains("InAWGaming")) {
                if(AWRound.isRunning()) {
                    String teamName = null;
                    for(String s : player.getScoreboardTags()) {
                        if(s.startsWith("TeamName_")) {
                            teamName = s.substring(9);
                            break;
                        }
                    }

                    if(teamName != null) {
                        QinTeam t = QinTeams.getQinTeamByName(teamName);
                        if(t != null) {
                            t.addTeamEntities(player);
                        }
                    }
                    player.teleport(BornAndDeathListener.playerRejoinAWWar(player));
                }
                return;
            }
        }

        initializationPlayer(player, plugin);

    }

    @EventHandler
    public void onPlayerToWorld(PlayerTeleportEvent e) {
        if (!Objects.equals(Objects.requireNonNull(e.getTo()).getWorld(), e.getFrom().getWorld())) {
            PluginScoreboard.changeScoreboard(e.getPlayer());
            if(Objects.requireNonNull(e.getTo().getWorld()).getName().equals("skyblock_copy")) {
                onEnterAWWorld(e.getPlayer());
            }
            else WeatherRunnable.getBossbar().addPlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMoveWorld(PlayerChangedWorldEvent e) {
        PluginScoreboard.changeScoreboard(e.getPlayer());
        if(e.getPlayer().getWorld().getName().equals("skyblock_copy")){
            onEnterAWWorld(e.getPlayer());
        } else WeatherRunnable.getBossbar().addPlayer(e.getPlayer());
    }

    private void onEnterAWWorld(Player player) {
        player.removeScoreboardTag(QinConstant.LOBBY_MARK);
        WeatherRunnable.getBossbar().removeAll();
        if(AWRound.isRunning()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("游戏正在进行...","观察模式",10,40,10);
        } else {
            ItemStack itemStack = new ItemStack(Material.CLOCK);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§a§l选队菜单");
                itemStack.setItemMeta(meta);
            }
            player.getInventory().clear();
            player.getInventory().setItem(4, itemStack);
        }
    }

    public static void initializationPlayer(Player player, QinKitPVPS plugin) {
        player.getInventory().clear();
        /* 传 送 玩 家 到 大 厅 */
        Function.playerTpLobby(player);
        player.sendTitle("§3§l欢迎 §b§l肥家！","    §3QinKitPVPS   §bVer 0.8.0      ",20,20*4,20);
        player.setGameMode(GameMode.ADVENTURE);
        refreshPlayer(player, plugin);
    }

    public static void refreshPlayer(Player player, QinKitPVPS plugin) {
        DataBaseCache.initPlayerCache(player);
        if(player.getGameMode().equals(GameMode.ADVENTURE))
            Function.playerReset(player);
        player.setLevel(0);
        player.setExp(1);
        try {
            PlayerDataSave.updatePlayerMoneyRecord(player, plugin.getMySQLDataBase().getPlayerMoney(player));
            PlayerDataSave.updatePlayerExpRecord(player, plugin.getMySQLDataBase().getPlayerExp(player));
            PlayerDataSave.setPlayerTextDataRecord(player,"playerPrefix", plugin.getMySQLDataBase().getPlayerEquipTitle(player));
            PlayerDataSave.setPlayerTextDataRecord(player, "killEffects",plugin.getMySQLDataBase().getPlayerEquipKillEffect(player));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // if(PlayerDataSave.getPlayerTextDataRecord(player, "playerPrefix") != null)
        //     player.setPlayerListName("§7[" + Function.changeColorCharacters(PlayerDataSave.getPlayerTextDataRecord(player, "playerPrefix")) + "§7]" + " §f" + player.getName());


        PluginScoreboard.changeScoreboard(player);
        WeatherRunnable.getBossbar().addPlayer(player);
    }
}
