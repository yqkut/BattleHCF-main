package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPLivesCommand {
    @Command(names = {"pvptimer lives", "timer lives", "pvp lives"}, permission = "", async = true)
    public static void pvpLives(final CommandSender sender, @Param(name = "player", defaultValue = "self") final Player player) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
        sender.sendMessage(ChatColor.GOLD + player.getName() + "'s Lives: " + ChatColor.WHITE + hcfProfile.getLives());
    }
}