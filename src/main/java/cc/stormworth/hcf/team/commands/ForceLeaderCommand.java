package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {
    @Command(names = {"ForceLeader"}, permission = "SENIORMOD")
    public static void forceLeader(final Player sender, @Param(name = "player", defaultValue = "self") final UUID player) {
        final Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);
        if (playerTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "That player is not on a team.");
            return;
        }
        final Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you leader of §b" + playerTeam.getName() + "§e.");
        }
        playerTeam.setOwner(player);
        sender.sendMessage(ChatColor.GOLD + UUIDUtils.name(player) + ChatColor.YELLOW + " is now the owner of " + ChatColor.GOLD + playerTeam.getName() + ChatColor.YELLOW + ".");
    }
}