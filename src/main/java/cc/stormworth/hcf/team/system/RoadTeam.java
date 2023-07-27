package cc.stormworth.hcf.team.system;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;

public class RoadTeam {
    public static class NorthRoadTeam extends Team {
        public NorthRoadTeam() {
            super("NorthRoad");
            int dtrInt = (int) this.getDTR();
            dtrInt += DTRBitmask.ROAD.getBitmask();
            this.setUniqueId(new ObjectId());
            this.setName("NorthRoad");
            this.setDTR(dtrInt);
            Main.getInstance().getTeamHandler().setupTeam(this);
        }
    }

    public static class SouthRoadTeam extends Team {
        public SouthRoadTeam() {
            super("SouthRoad");
            int dtrInt = (int) this.getDTR();
            dtrInt += DTRBitmask.ROAD.getBitmask();
            this.setUniqueId(new ObjectId());
            this.setName("SouthRoad");
            this.setDTR(dtrInt);
            Main.getInstance().getTeamHandler().setupTeam(this);
        }
    }

    public static class EastRoadTeam extends Team {
        public EastRoadTeam() {
            super("EastRoad");
            int dtrInt = (int) this.getDTR();
            dtrInt += DTRBitmask.ROAD.getBitmask();
            this.setUniqueId(new ObjectId());
            this.setName("EastRoad");
            this.setDTR(dtrInt);
            Main.getInstance().getTeamHandler().setupTeam(this);
        }
    }

    public static class WestRoadTeam extends Team {
        public WestRoadTeam() {
            super("WestRoad");
            int dtrInt = (int) this.getDTR();
            dtrInt += DTRBitmask.ROAD.getBitmask();
            this.setUniqueId(new ObjectId());
            this.setName("WestRoad");
            this.setDTR(dtrInt);
            Main.getInstance().getTeamHandler().setupTeam(this);
        }
    }
}