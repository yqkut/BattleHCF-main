package cc.stormworth.hcf.events.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class EventDeactivatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private cc.stormworth.hcf.events.Event event;

    public static HandlerList getHandlerList() {
        return (handlers);
    }

    public HandlerList getHandlers() {
        return (handlers);
    }

}