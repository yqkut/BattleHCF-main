package cc.stormworth.hcf.deathmessage.trackers;

import cc.stormworth.core.kt.util.EntityUtils;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.deathmessage.event.CustomPlayerDamageEvent;
import cc.stormworth.hcf.deathmessage.objects.Damage;
import cc.stormworth.hcf.deathmessage.objects.MobDamage;
import cc.stormworth.hcf.deathmessage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ArrowTracker implements Listener {

  @EventHandler
  public void onEntityShootBow(final EntityShootBowEvent event) {
    if (event.getEntity() instanceof Player) {
      event.getProjectile().setMetadata("ShotFromDistance",
          new FixedMetadataValue(Main.getInstance(), event.getProjectile().getLocation()));
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
    if (event.getCause() instanceof EntityDamageByEntityEvent) {
      final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();
      if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
        final Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();
        if (arrow.getShooter() instanceof Player) {
          final Player shooter = (Player) arrow.getShooter();
          for (final MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
            final Location shotFrom = (Location) value.value();
            final double distance = shotFrom.distance(event.getPlayer().getLocation());
            event.setTrackerDamage(
                new ArrowDamageByPlayer(event.getPlayer(), event.getDamage(),
                    shooter, shotFrom, distance));
          }
        } else if (arrow.getShooter() instanceof Entity) {
          event.setTrackerDamage(
              new ArrowDamageByMob(event.getPlayer(), event.getDamage(),
                  (Entity) arrow.getShooter()));
        } else {
          event.setTrackerDamage(new ArrowDamage(event.getPlayer(), event.getDamage()));
        }
      }
    }
  }

  public static class ArrowDamage extends Damage {

    public ArrowDamage(final Player damaged, final double damage) {
      super(damaged, damage);
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &ewas shot."));

      return clickable;
    }
  }

  public static class ArrowDamageByPlayer extends PlayerDamage {

    private final Location shotFrom;
    private final double distance;

    public ArrowDamageByPlayer(final Player damaged, final double damage, final Player damager,
        final Location shotFrom, final double distance) {
      super(damaged, damage, damager);
      this.shotFrom = shotFrom;
      this.distance = distance;
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &ewas shot by "));

      clickable.add(getHoverStats(this.getDamager()));

      clickable.add(" &efrom " + ChatColor.BLUE
          + (int) this.distance + " blocks" + ChatColor.YELLOW + ".");

      return clickable;
    }

    public Location getShotFrom() {
      return this.shotFrom;
    }

    public double getDistance() {
      return this.distance;
    }
  }

  public static class ArrowDamageByMob extends MobDamage {

    public ArrowDamageByMob(final Player damaged, final double damage, final Entity damager) {
      super(damaged, damage, damager.getType());
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(
          CC.translate(" &ewas shot by a &c" + EntityUtils.getName(this.getMobType()) + "&e."));

      return clickable;
    }
  }
}