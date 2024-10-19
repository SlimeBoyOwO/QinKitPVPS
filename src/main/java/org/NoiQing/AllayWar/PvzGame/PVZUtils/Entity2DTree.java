package org.NoiQing.AllayWar.PvzGame.PVZUtils;

import org.bukkit.entity.Entity;

import java.util.List;

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
                node.left = addEntity(node.left, entity, !xAxis);
            } else {
                node.right = addEntity(node.right, entity, !xAxis);
            }
        } else {
            if (entity.getLocation().getZ() < node.entity.getLocation().getZ()) {
                node.left = addEntity(node.left, entity, !xAxis);
            } else {
                node.right = addEntity(node.right, entity, !xAxis);
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
}
