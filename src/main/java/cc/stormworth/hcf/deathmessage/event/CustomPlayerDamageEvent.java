package cc.stormworth.hcf.deathmessage.event;

import cc.stormworth.hcf.deathmessage.objects.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class CustomPlayerDamageEvent extends Event {
    private static HandlerList handlerList;

    static {
        CustomPlayerDamageEvent.handlerList = new HandlerList();
    }

    private final EntityDamageEvent cause;
    private Damage trackerDamage;

    public CustomPlayerDamageEvent(final EntityDamageEvent cause, final Damage trackerDamage) {
        this.cause = cause;
        this.trackerDamage = trackerDamage;
    }

    public static HandlerList getHandlerList() {
        return CustomPlayerDamageEvent.handlerList;
    }

    public Player getPlayer() {
        return (Player) this.cause.getEntity();
    }

    public double getDamage() {
        return this.cause.getDamage();
    }

    public HandlerList getHandlers() {
        return CustomPlayerDamageEvent.handlerList;
    }

    public EntityDamageEvent getCause() {
        return this.cause;
    }

    public Damage getTrackerDamage() {
        return this.trackerDamage;
    }

    public void setTrackerDamage(final Damage trackerDamage) {
        this.trackerDamage = trackerDamage;
    }
}