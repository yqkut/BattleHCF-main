package cc.stormworth.hcf.misc.war.event;

import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWarParticipant;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.common.base.Preconditions;

@Getter
public final class FactionWarMatchEndEvent extends FactionWarEvent {

    private final FactionWarMatch match;
    @Setter
    private FactionWarParticipant winnerTeam;

    public FactionWarMatchEndEvent(FactionWar war, FactionWarMatch match, FactionWarParticipant winnerTeam) {
        super(war);

        this.match = Preconditions.checkNotNull(match, "Match can't be null.");
        this.winnerTeam = Preconditions.checkNotNull(winnerTeam, "Winner team can't be null.");
    }
}
