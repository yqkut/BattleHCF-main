package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GoppleResetCommand {
    @Command(names = {"gapple reset"}, permission = "op", hidden = true)
    public static void goppleReset(final Player sender, @Param(name = "player") final UUID player) {
        Main.getInstance().getOppleMap().resetCooldown(player);
        sender.sendMessage(ChatColor.RED + "Cooldown reset!");
    }
}