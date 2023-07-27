package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPAddLivesCommand {
    @Command(names = {"pvp addlives", "addlives"}, permission = "op", async = true)
    public static void pvpSetLives(final CommandSender sender, @Param(name = "player") final UUID uuid, @Param(name = "amount") final int amount) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        HCFProfile hcfProfile = HCFProfile.getByUUID(uuid);
        hcfProfile.addLives(amount);
        sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(uuid) + ChatColor.YELLOW + " " + amount + " lives.");
        final Player bukkitPlayer = Bukkit.getPlayer(uuid);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            final String suffix = (sender instanceof Player) ? (" from " + sender.getName()) : "";
            bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
        }
    }
}