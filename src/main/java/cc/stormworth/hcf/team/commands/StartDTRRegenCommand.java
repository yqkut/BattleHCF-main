package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartDTRRegenCommand {
    @Command(names = {"startdtrregen"}, permission = "SENIORMOD")
    public static void startDTRRegen(final Player sender, @Param(name = "team") final Team team) {
        team.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.GOLD + team.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }
}