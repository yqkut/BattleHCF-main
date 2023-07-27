package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeamUninviteCommand {

    @Command(names = {"team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke"}, permission = "", async = true)
    public static void teamUninvite(final Player sender, @Param(name = "all | player") final String allPlayer) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (allPlayer.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                new BukkitRunnable() {
                    public void run() {
                        final UUID nameUUID = UUIDUtils.uuid(allPlayer);
                        new BukkitRunnable() {
                            public void run() {
                                if (team.getInvitations().remove(nameUUID)) {

                                    TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_INVITE_REVOKED, ImmutableMap.of(
                                            "playerId", allPlayer,
                                            "uninvitedById", sender.getUniqueId().toString(),
                                            "date", System.currentTimeMillis()
                                    ));

                                    team.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + allPlayer + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + allPlayer + "'!");
                                }
                            }

                        }.runTask(Main.getInstance());
                    }
                }.runTaskAsynchronously(Main.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
}