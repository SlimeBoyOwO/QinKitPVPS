package org.NoiQing.BukkitRunnable;

import org.NoiQing.util.Function;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamModeRunnable extends BukkitRunnable {
    private int teamGamePrepareStarted = 0;
    private int teamGameReallyStarted = 0;
    private int teamGameTimeLeft = 30 * 20;
    private int ifTeamGameEnded = 0;
    private int teamGameOverTimeLeft = 10 * 20;

    @Override
    public void run() {
        if(Bukkit.getWorld("teamkitpvp") == null) return; // 如果没有这个世界，那么就退出
        if(Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers().isEmpty()){
            return;
        }
        // 检 测 准 备 的 玩 家 数 量
        int numberOfPlayersPrepared = 0;

        for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
            if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                numberOfPlayersPrepared += 1;
            }
        }
        if(numberOfPlayersPrepared >= 2 && teamGamePrepareStarted == 0 && ifTwoTeamsHaveMembers()){
            for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                    playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3团队模式已经就绪完毕！游戏将在 §b30 §3秒后开始");
                    teamGamePrepareStarted = 1;
                }
            }
        }else if(numberOfPlayersPrepared <= 1 && teamGamePrepareStarted == 1 && !ifTwoTeamsHaveMembers()){
            for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                    playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3游戏人数不足，启动取消！有狗退了 §b(*>A<*)");
                    teamGamePrepareStarted = 0;
                    teamGameTimeLeft = 30 * 20;
                }
            }
        }else if(!ifTwoTeamsHaveMembers() && teamGamePrepareStarted == 1){
            for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                    playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3队伍不足，启动取消! 一个队伍怎么打哇 §b(*>A<*)");
                    teamGamePrepareStarted = 0;
                    teamGameTimeLeft = 30 * 20;
                }
            }
        }else if(numberOfPlayersPrepared >=2 && teamGamePrepareStarted == 1 && ifTwoTeamsHaveMembers()){
            teamGameTimeLeft -= 1;
            for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame") && teamGameTimeLeft % 20 == 0){
                    List<String> commands = new ArrayList<>();
                    commands.add("console: title " + playersInTeamKitPvPWorld.getName() + " actionbar [\"\\u00a73\\u00a7l团队死竞游戏模式准备时间剩余 \\u00a7b" + teamGameTimeLeft / 20 + " \\u00a73\\u00a7l秒\"]");
                    Function.executeCommands(playersInTeamKitPvPWorld, commands,"none","none");
                    if(teamGameTimeLeft <= 5 * 20){
                        playersInTeamKitPvPWorld.playSound(playersInTeamKitPvPWorld.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1 + (5 - (float) teamGameTimeLeft / 20));
                    }
                }
            }
        }
        if(numberOfPlayersPrepared >=2 && teamGamePrepareStarted ==1 && teamGameTimeLeft == 0) {
            for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                    playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3游戏开始！祝你们好运 §b(*>▽<*)");
                    playersInTeamKitPvPWorld.sendTitle("§7꧁ꕥ §b游戏开始 §7ꕥ꧂","§e☘ 团队死战，祝君好运 ☘",20,3*20,20);
                    teamGamePrepareStarted = 0;
                    teamGameTimeLeft = 30 * 20;
                    teamGameReallyStarted = 1;
                    playersInTeamKitPvPWorld.removeScoreboardTag("InTeamedGame");
                    playersInTeamKitPvPWorld.performCommand("qinkit tpteamspawnwithoutinfo");
                    Function.executeCommand(playersInTeamKitPvPWorld,"console: tp @a[team=Red] @r[team=Red]");
                    Function.executeCommand(playersInTeamKitPvPWorld,"console: tp @a[team=Blue] @r[team=Blue]");
                    Function.executeCommand(playersInTeamKitPvPWorld,"console: tp @a[team=Yellow] @r[team=Yellow]");
                    Function.executeCommand(playersInTeamKitPvPWorld,"console: tp @a[team=Green] @r[team=Green]");
                    if(playersInTeamKitPvPWorld.getScoreboard().getEntryTeam(playersInTeamKitPvPWorld.getName()) == null){
                        playersInTeamKitPvPWorld.addScoreboardTag("InTeamedGamingSPC");
                    }
                    playersInTeamKitPvPWorld.setInvulnerable(false);
                    playersInTeamKitPvPWorld.setSaturation(0);
                    playersInTeamKitPvPWorld.setFoodLevel(20);
                    playersInTeamKitPvPWorld.removeScoreboardTag("QinKitLobby");
                    playersInTeamKitPvPWorld.playSound(playersInTeamKitPvPWorld, Sound.ENTITY_ENDER_DRAGON_AMBIENT,1,1);
                }
            }
        }
        /* 游 戏 已 经 开 始 的 事 件 */
        if(ifTeamGameEnded == 0 && teamGameReallyStarted == 1){
            teamGameModePlayerDeathReset();
            for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                    playersInTeamKitPvPWorld.performCommand("qinkit tpteamspawnwithoutinfo");
                    playersInTeamKitPvPWorld.addScoreboardTag("InTeamedGamingSPC");
                    playersInTeamKitPvPWorld.removeScoreboardTag("InTeamedGame");
                }
                if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGaming")){
                    if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() == 0 ){
                        teamGameReallyStarted = 0;
                        ifTeamGameEnded = 1;
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3恭喜 §a绿队 §3获得了胜利！ §b(*^▽^*)");
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3本局团队游戏将在 §b10 §3秒关闭");
                        playersInTeamKitPvPWorld.playSound(playersInTeamKitPvPWorld, Sound.ENTITY_ENDER_DRAGON_DEATH,1,1);
                    }else if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() == 0 ){
                        teamGameReallyStarted = 0;
                        ifTeamGameEnded = 1;
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3恭喜 §b蓝队 §3获得了胜利！ §b(*^▽^*)");
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3本局团队游戏将在 §b10 §3秒关闭");
                        playersInTeamKitPvPWorld.playSound(playersInTeamKitPvPWorld, Sound.ENTITY_ENDER_DRAGON_DEATH,1,1);
                    }else if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() == 0 ){
                        teamGameReallyStarted = 0;
                        ifTeamGameEnded = 1;
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3恭喜 §c红队 §3获得了胜利！ §b(*^▽^*)");
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3本局团队游戏将在 §b10 §3秒关闭");
                        playersInTeamKitPvPWorld.playSound(playersInTeamKitPvPWorld, Sound.ENTITY_ENDER_DRAGON_DEATH,1,1);
                    }else if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() == 0 ){
                        teamGameReallyStarted = 0;
                        ifTeamGameEnded = 1;
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3恭喜 §e黄队 §3获得了胜利！ §b(*^▽^*)");
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3本局团队游戏将在 §b10 §3秒关闭");
                        playersInTeamKitPvPWorld.playSound(playersInTeamKitPvPWorld, Sound.ENTITY_ENDER_DRAGON_DEATH,1,1);
                    }else if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() == 0
                            && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() == 0){
                        teamGameReallyStarted = 0;
                        ifTeamGameEnded = 1;
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3WTF? 你们是不是忘记选队伍了？ §b(*O^O*)");
                        playersInTeamKitPvPWorld.sendMessage("§7> > §b§lQinKitPVPS §7--> §3本局团队游戏将在 §b10 §3秒关闭");
                    }
                }
            }
        }
        if(ifTeamGameEnded == 1){
            teamGameOverTimeLeft -= 1;
            if(teamGameOverTimeLeft > 1){
                teamGameModePlayerDeathReset();
                if(teamGameOverTimeLeft % 20 == 0){
                    for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                        if(playersInTeamKitPvPWorld.getGameMode() == GameMode.ADVENTURE) {
                            Firework firework = playersInTeamKitPvPWorld.getWorld().spawn(playersInTeamKitPvPWorld.getLocation(), Firework.class);
                            firework.setMaxLife(40);
                        }
                    }
                }
            }else if(teamGameOverTimeLeft == 1){
                for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()) {
                    playersInTeamKitPvPWorld.setGameMode(GameMode.ADVENTURE);
                }
            }
            if(teamGameOverTimeLeft <= 0){
                ifTeamGameEnded = 0;
                teamGameOverTimeLeft = 10*20;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"tag @a remove InTeamedGamingSPC");
                for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
                    playersInTeamKitPvPWorld.setGameMode(GameMode.ADVENTURE);
                    Function.playerTpLobby(playersInTeamKitPvPWorld);
                    Function.clearPlayerTeam(playersInTeamKitPvPWorld);
                    Function.playerReset(playersInTeamKitPvPWorld);
                }
            }
        }
    }
    private void teamGameModePlayerDeathReset() {
        for (Player playersInEveryWorld : Bukkit.getOnlinePlayers()){
            // 检 测 从 战 场 中 死 回 来 的 玩 家
            if(playersInEveryWorld.getScoreboardTags().contains("InTeamedGaming") && playersInEveryWorld.getWorld().getName().equals("world")){
                /* 删 除 玩 家 所 有 t a g s */
                Function.playerReset(playersInEveryWorld);
                playersInEveryWorld.addScoreboardTag("InTeamedGamingSPC");
                playersInEveryWorld.performCommand("qinkit tpteamspawnwithoutinfo");
                // 检 测 带 有 观 察 者 标 签 的 玩 家
            }else if(playersInEveryWorld.getScoreboardTags().contains("InTeamedGamingSPC") && playersInEveryWorld.getWorld().getName().equals("teamkitpvp")){
                playersInEveryWorld.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    private boolean ifTwoTeamsHaveMembers(){
        for (Player playersInTeamKitPvPWorld : Objects.requireNonNull(Bukkit.getWorld("teamkitpvp")).getPlayers()){
            if(playersInTeamKitPvPWorld.getScoreboardTags().contains("InTeamedGame")){
                if(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red") == null
                        || playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue") == null
                        || playersInTeamKitPvPWorld.getScoreboard().getTeam("Green") == null
                        || playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow") == null){
                    return false;
                }
                if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() == 0
                        && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                        && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() == 0){
                    return false;
                }else if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() == 0
                        && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                        && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() == 0){
                    return false;
                }else if(Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() == 0
                        && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Blue")).getSize() == 0
                        && Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() == 0){
                    return false;
                }else return Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Red")).getSize() != 0
                        || Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Yellow")).getSize() != 0
                        || Objects.requireNonNull(playersInTeamKitPvPWorld.getScoreboard().getTeam("Green")).getSize() != 0;
            }else{
                return true;
            }
        }
        return false;
    }
}
