package cc.stormworth.hcf.team.commands.lives;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LivesGiveCommand {
    @Command(names = {"lives give", "lives add"}, permission = "op")
    public static void livesGive(final CommandSender sender, @Param(name = "player") final UUID uuid, @Param(name = "amount") final int amount) {
        HCFProfile hcfProfile = HCFProfile.getByUUID(uuid);
        hcfProfile.addLives(amount);
        if (sender instanceof Player)
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(uuid) + ChatColor.YELLOW + " " + amount + " friend lives.");
        final Player bukkitPlayer2 = Bukkit.getPlayer(uuid);
        if (bukkitPlayer2 != null && bukkitPlayer2.isOnline()) {
            final String suffix2 = (sender instanceof Player) ? (" from " + sender.getName()) : "";
            bukkitPlayer2.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix2);
        }
    }
}