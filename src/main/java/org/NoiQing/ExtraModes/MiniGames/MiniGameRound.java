package org.NoiQing.ExtraModes.MiniGames;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public abstract class MiniGameRound {
    protected int gameDuration;         // 游戏持续时间
    protected int maxGameDuration;      // 最大允许游戏时间
    protected boolean isRunning;        // 游戏是否在进行
    protected int prepareDuration;      // 游戏准备持续时间
    protected int maxPrepareDuration;   // 游戏最长等待时间
    protected boolean passPrepare;      // 是否过了准备时间
    protected BossBar gameBar;          // 用于显示游戏上方计分板

    public MiniGameRound(int maxGameDuration) {
        this.maxGameDuration = maxGameDuration;
        this.isRunning = false;
        this.passPrepare = false;
        this.gameDuration = 0;
        this.prepareDuration = 0;
        gameBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_6);
    }
    public void startGame() {
        if (isRunning) return;
        isRunning = true;
        gameDuration = 0;
        prepareDuration = 0;
        passPrepare = false;
        onStartGame();
    }
    public void addPrepareDuration() {
        if(passPrepare) return;
        prepareDuration++;
        onAddWaitDuration();
    }
    public void playerReady() {
        onPlayerReady();
    }
    public void endGame() {
        if(!isRunning) return;
        isRunning = false;
        onEndGame();
    }
    public void addGameDuration() {
        gameDuration++;
        onAddGameDuration();
    }
    protected void onAddWaitDuration() {}

    protected boolean judgePrepareEnd() {
        return prepareDuration == maxPrepareDuration;
    }
    protected boolean judgeEnd() {
        return gameDuration == maxGameDuration;
    }

    // 获取时钟格式的时间
    public String getTimeClock() {
        int newTime = gameDuration;
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
    public boolean isRunning() {return isRunning;}
    protected abstract void onStartGame();
    protected abstract void onPlayerReady();
    protected abstract void onEndGame();
    protected abstract void onAddGameDuration();
}
