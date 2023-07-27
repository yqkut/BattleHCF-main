package cc.stormworth.hcf.util.player;

public class Logout {
    private final int taskId;
    private final long logoutTime;

    public Logout(final int taskId, final long logoutTime) {
        this.taskId = taskId;
        this.logoutTime = logoutTime;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public long getLogoutTime() {
        return this.logoutTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Logout)) {
            return false;
        }
        final Logout other = (Logout) o;
        return other.canEqual(this) && this.getTaskId() == other.getTaskId() && this.getLogoutTime() == other.getLogoutTime();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Logout;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTaskId();
        final long $logoutTime = this.getLogoutTime();
        result = result * 59 + (int) ($logoutTime >>> 32 ^ $logoutTime);
        return result;
    }

    @Override
    public String toString() {
        return "Logout(taskId=" + this.getTaskId() + ", logoutTime=" + this.getLogoutTime() + ")";
    }
}
