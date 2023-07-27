package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CEffectsCommand {
    @Command(names = {"checkeffects"}, permission = "ADMINISTRATOR")
    public static void ceffects(final Player sender, @Param(name = "player") final UUID player) {
        final Player target = Bukkit.getPlayer(player);
        if (target == null) {
            return;
        }
        target.getActivePotionEffects().forEach(potionEffect -> sender.sendMessage(ChatColor.RED + "Effect: " + ChatColor.GRAY + potionEffect.getType().getName() + " : " + potionEffect.getAmplifier() + " : " + potionEffect.getDuration()));
    }
}
