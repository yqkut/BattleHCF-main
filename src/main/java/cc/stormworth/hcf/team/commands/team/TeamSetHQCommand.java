package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.PlayerWaypointType;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeamSetHQCommand {
    @Command(names = {"team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome", "sethq"}, permission = "", async = true)
    public static void teamSetHQ(final Player sender) {
        Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                    return;
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting HQ outside of your team's territory would normally be disallowed, but this check is being bypassed due to your rank.");
            }
            if (team.getRaidHologram() != null){
                team.getRaidHologram().destroy();
            }

            if (team.getRaidBlock() != null){
                team.getRaidBlock().getBlock().setType(Material.AIR);
            }
            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");
            for (Player player : team.getOnlineMembers()) {
                WaypointManager.updateWaypoint(player, PlayerWaypointType.FACTION_HQ);
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
}