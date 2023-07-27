package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamPromoteCommand {
    @Command(names = {"team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain"}, permission = "")
    public static void teamPromote(final Player sender, @Param(name = "player") final UUID player) {
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
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a leader.");
        } else if (team.isCoLeader(player)) {
            if (team.isOwner(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a co-leader! To make them a leader, use /t leader");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new leaders.");
            }
        } else if (team.isCaptain(player)) {
            if (team.isOwner(sender.getUniqueId())) {
                team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been promoted to Co-Leader!");
                team.addCoLeader(player);
                team.removeCaptain(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new Co-Leaders.");
            }
        } else {
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been promoted to Captain!");
            team.addCaptain(player);
        }
    }
}