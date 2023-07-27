package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.claims.VisualClaimType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamOpClaimCommand {

    @Command(names = {"team opclaim", "t opclaim", "f opclaim", "faction opclaim", "fac opclaim"}, permission = "op")
    public static void opclaim(final Player sender) {
        Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        new VisualClaim(sender, team, VisualClaimType.CREATE, true, false).draw(false);
    }

    @Command(names = {"team claimfor", "t claimfor", "f claimfor", "faction claimfor", "fac claimfor"}, permission = "op")
    public static void claimfor(final Player player, @Param(name = "team") final Team team) {
        if (team == null) {
            player.sendMessage(ChatColor.RED + "This team doesn't exists!");
            return;
        }
        new VisualClaim(player, team, VisualClaimType.CREATE, true, false).draw(false);
    }

    @Command(names = {"team claimfor2", "t claimfor2", "f claimfor2", "faction claimfor2", "fac claimfor2"}, permission = "op")
    public static void claimfor2(final Player player, @Param(name = "team") final Team team) {
        if (team == null) {
            player.sendMessage(ChatColor.RED + "This team doesn't exists!");
            return;
        }
        new VisualClaim(player, team, VisualClaimType.CREATE, true, true).draw(false);
    }
}