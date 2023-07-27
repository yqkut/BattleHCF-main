package cc.stormworth.hcf.commands.toggle;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleChatCommand {
    @Command(names = {"ToggleGlobalChat", "TGC"}, permission = "")
    public static void TGC(final Player sender) {
        HCFProfile hcfProfile = HCFProfile.getByUUIDIfAvailable(sender.getUniqueId());
        if (hcfProfile == null) return;
        hcfProfile.setGlobalChat(!hcfProfile.isGlobalChat());
        sender.sendMessage(ChatColor.YELLOW + "You are now " + (hcfProfile.isGlobalChat() ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see global chat!");
    }
}