package cc.stormworth.hcf.misc.map.stats.command;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.killstreaks.KillStreakActiveMenu;
import cc.stormworth.hcf.misc.map.killstreaks.KillStreakMenu;
import org.bukkit.entity.Player;

public class KillstreaksCommand {

    @Command(names = {"killstreaks", "ks", "killstreak"}, permission = "", requiresPlayer = true, async = true)
    public static void killstreaks(Player sender) {
        if (!Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a KitMap only command."));
            return;
        }
        new KillStreakMenu().openMenu(sender);
    }

    @Command(names = {"killstreaks active", "ks active", "killstreak active", "ksa"}, permission = "", requiresPlayer = true, async = true)
    public static void killstreaksActive(final Player sender) {
        if (!Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a KitMap only command."));
            return;
        }
        new KillStreakActiveMenu().openMenu(sender);
    }
}