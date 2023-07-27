package cc.stormworth.hcf.events;

import cc.stormworth.core.util.command.param.ParameterType;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EventParameterType implements ParameterType<Event> {

    public Event transform(CommandSender sender, String source) {
        if (source.equals("active")) {
            for (Event event : Main.getInstance().getEventHandler().getEvents()) {
                if (event.isActive() && !event.isHidden()) {
                    return event;
                }
            }

            sender.sendMessage(ChatColor.RED + "There is no active Event at the moment.");

            return null;
        }

        Event event = Main.getInstance().getEventHandler().getEvent(source);

        if (event == null) {
            sender.sendMessage(ChatColor.RED + "No Event with the name " + source + " found.");
            return (null);
        }

        return (event);
    }

}