package cc.stormworth.hcf.events.citadel.listeners;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.events.EventCapturedEvent;
import cc.stormworth.hcf.team.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitadelListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKOTHCaptured(EventCapturedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

            if (playerTeam != null) {
                Main.getInstance().getCitadelHandler().addCapper(playerTeam.getUniqueId());
                playerTeam.setCitadelsCapped(playerTeam.getCitadelsCapped() + 1);
            }
        }
    }
}