package cc.stormworth.hcf.team.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.PlayerUtils;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.region.glowmtn.GlowHandler;
import cc.stormworth.hcf.events.region.nether.NetherArea;
import cc.stormworth.hcf.events.region.nether.NetherHandler;
import cc.stormworth.hcf.events.region.oremountain.OreMountainHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.team.event.PlayerBuildInOthersClaimEvent;
import cc.stormworth.hcf.team.event.PlayerRaidTeamEvent;
import cc.stormworth.hcf.team.event.TeamRaidEvent;
import cc.stormworth.hcf.team.event.TeamUnRaidEvent;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Set;
import java.util.UUID;

public class TeamListener implements Listener {

  public static final Set<Material> ONLY_INCLAIM = ImmutableSet.of(Material.BEACON, Material.HOPPER,
      Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.PISTON_BASE,
      Material.PISTON_STICKY_BASE, Material.BREWING_STAND);
  public static final int HopperLimit = 300;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {

    if (CustomTimerCreateCommand.sotwday) {
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

    if (team != null) {
      team.sendMessage(ChatColor.GREEN + "Member Online: " + ChatColor.BOLD + event.getPlayer().getName());
      team.sendAllyMessage(Main.getInstance().getMapHandler().getAllyRelationColor() + "Member Online: " + ChatColor.BOLD + event.getPlayer().getName());

      if (team.getMembers().size() == Main.getInstance().getMapHandler().getTeamSize()) {
        team.claimReward();
      }

      TeamTrackerManager.logAsync(team, TeamActionType.MEMBER_CONNECTED, ImmutableMap.of(
              "playerId", event.getPlayer().getUniqueId().toString(),
              "date", System.currentTimeMillis()
      ));
    }

  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockFromTo(BlockFromToEvent event) {
    if (!event.getBlock().isLiquid()) {
      return;
    }

    Team factionAt = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
    Team factionTo = LandBoard.getInstance().getTeam(event.getToBlock().getLocation());

    if (factionTo == null && factionAt == null) {
      return;
    }

    if (factionTo != null && factionAt == factionTo) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    if (CustomTimerCreateCommand.sotwday) {
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

    if (team != null) {
      team.sendMessage(ChatColor.RED + "Member Offline: " + ChatColor.BOLD + event.getPlayer().getName());

      team.sendAllyMessage(Main.getInstance().getMapHandler().getAllyRelationColor() + "Ally Offline: " + ChatColor.BOLD + event.getPlayer().getName());
    }

    TeamTrackerManager.logAsync(team, TeamActionType.MEMBER_DISCONNECTED, ImmutableMap.of(
            "playerId", event.getPlayer().getUniqueId().toString(),
            "date", System.currentTimeMillis()
    ));
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockIgnite(final BlockIgniteEvent event) {

    if (event.getPlayer() != null && Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }

    if (Main.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
      return;
    }

    if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
      Team owner = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

      if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && owner.isMember(
          event.getPlayer().getUniqueId())) {
        return;
      }

      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(final BlockPlaceEvent event) {
    Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

    if (event.getPlayer().getGameMode() != GameMode.CREATIVE &&
        ONLY_INCLAIM.contains(event.getBlock().getType()) &&
        (team == null || !team.isMember(event.getPlayer().getUniqueId()))) {

      if (event.getBlock().hasMetadata("trap")) {
        return;
      }

      if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
        event.getPlayer()
            .sendMessage(CC.RED + "You can only place this block into your claim!");
      }

      event.setCancelled(true);
      return;
    }

    if (team != null && team.hasDTRBitmask(DTRBitmask.RESTRICTED_EVENT)) {
      if (Main.getInstance().getEventHandler().getEvent("citadel") != null &&
          !Main.getInstance().getEventHandler().getEvent("citadel").isActive() &&
          Main.getInstance().getConquestHandler().getGame() == null) {
        return;
      }

    }
    if (Main.getInstance().getMapHandler().isKitMap()
        && event.getBlock().getType() == Material.HOPPER) {
      Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) == playerTeam
          && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
        if (playerTeam.getHoppers() >= HopperLimit) {
          if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
            event.getPlayer().sendMessage(
                CC.RED + "Your faction already have the limit of " + HopperLimit + " hoppers.");
          }
          event.setCancelled(true);
          return;
        }
        playerTeam.addHopper();
      }
    }
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
        || Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getBlock().getLocation())) {
      return;
    }
        /*if (AbilityManager.getInstance().getAbilityItemByType(AbilityType.RADIUSANTITRAP).isEnabled() && team.getOwner() != null && event.getItemInHand() != null && event.getBlockPlaced().getType() == Material.JACK_O_LANTERN && event.getPlayer().getItemInHand().isSimilar(AbilityManager.getInstance().getAbilityItemByType(AbilityType.RADIUSANTITRAP).getItem())) {
            return;
        }*/
    if (!team.isMember(event.getPlayer().getUniqueId())) {
      if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
        if (event.getBlock().hasMetadata("trap")) {
          return;
        }
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
          event.getPlayer().sendMessage(
              ChatColor.YELLOW + "You cannot build in " + team.getName(event.getPlayer())
                  + ChatColor.YELLOW + "'s territory!");
        }
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(final BlockBreakEvent event) {
    final Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
        || Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getBlock().getLocation())) {
      if (team != null && Main.getInstance().getMapHandler().isKitMap()
          && event.getBlock().getType() == Material.HOPPER) {
        team.removeHopper();
      }
      return;
    }
    if (Main.getInstance().getGlowHandler() != null && team != null && Main.getInstance()
        .getGlowHandler().hasGlowMountain() && event.getBlock().getType() == Material.GLOWSTONE
        && team.getName().equals(GlowHandler.getGlowTeamName())) {
      return;
    }
    if (!Main.getInstance().getMapHandler().isKitMap() && team != null && event.getBlock().getType()
        .name().contains("LOG") && team.getName().equals("Forest")
        && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType()
        .name().contains("_AXE")) {

      if (event.getBlock().getRelative(BlockFace.UP) != null && event.getBlock()
          .getRelative(BlockFace.UP).getType().name().contains("RAIL")) {
        event.setCancelled(true);
        return;
      }
      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 2);
      Location location = event.getBlock().getLocation();
      Material material = event.getBlock().getType();
      byte data = event.getBlock().getData();
      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
        location.getBlock().setTypeIdAndData(material.getId(), data, false);
      }, 8 * 20L);
      return;
    }
    if (!Main.getInstance().getMapHandler().isKitMap() && team != null && Main.getInstance()
        .getNetherHandler().hasArea() && NetherArea.types.contains(event.getBlock().getType())
        && team.getName().equals(NetherHandler.getTeamName())) {
      return;
    }

    if (team.hasDTRBitmask(DTRBitmask.RESTRICTED_EVENT)) {
      if (Main.getInstance().getEventHandler().getEvent("citadel") != null && !Main.getInstance()
          .getEventHandler().getEvent("citadel").isActive()
          && Main.getInstance().getConquestHandler().getGame() == null) {
        return;
      }

    }
    if (Main.getInstance().getOreHandler() != null && team != null && Main.getInstance()
        .getOreHandler().hasOreMountain() && event.getBlock().getType().name().contains("ORE")
        && team.getName().equals(OreMountainHandler.getOreTeamName())) {
      return;
    }
    if (team.hasDTRBitmask(DTRBitmask.ROAD) && event.getBlock().getY() <= 40
        && event.getBlock().getWorld().getEnvironment() == World.Environment.NORMAL) {
      return;
    }
    if (team.isMember(event.getPlayer().getUniqueId())) {
      if (Main.getInstance().getMapHandler().isKitMap()
          && event.getBlock().getType() == Material.HOPPER) {
        team.removeHopper();
      }
      return;
    }
    final PlayerBuildInOthersClaimEvent buildEvent = new PlayerBuildInOthersClaimEvent(
        event.getPlayer(), event.getBlock(), team);
    Bukkit.getPluginManager().callEvent(buildEvent);
    if (buildEvent.isWillIgnore()) {
      return;
    }

    if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
      event.getPlayer().sendMessage(
          ChatColor.YELLOW + "You cannot build in the territory of " + team.getName(
              event.getPlayer()) + ChatColor.YELLOW + ".");
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
    if (!event.isSticky()) {
      return;
    }
    final Block retractBlock = event.getRetractLocation().getBlock();
    if (retractBlock.isEmpty() || retractBlock.isLiquid()) {
      return;
    }
    final Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
    final Team targetTeam = LandBoard.getInstance().getTeam(retractBlock.getLocation());
    if (pistonTeam == targetTeam) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
    final Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
    int i = 0;
    for (final Block block : event.getBlocks()) {
      ++i;
      final Block targetBlock = event.getBlock().getRelative(event.getDirection(), i + 1);
      final Team targetTeam = LandBoard.getInstance().getTeam(targetBlock.getLocation());
      if (targetTeam != pistonTeam && targetTeam != null) {
        if (targetTeam.isRaidable()) {
          continue;
        }
        if (!targetBlock.isEmpty() && !targetBlock.isLiquid()) {
          continue;
        }
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHangingPlace(final HangingPlaceEvent event) {
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
        || Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getEntity().getLocation())) {
      return;
    }
    final Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
    if (team.hasDTRBitmask(DTRBitmask.RESTRICTED_EVENT)) {
      if (Main.getInstance().getEventHandler().getEvent("citadel") != null && !Main.getInstance()
          .getEventHandler().getEvent("citadel").isActive()
          && Main.getInstance().getConquestHandler().getGame() == null) {
        return;
      }
    }
    if (!team.isMember(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
    if (!(event.getRemover() instanceof Player) || Main.getInstance().getServerHandler()
        .isAdminOverride((Player) event.getRemover())) {
      return;
    }
    if (Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getEntity().getLocation())) {
      return;
    }
    final Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
    if (team.hasDTRBitmask(DTRBitmask.RESTRICTED_EVENT)) {
      if (Main.getInstance().getEventHandler().getEvent("citadel") != null && !Main.getInstance()
          .getEventHandler().getEvent("citadel").isActive()
          && Main.getInstance().getConquestHandler().getGame() == null) {
        return;
      }
    }
    if (!team.isMember(event.getRemover().getUniqueId())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteractEntityEvent(final PlayerInteractEntityEvent event) {
    if (event.getRightClicked().getType() != EntityType.ITEM_FRAME ||
            (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) && !event.getPlayer().hasMetadata("invisible"))) {
      return;
    }

    if (Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getRightClicked().getLocation())) {
      return;
    }

    Team team = LandBoard.getInstance().getTeam(event.getRightClicked().getLocation());

    if (!team.isMember(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
    if (event.getEntity().getType() != EntityType.ITEM_FRAME) {
      return;
    }
    Player damager = null;
    if (event.getDamager() instanceof Player) {
      damager = (Player) event.getDamager();
    } else if (event.getDamager() instanceof Projectile) {
      final Projectile projectile = (Projectile) event.getDamager();
      if (projectile.getShooter() instanceof Player) {
        damager = (Player) projectile.getShooter();
      }
    }
    if (damager == null || Main.getInstance().getServerHandler().isAdminOverride(damager)
        || Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getEntity().getLocation())) {
      return;
    }

    Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

    if (!team.isMember(event.getDamager().getUniqueId())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByEntity2(final EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player damager = PlayerUtils.getDamageSource(event.getDamager());
    if (damager != null) {

      if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {

        Team team = Main.getInstance().getTeamHandler().getTeam(damager);
        Player victim = (Player) event.getEntity();

        if (team != null && team.isMember(victim.getUniqueId())) {
          if (!team.isFriendlyFire()) {
            
            if (event.getDamager() instanceof Arrow) {
              event.getDamager().remove();
            }

            damager.sendMessage(ChatColor.YELLOW + "You cannot hurt " + Main.getInstance().getMapHandler().getTeamRelationColor()
                    + victim.getName() + ChatColor.YELLOW + ".");

            event.setCancelled(true);
          }
        } else if (team != null && team.isAlly(victim.getUniqueId())) {
          damager.sendMessage(ChatColor.YELLOW + "Be careful, that's your ally " + Main.getInstance().getMapHandler().getAllyRelationColor()
                      + victim.getName() + ChatColor.YELLOW + ".");
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityHorseDamage(final EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Horse)) {
      return;
    }
    final Player damager = PlayerUtils.getDamageSource(event.getDamager());
    final Horse victim = (Horse) event.getEntity();
    if (damager != null && victim.isTamed()) {
      final Team damagerTeam = Main.getInstance().getTeamHandler().getTeam(damager);
      final UUID horseOwner = victim.getOwner().getUniqueId();
      if (!damager.getUniqueId().equals(horseOwner) && damagerTeam != null && damagerTeam.isMember(
          horseOwner)) {
        event.setCancelled(true);
        damager.sendMessage(
            ChatColor.YELLOW + "This horse belongs to " + Main.getInstance().getMapHandler()
                .getTeamRelationColor() + UUIDUtils.name(horseOwner) + ChatColor.YELLOW
                + " who is in your faction.");
      }
    }
  }

  @EventHandler
  public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
    final Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace())
        .getLocation();
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }
    final Team owner = LandBoard.getInstance().getTeam(checkLocation);
    if ((owner == null || !owner.isMember(event.getPlayer().getUniqueId()))
        && event.getBucket() == Material.LAVA_BUCKET) {
      event.getPlayer().sendMessage(CC.RED + "You can only empty buckets into your claim!");
      event.setCancelled(true);
      return;
    }
    if (owner != null) {
      if (!owner.isMember(event.getPlayer().getUniqueId())) {
        event.setCancelled(true);
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
          event.getPlayer().sendMessage(
              ChatColor.YELLOW + "You cannot build in the territory of " + owner.getName(
                  event.getPlayer()) + ChatColor.YELLOW + ".");
        }
      }
    }
  }

  @EventHandler
  public void onBucketFill(final PlayerBucketFillEvent event) {
    final Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace())
        .getLocation();
    if (Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
        || Main.getInstance().getServerHandler().isUnclaimedOrRaidable(checkLocation)) {
      return;
    }
    final Team owner = LandBoard.getInstance().getTeam(checkLocation);
    if (!owner.isMember(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
        event.getPlayer().sendMessage(
            ChatColor.YELLOW + "You cannot build in the territory of " + owner.getName(
                event.getPlayer()) + ChatColor.YELLOW + ".");
      }
    }
  }

  @EventHandler
  public void onPlayerRaidTeam(PlayerRaidTeamEvent event) {
    Player player = event.getPlayer();

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team == null) {
      return;
    }

    team.addRaids(1);
    team.sendMessage(CC.translate(
        " &6» " + player.getName() + " &ehas made &6" + event.getRaidTeam().getName()
            + " &eraidable."));


    Team raidTeam = event.getRaidTeam();


    if (raidTeam == null) {
      return;
    }

    raidTeam.removeAllEffects();

    raidTeam.sendMessage("&7(&cRaid&7) &eYour faction has lost your &6&lperks &eupgrades.");

    if (Main.getInstance().getServerHandler().isPreEOTW() || Main.getInstance().getServerHandler().isEOTW()) return;

    if (raidTeam.getPoints() < 50) {
      return;
    }

    team.addPoints(raidTeam.getPoints() / 2);

    raidTeam.removePoints(Math.max(raidTeam.getPoints() / 2, 0));

    team.sendMessage(
        CC.translate("&aYou Team has raided &e" + raidTeam.getName() + " &aand gained &e"
            + raidTeam.getPoints() + " &aPoints!"));
  }

  @EventHandler
  public void onTeamRaid(TeamRaidEvent event) {
    Team team = event.getTeam();


    if (Main.getInstance().getServerHandler().isPreEOTW() || Main.getInstance().getServerHandler().isEOTW()) return;

    if (team.getHQ() == null) {
      return;
    }

    Location location = team.getHQ();

    team.removeAllEffects();

    Block block = location.getBlock();

    TaskUtil.run(Main.getInstance(), () -> {
      block.setType(Material.BEACON);
      block.setMetadata("raid", new FixedMetadataValue(Main.getInstance(), true));

      Hologram hologram = Holograms.newHologram()
              .at(block.getLocation().add(0.5, 0.8, 0.5))
              .addLines("&eHits Left:&6 30")
              .updates()
              .onUpdate(hologram1 -> {
                if (block.hasMetadata("claimed")){
                  hologram1.setLine(0, "&6&lGems Block &eclaimed");
                } else if (block.hasMetadata("hits")){
                  int hits = 30 - block.getMetadata("hits").get(0).asInt();
                  hologram1.setLine(0, "&eHits left:&6 " + hits);
                }
              })
              .build();


      hologram.send();

      team.setRaidHologram(hologram);

      team.setRaidBlock(block.getLocation());
    });
  }

  @EventHandler
  public void onTeamUnRaid(TeamUnRaidEvent event) {
    Team team = event.getTeam();

    if (team.getHQ() == null) {
      return;
    }

    Location location = team.getHQ();

    TaskUtil.run(Main.getInstance(), () -> {

      Block block = location.getBlock();

      if (block.getType() == Material.BEACON) {
        block.setType(Material.AIR);
        block.removeMetadata("raid", Main.getInstance());
        block.removeMetadata("claimed", Main.getInstance());

        if (team.getRaidHologram() != null){
            team.getRaidHologram().destroy();
            team.setRaidHologram(null);
            team.setRaidBlock(null);
        }

      }
    });
  }


  @EventHandler
  public void onBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();

    if (event.getBlock().hasMetadata("raid")) {
      event.setCancelled(true);
      player.sendMessage(CC.translate("&cYou cannot break &6&lGems Block&c!"));
    }
  }

  @EventHandler
  public void onTeamRaid(PlayerInteractEvent event) {

    Player player = event.getPlayer();

    if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }

    if (event.getClickedBlock().getType() != Material.BEACON) {
      return;
    }

    if (!event.getClickedBlock().hasMetadata("raid")) {
      return;
    }

    if (event.getClickedBlock().hasMetadata("claimed")) {
      player.sendMessage(CC.translate("&cThis &6&lGems Block &chas already been claimed!"));
      return;
    }

    Location location = event.getClickedBlock().getLocation();

    Team team = LandBoard.getInstance().getTeam(location);

    if (team == null) {
      return;
    }

    if (!team.isRaidable()) {
      return;
    }

    if (team.isMember(player.getUniqueId())) {
      player.sendMessage(CC.translate("&cYou cannot break your own &6&lGems Block&c!"));
      return;
    }

    Block block = location.getBlock();

    int hits = 0;

    if(block.hasMetadata("hits")){
      hits = block.getMetadata("hits").get(0).asInt();
    }

    event.setCancelled(true);

    if (hits == 30) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gems give " + player.getName() + " 100");
      player.sendMessage(
          CC.translate("&eYou have received &6&l100 Gems &efor breaking the &6&lGems Block&e."));

      block.removeMetadata("hits", Main.getInstance());
      block.setMetadata("claimed", new FixedMetadataValue(Main.getInstance(), true));
    } else {
      int newHits = hits + 1;
      block.setMetadata("hits", new FixedMetadataValue(Main.getInstance(), newHits));
      player.sendMessage(CC.translate("&eHits left: &6" + (30 - newHits)));
    }
  }
}