package cc.stormworth.hcf.team.commands.team.chatspy;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyDelCommand {
    @Command(names = {"team chatspy del", "t chatspy del", "f chatspy del", "faction chatspy del", "fac chatspy del"}, permission = "MODPLUS")
    public static void teamChatSpyDel(final Player sender, @Param(name = "team") final Team team) {
        HCFProfile profile = HCFProfile.getByUUIDIfAvailable(sender.getUniqueId());

        if (!profile.getSpyTeam().contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + team.getName() + ".");
            return;
        }
        profile.getSpyTeam().remove(team.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ".");
    }
}
