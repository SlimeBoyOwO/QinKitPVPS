package org.NoiQing.ExtraModes.PvzGame.PVZUtils;

import org.NoiQing.ExtraModes.MiniGames.MiniGameUtils.MiniFunction;
import org.NoiQing.mainGaming.QinTeams;
import org.bukkit.Location;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entity2DTree {

    private static class Node {
        Entity entity;
        Node left;
        Node right;

        Node(Entity entity) {
            this.entity = entity;
            this.left = null;
            this.right = null;
        }
    }
    public enum FilterMode {
        ARMY,       // 适用于过滤与军队相关的实体
        TOWER,      // 适用于过滤与塔相关的实体
        FRIENDLY_TOWER, // 适用于过滤友军塔相关的实体
        NONE        // 不进行过滤
    }
    private Node root;
    public Entity2DTree() {
        root = null;
    }
    // 将Entity列表中的元素添加到树中
    public void addEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            root = addEntity(root, entity, true);
        }
    }
    private Node addEntity(Node node, Entity entity, boolean xAxis) {
        if (node == null) {
            return new Node(entity);
        }

        if (xAxis) {
            if (entity.getLocation().getX() < node.entity.getLocation().getX()) {
                node.left = addEntity(node.left, entity, false);
            } else {
                node.right = addEntity(node.right, entity, false);
            }
        } else {
            if (entity.getLocation().getZ() < node.entity.getLocation().getZ()) {
                node.left = addEntity(node.left, entity, true);
            } else {
                node.right = addEntity(node.right, entity, true);
            }
        }

        return node;
    }
    // 清空树
    public void clear() {
        root = null;
    }
    // 查找树中与给定Entity最近的Entity
    public Entity findNearest(Entity entity) {
        Node target = findNearest(root, entity, null, true);
        if(target == null){
            return null;
        }
        return target.entity;
    }
    public Set<Entity> findEntitiesWithinRadius(Entity queryEntity, double radius, FilterMode mode) {
        Set<Entity> entities = findEntitiesWithinRadius(queryEntity,radius);
        entities.removeIf(entity -> shouldFilterEntity(entity, queryEntity, mode));
        return entities;
    }
    // 查找半径内的所有实体
    public Set<Entity> findEntitiesWithinRadius(Entity queryEntity, double radius) {
        Set<Entity> entitiesInRange = new HashSet<>();
        findEntitiesWithinRadius(root, queryEntity, radius, entitiesInRange, true);
        return entitiesInRange;
    }

    private void findEntitiesWithinRadius(Node node, Entity queryEntity, double radius, Set<Entity> entitiesInRange, boolean xAxis) {
        if (node == null) {
            return;
        }

        Location center = queryEntity.getLocation();

        // 计算当前节点到查询点的距离
        double distance = distanceSquared(queryEntity, node.entity);

        // 如果当前实体在范围内，加入结果集
        if (distance <= radius * radius) {
            entitiesInRange.add(node.entity);
        }

        // 判断是否需要继续搜索下一个分支
        Node nextBranch = null;
        Node oppositeBranch = null;

        if (xAxis) {
            if (center.getX() < node.entity.getLocation().getX()) {
                nextBranch = node.left;
                oppositeBranch = node.right;
            } else {
                nextBranch = node.right;
                oppositeBranch = node.left;
            }
        } else {
            if (center.getZ() < node.entity.getLocation().getZ()) {
                nextBranch = node.left;
                oppositeBranch = node.right;
            } else {
                nextBranch = node.right;
                oppositeBranch = node.left;
            }
        }

        // 递归查找下一个分支
        findEntitiesWithinRadius(nextBranch, queryEntity, radius, entitiesInRange, !xAxis);

        // 剪枝：如果查询点到当前分割平面的距离小于半径，说明可能需要检查另一分支
        if (Math.abs(xAxis ? center.getX() - node.entity.getLocation().getX() : center.getZ() - node.entity.getLocation().getZ()) <= radius) {
            findEntitiesWithinRadius(oppositeBranch, queryEntity, radius, entitiesInRange, !xAxis);
        }
    }
    public Entity findNearest(Entity entity, FilterMode mode) {
        Node target = findNearest(root, entity, null, true);
        if(target == null){
            return null;
        }
        return target.entity;
    }
    private Node findNearest(Node node, Entity entity, Node nearest, boolean xAxis) {
        if (node == null) {
            return nearest;
        }

        if (nearest == null || distanceSquared(entity, node.entity) < distanceSquared(entity, nearest.entity)) {
            nearest = node;
        }

        Node nextBranch = null;
        Node oppositeBranch = null;

        if (xAxis) {
            if (entity.getLocation().getX() < node.entity.getLocation().getX()) {
                nextBranch = node.left;
                oppositeBranch = node.right;
            } else {
                nextBranch = node.right;
                oppositeBranch = node.left;
            }
        } else {
            if (entity.getLocation().getZ() < node.entity.getLocation().getZ()) {
                nextBranch = node.left;
                oppositeBranch = node.right;
            } else {
                nextBranch = node.right;
                oppositeBranch = node.left;
            }
        }

        nearest = findNearest(nextBranch, entity, nearest, !xAxis);

        // Check if we need to check the opposite branch
        if (Math.abs(xAxis ? Math.pow(entity.getLocation().getX() - node.entity.getLocation().getX(),2) : Math.pow(entity.getLocation().getZ() - node.entity.getLocation().getZ(),2)) < distanceSquared(entity, nearest.entity)) {
            nearest = findNearest(oppositeBranch, entity, nearest, !xAxis);
        }

        return nearest;
    }
    // 查找直线附近的所有僵尸
    public Set<Entity> findZombiesNearLine(Location from, Location to) {
        Set<Entity> zombiesNearLine = new HashSet<>();
        findZombiesNearLine(root, from, to, zombiesNearLine, true);
        return zombiesNearLine;
    }
    private void findZombiesNearLine(Node node, Location from, Location to, Set<Entity> zombiesNearLine, boolean xAxis) {
        if (node == null) {
            return;
        }

        // 检查当前节点的实体是否为僵尸，且是否在直线范围内
        if (node.entity != null) {
            double distanceToLine = distanceToLine(from, to, node.entity.getLocation());
            if (distanceToLine <= 3.0) {
                zombiesNearLine.add(node.entity);
            }
        }

        // 计算分割平面的距离，用于判断是否需要检查另一子树
        double planeDistance = xAxis
                ? Math.abs(from.getX() - node.entity.getLocation().getX())
                : Math.abs(from.getZ() - node.entity.getLocation().getZ());

        // 确定下一个遍历的子树
        Node nextBranch = (xAxis
                ? (from.getX() < node.entity.getLocation().getX() ? node.left : node.right)
                : (from.getZ() < node.entity.getLocation().getZ() ? node.left : node.right));
        Node oppositeBranch = (nextBranch == node.left ? node.right : node.left);

        // 递归查找下一个分支
        findZombiesNearLine(nextBranch, from, to, zombiesNearLine, !xAxis);

        // 如果平面距离小于3米，也需要检查另一分支
        if (planeDistance <= 3.0) {
            findZombiesNearLine(oppositeBranch, from, to, zombiesNearLine, !xAxis);
        }
    }
    // 计算点到直线的垂直距离
    private double distanceToLine(Location from, Location to, Location point) {
        Vector lineVec = to.toVector().subtract(from.toVector());
        Vector pointVec = point.toVector().subtract(from.toVector());
        double lineLength = lineVec.length();

        // 点到直线的距离 = |(pointVec × lineVec) / lineLength|
        return pointVec.crossProduct(lineVec).length() / lineLength;
    }
    private double distance(Entity e1, Entity e2) {
        double dx = e1.getLocation().getX() - e2.getLocation().getX();
        double dz = e1.getLocation().getZ() - e2.getLocation().getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }
    private double distanceSquared(Entity e1, Entity e2) {
        double dx = e1.getLocation().getX() - e2.getLocation().getX();
        double dz = e1.getLocation().getZ() - e2.getLocation().getZ();
        return dx * dx + dz * dz;
    }
    // 根据模式选择相应的过滤规则
    private boolean shouldFilterEntity(Entity nodeEntity, Entity queryEntity, FilterMode filterMode) {
        return switch (filterMode) {
            case ARMY ->
                // 过滤与军队相关的实体
                    MiniFunction.filterArmy((Mob) queryEntity, QinTeams.getEntityTeam(queryEntity), nodeEntity, QinTeams.getEntityTeam(nodeEntity));
            case TOWER ->
                // 过滤与塔相关的实体
                    MiniFunction.filterTower((Allay) queryEntity, QinTeams.getEntityTeam(queryEntity), nodeEntity);
            case FRIENDLY_TOWER ->
                // 过滤友军塔相关的实体
                    MiniFunction.filterFriendlyTower(QinTeams.getEntityTeam(queryEntity), nodeEntity);
            case NONE -> false;// 默认不过滤
        };
    }
}
