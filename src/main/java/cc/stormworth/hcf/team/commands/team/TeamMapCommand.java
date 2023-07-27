package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamMapCommand {

    @Command(names = {"team map", "t map", "f map", "faction map", "fac map", "map"}, permission = "", async = true)
    public static void teamMap(Player player) {
        Team team = Main.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        LandBoard.getTeamMap().showFactionMap(player, false, (CustomTimerCreateCommand.sotwday || Bukkit.getOnlinePlayers().size() >= 600));
    }
}