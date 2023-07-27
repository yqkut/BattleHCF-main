package cc.stormworth.hcf.pvpclasses.pvpclasses;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.deathmessage.DeathMessageHandler;
import cc.stormworth.hcf.deathmessage.trackers.ArrowTracker;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.RestoreEffect;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ArcherClass extends PvPClass {

  public static final String ULTIMATE_TIMER = "ArcherUltimate";
  private static final int MARK_SECONDS = 10;
  private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
  private static final Map<String, Long> lastJumpUsage = new HashMap<>();
  private static final Map<String, Long> markedPlayers = new HashMap<>();
  private static final Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();
  public static PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
  public static PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 7);
  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  public ArcherClass() {
    super("Archer", Arrays.asList(Material.SUGAR, Material.FEATHER), 2);
  }

  public static boolean isMarked(final Player player) {
    return getMarkedPlayers().containsKey(player.getName())
        && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis();
  }

  public static Map<String, Long> getMarkedPlayers() {
    return ArcherClass.markedPlayers;
  }

  public static Map<String, Set<Pair<String, Long>>> getMarkedBy() {
    return ArcherClass.markedBy;
  }

  @Override
  public boolean qualifies(final PlayerInventory armor) {
    return this.wearingAllArmor(armor) && armor.getHelmet().getType() == Material.LEATHER_HELMET
        && armor.getChestplate().getType() == Material.LEATHER_CHESTPLATE
        && armor.getLeggings().getType() == Material.LEATHER_LEGGINGS
        && armor.getBoots().getType() == Material.LEATHER_BOOTS;
  }

  @Override
  public void apply(final Player player) {
    super.apply(player);
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
  }

  @Override
  public void tick(final Player player) {
    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      player.addPotionEffect(
          new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    }
    super.tick(player);
  }

  @Override
  public void remove(final Player player) {
    super.remove(player);

    Map<Effect, RestoreEffect> entry = Main.getInstance().getEffectRestorer().getRestores().row(player.getUniqueId());

    for (Iterator<RestoreEffect> it = entry.values().iterator(); it.hasNext();) {
      RestoreEffect restore = it.next();
      PotionEffect potionEffect = restore.getEffect();

      if (potionEffect.getType().getName().equals("SPEED") && potionEffect.getAmplifier() == 2) {
        it.remove();
      } else if (potionEffect.getType().getName().equals("DAMAGE_RESISTANCE") && potionEffect.getAmplifier() == 1) {
        it.remove();
      }
    }

    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
      if(activePotionEffect.getAmplifier() == 2 && activePotionEffect.getType().equals(PotionEffectType.SPEED)) {
        player.removePotionEffect(activePotionEffect.getType());
      }

      if(activePotionEffect.getAmplifier() == 1 && activePotionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
        player.removePotionEffect(activePotionEffect.getType());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityArrowHit(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
      final Arrow arrow = (Arrow) event.getDamager();
      final Player victim = (Player) event.getEntity();

      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }

      if (arrow.hasMetadata("hunter")) {
        return;
      }

      final Player shooter = (Player) arrow.getShooter();

      if (shooter == victim) {
        return;
      }

      final float pullback = arrow.getMetadata("Pullback").get(0).asFloat();

      if (!PvPClassHandler.hasKitOn(shooter, this)) {
        return;
      }

      if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")
          && !CustomTimerCreateCommand.hasSOTWEnabled(shooter.getUniqueId())) {
        event.setCancelled(true);
        return;
      }

            /*if (Main.getInstance().getMapHandler().isKitMap()) {
                if (PlayerUtils.isNaked(shooter)) {
                    LeatherArmorMeta helmMeta = (LeatherArmorMeta) shooter.getInventory().getHelmet().getItemMeta();
                    LeatherArmorMeta chestMeta = (LeatherArmorMeta) shooter.getInventory().getChestplate().getItemMeta();
                    LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) shooter.getInventory().getLeggings().getItemMeta();
                    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) shooter.getInventory().getBoots().getItemMeta();
                    Color blue = Color.fromRGB(3361970);
                    Color aqua = Color.fromRGB(6724056);
                    if (shooter.getInventory().getHelmet() != null
                            && shooter.getInventory().getChestplate() != null
                            && shooter.getInventory().getLeggings() != null
                            && shooter.getInventory().getBoots() != null) {
                        if ((helmMeta.getColor().equals(aqua)) && (chestMeta.getColor().equals(blue)) && (leggingsMeta.getColor().equals(blue)) && (bootsMeta.getColor().equals(aqua))) {

                            double distance = shooter.getLocation().distance(victim.getLocation());
                            damage = distance < 10 ? 1 :
                                    distance < 20 ? 2 :
                                            distance < 30 ? 3 :
                                                    distance < 40 ? 3.5 :
                                                            distance < 50 ? 4 :
                                                                    distance < 60 ? 4.5 :
                                                                            distance < 70 ? 5.5 : 5.5;
                        } else {
                            if (pullback >= 0.5f) {
                                damage = 4;
                            } else {
                                damage = 1;
                            }
                        }
                    }
                }
            } else {
                if (pullback >= 0.5f) {
                    damage = 4;
                } else {
                    damage = 1;
                }
            }*/

      double[] intArray = {2, 3, 4, 4, 4, 4};

      double randomDamage = intArray[Main.RANDOM.nextInt(intArray.length)];

      double damage = isMarked(victim) ? randomDamage : 2.5; // Ternary for getting damage!

      // If the bow isn't 100% pulled back we do 1 heart no matter what.
      if (pullback < 0.5F) {
        damage = 1; // 1 heart
      }

      if (victim.getHealth() - damage <= 0D) {
        event.setCancelled(true);
      } else {
        event.setDamage(0D);
      }

      if (CooldownAPI.hasCooldown(shooter, "anti_class")) {
        return;
      }

      final Location shotFrom = (Location) arrow.getMetadata("ShotFromDistance").get(0).value();
      final double distance = shotFrom.distance(victim.getLocation());

      DeathMessageHandler.addDamage(victim,
          new ArrowTracker.ArrowDamageByPlayer(victim, damage,
              ((Player) arrow.getShooter()), shotFrom, distance));

      victim.setHealth(FastMath.max(0.0, victim.getHealth() - damage));

      if (PvPClassHandler.hasKitOn(victim, this)) {
        shooter.sendMessage(
            ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " ("
                + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.RED
                + "Cannot mark other Archers. " + ChatColor.BLUE + ChatColor.BOLD + "("
                + formatDamage(damage)
                + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
      } else if (pullback >= 0.5f) {
        shooter.sendMessage(
            ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " ("
                + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.GOLD
                + "Marked player for " + 10 + " seconds. " + ChatColor.BLUE + ChatColor.BOLD + "("
                + formatDamage(damage) + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
        if (!isMarked(victim)) {
          victim.sendMessage(
              ChatColor.RED.toString() + ChatColor.BOLD + "Marked! " + ChatColor.YELLOW
                  + "An archer has shot you and marked you (+20% damage) for " + 10 + " seconds.");
        }
        if (!EOTWCommand.isFfaEnabled()) {
          if ((victim.getInventory().getHelmet() == null || (
              victim.getInventory().getHelmet() != null && !enchantmentsManager
                  .hasEnchantment(victim.getInventory().getHelmet(), CEnchantments.SPECTRAL)))
              || (victim.getInventory().getChestplate() == null || (
              victim.getInventory().getChestplate() != null && !enchantmentsManager
                  .hasEnchantment(victim.getInventory().getChestplate(), CEnchantments.SPECTRAL)))
              || (victim.getInventory().getLeggings() == null || (
              victim.getInventory().getLeggings() != null && !enchantmentsManager
                  .hasEnchantment(victim.getInventory().getLeggings(), CEnchantments.SPECTRAL)))
              || (victim.getInventory().getBoots() == null || (
              victim.getInventory().getBoots() != null && !enchantmentsManager
                  .hasEnchantment(victim.getInventory().getBoots(), CEnchantments.SPECTRAL)))) {
            PotionEffect invis = null;
            for (final PotionEffect potionEffect : victim.getActivePotionEffects()) {
              if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                invis = potionEffect;
                break;
              }
            }
            if (invis != null) {
              final PvPClass playerClass = PvPClassHandler.getPvPClass(victim);
              victim.removePotionEffect(invis.getType());
              final PotionEffect invisFinal = invis;
              if (playerClass instanceof MinerClass) {
                MinerClass.getInvis().put(victim.getName(), 10);
              } else {
                new BukkitRunnable() {
                  public void run() {
                    if (invisFinal.getDuration() > 1000000) {
                      return;
                    }
                    victim.addPotionEffect(invisFinal);
                  }
                }.runTaskLater(Main.getInstance(), 205L);
              }
            }
          }
        }

        getMarkedPlayers().put(victim.getName(), System.currentTimeMillis() + 10000L);
        getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
        getMarkedBy().get(shooter.getName()).add(new Pair<>(victim.getName(), System.currentTimeMillis() + 10000L));

        CorePlugin.getInstance().getNametagEngine().reloadPlayer(victim);

        new BukkitRunnable() {
          public void run() {
            CorePlugin.getInstance().getNametagEngine().reloadPlayer(victim);
          }
        }.runTaskLater(Main.getInstance(), (MARK_SECONDS * 20) + 5);

        boolean addEffect = Main.RANDOM.nextBoolean();

        if (Main.getInstance().getMapHandler().isKitMap()) {
          if (addEffect) {
            if (hasColorArmor(shooter, Color.GRAY)) {
              victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 1));
            } else if (hasColorArmor(shooter, Color.BLUE)) {
              victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 1));
            } else if (hasColorArmor(shooter, Color.WHITE)) {
              victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
            } else if (hasColorArmor(shooter, Color.PURPLE)) {
              victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1));
            }
          }
        }
      } else {
        shooter.sendMessage(
            ChatColor.YELLOW + "[" + ChatColor.BLUE + "Arrow Range" + ChatColor.YELLOW + " ("
                + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.RED
                + "Bow wasn't fully drawn back. " + ChatColor.BLUE + ChatColor.BOLD + "("
                + formatDamage(damage) + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
      }
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (isMarked(player)) {
        Player damager = null;
        if (event.getDamager() instanceof Player) {
          damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile
            && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
          damager = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (damager != null && !this.canUseMark(damager, player)) {
          return;
        }
        event.setDamage(event.getDamage() * 1.20);
      }
    }
  }

  @EventHandler
  public void onEntityShootBow(final EntityShootBowEvent event) {
    event.getProjectile()
        .setMetadata("Pullback", new FixedMetadataValue(Main.getInstance(), event.getForce()));
  }

  @Override
  public boolean itemConsumed(final Player player, final Material material) {
    if (material == Material.SUGAR) {
      if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
        player.sendMessage(ChatColor.RED + "You cannot use this in spawn!");
        return false;
      }
      if (ArcherClass.lastSpeedUsage.containsKey(player.getName())
          && ArcherClass.lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
        final long millisLeft =
            ArcherClass.lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
        final String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
        player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
        return false;
      }
      ArcherClass.lastSpeedUsage.put(player.getName(),
          System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
      player.addPotionEffect(ArcherClass.ARCHER_SPEED_EFFECT, true);
      SpawnTagHandler.addPassiveSeconds(player, SpawnTagHandler.getMaxTagTime());
      return true;
    } else {
      if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
        player.sendMessage(ChatColor.RED + "You cannot use this in spawn!");
        return false;
      }
      if (ArcherClass.lastJumpUsage.containsKey(player.getName())
          && ArcherClass.lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
        final long millisLeft =
            ArcherClass.lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
        final String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
        player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
        return false;
      }
      ArcherClass.lastJumpUsage.put(player.getName(),
          System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
      player.addPotionEffect(ArcherClass.ARCHER_JUMP_EFFECT, true);
      SpawnTagHandler.addPassiveSeconds(player, SpawnTagHandler.getMaxTagTime());
      return true;
    }
  }

  private boolean canUseMark(final Player player, final Player victim) {
    if (Main.getInstance().getTeamHandler().getTeam(player) != null) {
      final Team team = Main.getInstance().getTeamHandler().getTeam(player);
      int amount = 0;
      for (final Player member : team.getOnlineMembers()) {
        if (PvPClassHandler.hasKitOn(member, this) && ++amount > 2) {
          break;
        }
      }
      if (amount > 2) {
        player.sendMessage(
            ChatColor.RED + "Your team has too many archers. Archer mark was not applied.");
        return false;
      }
    }
    if (ArcherClass.markedBy.containsKey(player.getName())) {
      for (final Pair<String, Long> pair : ArcherClass.markedBy.get(player.getName())) {
        if (victim.getName().equals(pair.getKey())
            && pair.getValue() > System.currentTimeMillis()) {
          return false;
        }
      }
      return true;
    }
    return true;
  }

  private boolean hasColorArmor(Player player, Color color) {
    return ((LeatherArmorMeta) player.getInventory().getHelmet().getItemMeta()).getColor()
        .equals(color)
        && ((LeatherArmorMeta) player.getInventory().getChestplate().getItemMeta()).getColor()
        .equals(color)
        && ((LeatherArmorMeta) player.getInventory().getLeggings().getItemMeta()).getColor()
        .equals(color)
        && ((LeatherArmorMeta) player.getInventory().getBoots().getItemMeta()).getColor()
        .equals(color);
  }

  private final DecimalFormat df = new DecimalFormat("0.00");

  private String formatDamage(final double damage) {
    return df.format(damage / 2).replace(".00", "");
  }

  public double getRandomDamage(double min, double max) {
    return min + (Math.random() * (max - min));
  }
}