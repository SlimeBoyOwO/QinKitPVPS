package org.NoiQing.ExtraModes.MiniGames.MiniGameUtils;

import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWFunction;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.GameMode;
import org.bukkit.entity.*;

import java.util.Objects;

public class MiniFunction {

    public static boolean filterArmy(Mob mob, QinTeam mobTeam, Entity entity, QinTeam entityTeam) {
        //无视生物移动标记
        if(entity.getScoreboardTags().contains("move_tag")) return true;
        //无视无敌的玩家
        if (entity instanceof Player p)
            if(p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE
                    || p.isInvulnerable() || Objects.equals(entityTeam, mobTeam)) return true;

        //如果是一些特殊的实体，无视
        if (MiniFunction.ignoreSomeEntities(mobTeam, entity, entityTeam, entity.equals(mob), entity.isDead()))
            return true;

        if(!(entity instanceof LivingEntity)) return true;
        if(entity instanceof Villager) return true;
        return false;
    }
    public static boolean filterTower(Allay allay, QinTeam allayTeam, Entity entity) {
        QinTeam entityTeam = QinTeams.getEntityTeam(entity);
        if(entity.getScoreboardTags().contains("move_tag")) return true;
        if (entity instanceof Player p) {
            if (allayTeam == null) return true;
            if ((p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE
                    || p.isInvulnerable() || allayTeam.equals(entityTeam))) return true;
        }
        if (entity.getType() == EntityType.VILLAGER || !(entity instanceof LivingEntity)) return true;
        if(entity.getScoreboardTags().contains("move_tag")) return true;
        if (MiniFunction.ignoreSomeEntities(allayTeam, entity, entityTeam, entity.equals(allay), entity.isDead()))
            return true;
        return false;
    }
    public static boolean filterFriendlyTower(QinTeam mobTeam, Entity entity) {
        QinTeam entityTeam = QinTeams.getEntityTeam(entity);
        if(!AWFunction.isAllayTower(entity)) return true;
        if(mobTeam.equals(entityTeam)) return true;
        String towerName = Function.getNameWithoutColor(entity.getCustomName()).replaceAll("[^\\p{IsHan}]", "");
        if(towerName.endsWith("塔") || towerName.endsWith("炮") || towerName.equals("生命核心")) return true;
        return false;
    }

    public static boolean ignoreSomeEntities(QinTeam allayTeam, Entity entity, QinTeam entityTeam, boolean equals, boolean dead) {
        //是自己的话，排除
        if(equals) return true;
        //是中立油井的话，排除
        if(entityTeam == null && entity.getCustomName() != null && entity.getCustomName().contains("油井")) return true;
        //是盔甲架排除
        if(entity instanceof ArmorStand) return true;
        //如果两个队伍相同，排除
        if(allayTeam != null && Objects.equals(entityTeam, allayTeam))
            return true;
        //死了的话，排除
        if(dead) return true;

        return false;
    }
}
