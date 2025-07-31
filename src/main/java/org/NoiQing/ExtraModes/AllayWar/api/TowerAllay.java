package org.NoiQing.ExtraModes.AllayWar.api;

import org.bukkit.entity.Allay;

public class TowerAllay {
    private Allay allay;
    private int attackCD;

    public TowerAllay(Allay allay) {
        this.allay = allay;
        this.attackCD = 20;         // 默认攻击CD为1s
    }

    public Allay getAllay() {return allay;}
    public int getAttackCD() {return attackCD;}
}
