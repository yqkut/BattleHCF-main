package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamPointBreakDownCommand {

    @Command(names = {"team pointbr", "team pbr", "t pointbr", "t pbr", "f pbr"}, permission = "op", async = true)
    public static void teamPointBreakDown(final Player player, @Param(name = "team", defaultValue = "self") final Team team) {
        player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + team.getName());
        for (final String info : team.getPointBreakDown()) {
            player.sendMessage(info);
        }
    }
}