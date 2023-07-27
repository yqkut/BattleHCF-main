package cc.stormworth.hcf.deathmessage.trackers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.deathmessage.DeathMessageHandler;
import cc.stormworth.hcf.deathmessage.event.CustomPlayerDamageEvent;
import cc.stormworth.hcf.deathmessage.objects.Damage;
import cc.stormworth.hcf.deathmessage.objects.PlayerDamage;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallTracker implements Listener {

  @EventHandler(priority = EventPriority.LOW)
  public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
    if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    final List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
    Damage knocker = null;
    long knockerTime = 0L;
    if (record != null) {
      for (final Damage damage : record) {
        if (!(damage instanceof FallDamage)) {
          if (damage instanceof FallDamageByPlayer) {
            continue;
          }
          if (!(damage instanceof PlayerDamage) || (knocker != null
              && damage.getTime() <= knockerTime)) {
            continue;
          }
          knocker = damage;
          knockerTime = damage.getTime();
        }
      }
    }
    if (knocker != null
        && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
      event.setTrackerDamage(
          new FallDamageByPlayer(event.getPlayer(), event.getDamage(),
              ((PlayerDamage) knocker).getDamager()));
    } else {
      event.setTrackerDamage(new FallDamage(event.getPlayer(), event.getDamage()));
    }
  }

  public static class FallDamage extends Damage {

    public FallDamage(final Player damaged, final double damage) {
      super(damaged, damage);
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &ehit the ground too hard."));
      return clickable;
    }
  }

  public static class FallDamageByPlayer extends PlayerDamage {

    public FallDamageByPlayer(final Player damaged, final double damage, final Player damager) {
      super(damaged, damage, damager);
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(
          " &ehit the ground too hard thanks to "));

      clickable.add(getHoverStats(getDamager()));
      return clickable;
    }
  }
}