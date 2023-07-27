package cc.stormworth.hcf.commands.toggle;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TogglePayCommand {

    @Command(names = {"togglepay", "TGP"}, permission = "")
    public static void TGP(final Player sender) {
        HCFProfile hcfProfile = HCFProfile.getByUUID(sender.getUniqueId());
        final boolean val = !hcfProfile.isPaymentsToggled();
        sender.sendMessage(ChatColor.YELLOW + "You are now " + (val ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable")) + ChatColor.YELLOW + " to receive payments!");
        hcfProfile.setPaymentsToggled(val);
    }
}