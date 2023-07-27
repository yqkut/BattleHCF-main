package cc.stormworth.hcf.events.koth.commands.koth;

import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventScheduledTime;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class KOTHCommand {

    // Make this pretty.
    @Command(names = {"Event", "Event Next", "Event Info", "Event", "koth", "koth next", "koth info"}, permission = "")
    public static void koth(Player sender) {
        for (Event koth : Main.getInstance().getEventHandler().getEvents()) {
            if (!koth.isHidden() && koth.isActive()) {
                FormatingMessage fm = new FormatingMessage("[Events] ")
                        .color(GOLD)
                        .then(koth.getName())
                        .color(YELLOW) // koth name should be yellow
                        .style(UNDERLINE);
                if (koth instanceof KOTH) {
                    fm.tooltip(YELLOW.toString() + ((KOTH) koth).getCapLocation().getBlockX() + ", " + ((KOTH) koth).getCapLocation().getBlockZ());
                }
                fm.color(YELLOW) // should color Event coords gray
                        .then(" can be contested now.")
                        .color(GOLD);
                fm.send(sender);
                return;
            }
        }
        Date now = new Date();
        for (Map.Entry<EventScheduledTime, String> entry : Main.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + entry.getValue() + GOLD + " can be captured at " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GOLD + ".");
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + "It is currently " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(now) + GOLD + ".");
                sender.sendMessage(YELLOW + "Type '/koth schedule' to see more upcoming Events.");
                return;
            }
        }
        sender.sendMessage(GOLD + "[KingOfTheHill] " + RED + "Next Event: " + YELLOW + "Undefined");
    }

    @Command(names = "koth capper", permission = "MOD")
    public static void capper(Player sender, @Param(name = "koth") Event event) {

        if(!(event instanceof KOTH)) {
            sender.sendMessage(RED + "That is not a KOTH Event.");
            return;
        }

        HCFProfile profile = HCFProfile.get(sender);

        KOTH koth = (KOTH) event;

        if(koth.getCurrentCapper() == null) {
            sender.sendMessage(RED + "There is no one capping this KOTH.");
            return;
        }

        profile.setOnlyShowCaper(!profile.isOnlyShowCaper());

        sender.sendMessage(GOLD + "Only showing cappers: " + profile.isOnlyShowCaper());
    }
}