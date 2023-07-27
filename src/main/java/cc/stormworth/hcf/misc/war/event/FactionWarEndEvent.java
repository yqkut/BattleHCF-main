package cc.stormworth.hcf.misc.war.event;

import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWarParticipant;
import lombok.Getter;
import net.minecraft.util.com.google.common.base.Preconditions;

@Getter
public final class FactionWarEndEvent extends FactionWarEvent {

    private final FactionWarParticipant winner;

    public FactionWarEndEvent(FactionWar war, FactionWarParticipant winner) {
        super(war);

        this.winner = Preconditions.checkNotNull(winner, "Winner can't be null.");
    }
}
