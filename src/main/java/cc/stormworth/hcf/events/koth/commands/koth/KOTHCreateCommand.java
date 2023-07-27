package cc.stormworth.hcf.events.koth.commands.koth;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.claims.VisualClaimType;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KOTHCreateCommand {

    @Command(names = {"KOTH Create"}, permission = "op")
    public static void kothCreate(final Player sender, @Param(name = "koth") final String koth) {
        if (Main.getInstance().getEventHandler().getEvent(koth) != null) {
            sender.sendMessage(CC.RED + "There is another koth called " + koth + ".");
            return;
        }
        new KOTH(koth, sender.getLocation());
        if (koth.startsWith("conquest-")) {
            Main.getInstance().getEventHandler().getEvent(koth).setHidden(true);
        }
        if (!koth.startsWith("conquest-")) {
            new KOTHTeam(sender.getLocation(), koth);
            new VisualClaim(sender, Main.getInstance().getTeamHandler().getTeam(koth), VisualClaimType.CREATE, true, false).draw(false);
        }
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + koth + ".");
    }

    public static class KOTHTeam extends Team {
        public KOTHTeam(final Location hq, final String name) {
            super(name);
            int dtrInt = (int) this.getDTR();
            if (name.equalsIgnoreCase("Citadel")) {
                dtrInt += DTRBitmask.CITADEL.getBitmask() + DTRBitmask.NO_ENDERPEARL.getBitmask();
            } else if (name.startsWith("conquest-")) {
                dtrInt += DTRBitmask.KOTH.getBitmask();
            } else if (name.equalsIgnoreCase("conquest")) {
                dtrInt += DTRBitmask.CONQUEST.getBitmask();
            } else {
                dtrInt += DTRBitmask.KOTH.getBitmask();
            }
            this.setUniqueId(new ObjectId());
            this.setName(name);
            this.setDTR(dtrInt);
            this.setHQ(hq);
            Main.getInstance().getTeamHandler().setupTeam(this);
        }
    }
}