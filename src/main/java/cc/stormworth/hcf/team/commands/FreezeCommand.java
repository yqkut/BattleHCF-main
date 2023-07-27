package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.server.utils.FreezeInfo;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.entity.Player;

public class FreezeCommand {
    @Command(names = {"freezeteam"}, permission = "op")
    public static void freezeteam(final Player player, @Param(name = "team") Team team) {
        for (Player online : team.getOnlineMembers()) {
            cc.stormworth.core.cmds.staff.FreezeCommand.freezes.put(online.getUniqueId(), new FreezeInfo(null, online.getUniqueId(), online.getLocation(), System.currentTimeMillis()));
        }
        player.sendMessage(CC.YELLOW + "You have frozen the " + team.getName() + " team.");
    }
}