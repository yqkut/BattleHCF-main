package cc.stormworth.hcf.team.system;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.nether.NetherArea;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class NetherTeam {
    public NetherTeam() {
        Team nether = new Team("Nether");
        int dtrIntA = (int) nether.getDTR();
        dtrIntA += DTRBitmask.NETHER.getBitmask();
        nether.setUniqueId(new ObjectId());
        nether.setName("Nether");
        nether.setDTR(dtrIntA);
        nether.setHQ(Main.getInstance().getServerHandler().getSpawnLocation());
        Main.getInstance().getTeamHandler().setupTeam(nether);

        Team netherZone = new Team("NetherZone");
        int dtrIntB = (int) netherZone.getDTR();
        dtrIntB += DTRBitmask.NETHER_ZONE.getBitmask();
        netherZone.setUniqueId(new ObjectId());
        netherZone.setName("NetherZone");
        netherZone.setDTR(dtrIntB);
        netherZone.setHQ(Main.getInstance().getServerHandler().getSpawnLocation());
        Main.getInstance().getTeamHandler().setupTeam(netherZone);

        Claim claim = new Claim(new Location(Bukkit.getWorld("void"), -522, 0, -479), new Location(Bukkit.getWorld("void"), -478, 0, -523));
        claim.setName(nether.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        claim.setY1(0);
        claim.setY2(256);

        LandBoard.getInstance().setTeamAt(claim, nether);
        nether.getClaims().add(claim);
        nether.flagForSave();

        claimNether(claim);

        // scan
        if (!Main.getInstance().getNetherHandler().hasArea()) {
            Main.getInstance().getNetherHandler().setArea(new NetherArea());
        }
        Main.getInstance().getNetherHandler().getArea().scan();
        Main.getInstance().getNetherHandler().save();
    }

    private void claimNether(Claim claim) {
        int buffer = 200;
        Location corner1 = claim.getCornerLocations()[0];
        Location corner2 = claim.getCornerLocations()[1];
        Location corner3 = claim.getCornerLocations()[2];
        Location corner4 = claim.getCornerLocations()[3];

        this.claimBuffer(corner1.clone().add(-buffer, 0, -1), corner4.clone().add(buffer, 0, -buffer));
        this.claimBuffer(corner4.clone().add(buffer, 0, -buffer), corner2.clone().add(1, 0, buffer));
        this.claimBuffer(corner2.clone().add(1, 0, buffer), corner3.clone().add(-buffer, 0, 1));
        this.claimBuffer(corner3.clone().add(-buffer, 0, 1), corner1.clone().add(-1, 0, -buffer));
    }

    public void claimBuffer(Location corner1, Location corner2) {
        Team claimTeam = Main.getInstance().getTeamHandler().getTeam("NetherZone");
        Claim bufferclaim = new Claim(corner1, corner2);
        bufferclaim.setName(claimTeam.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        bufferclaim.setY1(0);
        bufferclaim.setY2(256);

        LandBoard.getInstance().setTeamAt(bufferclaim, claimTeam);
        claimTeam.getClaims().add(bufferclaim);
        claimTeam.flagForSave();
    }
}