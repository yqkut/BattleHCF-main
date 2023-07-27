package cc.stormworth.hcf.events.region.nether.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.nether.NetherArea;
import cc.stormworth.hcf.events.region.nether.NetherHandler;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class NetherListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if(location.getWorld().getEnvironment() != World.Environment.NETHER){
            return;
        }

        NetherHandler handler = Main.getInstance().getNetherHandler();
        NetherArea area = handler.getArea();
        Team teamAt = LandBoard.getInstance().getTeam(location);

        if (Main.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || !handler.hasArea() || NetherArea.types.contains(event.getBlock().getType())) {
            return;
        }

        if (teamAt == null || !teamAt.getName().equals(NetherHandler.getTeamName())) {
            return;
        }

        if (!area.getBlocks().contains(location.toVector().toBlockVector())) {
            return;
        }

        event.setCancelled(false);
        area.setRemaining(area.getRemaining() - 1);

        final double total = area.getBlocks().size();
        final double remaining = area.getRemaining();

        String message = "";
        if (total / 1.5 == remaining) {
            message = CC.translate("&c[Nether] &ahas been &l25% &amined.");
        } else if (total / 2.0 == remaining) {
            message = CC.translate("&c[Nether] &ahas been &l50% &amined.");
        } else if (total / 3.0 == remaining) {
            message = CC.translate("&c[Nether] &ahas been &l75% &amined.");
        } else if (remaining == 0.0) {
            message = CC.translate("&c[Nether] &aAll nether has just been mined.");
        }

        if (message == "") return;
        Bukkit.getConsoleSender().sendMessage(message);
        String finalMessage = message;
        Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getName().equalsIgnoreCase("void") && !HCFProfile.get(player).isDeathBanned()).forEach(player -> player.sendMessage(finalMessage));
    }
}