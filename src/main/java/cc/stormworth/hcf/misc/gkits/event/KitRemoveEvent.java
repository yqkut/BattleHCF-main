package cc.stormworth.hcf.misc.gkits.event;

import cc.stormworth.hcf.misc.gkits.Kit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitRemoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final Kit kit;
    private boolean cancelled;

    public KitRemoveEvent(final Kit kit) {
        this.cancelled = false;
        this.kit = kit;
    }

    public static HandlerList getHandlerList() {
        return KitRemoveEvent.handlers;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return KitRemoveEvent.handlers;
    }
}
