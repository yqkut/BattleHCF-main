package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeamCheckInvCommand {
    @Command(names = {"team checkinv", "t checkinv", "f checkinv", "faction checkinv", "fac checkinv"}, permission = "PLATFORMADMINISTRATOR")
    public static void teamCheckInv(final Player sender, @Param(name = "team") final Team team, @Param(name = "item") final String material) {
        if (Material.getMaterial(material) == null) {
            sender.sendMessage(CC.translate("&cThat material is invalid!"));
            return;
        }
        sender.sendMessage(CC.translate("&c&m--------------------------------"));
        sender.sendMessage(CC.translate("&3&lTeam &eInventory Lookup for &f" + team.getName(sender)));
        sender.sendMessage(CC.translate("&eSearching for &f" + material));
        sender.sendMessage(CC.translate("&c&m--------------------------------"));
        for (final Player player : team.getOnlineMembers()) {
            if (player.getInventory().contains(Material.getMaterial(material))) {
                sender.sendMessage(CC.translate(player.getDisplayName() + " &ehas that item in their inventory!"));
            } else {
                sender.sendMessage(CC.translate("&cNo one else in that faction has the item"));
            }
        }
        sender.sendMessage(CC.translate("&c&m--------------------------------"));
    }
}