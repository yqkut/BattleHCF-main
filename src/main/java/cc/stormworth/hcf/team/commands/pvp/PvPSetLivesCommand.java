package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PvPSetLivesCommand {
    @Command(names = {"pvptimer setlives", "timer setlives", "pvp setlives", "pvptimer setlives", "timer setlives", "pvp setlives"}, permission = "op", async = true)
    public static void pvpSetLives(final CommandSender sender, @Param(name = "player") final UUID uuid, @Param(name = "amount") final int amount) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        HCFProfile hcfProfile = HCFProfile.getByUUID(uuid);
        hcfProfile.addLives(amount);
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(uuid) + ChatColor.YELLOW + "'s life count to " + amount + ".");
    }
}