package cc.stormworth.hcf.deathmessage.trackers;

import cc.stormworth.core.kt.util.EntityUtils;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.deathmessage.event.CustomPlayerDamageEvent;
import cc.stormworth.hcf.deathmessage.objects.MobDamage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityTracker implements Listener {

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
    if (event.getCause() instanceof EntityDamageByEntityEvent) {
      final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();
      if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Arrow)) {
        event.setTrackerDamage(
            new EntityDamage(event.getPlayer(), event.getDamage(), e.getDamager()));
      }
    }
  }

  public static class EntityDamage extends MobDamage {

    public EntityDamage(final Player damaged, final double damage, final Entity entity) {
      super(damaged, damage, entity.getType());
    }

    @Override
    public Clickable getDeathMessage() {
      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &ewas slain by a ") + ChatColor.RED + EntityUtils.getName(
          this.getMobType()) + ChatColor.YELLOW + ".");

      return clickable;
    }
  }
}