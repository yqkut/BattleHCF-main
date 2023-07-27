package cc.stormworth.hcf.team.duel.event;

import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;

public final class FactionDuelMatchStartEvent extends FactionDuelMatchEvent {

	public FactionDuelMatchStartEvent(FactionDuelMatch match) {
		super(match);
	}
}
