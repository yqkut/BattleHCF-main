package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamNullLeaderCommand {
    @Command(names = {"team nullleader", "t nullleader", "f nullleader", "faction nullleader", "fac nullleader"}, permission = "op", async = true)
    public static void teamNullLeader(final Player sender) {
        int nullLeaders = 0;
        for (final Team team : Main.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() == null) {
                ++nullLeaders;
                sender.sendMessage(ChatColor.RED + "- " + team.getName());
            }
        }
        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null teams found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null teams found.");
        }
    }
}