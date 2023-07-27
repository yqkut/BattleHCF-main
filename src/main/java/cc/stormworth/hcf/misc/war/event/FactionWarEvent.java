package cc.stormworth.hcf.misc.war.event;

import cc.stormworth.hcf.misc.war.FactionWar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class FactionWarEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final FactionWar war;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
