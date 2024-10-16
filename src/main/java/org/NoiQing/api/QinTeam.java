package org.NoiQing.api;

import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QinTeam {
    private final String teamName;
    private final Set<Entity> teamEntities;
    private final Color teamColor;
    public QinTeam(String teamName, Color color) {
        this.teamName = teamName;
        this.teamColor = color;
        teamEntities = new HashSet<>();
    }

    public void addTeamEntities(Entity... p) {
        teamEntities.addAll(Arrays.asList(p));
    }
    public void removeTeamEntities(Entity... p) {
        Arrays.asList(p).forEach(teamEntities::remove);
    }
    public void clearTeamEntities() {
        teamEntities.clear();
    }
    public String getTeamName() {
        return teamName;
    }
    public Set<Entity> getTeamEntities() {
        return teamEntities;
    }
    public Set<Player> getTeamPlayers() {
        return teamEntities.stream().filter(p -> p instanceof Player).map(p -> (Player) p).collect(Collectors.toSet());
    }
    public Color getTeamColor() {return teamColor;}
}
