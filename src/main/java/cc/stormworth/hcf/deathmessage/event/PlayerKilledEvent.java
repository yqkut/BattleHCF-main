package cc.stormworth.hcf.deathmessage.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKilledEvent extends Event {
    private static HandlerList handlerList;

    static {
        PlayerKilledEvent.handlerList = new HandlerList();
    }

    private final Player killer;
    private final Player victim;

    public PlayerKilledEvent(final Player killer, final Player victim) {
        this.killer = killer;
        this.victim = victim;
    }

    public static HandlerList getHandlerList() {
        return PlayerKilledEvent.handlerList;
    }

    public HandlerList getHandlers() {
        return PlayerKilledEvent.handlerList;
    }

    public Player getKiller() {
        return this.killer;
    }

    public Player getVictim() {
        return this.victim;
    }
}