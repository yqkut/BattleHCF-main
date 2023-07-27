package cc.stormworth.hcf.commands.toggle;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleDeathMessagesCommand {
    @Command(names = {"toggledeathmessages", "tdm"}, permission = "")
    public static void TDM(final Player sender) {
        HCFProfile hcfProfile = HCFProfile.getByUUID(sender.getUniqueId());
        final boolean val = !hcfProfile.isDeathMessages();
        sender.sendMessage(ChatColor.YELLOW + "You are now " + (val ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable")) + ChatColor.YELLOW + " to see death messages!");
        hcfProfile.setDeathMessages(val);
    }
}