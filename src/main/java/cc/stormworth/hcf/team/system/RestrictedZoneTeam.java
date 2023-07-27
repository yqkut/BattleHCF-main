package cc.stormworth.hcf.team.system;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;

public class RestrictedZoneTeam extends Team {

    public RestrictedZoneTeam() {
        super("RestrictedZone");
        int dtrInt = (int) this.getDTR();
        dtrInt += DTRBitmask.RESTRICTED_ZONE.getBitmask();
        this.setUniqueId(new ObjectId());
        this.setName("RestrictedZone");
        this.setDTR(dtrInt);
        Main.getInstance().getTeamHandler().setupTeam(this);
    }
}