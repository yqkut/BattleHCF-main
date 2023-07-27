package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReviveCommand {
    @Command(names = {"Revive", "Staffrevive"}, permission = "MODPLUS")
    public static void revive(final CommandSender sender, @Param(name = "player") final UUID player) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }

        HCFProfile profile = HCFProfile.getByUUID(player);

        if (profile == null) {
            CompletableFuture<HCFProfile> future = HCFProfile.load(player);

            future.thenAccept(hcfProfile -> {
                if (hcfProfile == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }

                if (!hcfProfile.isDeathBanned()) {
                    sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
                    return;
                }

                sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "!");

                hcfProfile.setDeathban(null);
                hcfProfile.asyncSave();
            });

            return;
        }

        if (profile.isDeathBanned()) {
            profile.getDeathban().revive(player);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(player) + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }
}