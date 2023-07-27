package cc.stormworth.hcf.events.citadel.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.citadel.CitadelHandler;
import cc.stormworth.hcf.team.Team;
import com.google.common.base.Joiner;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CitadelCommand {

    @Command(names = {"citadel"}, permission = "")
    public static void citadel(Player sender) {
        Set<ObjectId> cappers = Main.getInstance().getCitadelHandler().getCappers();
        Set<String> capperNames = new HashSet<>();

        for (ObjectId capper : cappers) {
            Team capperTeam = Main.getInstance().getTeamHandler().getTeam(capper);

            if (capperTeam != null) {
                capperNames.add(capperTeam.getName());
            }
        }

        if (!capperNames.isEmpty()) {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + Joiner.on(", ").join(capperNames) + ChatColor.YELLOW + ".");
        } else {
            Event citadel = Main.getInstance().getEventHandler().getEvent("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        }
    }
}