package cc.stormworth.hcf.events.koth.commands.koth;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHActivateCommand {

    @Command(names = {"KOTH Activate", "KOTH Active", "events activate", "event start", "koth start"}, permission = "op")
    public static void kothActivate(CommandSender sender, @Param(name = "event") Event koth, @Param(name = "deactivateOthers", defaultValue = "false") boolean deactivateOthers) {
        // Don't start a KOTH if another one is active.
        if (!deactivateOthers) {
            for (Event otherKoth : Main.getInstance().getEventHandler().getEvents()) {
                if (otherKoth.isActive() && !otherKoth.getName().contains("conquest")) {
                    sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                    return;
                }
            }
        } else {
            Main.getInstance().getEventHandler().deactivateOthers();
        }

        if ((koth.getName().equalsIgnoreCase("citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }

        koth.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + ".");
    }
}