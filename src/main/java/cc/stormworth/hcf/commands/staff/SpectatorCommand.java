package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.listener.SpectatorListener;
import org.bukkit.entity.Player;

public class SpectatorCommand {
    @Command(names = {"spectator toggle"}, permission = "TRIALMOD")
    public static void spectators(final Player sender) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(CC.RED + "This command is only available during eotw!");
            return;
        }
        SpectatorListener.toggleSpectators(sender, false);
    }

    @Command(names = {"spectator revive"}, permission = "op")
    public static void revive(final Player sender, @Param(name = "target") Player target) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(CC.RED + "This command is only available during eotw!");
            return;
        }
        SpectatorListener.disableSpectator(target);
        sender.sendMessage(CC.YELLOW + "You have removed " + target.getName() + " from the spectator mode.");
    }

    @Command(names = {"spectator add"}, permission = "op")
    public static void add(final Player sender, @Param(name = "target") Player target) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(CC.RED + "This command is only available during eotw!");
            return;
        }
        SpectatorListener.enableSpectator(target);
        sender.sendMessage(CC.YELLOW + "You have added " + target.getName() + " to the spectator mode.");
    }
}