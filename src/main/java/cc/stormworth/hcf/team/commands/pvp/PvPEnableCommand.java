package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPEnableCommand {
    @Command(names = {"pvptimer enable", "timer enable", "pvp enable", "pvptimer remove", "timer remove", "pvp remove"}, permission = "", async = true)
    public static void pvpEnable(final Player sender, @Param(name = "target", defaultValue = "self") final Player target) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        if (target != sender && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "That command was not found.");
            return;
        }

        HCFProfile profile = HCFProfile.get(target);

        if (profile.hasPvPTimer()) {
            profile.setPvpTimer(null);

            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Your PvP Timer has been removed!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + "'s PvP Timer has been removed!");
            }
        } else if (target == sender) {
            sender.sendMessage(ChatColor.RED + "You don't have a PvP Timer!");
        } else {
            sender.sendMessage(ChatColor.RED + target.getName() + " does not have a PvP Timer.");
        }
    }
}