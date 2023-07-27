package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceJoinCommand {

    @Command(names = {"ForceJoin"}, permission = "SENIORMOD")
    public static void forceJoin(final Player sender, @Param(name = "team") final Team team, @Param(name = "player", defaultValue = "self") final Player player) {
        if (Main.getInstance().getTeamHandler().getTeam(player) != null) {
            if (player == sender) {
                sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player needs to leave their current team first!");
            }
            return;
        }
        team.addMember(player.getUniqueId());
        Main.getInstance().getTeamHandler().setTeam(player, team);
        player.sendMessage(ChatColor.YELLOW + "You are now a member of " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + "!");
        if (player != sender) {
            sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " added to " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + "!");
        }
    }
}