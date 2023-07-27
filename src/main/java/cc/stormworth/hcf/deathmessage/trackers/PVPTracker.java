package cc.stormworth.hcf.deathmessage.trackers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.deathmessage.event.CustomPlayerDamageEvent;
import cc.stormworth.hcf.deathmessage.objects.PlayerDamage;
import cc.stormworth.hcf.deathmessage.util.MobUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PVPTracker implements Listener {

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
    if (event.getCause() instanceof EntityDamageByEntityEvent) {
      final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();
      if (e.getDamager() instanceof Player) {
        final Player damager = (Player) e.getDamager();
        final Player damaged = event.getPlayer();

        event.setTrackerDamage(
            new PVPDamage(damaged, event.getDamage(), damager, damager.getItemInHand()));
      }
    }
  }

  public static class PVPDamage extends PlayerDamage {

    private String itemString;

    public PVPDamage(final Player damaged, final double damage, final Player damager,
        final ItemStack itemStack) {

      super(damaged, damage, damager);
      this.itemString = ChatColor.RED + "Error";
      if (itemStack.getType() == Material.AIR) {
        this.itemString = ChatColor.RED + "their fists";
      } else {
        this.itemString = MobUtil.getItemName(itemStack);
      }
    }

    @Override
    public Clickable getDeathMessage() {
      final String extension =
          ChatColor.YELLOW + " using " + ChatColor.RED + this.itemString + ChatColor.YELLOW
              + ".";

      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &ewas slain by "));

      clickable.add(getHoverStats(this.getDamager()));

      clickable.add(extension);

      return clickable;
    }
  }
}