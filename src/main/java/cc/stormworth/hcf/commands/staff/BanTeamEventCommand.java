package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BanTeamEventCommand {

    @Command(names = {"eventban"}, hidden = true, permission = "op")
    public static void ban(Player player, @Param(name = "team") Team team) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }

        if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
            player.sendMessage(CC.RED + team.getName() + " is already banned from the event.");
            return;
        }

        Main.getInstance().getEventHandler().getBannedTeams().add(team);
        for (Player online : team.getOnlineMembers()) {
            SpawnTagHandler.removeTag(online);
            online.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
        }
        team.sendMessage(CC.translate("&eYou have banned " + team.getName() + " from the event."));
    }
}