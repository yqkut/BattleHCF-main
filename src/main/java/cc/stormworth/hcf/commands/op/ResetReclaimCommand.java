package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ResetReclaimCommand {
    @Command(names = {"resetreclaim", "reclaimreset"}, hidden = true, permission = "op", async = true)
    public static void execute(final CommandSender sender, @Param(name = "player") final UUID uuid) {
        HCFProfile hcfProfile = HCFProfile.getByUUID(uuid);
        if (!hcfProfile.isReclaimed()) {
            if (sender instanceof Player)
                sender.sendMessage(ChatColor.RED + "That player has not reclaimed yet this map!");
            return;
        }
        hcfProfile.setReclaimed(false);
        if (sender instanceof Player)
            sender.sendMessage(ChatColor.GOLD + "Reset " + ChatColor.YELLOW + UUIDUtils.name(uuid) + ChatColor.GOLD + "'s reclaim.");
    }
}