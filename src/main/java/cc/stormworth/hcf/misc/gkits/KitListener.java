package cc.stormworth.hcf.misc.gkits;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.gkits.event.KitApplyEvent;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KitListener implements Listener {

  private final Set<EntityType> disabledEntities = ImmutableSet.of(
      EntityType.BAT,
      EntityType.CHICKEN,
      EntityType.PIG_ZOMBIE,
      EntityType.GHAST,
      EntityType.GIANT,
      EntityType.HORSE,
      EntityType.IRON_GOLEM,
      EntityType.SNOWMAN,
      EntityType.MUSHROOM_COW,
      EntityType.SHEEP,
      EntityType.SQUID,
      EntityType.WITCH,
      EntityType.MINECART,
      EntityType.MINECART_CHEST,
      EntityType.MINECART_COMMAND,
      EntityType.MINECART_FURNACE,
      EntityType.MINECART_HOPPER,
      EntityType.MINECART_MOB_SPAWNER,
      EntityType.MINECART_TNT
  );

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onEntitySpawn(CreatureSpawnEvent event) {

    if (event.getSpawnReason() == SpawnReason.CUSTOM) {
      return;
    }

    if (!Main.getInstance().getMapHandler().isKitMap()) {
      return;
    }

    if (this.disabledEntities.contains(event.getEntity().getType())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntitySpawn(VehicleCreateEvent event) {
    if (this.disabledEntities.contains(event.getVehicle().getType())) {
      event.getVehicle().remove();
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onKitApply(final KitApplyEvent event) {
    if (event.isForce()) {
      return;
    }
    final Player player = event.getPlayer();
    final Kit kit = event.getKit();
    if (SpawnTagHandler.isTagged(player) && kit.getMinPlaytimeMillis() == 1000) {
      player.sendMessage(CC.RED + "You cannot pick free kits while combat tagged.");
      event.setCancelled(true);
      return;
    }
    if (!player.isOp() && !kit.isEnabled()) {
      event.setCancelled(true);
      player.sendMessage(
          ChatColor.RED + "The " + kit.getDisplayName() + " kit is currently disabled.");
      return;
    }

    if (Main.getInstance().getConquestHandler().getGame() != null) {
      if (SpawnTagHandler.isTagged(player)) {
        player.sendMessage(
            CC.translate("You cannot use this gkit in combat while the conquest is active."));
        event.setCancelled(true);
        return;
      }

      if (DTRBitmask.CONQUEST.appliesAt(player.getLocation())) {
        player.sendMessage(CC.translate("&cCannot use this gkit in the conquest zone."));
      }
    }

    if (!kit.canUse(player)) {
      event.setCancelled(true);
      player.sendMessage(CC.translate(""));

      if (kit.getMinPlaytimeMillis() == 0) {
        player.sendMessage(
            CC.translate("&cPurchase kit " + kit.getName() + " at &e&ostore.battle.rip"));
      } else {
        player.sendMessage(CC.translate("&cYou must have minimum " + ScoreFunction.TIME_FANCY.apply((float) TimeUnit.MILLISECONDS.toSeconds(kit.getMinPlaytimeMillis()))
                + " of playtime to use " + kit.getName() + " kit."));
      }

      player.sendMessage(CC.translate(""));
      return;
    }

    HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());

    if(profile == null){
      return;
    }

    if (!profile.canUseKit(kit)) {
      if (kit.getMaxUses() > 0 && profile.getKitUses().getOrDefault(kit.getName(), 0) >= kit.getMaxUses()) {
        player.sendMessage(CC.translate("&cYou have used this kit " + profile.getKitUses().get(kit.getName()) + " out of " + kit.getMaxUses() + "."));
        event.setCancelled(true);
        return;
      }
      if (kit.getMinPlaytimeMillis() != 0) {
        int minPlaytime = (int) TimeUnit.MILLISECONDS.toSeconds(kit.getMinPlaytimeMillis());

        int playtimeTime = (int) TimeUnit.MILLISECONDS.toSeconds(profile.getTotalPlayTime());

        if (playtimeTime < minPlaytime) {
          player.sendMessage(CC.RED + "You must have minimum " + ScoreFunction.TIME_FANCY.apply((float) minPlaytime) + " of playtime to use this kit.");
          player.sendMessage(CC.YELLOW + "Your playtime is " + CC.LIGHT_PURPLE + ScoreFunction.TIME_FANCY.apply((float) playtimeTime) + CC.YELLOW + ".");
          event.setCancelled(true);
          return;
        }
      }

      long remaining = profile.getRemainingKitCooldown(kit);
      long millisLeft = remaining - System.currentTimeMillis();

      String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
      player.sendMessage(
          ChatColor.RED + "You cannot use the " + kit.getDisplayName() + " kit for " + msg
              + '.');
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKitApplyMonitor(final KitApplyEvent event) {
    if (!event.isForce()) {
      Kit kit = event.getKit();
      HCFProfile profile = HCFProfile.getByUUID(event.getPlayer().getUniqueId());

      if (kit.getMaxUses() > 0) {
        profile.getKitUses().put(kit.getName(), profile.getKitUses(kit) + 1);
      }
      profile.getKitCooldowns()
          .put(kit.getName(), System.currentTimeMillis() + kit.getDelayMillis());
    }
  }
}