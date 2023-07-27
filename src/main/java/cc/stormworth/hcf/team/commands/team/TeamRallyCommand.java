package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.PlayerWaypointType;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TeamRallyCommand {

    @Command(names = {"team rally", "t rally", "f rally", "faction rally", "fac rally"}, permission = "", async = true)
    public static void teamrally(final Player sender) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        team.setRally(sender.getLocation().add(0.5, 0.0, 0.5));
        team.sendMessage(sender.getDisplayName() + ChatColor.YELLOW + " has set a rally point!", Sound.LEVEL_UP);
        for (Player player : team.getOnlineMembers()) {
            WaypointManager.updateWaypoint(player, PlayerWaypointType.FACTION_RALLY);
        }
    }
}