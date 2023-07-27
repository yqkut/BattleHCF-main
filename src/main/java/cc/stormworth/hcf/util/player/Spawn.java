package cc.stormworth.hcf.util.player;

public class Spawn {
    private final int taskId;
    private final long spawnTime;

    public Spawn(final int taskId, final long spawnTime) {
        this.taskId = taskId;
        this.spawnTime = spawnTime;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public long getSpawnTime() {
        return this.spawnTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Spawn)) {
            return false;
        }
        final Spawn other = (Spawn) o;
        return other.canEqual(this) && this.getTaskId() == other.getTaskId() && this.getSpawnTime() == other.getSpawnTime();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Spawn;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTaskId();
        final long $spawnTime = this.getSpawnTime();
        result = result * 59 + (int) ($spawnTime >>> 32 ^ $spawnTime);
        return result;
    }

    @Override
    public String toString() {
        return "Spawn(taskId=" + this.getTaskId() + ", spawnTime=" + this.getSpawnTime() + ")";
    }
}