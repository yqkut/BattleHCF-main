package cc.stormworth.hcf.deathmessage.trackers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.deathmessage.event.CustomPlayerDamageEvent;
import cc.stormworth.hcf.deathmessage.objects.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GeneralTracker implements Listener {

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
    switch (event.getCause().getCause()) {
      case SUFFOCATION: {
        event.setTrackerDamage(
            new GeneralDamage(event.getPlayer(), event.getDamage(), "suffocated"));
        break;
      }
      case DROWNING: {
        event.setTrackerDamage(
            new GeneralDamage(event.getPlayer(), event.getDamage(), "drowned"));
        break;
      }
      case STARVATION: {
        event.setTrackerDamage(
            new GeneralDamage(event.getPlayer(), event.getDamage(), "starved to death"));
        break;
      }
      case LIGHTNING: {
        event.setTrackerDamage(new GeneralDamage(event.getPlayer(), event.getDamage(),
            "was struck by lightning"));
        break;
      }
      case POISON: {
        event.setTrackerDamage(
            new GeneralDamage(event.getPlayer(), event.getDamage(), "was poisoned"));
        break;
      }
      case WITHER: {
        event.setTrackerDamage(
            new GeneralDamage(event.getPlayer(), event.getDamage(), "withered away"));
        break;
      }
    }
  }

  public static class GeneralDamage extends Damage {

    private final String message;

    public GeneralDamage(final Player damaged, final double damage, final String message) {
      super(damaged, damage);
      this.message = message;
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate("&e ") + this.message + CC.translate("&e."));
      return clickable;
    }
  }
}
