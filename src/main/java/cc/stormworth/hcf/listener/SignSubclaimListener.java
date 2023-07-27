package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SignSubclaimListener implements Listener {

  public static final Set<BlockFace> OUTSIDE_FACES;
  public static final String SUBCLAIM_IDENTIFIER;
  public static final String NO_ACCESS;

  static {
    OUTSIDE_FACES = ImmutableSet.of(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
        BlockFace.SOUTH);
    SUBCLAIM_IDENTIFIER = ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Subclaim]";
    NO_ACCESS = ChatColor.YELLOW + "You don't have access to this chest subclaim!";
  }

  private final int maximumsubclaims = 20;

  public SignSubclaimListener() {
    Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
  }

  public static Set<Sign> subclaimSigns(final Block check) {
    final Set<Sign> signs = new HashSet<Sign>();
    for (final BlockFace blockFace : SignSubclaimListener.OUTSIDE_FACES) {
      final Block relBlock = check.getRelative(blockFace);
      if (relBlock.getType() == check.getType()) {
        subclaimSigns0(signs, relBlock);
      }
    }
    subclaimSigns0(signs, check);
    return signs;
  }

  public static void subclaimSigns0(final Set<Sign> signs, final Block check) {
    for (final BlockFace blockFace : SignSubclaimListener.OUTSIDE_FACES) {
      final Block relBlock = check.getRelative(blockFace);
      if (relBlock.getType() == Material.WALL_SIGN || relBlock.getType() == Material.SIGN) {
        final Sign sign = (Sign) relBlock.getState();
        if (!sign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_IDENTIFIER)) {
          continue;
        }
        signs.add(sign);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onSignUpdate(final SignChangeEvent event) {

    if (!(event.getBlock().getState() instanceof Sign)) {
      return;
    }

    if (!event.getLine(0).toLowerCase().contains("subclaim")) {
      return;
    }

    final Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

    final Sign sign = (Sign) event.getBlock().getState();
    if (playerTeam == null) {
      event.getBlock().breakNaturally();
      event.getPlayer().sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }

    if (!playerTeam.ownsLocation(sign.getLocation())) {
      event.getBlock().breakNaturally();
      event.getPlayer().sendMessage(ChatColor.RED + "You don't own this land!");
      return;
    }

    final BlockFace attachedFace = ((org.bukkit.material.Sign) sign.getData()).getAttachedFace();
    final Block attachedTo = event.getBlock().getRelative(attachedFace);
    
    if (!attachedTo.getType().equals(Material.CHEST) && !attachedTo.getType()
        .equals(Material.TRAPPED_CHEST)) {
      event.getPlayer().sendMessage("§cSign subclaims only work on chests.");
      return;
    }
    if (subclaimSigns(attachedTo).size() != 0) {
      event.getBlock().breakNaturally();
      event.getPlayer().sendMessage(ChatColor.RED + "This chest is already subclaimed!");
      return;
    }
    boolean found = false;
    for (int i = 1; i <= 3; ++i) {
      if (sign.getLine(i) != null && sign.getLine(i)
          .equalsIgnoreCase(event.getPlayer().getName())) {
        found = true;
        break;
      }
    }
    if (!found && event.getPlayer().getName().length() > 15) {
      event.getBlock().breakNaturally();
      event.getPlayer().sendMessage(
          "§cYour name is too long for sign subclaims. Consider changing your username.");
      return;
    }
    if (Main.getInstance().getMapHandler().isKitMap()
        && playerTeam.getSubclaims() >= maximumsubclaims) {
      event.getPlayer().sendMessage(
          CC.RED + "Your team already have the limit of " + maximumsubclaims + " subclaims.");
      return;
    }
    final String signText = event.getLine(1) + event.getLine(2) + event.getLine(3);
    if (signText.isEmpty()) {
      event.getPlayer()
          .sendMessage(ChatColor.GREEN + "We automatically added you to this subclaim.");
      event.setLine(1, event.getPlayer().getName());
    }
    event.setLine(0, SignSubclaimListener.SUBCLAIM_IDENTIFIER);
    event.getPlayer().sendMessage(ChatColor.GREEN + "Sign subclaim created!");
    if (Main.getInstance().getMapHandler().isKitMap()) {
      playerTeam.addSubclaim();
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreakSign(final BlockBreakEvent event) {
    if (!(event.getBlock().getState() instanceof Sign)) {
      return;
    }
    final Team owningTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
    if (Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getBlock().getLocation()) || Main.getInstance()
        .getServerHandler().isAdminOverride(event.getPlayer())) {
      if (Main.getInstance().getMapHandler().isKitMap() && owningTeam != null) {
        owningTeam.removeSubclaim();
      }
      return;
    }
    final Sign sign = (Sign) event.getBlock().getState();
    final UUID uuid = event.getPlayer().getUniqueId();
    if (sign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_IDENTIFIER)) {
      boolean canAccess =
          owningTeam.isOwner(uuid) || owningTeam.isCoLeader(uuid) || owningTeam.isCaptain(
              uuid);
      for (int i = 0; i <= 3; ++i) {
        if (sign.getLine(i) != null && sign.getLine(i)
            .equalsIgnoreCase(event.getPlayer().getName())) {
          canAccess = true;
          break;
        }
      }
      if (!canAccess) {
        event.getPlayer().sendMessage(SignSubclaimListener.NO_ACCESS);
        event.setCancelled(true);
      } else {
        if (Main.getInstance().getMapHandler().isKitMap()) {
          event.getPlayer().sendMessage(
              CC.YELLOW + "Your team now have " + (maximumsubclaims
                  - owningTeam.getSubclaims()) + " subclaims remaining.");
          owningTeam.removeSubclaim();
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !(event.getClickedBlock()
        .getState() instanceof Chest) || Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || Main.getInstance()
        .getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }
    final Team owningTeam = LandBoard.getInstance()
        .getTeam(event.getClickedBlock().getLocation());
    final UUID uuid = event.getPlayer().getUniqueId();
    for (final Sign sign : subclaimSigns(event.getClickedBlock())) {
      boolean canAccess =
          owningTeam.isOwner(uuid) || owningTeam.isCoLeader(uuid) || owningTeam.isCaptain(
              uuid);
      for (int i = 0; i <= 3; ++i) {
        if (sign.getLine(i) != null && sign.getLine(i)
            .equalsIgnoreCase(event.getPlayer().getName())) {
          canAccess = true;
          break;
        }
      }
      if (!canAccess) {
        event.getPlayer().sendMessage(SignSubclaimListener.NO_ACCESS);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreakChest(final BlockBreakEvent event) {

    if (event.getBlock().getType() != Material.CHEST && event.getBlock().getType() != Material.TRAPPED_CHEST) {
      return;
    }

    Team owningTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

    if (Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getBlock().getLocation()) ||
            Main.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
      if (Main.getInstance().getMapHandler().isKitMap() && owningTeam != null) {
        owningTeam.removeSubclaim();
      }
      return;
    }

    UUID uuid = event.getPlayer().getUniqueId();

    for (final Sign sign : subclaimSigns(event.getBlock())) {
      if (!owningTeam.isOwner(uuid) && !owningTeam.isCoLeader(uuid) && !owningTeam.isCaptain(uuid)) {
        event.getPlayer().sendMessage(SignSubclaimListener.NO_ACCESS);
        event.setCancelled(true);
        continue;
      }
      if (Main.getInstance().getMapHandler().isKitMap()) {
        event.getPlayer().sendMessage(CC.YELLOW + "Your team now have " + (maximumsubclaims
            - owningTeam.getSubclaims()) + " subclaims remaining.");
        owningTeam.removeSubclaim();
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPlace(final BlockPlaceEvent event) {
    if (event.getBlockPlaced().getRelative(BlockFace.UP) == null) {
      return;
    }
    if (!(event.getBlockPlaced().getRelative(BlockFace.UP).getState() instanceof Chest)) {
      return;
    }
    if (event.getBlock().getType() != Material.HOPPER) {
      return;
    }
    if (Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getBlock().getLocation()) || Main.getInstance()
        .getServerHandler().isAdminOverride(event.getPlayer())) {
      return;
    }

    if (subclaimSigns(event.getBlockPlaced().getRelative(BlockFace.UP)).size() != 0) {
      event.getPlayer().sendMessage(SignSubclaimListener.NO_ACCESS);
      event.setCancelled(true);
    }
  }
}