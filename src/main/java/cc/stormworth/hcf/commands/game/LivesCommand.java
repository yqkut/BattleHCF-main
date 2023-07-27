package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LivesCommand {
    @Command(names = {"lives"}, permission = "")
    public static void lives(final Player sender, @Param(name = "target", defaultValue = "self") UUID target) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        HCFProfile hcfProfile = HCFProfile.getByUUID(target);
        sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(target) + "'s Lives: " + ChatColor.RED + hcfProfile.getLives());
    }
}