package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceDisbandCommand {
    @Command(names = {"forcedisband"}, permission = "SENIORMOD")
    public static void forceDisband(final Player sender, @Param(name = "team") final Team team) {
        if (!sender.isOp() && team.getOwner() == null) {
            sender.sendMessage(CC.translate("&cYou don't have permissions to disband system factions."));
            return;
        }
        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        team.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + ".");
    }
}