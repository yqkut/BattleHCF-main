package cc.stormworth.hcf.team.duel.event;

import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatchTeam;
import com.google.common.base.Preconditions;
import lombok.Getter;

@Getter
public final class FactionDuelMatchEndEvent extends FactionDuelMatchEvent {

	private final FactionDuelMatchTeam winnerTeam;
	
	public FactionDuelMatchEndEvent(FactionDuelMatch match, FactionDuelMatchTeam winnerTeam) {
		super(match);
		
		this.winnerTeam = Preconditions.checkNotNull(winnerTeam, "Winner team can't be null.");
	}
}
