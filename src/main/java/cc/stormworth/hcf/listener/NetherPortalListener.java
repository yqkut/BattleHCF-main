package cc.stormworth.hcf.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NetherPortalListener implements Listener {

  @EventHandler
  public void onPlayerPortal(final PlayerPortalEvent event) {
    final Player player = event.getPlayer();
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
      return;
    }
    if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL
        && DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom())) {
      event.setCancelled(true);
      player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
    } else if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER
        && DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom())) {
      event.setCancelled(true);
      player.teleport(event.getTo().getWorld().getSpawnLocation());
    }
    final Location to = event.getTo();
    if (DTRBitmask.ROAD.appliesAt(to)) {
      final Team team = LandBoard.getInstance().getTeam(to);
      if (team.getName().contains("North")) {
        to.add(20.0, 0.0, 0.0);
      } else if (team.getName().contains("South")) {
        to.subtract(20.0, 0.0, 0.0);
      } else if (team.getName().contains("East")) {
        to.add(0.0, 0.0, 20.0);
      } else if (team.getName().contains("West")) {
        to.subtract(0.0, 0.0, 20.0);
      }
    }
    event.setTo(to);
  }
}