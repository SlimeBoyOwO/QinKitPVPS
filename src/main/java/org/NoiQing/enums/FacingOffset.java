package org.NoiQing.enums;

public enum FacingOffset {
    NORTH(0, 0, -0.8, 1.5f),
    SOUTH(0, 0, 0.8, 0.75f),
    WEST(-0.8, 0, 0, 1.5f),
    EAST(0.8, 0, 0, 0.75f);


    public final double xOffset;
    public final double yOffset;
    public final double zOffset;

    public final float distance;

    FacingOffset(double xOffset, double yOffset, double zOffset, float distance) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.distance = distance;
    }
}
