package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAcceptCommand {
    @Command(names =
            {"team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a",
                    "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join", "team j", "t j", "f j", "faction j", "fac j"},
            permission = "", async = true)
    public static void teamAccept(final Player sender, @Param(name = "team") final Team team) {
        if (team.getInvitations().contains(sender.getUniqueId()) || team.isOpen()) {
            if (Main.getInstance().getTeamHandler().getTeam(sender) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }
            Profile profile = Profile.getByUuid(team.getOwner());

            if (!TeamInviteCommand.canBypassMembersSize(profile)) {
                if (team.getMembers().size() >= Main.getInstance().getMapHandler().getTeamSize()) {
                    sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                    return;
                }
            }

            if (DTRHandler.isOnCooldown(team) && !Main.getInstance().getServerHandler().isPreEOTW() && !Main.getInstance().getMapHandler().isKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team now regenerating DTR!");
                return;
            }
            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: You are combat tagged!");
                return;
            }
            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            Main.getInstance().getTeamHandler().setTeam(sender, team);
            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");

            CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
            CorePlugin.getInstance().getNametagEngine().reloadOthersFor(sender);
            WaypointManager.updatePlayerFactionChange(sender);

            if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer"))
                team.setDTR(team.getMaxDTR());
        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }
}