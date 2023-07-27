package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.general.PlayerUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SpawnListener implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockIgnite(final BlockIgniteEvent event) {
    if (event.getPlayer() != null && Main.getInstance().getServerHandler()
        .isAdminOverride(event.getPlayer())) {
      return;
    }
    if (event.getBlock().getWorld().getName().equalsIgnoreCase("void")) {
      event.setCancelled(true);
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPlace(final BlockPlaceEvent event) {
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
      event.setCancelled(true);
      if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
        event.getPlayer().sendMessage(
            ChatColor.YELLOW + "You cannot build in the territory of " + ChatColor.GREEN
                + "Spawn"
                + ChatColor.YELLOW + ".");
      }
    } else if (
        Main.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation())
            || Main.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())) {
      if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())
          && event.getItemInHand() != null && event.getItemInHand().getType() == Material.WEB
          && Main.getInstance().getMapHandler().isKitMap()) {
        for (Event playableEvent : Main.getInstance().getEventHandler().getEvents()) {
          if (!playableEvent.isActive() || !(playableEvent instanceof KOTH)) {
            continue;
          }

          KOTH koth = (KOTH) playableEvent;

          if (koth.onCap(event.getBlockPlaced().getLocation())) {
            event.setCancelled(true);
            event.getPlayer()
                .sendMessage(ChatColor.YELLOW + "You can't place web on cap!");
            event.getPlayer().setItemInHand(null);

            event.getPlayer().setMetadata("ImmuneFromGlitchCheck",
                new FixedMetadataValue(Main.getInstance(), new Object()));

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
              event.getPlayer()
                  .removeMetadata("ImmuneFromGlitchCheck", Main.getInstance());
            });

            return;
          }
        }
      } else {
        if (event.getBlock().hasMetadata("trap")) {
          return;
        }
        event.setCancelled(true);
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
          event.getPlayer()
              .sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
        }
      }
    }
  }


  @EventHandler
  public void onItemSpawn(ItemSpawnEvent event){
    Item item = event.getEntity();

    if (CustomTimerCreateCommand.sotwday) {
      item.setTicksLived(500);
    }

  }

  @EventHandler
  public void onItemSpawn(CreatureSpawnEvent event){

    if (CustomTimerCreateCommand.sotwday && !(event.getEntity() instanceof Cow)) {
      if (event.getEntity().getLocation().getWorld().getEnvironment() != World.Environment.THE_END) {
        event.setCancelled(true);
      }
    }

  }


  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreak(final BlockBreakEvent event) {

    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }

    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
      event.setCancelled(true);
      if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
        event.getPlayer().sendMessage(
            ChatColor.YELLOW + "You cannot build in the territory of " + ChatColor.GREEN
                + "Spawn"
                + ChatColor.YELLOW + ".");
      }
    } else if (!DTRBitmask.DTC.appliesAt(event.getBlock().getLocation()) && (
        Main.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation())
            || Main.getInstance().getServerHandler()
            .isNetherBufferZone(event.getBlock().getLocation()))) {

      if (event.getBlock().hasMetadata("trap")) {
        return;
      }

      event.setCancelled(true);
      if (event.getBlock().getType() != Material.LONG_GRASS
          && event.getBlock().getType() != Material.GRASS) {
        if (event.getBlock().getType() == Material.GLOWSTONE) {
          return;
        }
        event.getPlayer()
            .sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHangingPlace(final HangingPlaceEvent event) {
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }
    if (event.getPlayer().getWorld().getName().equalsIgnoreCase("void")
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.setCancelled(true);
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
    if (!(event.getRemover() instanceof Player) || Main.getInstance().getServerHandler()
        .isAdminOverride((Player) event.getRemover())) {
      return;
    }
    if (event.getRemover().getWorld().getName().equalsIgnoreCase("void")
        && ((Player) event.getRemover()).getGameMode() != GameMode.CREATIVE) {
      event.setCancelled(true);
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteractEntityEvent(final PlayerInteractEntityEvent event) {
    if (event.getRightClicked().getType() != EntityType.ITEM_FRAME || Main.getInstance()
        .getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }
    if (event.getPlayer().getName().equalsIgnoreCase("void") && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.setCancelled(true);
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getRightClicked().getLocation())) {
      if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)
        || event.getEntity().getType() != EntityType.ITEM_FRAME || Main.getInstance()
        .getServerHandler().isAdminOverride((Player) event.getDamager())) {
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageEvent event) {
    if (Main.getInstance().getServerHandler().isEOTW()) {
      return;
    }
    if ((event.getEntity() instanceof Player || event.getEntity() instanceof Horse) && DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBucketEmpty(final PlayerBucketEmptyEvent event) {

    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }

    if (Main.getInstance().getServerHandler().isSpawnBufferZone(event.getBlockClicked().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByEntity2(final EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    final Player damager = PlayerUtils.getDamageSource(event.getDamager());
    if (damager != null) {
      final Player victim = (Player) event.getEntity();
      if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())
          || DTRBitmask.SAFE_ZONE.appliesAt(
          damager.getLocation())) {
        event.setCancelled(true);
      }
    }
  }
}