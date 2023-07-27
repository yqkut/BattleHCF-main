package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Constants;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TellLocationCommand {

    @Command(names = {"teamlocation", "tl"}, permission = "")
    public static void tellLocation(final Player sender, @Param(name = "type", defaultValue = "self") final String place) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!place.equalsIgnoreCase("home")) {
            final Location l = sender.getLocation();
            team.sendMessage(Constants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", l.getX(), l.getY(), l.getZ())));
            return;
        }
        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "Your faction doesn't have a home set!");
            return;
        }
        final Location l = team.getHQ();
        team.sendMessage(Constants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", l.getX(), l.getY(), l.getZ())));
    }
}