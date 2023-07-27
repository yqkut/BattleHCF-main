package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FocusCommand {
    @Command(names = {"focus"}, permission = "")
    public static void focus(final Player sender, @Param(name = "player") final Player target) {
        final Team senderTeam = Main.getInstance().getTeamHandler().getTeam(sender);
        final Team targetTeam = Main.getInstance().getTeamHandler().getTeam(target);
        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your team.");
            return;
        }
        senderTeam.setFocused(target.getUniqueId());
        senderTeam.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + ".");
        for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                CorePlugin.getInstance().getNametagEngine().reloadOthersFor(onlinePlayer);
            }
        }
    }
}