package cc.stormworth.hcf.server;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;

public class EnderpearlCooldownHandler implements Listener {

  public void putCooldown(ProjectileLaunchEvent event) {
    if (!(event.getEntity().getShooter() instanceof Player)) {
      return;
    }
    final Player shooter = (Player) event.getEntity().getShooter();
    if (event.getEntity() instanceof EnderPearl) {
      shooter.setMetadata("LastEnderPearl",
          new FixedMetadataValue(Main.getInstance(), event.getEntity()));

      final long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(
          event.getEntity().getLocation()) ? TimeUtil.parseTimeLong("30s")
          : TimeUtil.parseTimeLong("16s");

      CooldownManager.addCooldown(shooter.getUniqueId(), CooldownType.ENDERPEARL,
          (int) TimeUnit.MILLISECONDS.toSeconds(timeToApply));

      CooldownAPI.setCooldown(shooter, "enderpearl", timeToApply);
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getItem() == null) {
      return;
    }
    if (event.getItem().getType() != Material.ENDER_PEARL) {
      return;
    }

    Player player = event.getPlayer();

    if (!hasTimer(player)) {
      return;
    }

    HCFProfile profile = HCFProfile.get(player);

    if (!profile.isEnderpearlCooldown()) {
      return;
    }

    player.sendMessage(CC.translate("&c&lYou still have a &6&lEnderpearl &c&lcooldown for " +
            TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "enderpearl"))));
    event.setCancelled(true);
  }

  public void clearTimer(final Player player) {
    CooldownAPI.removeCooldown(player, "enderpearl");
    CooldownManager.removeCooldown(player.getUniqueId(), CooldownType.ENDERPEARL);
  }

  public boolean hasTimer(final Player player) {
    return CooldownAPI.hasCooldown(player, "enderpearl");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onProjectileLaunch(final ProjectileLaunchEvent event) {
    if (!(event.getEntity() instanceof EnderPearl)) {
      return;
    }
    if (!(event.getEntity().getShooter() instanceof Player)) {
      return;
    }
    final Player shooter = (Player) event.getEntity().getShooter();

    if (hasTimer(shooter)) {
      HCFProfile profile = HCFProfile.get(shooter);

      if (!profile.isEnderpearlCooldown()) {
        return;
      }

      shooter.sendMessage(ChatColor.RED + "You have to wait " + TimeUtil.millisToRoundedTime(
          CooldownAPI.getCooldown(shooter, "enderpearl")) +
          " to use ender pearl again");
      event.setCancelled(true);
      return;
    }

    putCooldown(event);
    if (DTRBitmask.NO_ENDERPEARL.appliesAt(shooter.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(shooter.getLocation())) {
      event.setCancelled(true);
      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "resetpearl " + shooter.getName());
      shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));

    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(ProjectileLaunchEvent event) {
    if (!(event.getEntity() instanceof EnderPearl)) {
      return;
    }

    if (!(event.getEntity().getShooter() instanceof Player)) {
      return;
    }

    Player thrower = (Player) event.getEntity().getShooter();

    if (hasTimer(thrower)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
      return;
    } else if (!hasTimer(event.getPlayer())) {
      event.setCancelled(
          true);  // only reason for this would be player died before pearl landed, so cancel it!
      return;
    }

    Location to = event.getTo();
    Location from = event.getFrom();
    Player player = event.getPlayer();

    HCFProfile profile = HCFProfile.get(player);

    if (DTRBitmask.SAFE_ZONE.appliesAt(to)) {

      if(!profile.isDeathBanned()){
        event.setCancelled(true);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        event.getPlayer().updateInventory();
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "resetpearl " + event.getPlayer().getName());
        return;
      }
      if (!DTRBitmask.SAFE_ZONE.appliesAt(from)) {
        if(profile.isDeathBanned()){
          return;
        }

        event.setCancelled(true);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        event.getPlayer().updateInventory();
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
            "resetpearl " + event.getPlayer().getName());
        return;
      }
    }

    if (DTRBitmask.NO_ENDERPEARL.appliesAt(to)) {
      event.setCancelled(true);
      event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
      event.getPlayer().updateInventory();
      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
          "resetpearl " + event.getPlayer().getName());
      return;
    }

    Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

    if (profile.hasPvPTimer() && ownerTo != null) {
      if (ownerTo.isMember(event.getPlayer().getUniqueId())) {
        profile.setPvpTimer(null);
      } else if (ownerTo.getOwner() != null || (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo()))) {
        event.setCancelled(true);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        event.getPlayer().updateInventory();
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "resetpearl " + event.getPlayer().getName());
      }
    }
  }

  @EventHandler
  public void onRefund(final PlayerTeleportEvent event) {
    if (event.isCancelled()
        && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
      final Player player = event.getPlayer();
      if (!player.isOnline()) {
        return;
      }
      final ItemStack inPlayerHand = player.getItemInHand();
      if (inPlayerHand != null && inPlayerHand.getType() == Material.ENDER_PEARL
          && inPlayerHand.getAmount() < 16) {
        inPlayerHand.setAmount(inPlayerHand.getAmount() + 1);
      }
      clearTimer(player);
    }
  }

  public boolean clippingThrough(final Location target, final Location from,
      final double thickness) {
    return (from.getX() > target.getX() && from.getX() - target.getX() < thickness) || (
        target.getX() > from.getX() && target.getX() - from.getX() < thickness) || (
        from.getZ() > target.getZ() && from.getZ() - target.getZ() < thickness) || (
        target.getZ() > from.getZ() && target.getZ() - from.getZ() < thickness);
  }
}