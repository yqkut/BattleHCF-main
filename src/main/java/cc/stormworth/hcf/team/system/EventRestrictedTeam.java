package cc.stormworth.hcf.team.system;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;

public class EventRestrictedTeam extends Team {

    public EventRestrictedTeam() {
        super("RestrictedEvent");
        int dtrInt = (int) this.getDTR();
        dtrInt += DTRBitmask.RESTRICTED_EVENT.getBitmask();
        this.setUniqueId(new ObjectId());
        this.setName("RestrictedEvent");
        this.setDTR(dtrInt);
        Main.getInstance().getTeamHandler().setupTeam(this);
    }
}