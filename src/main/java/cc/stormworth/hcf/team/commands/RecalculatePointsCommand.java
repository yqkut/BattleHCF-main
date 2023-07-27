package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RecalculatePointsCommand {
    @Command(names = {"team pointscorrect", "f pointscorrect", "team pointscorrect"}, permission = "op", async = true)
    public static void recalculate(final CommandSender sender) {
        int changed = 0;
        for (final Team team : Main.getInstance().getTeamHandler().getTeams()) {
            final int oldPoints = team.getPoints();
            team.recalculatePoints();
            if (team.getPoints() != oldPoints) {
                team.flagForSave();
                sender.sendMessage(ChatColor.YELLOW + "Changed " + team.getName() + "'s points from " + oldPoints + " to " + team.getPoints());
                ++changed;
            }
        }
        sender.sendMessage(ChatColor.YELLOW + "Changed a total of " + changed + " teams points.");
    }
}