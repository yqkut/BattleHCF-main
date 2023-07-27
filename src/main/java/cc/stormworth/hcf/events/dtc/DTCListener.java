package cc.stormworth.hcf.events.dtc;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DTCListener implements Listener {

    public DTCListener() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST) // so people don't get their chat spammed
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Location location = event.getBlock().getLocation();
        if (!DTRBitmask.DTC.appliesAt(location)) {
            return;
        }

        DTC activeDTC = (DTC) Main.getInstance().getEventHandler().getEvents().stream().filter(Event::isActive).filter(ev -> ev.getType() == EventType.DTC).findFirst().orElse(null);

        if (activeDTC == null) {
            return;
        }

        if (event.getBlock().getType() == Material.OBSIDIAN) {
            event.setCancelled(true);
            activeDTC.blockBroken(event.getPlayer());
        }
    }
}