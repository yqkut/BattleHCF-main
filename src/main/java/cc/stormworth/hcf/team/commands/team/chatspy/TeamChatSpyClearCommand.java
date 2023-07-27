package cc.stormworth.hcf.team.commands.team.chatspy;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyClearCommand {
    @Command(names = {"team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear"}, permission = "MODPLUS")
    public static void teamChatSpyClear(final Player sender) {
        HCFProfile profile = HCFProfile.get(sender);
        profile.getSpyTeam().clear();
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }
}