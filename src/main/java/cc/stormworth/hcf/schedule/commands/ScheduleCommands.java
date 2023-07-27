package cc.stormworth.hcf.schedule.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.schedule.menu.ScheduleMenu;
import org.bukkit.entity.Player;

public class ScheduleCommands {

    @Command(names = {"schedule", "schedules"}, permission = "ADMINISTRATOR")
    public static void schedule(Player player){
        new ScheduleMenu(Main.getInstance().getScheduleManager()).open(player);
    }

}
