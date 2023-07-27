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

public class BurnTracker implements Listener {

  @EventHandler(priority = EventPriority.LOW)
  public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
    if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FIRE_TICK
        && event.getCause().getCause() != EntityDamageEvent.DamageCause.LAVA) {
      return;
    }
    final List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
    Damage knocker = null;
    long knockerTime = 0L;
    if (record != null) {
      for (final Damage damage : record) {
        if (!(damage instanceof BurnDamage)) {
          if (damage instanceof BurnDamageByPlayer) {
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
          new BurnDamageByPlayer(event.getPlayer(), event.getDamage(),
              ((PlayerDamage) knocker).getDamager()));
    } else {
      event.setTrackerDamage(new BurnDamage(event.getPlayer(), event.getDamage()));
    }
  }

  public static class BurnDamage extends Damage {

    public BurnDamage(final Player damaged, final double damage) {
      super(damaged, damage);
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &eburned to death"));
      return clickable;
    }
  }

  public static class BurnDamageByPlayer extends PlayerDamage {

    public BurnDamageByPlayer(final Player damaged, final double damage, final Player damager) {
      super(damaged, damage, damager);
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &eburned to death thanks to "));

      clickable.add(getHoverStats(this.getDamager()));

      clickable.add("&e.");

      return clickable;
    }
  }
}