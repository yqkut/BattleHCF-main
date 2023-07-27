package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaveCommand {
    @Command(names = {"forceleave"}, permission = "SENIORMOD")
    public static void forceLeave(final Player player) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        team.removeMember(player.getUniqueId());
        Main.getInstance().getTeamHandler().setTeam(player, null);
        player.sendMessage(ChatColor.YELLOW + "Force left your team.");
    }
}