package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class RecachePlayerTeamsCommand {

    @Command(names = {"team cache rebuild"}, permission = "op", hidden = true)
    public static void recachePlayerTeamsRebuild(final Player sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Rebuilding player team cache...");
        Main.getInstance().getTeamHandler().recachePlayerTeams();
        sender.sendMessage(ChatColor.DARK_PURPLE + "The player death cache has been rebuilt.");
    }

    @Command(names = {"team cache check"}, permission = "op", hidden = true)
    public static void recachePlayerTeams(final Player sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Checking player team cache...");
        final Map<UUID, String> dealtWith = new HashMap<UUID, String>();
        final Set<UUID> errors = new HashSet<UUID>();
        for (final Team team : Main.getInstance().getTeamHandler().getTeams()) {
            for (final UUID member : team.getMembers()) {
                if (dealtWith.containsKey(member) && !errors.contains(member)) {
                    errors.add(member);
                    sender.sendMessage(ChatColor.RED + " - " + member + " (Team: " + team.getName() + ", Expected: " + dealtWith.get(member) + ")");
                } else {
                    dealtWith.put(member, team.getName());
                }
            }
        }
        if (errors.size() == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No errors found while checking player team cache.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + errors.size() + " error(s) found while checking player team cache.");
        }
    }
}