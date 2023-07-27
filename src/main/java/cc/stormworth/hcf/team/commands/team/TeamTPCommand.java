package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamTPCommand {
    @Command(names = {"team tp", "t tp", "f tp", "faction tp", "fac tp"}, permission = "op")
    public static void teamTP(final Player sender, @Param(name = "team", defaultValue = "self") final Team team) {
        if (team.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(team.getHQ());
        } else if (team.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(team.getClaims().get(0).getMaximumPoint().add(0.0, 100.0, 0.0));
        } else {
            sender.sendMessage(ChatColor.GOLD + team.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }
}