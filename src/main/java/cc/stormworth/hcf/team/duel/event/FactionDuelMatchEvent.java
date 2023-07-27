package cc.stormworth.hcf.team.duel.event;

import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class FactionDuelMatchEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final FactionDuelMatch match;
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
