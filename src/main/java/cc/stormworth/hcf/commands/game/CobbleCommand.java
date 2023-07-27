package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CobbleCommand {
    @Command(names = {"cobble", "cobblestone"}, permission = "")
    public static void cobble(final Player sender) {
        sender.setPickingCobble(!sender.isPickingCobble());
        sender.sendMessage(ChatColor.YELLOW + "You are now " + (sender.isPickingCobble() ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable")) + ChatColor.YELLOW + " to pick up cobblestone while in MinerClass class!");
    }
}