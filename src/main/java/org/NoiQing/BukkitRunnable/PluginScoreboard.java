package org.NoiQing.BukkitRunnable;

import fr.mrmicky.fastboard.FastBoard;
import net.md_5.bungee.api.ChatColor;
import org.NoiQing.ExtraModes.AllayWar.AWAPI.AllayGame;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginScoreboard extends BukkitRunnable {

    @Override
    public void run(){
        for(Player player : Bukkit.getOnlinePlayers())
            changeScoreboard(player);

    }

    public static void changeScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();

        if(player.getWorld().getName().equals("skyblock_copy") || player.getWorld().getName().equals("skyblock")) {
            // Set the title
            board.updateTitle(Function.changeColorCharacters(">&#3dd4d1>&l悦灵战争<&#02faf6<"));

            String info;
            QinTeam team = QinTeams.getEntityTeam(player);
            if(team == null || team.getTeamName() == null || allayGame.getTeamLevels(team.getTeamName()).getOrDefault("HaveBase",0) == 0)
                info = "丢失";
            else info = "存在";

            // Change the lines
            if(allayGame.isRunning()) {
                board.updateLines(
                        "",
                        Function.changeColorCharacters(">&#e8dc00>&l信息：<&#fcf003<"),
                        "§a名字: §f" + player.getName(),
                        "§§a游戏时间： §f" + allayGame.getTimeClock(),
                        "§§a经济： §f" + ChatColor.of("#d6b865")+ AWPlayer.getPlayerAWMoney(player),
                        "§§a主基地： §f" + info,
                        "",
                        Function.changeColorCharacters(">&#056e6c>&l天气预报：<&#05b0ad<") + (WeatherDataSave.getWeatherStorage().get(player.getWorld()) == null ? "§7无" : WeatherDataSave.getWeatherStorage().get(player.getWorld())),
                        "",
                        Function.changeColorCharacters(">&#fc5603>&l连杀数：<&#fc4103<"),
                        "§f" + PlayerDataSave.getPlayerKillStreaks(player,"KillStreaks") + " 连杀",
                        "",
                        "§7QQ群：665188287"
                );
            } else if(PvzRound.isRunning()) {
                board.updateLines(
                        "",
                        Function.changeColorCharacters(">&#e8dc00>&l信息：<&#fcf003<"),
                        "§a名字: §f" + player.getName(),
                        "§a阳光： §f" + ChatColor.of("#d6b865")+ PvzRound.getTotalSun(),
                        "§a钱钱： §f" + "§e" + PvzEntity.getPlayerMoney(player),
                        "§a波数： §f" + ChatColor.AQUA + PvzRound.getCurrentSmallWave() + " / " + PvzRound.getTotalSmallWaves(),
                        "§a剩余僵尸： §f" + ChatColor.AQUA + PvzRound.getRemainZombies(),
                        "",
                        Function.changeColorCharacters(">&#056e6c>&l天气预报：<&#05b0ad<") + (WeatherDataSave.getWeatherStorage().get(player.getWorld()) == null ? "§7无" : WeatherDataSave.getWeatherStorage().get(player.getWorld())),
                        "",
                        Function.changeColorCharacters(">&#fc5603>&l连杀数：<&#fc4103<"),
                        "§f" + PlayerDataSave.getPlayerKillStreaks(player,"KillStreaks") + " 连杀",
                        "",
                        "§7QQ群：665188287"
                );
            }else {
                board.updateLines(
                        "│ 等待游戏开始...",
                        "│ 当前选择地图：" + QinKitPVPS.getPlugin().getKitGame().getMaps().getAllayQinMapByMapID(allayGame.getChooseMapID()).getMapName()
                );
            }

        } else {
            // Set the title
            board.updateTitle(Function.changeColorCharacters(">&#3dd4d1>&lQinKitPVP<&#02faf6<"));



            // Change the lines
            board.updateLines(
                    "",
                    Function.changeColorCharacters(">&#e8dc00>&l信息：<&#fcf003<"),
                    "§a名字: §f" + player.getName(),
                    "§a等级： §f" + PlayerDataSave.getPlayerRank(player),
                    "§a进度： §b" + PlayerDataSave.getPlayerRankExp(player) + "§7 / §a" + PlayerDataSave.getPlayerMaxRankExp(player),
                    "§a钱钱： §f" + ChatColor.of("#d6b865")+ PlayerDataSave.getPlayerMoneyRecord(player),
                    "",
                    Function.changeColorCharacters(">&#056e6c>&l天气预报：<&#05b0ad<") + (WeatherDataSave.getWeatherStorage().get(player.getWorld()) == null ? "§7无" : WeatherDataSave.getWeatherStorage().get(player.getWorld())),
                    "",
                    Function.changeColorCharacters(">&#fc5603>&l连杀数：<&#fc4103<"),
                    "§f" + PlayerDataSave.getPlayerKillStreaks(player,"KillStreaks") + " 连杀",
                    "",
                    "§7QQ群：665188287"
            );
        }


    }

}
