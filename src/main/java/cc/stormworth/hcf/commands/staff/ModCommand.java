package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.cmds.staff.StaffModeCommand;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.entity.Player;

public class ModCommand {

    @Command(names = "h", permission = "TRIALMOD", requiresPlayer = true)
    public static void h(Player player){
        StaffModeCommand.staffmode(player);
    }

    public static boolean createFactionsMessage = true;

    @Command(names = "togglecreatefations", permission = "op")
    public static void togglecreatefations(Player player){
        createFactionsMessage = !createFactionsMessage;
        player.sendMessage(CC.translate("&eCreate Factions Message: " + (createFactionsMessage ? "&aEnabled" : "&cDisabled")));
    }
}
