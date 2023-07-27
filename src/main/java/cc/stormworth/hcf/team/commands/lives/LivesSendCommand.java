package cc.stormworth.hcf.team.commands.lives;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LivesSendCommand {

    @Command(names = {"lives send", "lives pay"}, permission = "", async = true)
    public static void livesGive(final Player sender, @Param(name = "player") final UUID player, @Param(name = "amount") final int amount) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }

        if (Main.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "You may not use lives while &4EOTW &cis active!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(CC.RED + "You cannot send minus than 0 lives.");
            return;
        }

        HCFProfile hcfProfile = HCFProfile.getByUUID(sender.getUniqueId());
        int lives = hcfProfile.getLives();

        if (lives <= amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough lives to send.");
            return;
        }

        hcfProfile.removeLives(amount);

        Player target = Bukkit.getPlayer(player);
        HCFProfile targetProfile = HCFProfile.getByUUID(target.getUniqueId());

        if (targetProfile != null) {
            targetProfile.addLives(amount);
            target.sendMessage(CC.translate("&eYou have received &6" + amount + "&e lives from &7" + sender.getName()));
            sender.sendMessage(CC.translate("&eYou have sent &6" + amount + "&e lives to &7" + UUIDUtils.name(player)));
        }else{
            sender.sendMessage(CC.translate("&c" + UUIDUtils.name(player) + " is not online."));
        }
    }
}