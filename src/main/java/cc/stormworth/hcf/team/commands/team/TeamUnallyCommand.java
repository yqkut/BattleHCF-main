package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamUnallyCommand {

    @Command(names = {"team unally", "t unally", "f unally", "faction unally", "fac unally"}, permission = "")
    public static void teamUnally(final Player sender, @Param(name = "team") final Team team) {
        final Team senderTeam = Main.getInstance().getTeamHandler().getTeam(sender);
        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!senderTeam.isOwner(sender.getUniqueId()) && !senderTeam.isCoLeader(sender.getUniqueId()) && !senderTeam.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }
        if (!senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.RED + "You are not allied to " + team.getName() + "!");
            return;
        }
        senderTeam.getAllies().remove(team.getUniqueId());
        team.getAllies().remove(senderTeam.getUniqueId());
        senderTeam.flagForSave();
        team.flagForSave();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player.getUniqueId())) {
                        player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your team.");
                    } else if (senderTeam.isMember(player.getUniqueId())) {
                        player.sendMessage(ChatColor.YELLOW + "Your team has dropped its alliance with " + team.getName(sender) + ChatColor.YELLOW + ".");
                    }
                    if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                        CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
                        CorePlugin.getInstance().getNametagEngine().reloadOthersFor(sender);
                        WaypointManager.updatePlayerFactionChange(sender);
                    }
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}