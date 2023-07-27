package cc.stormworth.hcf.events.ktk.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.ktk.KillTheKing;
import cc.stormworth.hcf.events.ktk.listener.KillTheKingListener;
import org.bukkit.entity.Player;

public class KTKCommand {

    public static KillTheKingListener killTheKingListener;

    @Command(names = {"ktk start"}, permission = "op")
    public static void start(final Player sender, @Param(name = "target") final Player target) {
        if (!Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a KitMap only command."));
            return;
        }
        killTheKingListener = new KillTheKingListener();
        Main.getInstance().setKillTheKing(new KillTheKing(target.getUniqueId()));
        sender.sendMessage(CC.translate("&aYou have started the kill the king with &l" + target.getName()));
    }

    @Command(names = {"ktk stop"}, permission = "op")
    public static void stop(final Player sender) {
        if (!Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a KitMap only command."));
            return;
        }
        Main.getInstance().setKillTheKing(null);
        if (killTheKingListener != null) {
            killTheKingListener.unload();
            killTheKingListener = null;
        }
        sender.sendMessage(CC.translate("&cYou have stopped the kill the king"));
    }
}