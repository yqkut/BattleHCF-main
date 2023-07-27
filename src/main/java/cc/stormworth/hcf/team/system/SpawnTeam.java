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

public class SpawnTeam extends Team {

  public SpawnTeam() {
    super("Spawn");
    int dtrInt = (int) this.getDTR();
    dtrInt += DTRBitmask.SAFE_ZONE.getBitmask();
    dtrInt += DTRBitmask.NO_ENDERPEARL.getBitmask();
    this.setUniqueId(new ObjectId());
    this.setName("Spawn");
    this.setDTR(dtrInt);
    this.setHQ(new Location(Bukkit.getWorlds().get(0), 0.0, 80.0, 0.0));

    if (!Main.getInstance().getMapHandler().isKitMap()) {
      final Claim claim = new Claim(new Location(Bukkit.getWorld("void"), -14, 0, 14),
          new Location(Bukkit.getWorld("void"), 14, 256, -14));
      claim.setName(this.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
      LandBoard.getInstance().setTeamAt(claim, this);
      this.getClaims().add(claim);
      this.flagForSave();
    }

    Main.getInstance().getTeamHandler().setupTeam(this);
  }
}