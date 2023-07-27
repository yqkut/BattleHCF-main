package cc.stormworth.hcf.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

@AllArgsConstructor
public enum RegionType {

  WARZONE(RegionMoveHandler.ALWAYS_TRUE),
  WILDNERNESS(RegionMoveHandler.ALWAYS_TRUE),
  ROAD(RegionMoveHandler.ALWAYS_TRUE),
  KOTH(RegionMoveHandler.PVP_TIMER),
  CITADEL(RegionMoveHandler.PVP_TIMER),
  CONQUEST(RegionMoveHandler.PVP_TIMER),
  CLAIMED_LAND(RegionMoveHandler.PVP_TIMER),
  END_PORTAL(RegionMoveHandler.PVP_TIMER),
  BUFFER_ZONE(RegionMoveHandler.ALWAYS_TRUE),
  RESTRICTED_ZONE(RegionMoveHandler.ALWAYS_TRUE),
  HUNGER_GAMES(RegionMoveHandler.PVP_TIMER),
  NETHER_ZONE(RegionMoveHandler.ALWAYS_TRUE),
  NETHER(RegionMoveHandler.ALWAYS_TRUE),
  SAND_ZONE(RegionMoveHandler.ALWAYS_TRUE),
  SAND(RegionMoveHandler.ALWAYS_TRUE),
  FOREST(RegionMoveHandler.ALWAYS_TRUE),
  SPAWN(event -> {
    if (SpawnTagHandler.isTagged(event.getPlayer())
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {

      event.getPlayer().setVelocity(event.getPlayer().getWorld().getSpawnLocation().toVector()
          .subtract(event.getPlayer().getLocation().toVector()).normalize().multiply(-0.15));

      event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter spawn while spawn-tagged.");
      event.setTo(event.getFrom());
      return (false);
    }
    if (!event.getPlayer().isDead()) {
      event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
      event.getPlayer().setFoodLevel(20);
    }
    return (true);
  });

  @Getter
  private RegionMoveHandler moveHandler;
}