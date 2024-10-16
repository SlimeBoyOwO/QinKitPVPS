package org.NoiQing.api;

import org.bukkit.boss.BossBar;

public class QinBossBar {
    private final String scoreboard;        //记录该BossBar关系的计分板
    private final boolean ifASC;            //记录是否为正序计数
    private final int max;
    private final BossBar bossBar;
    private final String tag;
    public QinBossBar(BossBar bar, String scoreboard, boolean ifASC, int max, String tag){
        this.bossBar = bar;
        this.scoreboard = scoreboard;
        this.ifASC = ifASC;
        this.max = max;
        this.tag = tag;
    }

    public BossBar getBossBar() {return bossBar;}
    public String getScoreboard() {return scoreboard;}
    public boolean getASC() {return ifASC;}
    public int getMax() {return max;}
    public String getTag() {return tag;}
}
