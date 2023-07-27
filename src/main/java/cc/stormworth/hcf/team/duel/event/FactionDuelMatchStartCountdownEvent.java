package cc.stormworth.hcf.team.duel.event;

import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;

public final class FactionDuelMatchStartCountdownEvent extends FactionDuelMatchEvent {

	public FactionDuelMatchStartCountdownEvent(FactionDuelMatch match) {
		super(match);
	}
}
