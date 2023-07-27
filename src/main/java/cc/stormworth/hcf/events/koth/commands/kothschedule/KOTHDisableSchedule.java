package cc.stormworth.hcf.events.koth.commands.kothschedule;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDisableSchedule {

    @Command(names = "KOTHSchedule Disable", permission = "op")
    public static void kothScheduleDisable(CommandSender sender) {
        Main.getInstance().getEventHandler().setScheduleEnabled(false);
        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }

}
