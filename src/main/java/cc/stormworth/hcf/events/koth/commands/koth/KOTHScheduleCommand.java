package cc.stormworth.hcf.events.koth.commands.koth;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventScheduledTime;
import cc.stormworth.hcf.events.EventType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class KOTHScheduleCommand {

    public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE h:mm a");

    // Make this pretty.
    @Command(names = {"KOTH Schedule"}, permission = "")
    public static void kothSchedule(Player sender) {
        int sent = 0;
        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Main.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            Event resolved = Main.getInstance().getEventHandler().getEvent(entry.getValue());

            if (resolved == null || resolved.isHidden() || !entry.getKey().toDate().after(now) || resolved.getType() != EventType.KOTH) {
                continue;
            }

            if (sent > 5) {
                break;
            }

            sent++;
            sender.sendMessage(ChatColor.GOLD + "[Events] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.GOLD + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GOLD + "[Events] " + ChatColor.RED + "Schedule: " + ChatColor.YELLOW + "Undefined");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[Events] " + ChatColor.YELLOW + "It is currently " + ChatColor.GOLD + KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
        }
    }
}