package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInvitesCommand {
    @Command(names = {"team invites", "t invites", "f invites", "faction invites", "fac invites"}, permission = "")
    public static void teamInvites(final Player sender) {
        final StringBuilder yourInvites = new StringBuilder();
        for (final Team team : Main.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(team.getName()).append(ChatColor.YELLOW).append(", ");
            }
        }
        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }
        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites);
        final Team current = Main.getInstance().getTeamHandler().getTeam(sender);
        if (current != null) {
            final StringBuilder invitedToYourTeam = new StringBuilder();
            for (final UUID invited : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(UUIDUtils.name(invited)).append(ChatColor.YELLOW).append(", ");
            }
            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }
            sender.sendMessage(ChatColor.YELLOW + "Invited to your Team: " + invitedToYourTeam);
        }
    }
}