package cc.stormworth.hcf.events.dtc.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.dtc.DTC;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.claims.VisualClaimType;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DTCCreateCommand {

    @Command(names = {"DTC Create"}, permission = "op")
    public static void Create(Player sender, @Param(name = "dtc") String dtc) {
        if (Main.getInstance().getTeamHandler().getTeam(dtc) != null) {
            sender.sendMessage(CC.RED + "DTC called  " + dtc + " already exist.");
            return;
        }
        new DTC(dtc, sender.getLocation());
        new DTCTeam(sender.getLocation(), dtc);
        new VisualClaim(sender, Main.getInstance().getTeamHandler().getTeam(dtc), VisualClaimType.CREATE, true, false).draw(false);
        sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + dtc + ".");
    }

    @Command(names = {"DTC delete"}, permission = "op")
    public static void delete(Player sender, @Param(name = "dtc") String dtc) {
        Event DTC = Main.getInstance().getEventHandler().getEvent(dtc);
        if (DTC == null) {
            sender.sendMessage(CC.RED + "Invalid DTC.");
            return;
        }
        Main.getInstance().getEventHandler().getEvents().remove(DTC);
        Main.getInstance().getEventHandler().saveEvents();
        if (!DTC.getName().startsWith("conquest-") && Main.getInstance().getTeamHandler().getTeam(DTC.getName()) != null) {
            Main.getInstance().getTeamHandler().getTeam(DTC.getName()).disband();
        }
        sender.sendMessage(ChatColor.GRAY + "Deleted a DTC named " + dtc + ".");
    }

    @Command(names = {"DTC setpoints"}, permission = "op")
    public static void setpoints(Player sender, @Param(name = "dtc") String dtc, @Param(name = "points") int points) {
        if (Main.getInstance().getTeamHandler().getTeam(dtc) == null) {
            sender.sendMessage(CC.RED + "DTC called  " + dtc + " doesn't exist.");
            return;
        }
        DTC event = (DTC) Main.getInstance().getEventHandler().getEvent(dtc);
        DTC.startingPoints = points;
        sender.sendMessage(ChatColor.GRAY + "Set points for DTC called " + dtc + " to " + points + ".");
    }

    public static class DTCTeam extends Team {
        public DTCTeam(final Location hq, final String name) {
            super(name);
            int dtrInt = (int) this.getDTR();
            dtrInt += DTRBitmask.DTC.getBitmask();
            this.setUniqueId(new ObjectId());
            this.setName(name);
            this.setDTR(dtrInt);
            this.setHQ(hq);
            Main.getInstance().getTeamHandler().setupTeam(this);
        }
    }
}