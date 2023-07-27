package cc.stormworth.hcf.events.koth.commands.koth;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDeleteCommand {

    @Command(names = {"KOTH Delete", "events delete", "event delete"}, permission = "op")
    public static void kothDelete(final Player sender, @Param(name = "koth") final Event koth) {
        Main.getInstance().getEventHandler().getEvents().remove(koth);
        Main.getInstance().getEventHandler().saveEvents();
        if (!koth.getName().startsWith("conquest-") && Main.getInstance().getTeamHandler().getTeam(koth.getName()) != null) {
            Main.getInstance().getTeamHandler().getTeam(koth.getName()).disband();
        }
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + ".");
    }
}