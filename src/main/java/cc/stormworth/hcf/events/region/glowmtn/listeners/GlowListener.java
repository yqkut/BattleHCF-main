package cc.stormworth.hcf.events.region.glowmtn.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.region.glowmtn.GlowHandler;
import cc.stormworth.hcf.events.region.glowmtn.GlowMountain;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GlowListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if(location.getWorld().getEnvironment() != World.Environment.NETHER){
            return;
        }

        GlowHandler glowHandler = Main.getInstance().getGlowHandler();
        GlowMountain glowMountain = glowHandler.getGlowMountain();
        Team teamAt = LandBoard.getInstance().getTeam(location);

        // If its unclaimed, or the server doesn't even have a mountain, or not even glowstone, why continue?
        if (Main.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || !glowHandler.hasGlowMountain() || event.getBlock().getType() != Material.GLOWSTONE) {
            return;
        }

        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals(GlowHandler.getGlowTeamName())) {
            return;
        }

        if (!glowMountain.getGlowstone().contains(location.toVector().toBlockVector())) {
            return;
        }

        // Right, we can break this glowstone block, lets do it.
        event.setCancelled(false);

        // Now, we will decrease the value of the remaining glowstone
        glowMountain.setRemaining(glowMountain.getRemaining() - 1);
        Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer().getUniqueId());
        if (team != null && CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lGlowstone")) {
            //team.setGlowstoneMined(team.getGlowstoneMined() + 1);
        }

        final double total = glowMountain.getGlowstone().size();
        final double remaining = glowMountain.getRemaining();
        if (total / 1.5 == remaining) {
            Bukkit.broadcastMessage(CC.translate("&6[Glowstone Mountain] &ahas been &l25% &amined."));
        } else if (total / 2.0 == remaining) {
            Bukkit.broadcastMessage(CC.translate("&6[Glowstone Mountain] &ahas been &l50% &amined."));
        } else if (total / 3.0 == remaining) {
            Bukkit.broadcastMessage(CC.translate("&6[Glowstone Mountain] &ahas been &l75% &amined."));
        } else if (remaining == 0.0) {
            Bukkit.broadcastMessage(CC.translate("&6[Glowstone Mountain] &aAll glowstone has just been mined."));
            if (CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lGlowstone")) {
                glowMountain.reset();
            }
        }
    }
}