package cc.stormworth.hcf.team.commands.team.chatspy;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyAddCommand {

    @Command(names = {"team chatspy add", "t chatspy add", "f chatspy add", "faction chatspy add", "fac chatspy add"}, permission = "MODPLUS")
    public static void teamChatSpyAdd(final Player sender, @Param(name = "team") final Team team) {
        HCFProfile profile = HCFProfile.getByUUIDIfAvailable(sender.getUniqueId());

        if (!profile.getSpyTeam().contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already spying on " + team.getName() + ".");
            return;
        }

        profile.getSpyTeam().add(team.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ".");
    }
}