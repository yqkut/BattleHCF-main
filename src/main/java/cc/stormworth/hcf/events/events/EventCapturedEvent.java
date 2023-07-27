package cc.stormworth.hcf.events.events;

import cc.stormworth.hcf.events.Event;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class EventCapturedEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Event event;
    @Getter
    @Setter
    private boolean cancelled;

    public EventCapturedEvent(Event event, Player capper) {
        super(capper);

        this.event = event;
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

    public HandlerList getHandlers() {
        return (handlers);
    }

}