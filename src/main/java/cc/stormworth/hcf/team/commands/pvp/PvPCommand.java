package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.entity.Player;

public class PvPCommand {
    @Command(names = {"pvptimer", "timer", "pvp"}, permission = "", async = true)
    public static void pvp(final Player sender) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        final String[] msges = {
                "§c/pvp revive <player> - Revives targeted player",
                "§c/pvp time - Shows time left on PVP Timer",
                "§c/pvp enable - Remove PVP Timer"};
        sender.sendMessage(msges);
    }
}