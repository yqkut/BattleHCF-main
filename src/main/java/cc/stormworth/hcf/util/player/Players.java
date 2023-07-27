package cc.stormworth.hcf.util.player;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Players {

  public static boolean isNaked(Player player) {
    return player.getInventory().getHelmet() == null
        && player.getInventory().getChestplate() == null
        && player.getInventory().getLeggings() == null
        && player.getInventory().getBoots() == null;
  }

  public static void reset(Player player, GameMode gameMode, boolean disableFlight) {
    player.getInventory().clear();
    player.getInventory().setHelmet(null);
    player.getInventory().setChestplate(null);
    player.getInventory().setLeggings(null);
    player.getInventory().setBoots(null);
    player.getActivePotionEffects().stream().map(PotionEffect::getType)
        .forEach(player::removePotionEffect);
    player.setGameMode(gameMode);
    player.setMaxHealth(20.0D);
    player.setHealth(20.0D);
    player.setFoodLevel(20);
    player.setLevel(0);
    player.setExp(0.0F);
    player.setSaturation(20.0F);
    player.setFireTicks(0);

    if (disableFlight) {
      player.setAllowFlight(false);
      player.setFlying(false);
    }

    player.updateInventory();
  }

  public static void teleportWithChunkLoad(Player player, Location location) {
    player.teleport(location);
    removeThrownPearls(player);
  }

  public static void removeThrownPearls(final Player player) {
    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
      if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
        enderPearl.remove();
        break;
      }
    }
  }

  public static void playSoundForAll(Sound sound) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.playSound(player.getLocation(), sound, 1, 1);
    }
  }

  public static void denyMovement(Player player) {
    player.setWalkSpeed(0.0F);
    player.setFlySpeed(0.0F);
    player.setFoodLevel(0);
    player.setSprinting(false);
    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
  }

  public static void allowMovement(Player player) {
    player.setWalkSpeed(0.2F);
    player.setFlySpeed(0.1F);
    player.setFoodLevel(20);
    player.setSprinting(true);
    player.removePotionEffect(PotionEffectType.JUMP);
    player.removePotionEffect(PotionEffectType.SLOW);
  }

  public static void hidePlayersInSpawn(Player player, Player toHide) {
    boolean playerSeen = Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.WARRIOR);
    boolean otherSeen = Profile.getByUuid(toHide.getUniqueId()).getRank().isAboveOrEqual(Rank.WARRIOR);

    if (!toHide.equals(player)) {
      if (playerSeen && !CorePlugin.getInstance().getStaffModeManager()
              .hasStaffToggled(player)) {
        toHide.showPlayer(player);
      } else {
        toHide.hidePlayer(player);
      }

      if (DTRBitmask.SAFE_ZONE.appliesAt(toHide.getLocation())) {
        if (otherSeen && !CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
          player.showPlayer(toHide);
        } else {
          player.hidePlayer(toHide);
        }
      }
    }
  }

  public static void showPlayersInSpawn(Player player, Player toShow) {
    if (!toShow.equals(player)) {
      if (!CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
        toShow.showPlayer(player);
      }

      if (DTRBitmask.SAFE_ZONE.appliesAt(toShow.getLocation())) {
        if (!CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
          player.showPlayer(toShow);
        }
      }
    }
  }

    public static void senMessageStaff(String s) {
      for (Player player : Bukkit.getOnlinePlayers()) {
          if (player.isOp()) {
              player.sendMessage(s);
          }
      }
    }

    public static PotionEffect getActivePotionEffect(Player player, PotionEffectType type) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(type)) {
                return effect;
            }
        }
        return null;
    }

    public static boolean hasPotionEffect(Player player, PotionEffectType type) {
        return getActivePotionEffect(player, type) != null;
    }
}