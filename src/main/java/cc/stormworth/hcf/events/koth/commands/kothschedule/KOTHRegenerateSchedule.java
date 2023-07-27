package cc.stormworth.hcf.events.koth.commands.kothschedule;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class KOTHRegenerateSchedule {

    @Command(names = {"KOTHSchedule Regenerate", "KOTHSchedule Regen"}, permission = "op", async = true)
    public static void kothScheduleEnable(CommandSender sender) {
        File kothSchedule = new File(Main.getInstance().getDataFolder(), "eventSchedule.json");

        if (kothSchedule.delete()) {
            Main.getInstance().getEventHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The event schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete event schedule file.");
        }
    }

    @Command(names = {"KOTHSchedule load"}, permission = "op")
    public static void load(CommandSender sender) {
        Main.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(ChatColor.GREEN + "The event schedule has been loaded.");
    }

    @Command(names = {"KOTHSchedule debug"}, permission = "op")
    public static void kothScheduleDebug(CommandSender sender) {
        Main.getInstance().getEventHandler().fillSchedule();
        sender.sendMessage(ChatColor.GREEN + "The event schedule has been filled.");
    }
}
