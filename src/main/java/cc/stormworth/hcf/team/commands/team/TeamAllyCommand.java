package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAllyCommand {

    @Command(names = {"team ally", "t ally", "f ally", "faction ally", "fac ally"}, permission = "")
    public static void teamAlly(final Player sender, @Param(name = "team") final Team team) {
        final Team senderTeam = Main.getInstance().getTeamHandler().getTeam(sender);
        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!senderTeam.isOwner(sender.getUniqueId()) && !senderTeam.isCaptain(sender.getUniqueId()) && !senderTeam.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }
        if (senderTeam.equals(team)) {
            sender.sendMessage(ChatColor.YELLOW + "You cannot ally your own team!");
            return;
        }
        if (senderTeam.getAllies().size() >= Main.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team already has the max number of allies, which is " + Main.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }
        if (team.getAllies().size() >= Main.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "The team you're trying to ally already has the max number of allies, which is " + Main.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }
        if (senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.YELLOW + "You're already allied to " + team.getName(sender) + ChatColor.YELLOW + ".");
            return;
        }
        if (senderTeam.getRequestedAllies().contains(team.getUniqueId())) {
            senderTeam.getRequestedAllies().remove(team.getUniqueId());
            team.getAllies().add(senderTeam.getUniqueId());
            senderTeam.getAllies().add(team.getUniqueId());
            team.flagForSave();
            senderTeam.flagForSave();
            for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has accepted your request to ally. You now have " + Main.getInstance().getMapHandler().getAllyRelationColor() + team.getAllies().size() + "/" + Main.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has allied " + team.getName(sender) + ChatColor.YELLOW + ". You now have " + Main.getInstance().getMapHandler().getAllyRelationColor() + senderTeam.getAllies().size() + "/" + Main.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                }
                if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                    CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
                    CorePlugin.getInstance().getNametagEngine().reloadOthersFor(sender);
                    WaypointManager.updatePlayerFactionChange(sender);
                }
            }
        } else {
            if (team.getRequestedAllies().contains(senderTeam.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + "You have already requested to ally " + team.getName(sender) + ChatColor.YELLOW + ".");
                return;
            }
            team.getRequestedAllies().add(senderTeam.getUniqueId());
            team.flagForSave();
            for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player.getPlayer()) + ChatColor.YELLOW + " has requested to be your ally. Type " + Main.getInstance().getMapHandler().getAllyRelationColor() + "/team ally " + senderTeam.getName() + ChatColor.YELLOW + " to accept.");
                } else {
                    if (!senderTeam.isMember(player.getUniqueId())) {
                        continue;
                    }
                    player.sendMessage(ChatColor.YELLOW + "Your team has requested to ally " + team.getName(player) + ChatColor.YELLOW + ".");
                }
            }
        }
    }
}