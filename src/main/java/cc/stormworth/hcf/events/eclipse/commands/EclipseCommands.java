package cc.stormworth.hcf.events.eclipse.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.eclipse.EclipseEvent;
import org.bukkit.entity.Player;
public class EclipseCommands {

    @Command(names = {"eclipse start"}, permission = "op")
    public static void start(Player player){
        EclipseEvent event = Main.getInstance().getEventHandler().getEclipseEvent();

        if(event.isActive()){
            player.sendMessage("§cEclipse is already active!");
            return;
        }

        event.start();
    }

    @Command(names = {"eclipse end", "eclipse stop"}, permission = "op")
    public static void end(Player player){
        EclipseEvent event = Main.getInstance().getEventHandler().getEclipseEvent();

        if(!event.isActive()){
            player.sendMessage("§cEclipse is not active!");
            return;
        }

        event.end();
    }
}
