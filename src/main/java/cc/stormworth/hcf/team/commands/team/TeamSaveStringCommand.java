package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamSaveStringCommand {
    @Command(names = {"team strings", "t strings", "f strings", "faction strings", "fac strings"}, permission = "op")
    public static void teamSaveString(final CommandSender sender, @Param(name = "team", defaultValue = "self") final Team team) {
        final String saveString = team.saveString(false);
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Save String (" + team.getName() + ")");
        sender.sendMessage("");
        for (final String line : saveString.split("\n")) {
            sender.sendMessage(ChatColor.GOLD + line.substring(0, line.indexOf(":")) + ": " + ChatColor.YELLOW + line.substring(line.indexOf(":") + 1).replace(",", ChatColor.GOLD + "," + ChatColor.YELLOW).replace(":", ChatColor.GOLD + ":" + ChatColor.YELLOW));
        }
    }
}