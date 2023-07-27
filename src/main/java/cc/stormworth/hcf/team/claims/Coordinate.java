package cc.stormworth.hcf.team.claims;

public class Coordinate {
    int x;
    int z;

    public Coordinate(final int x, final int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return this.x + ", " + this.z;
    }

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }
}