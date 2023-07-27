package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.cmds.staff.FreezeCommand;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LogoutCommand {
    @Command(names = {"Logout"}, permission = "")
    public static void logout(final Player sender) {
        if (FreezeCommand.getFreezes().containsKey(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot log out while you're frozen!");
            return;
        }
        if (Main.getInstance().getServerHandler().getLogouttasks().containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out.");
            return;
        }

        HCFProfile profile = HCFProfile.get(sender);

        if (profile.hasSotwTimer()) {
            sender.sendMessage(CC.RED + "HELLO? just disconnect, you have sotw timer.");
            return;
        }

        if (!CustomTimerCreateCommand.hasSOTWEnabled(sender) && CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
            sender.sendMessage(CC.RED + "HELLO? just disconnect, there is sotw timer.");
            return;
        }
        Main.getInstance().getServerHandler().startLogoutSequence(sender);
    }
}