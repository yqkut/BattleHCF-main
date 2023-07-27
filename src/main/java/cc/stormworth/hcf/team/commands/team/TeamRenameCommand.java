package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRenameCommand {
    @Command(names = {"team rename", "t rename", "f rename", "faction rename", "fac rename"}, permission = "", async = true)
    public static void teamRename(final Player player, @Param(name = "new name") final String newName) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (Main.getInstance().getCitadelHandler().getCappers().contains(team.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Citadel cappers cannot change their name. Please contact an admin to rename your team.");
            return;
        }

        if (!team.isOwner(player.getUniqueId()) && !team.isCoLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only team owners and co-leaders can use this command!");
            return;
        }
        if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
            player.sendMessage(CC.RED + "You cannot disband your team while is banned.");
            return;
        }
        if (newName.length() > 16) {
            player.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }
        if (newName.length() < 3) {
            player.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }
        if (TeamCreateCommand.disallowedTeamNames.contains(newName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Team name disabled!");
            return;
        }
        if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(newName).find()) {
            if (Main.getInstance().getTeamHandler().getTeam(newName) == null) {
                team.rename(newName);
                player.sendMessage(ChatColor.GREEN + "Team renamed to " + newName);
                if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
                    player.sendMessage(CC.RED + "You cannot rename your team while is banned.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "A team with that name already exists!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
        }
    }
}