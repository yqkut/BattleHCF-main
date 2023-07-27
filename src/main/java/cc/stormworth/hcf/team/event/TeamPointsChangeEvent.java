package cc.stormworth.hcf.team.event;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamPointsChangeEvent extends Event {
    private static HandlerList handlerList;

    static {
        TeamPointsChangeEvent.handlerList = new HandlerList();
    }

    private final Team team;

    public TeamPointsChangeEvent(final Team team) {
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return TeamPointsChangeEvent.handlerList;
    }

    public HandlerList getHandlers() {
        return TeamPointsChangeEvent.handlerList;
    }

    public void call() {
        Main.getInstance().getServer().getPluginManager().callEvent(this);
    }

    public Team getTeam() {
        return this.team;
    }
}