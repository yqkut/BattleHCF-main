package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.entity.Player;

public class TeamInfoCommand {

    @Command(names = {"team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show", "team i", "t i", "f i", "faction i", "fac i"}, permission = "", async = true)
    public static void teamInfo(final Player sender, @Param(name = "team", defaultValue = "self") final Team team) {
        Team exactPlayerTeam = Main.getInstance().getTeamHandler().getTeam(team.getName());
        if (exactPlayerTeam != null && exactPlayerTeam != team) {
            exactPlayerTeam.sendTeamInfo(sender);
        }
        team.sendTeamInfo(sender);
    }
}