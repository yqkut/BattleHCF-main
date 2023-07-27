package cc.stormworth.hcf.events.region.oremountain.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.region.oremountain.OreMountain;
import cc.stormworth.hcf.events.region.oremountain.OreMountainHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OreListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        OreMountainHandler oreHandler = Main.getInstance().getOreHandler();
        OreMountain oreMountain = oreHandler.getOreMountain();
        Team teamAt = LandBoard.getInstance().getTeam(location);

        // If its unclaimed, or the server doesn't even have a mountain, or not even ore, why continue?
        if (Main.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || !oreHandler.hasOreMountain() || event.getBlock().getType().name().contains("ORE")) {
            return;
        }

        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals(OreMountainHandler.getOreTeamName())) {
            return;
        }

        if (!oreMountain.getOres().contains(location.toVector().toBlockVector())) {
            return;
        }

        // Right, we can break this ore block, lets do it.
        event.setCancelled(false);

        // Now, we will decrease the value of the remaining ore
        oreMountain.setRemaining(oreMountain.getRemaining() - 1);
        /*Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer().getUniqueId());
        if (team != null && CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lDouble Ores")) {
            team.setGlowstoneMined(team.getGlowstoneMined() + 1);
        }*/

        final double total = oreMountain.getOres().size();
        final double remaining = oreMountain.getRemaining();
        if (total / 1.5 == remaining) {
            Bukkit.broadcastMessage(CC.translate("&6[Ore Mountain] &ahas been &l25% &amined."));
        } else if (total / 2.0 == remaining) {
            Bukkit.broadcastMessage(CC.translate("&6[Ore Mountain] &ahas been &l50% &amined."));
        } else if (total / 3.0 == remaining) {
            Bukkit.broadcastMessage(CC.translate("&6[Ore Mountain] &ahas been &l75% &amined."));
        } else if (remaining == 0.0) {
            Bukkit.broadcastMessage(CC.translate("&6[Ore Mountain] &aAll ores has just been mined."));
            if (CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lDouble Ores")) {
                oreMountain.reset();
            }
        }
    }
}