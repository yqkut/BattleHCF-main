package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAnnouncementCommand {
    @Command(names = {"team announcement", "t announcement", "f announcement", "faction announcement", "fac announcement", "team anouncement", "t anouncement", "f anouncement", "faction anouncement", "fac anouncement"}, permission = "")
    public static void teamAnnouncement(final Player sender, @Param(name = "new announcement", wildcard = true) final String newAnnouncement) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!team.isOwner(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }
        if (newAnnouncement.equalsIgnoreCase("clear")) {
            team.setAnnouncement(null);
            sender.sendMessage(ChatColor.YELLOW + "Team announcement cleared.");
            return;
        }
        team.setAnnouncement(newAnnouncement);
        team.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " changed the team announcement to " + ChatColor.GOLD + newAnnouncement);
    }
}
