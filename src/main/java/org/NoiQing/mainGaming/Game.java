package org.NoiQing.mainGaming;

import org.NoiQing.QinKitPVPS;

public class Game {
    private final QinKits QinKits;
    private final QinSkills QinSkills;
    private final QinMaps QinMaps;
    private final QinMenus QinMenus;
    private final QinBossBars QinBossBars;
    private final QinTeams QinTeams;
    public Game(QinKitPVPS plugin){
        this.QinKits = new QinKits(plugin);
        this.QinSkills = new QinSkills(plugin,this);
        this.QinMaps = new QinMaps(plugin);
        this.QinMenus = new QinMenus(plugin);
        this.QinBossBars = new QinBossBars(plugin);
        this.QinTeams = new QinTeams();
    }

    public QinKits getKits() { return QinKits; }
    public QinSkills getSkills() { return QinSkills; };
    public QinMaps getMaps() {return QinMaps;}
    public QinMenus getQinMenus() {return QinMenus;}
    public QinBossBars getQinBossBars() {return QinBossBars;}
    public QinTeams getQinTeams() {return QinTeams;}
}
