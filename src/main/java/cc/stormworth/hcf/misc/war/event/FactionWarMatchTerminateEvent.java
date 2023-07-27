package cc.stormworth.hcf.misc.war.event;

import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWarParticipant;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import lombok.Getter;
import net.minecraft.util.com.google.common.base.Preconditions;

@Getter
public final class FactionWarMatchTerminateEvent extends FactionWarEvent {

    private final FactionWarMatch match;
    private final FactionWarParticipant winnerTeam;

    public FactionWarMatchTerminateEvent(FactionWar war, FactionWarMatch match, FactionWarParticipant winnerTeam) {
        super(war);

        this.match = Preconditions.checkNotNull(match, "Match can't be null.");
        this.winnerTeam = Preconditions.checkNotNull(winnerTeam, "Winner team can't be null.");
    }
}
