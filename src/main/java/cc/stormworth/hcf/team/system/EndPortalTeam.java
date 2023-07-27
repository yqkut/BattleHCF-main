package cc.stormworth.hcf.team.system;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class EndPortalTeam extends Team {

    public EndPortalTeam() {
        super("EndPortal");
        int dtrInt = (int) this.getDTR();
        dtrInt += DTRBitmask.END_PORTAL.getBitmask();
        this.setUniqueId(new ObjectId());
        this.setName("EndPortal");
        this.setDTR(dtrInt);
        this.addclaims(this);
        this.setHQ(new Location(Bukkit.getWorlds().get(0), 1000.0, 0.0, 1000.0).add(0.5, 0.0, 0.5));
        Main.getInstance().getTeamHandler().setupTeam(this);
    }

    public void addclaims(final Team team) {
        final Claim claim1 = new Claim(new Location(Bukkit.getWorlds().get(0), 1010.0, 0.0, 1010.0), new Location(Bukkit.getWorlds().get(0), 990.0, 0.0, 990.0));
        final Claim claim2 = new Claim(new Location(Bukkit.getWorlds().get(0), -1010.0, 0.0, 1010.0), new Location(Bukkit.getWorlds().get(0), -990.0, 0.0, 990.0));
        final Claim claim3 = new Claim(new Location(Bukkit.getWorlds().get(0), 1010.0, 0.0, -1010.0), new Location(Bukkit.getWorlds().get(0), 990.0, 0.0, -990.0));
        final Claim claim4 = new Claim(new Location(Bukkit.getWorlds().get(0), -1010.0, 0.0, -1010.0), new Location(Bukkit.getWorlds().get(0), -990.0, 0.0, -990.0));
        claim1.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        claim1.setY1(0);
        claim1.setY2(256);
        LandBoard.getInstance().setTeamAt(claim1, team);
        team.getClaims().add(claim1);
        claim2.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        claim2.setY1(0);
        claim2.setY2(256);
        LandBoard.getInstance().setTeamAt(claim2, team);
        team.getClaims().add(claim2);
        claim3.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        claim3.setY1(0);
        claim3.setY2(256);
        LandBoard.getInstance().setTeamAt(claim3, team);
        team.getClaims().add(claim3);
        claim4.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        claim4.setY1(0);
        claim4.setY2(256);
        LandBoard.getInstance().setTeamAt(claim4, team);
        team.getClaims().add(claim4);
        team.flagForSave();
    }
}