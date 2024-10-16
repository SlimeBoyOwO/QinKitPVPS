package org.NoiQing.mainGaming;

import org.NoiQing.api.QinTeam;
import org.bukkit.Color;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

public class QinTeams {
    private static Set<QinTeam> teams;
    public QinTeams() {
        teams = new HashSet<>();
        initQinTeams();
    }
    public void createQinTeam(String teamName, Color color) {
        QinTeam newTeam = new QinTeam(teamName, color);
        teams.add(newTeam);
    }

    public void initQinTeams() {
        createQinTeam("悦灵红",Color.RED);
        createQinTeam("悦灵蓝",Color.BLUE);
        createQinTeam("悦灵黄",Color.YELLOW);
        createQinTeam("悦灵绿",Color.GREEN);
        createQinTeam("默认白",Color.WHITE);

        createQinTeam("植物",Color.LIME);
        createQinTeam("僵尸",Color.RED);

        createQinTeam("团战红",Color.RED);
        createQinTeam("团战蓝",Color.BLUE);
        createQinTeam("团战黄",Color.YELLOW);
        createQinTeam("团战绿",Color.GREEN);
    }
    public static QinTeam getQinTeamByName(String teamName) {
        for(QinTeam team : teams) if(team.getTeamName().equals(teamName)) return team;
        return null;
    }
    public static QinTeam getEntityTeam(Entity e) {
        for(QinTeam team : teams) if(team.getTeamEntities().contains(e)) return team;
        return null;
    }

    public static void leaveTeam(Entity e) {
        for(QinTeam team : teams) if(team.getTeamEntities().contains(e)) team.removeTeamEntities(e);
    }

    public static Set<QinTeam> getTeams() {
        return teams;
    }
}
