package cc.stormworth.hcf.team.system;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class OreMountainTeam extends Team {
    public OreMountainTeam() {
        super("OreMountain");
        int dtrInt = (int) this.getDTR();
        dtrInt += DTRBitmask.ORE_MOUNTAIN.getBitmask();
        this.setUniqueId(new ObjectId());
        this.setName("OreMountain");
        this.setDTR(dtrInt);
        this.setHQ(new Location(Bukkit.getWorld("world_nether"), 100, 100, 550));
        Main.getInstance().getTeamHandler().setupTeam(this);
    }
}