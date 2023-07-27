package cc.stormworth.hcf.team.system;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConquestTeam extends Team {
    public ConquestTeam() {
        super("Conquest");
        int dtrInt = (int) this.getDTR();
        dtrInt += DTRBitmask.CONQUEST.getBitmask();
        this.setUniqueId(new ObjectId());
        this.setName("Conquest");
        this.setDTR(dtrInt);
        this.setHQ(new Location(Bukkit.getWorld("world"), -500, 80.0, -500));
        Main.getInstance().getTeamHandler().setupTeam(this);
    }
}