package cc.stormworth.hcf.misc.war.event;

import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import lombok.Getter;
import net.minecraft.util.com.google.common.base.Preconditions;

@Getter
public final class FactionWarMatchStartCountdownEvent extends FactionWarEvent {

    private final FactionWarMatch match;

    public FactionWarMatchStartCountdownEvent(FactionWar war, FactionWarMatch match) {
        super(war);

        this.match = Preconditions.checkNotNull(match, "Match can't be null.");
    }
}
