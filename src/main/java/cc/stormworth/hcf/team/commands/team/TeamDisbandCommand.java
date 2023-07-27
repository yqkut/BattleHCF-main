package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDisbandCommand {

    @Command(names = {"team disband", "t disband", "f disband", "faction disband", "fac disband"}, permission = "")
    public static void teamDisband(final Player player) {
        Team team = Main.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the team to disband it!");
            return;
        }

        if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your team while raidable.");
            return;
        }

        if (DTRHandler.isOnCooldown(team)) {
            player.sendMessage(CC.RED + "You cannot disband your team while are regenerating DTR!");
            return;
        }

        if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
            player.sendMessage(CC.RED + "You cannot disband your team while is banned.");
            return;
        }

        //team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getDisplayName() + ChatColor.YELLOW + " has disbanded the team.");
        team.disband();
        Bukkit.broadcastMessage(CC.translate("&eTeam " + ChatColor.BLUE + team.getName() + " &ehas been &cdisbanded&e by " + player.getDisplayName() + "&e."));

        TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_DISBAND_TEAM, ImmutableMap.of(
                "playerId", player.getUniqueId().toString(),
                "date", System.currentTimeMillis()
        ));
    }
}