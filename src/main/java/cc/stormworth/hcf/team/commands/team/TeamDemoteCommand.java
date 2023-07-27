package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamDemoteCommand {
    @Command(names = {"team demote", "t demote", "f demote", "faction demote", "fac demote"}, permission = "")
    public static void teamDemote(final Player sender, @Param(name = "player") final UUID player) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }
        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is not on your team.");
            return;
        }
        if (team.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is the leader. To change leaders, the team leader must use /t leader <name>");
        } else if (team.isCoLeader(player)) {
            if (team.isOwner(sender.getUniqueId())) {
                team.removeCoLeader(player);
                team.addCaptain(player);
                team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been demoted to Captain!");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can demote Co-Leaders.");
            }
        } else if (team.isCaptain(player)) {
            team.removeCaptain(player);
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been demoted to a member!");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is currently a member. To kick them, use /t kick");
        }
    }
}