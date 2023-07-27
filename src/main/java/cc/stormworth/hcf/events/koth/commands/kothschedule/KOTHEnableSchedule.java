package cc.stormworth.hcf.events.koth.commands.kothschedule;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHEnableSchedule {

    @Command(names = "KOTHSchedule Enable", permission = "op")
    public static void kothScheduleEnable(CommandSender sender) {
        Main.getInstance().getEventHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
    }

}
