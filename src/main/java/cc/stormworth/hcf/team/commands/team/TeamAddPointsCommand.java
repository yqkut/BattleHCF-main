package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamAddPointsCommand {
    @Command(names = {"team addpoints", "t addpoints", "f addpoints", "faction addpoints", "fac addpoints"}, permission = "op")
    public static void teamAddPoints(final CommandSender sender, @Param(name = "team") final Team team, @Param(name = "amount") final int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }
        team.addPoints(amount);
        sender.sendMessage(CC.translate("&eYou have successfully added &6" + amount + "&e points to &a" + team.getName() + "&e."));
    }
}