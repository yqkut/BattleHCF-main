package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.menu.TrackerMenu;
import org.bukkit.entity.Player;

public class TeamLogsCommand {

    @Command(names = {"team logs", "t logs", "f logs", "factions logs", "team log", "t log", "f log", "factions log"}, permission = "")
    public static void logs(Player player, @Param(name = "team", defaultValue = "self") final Team team){

        if (team == null) {
            player.sendMessage(CC.translate("&cTeam not found."));
            return;
        }

        if(!player.isOp() && !team.isMember(player.getUniqueId())){
            player.sendMessage(CC.translate("&cYou are not a member of this team."));
            return;
        }

        new TrackerMenu(team).openMenu(player);
    }

}
