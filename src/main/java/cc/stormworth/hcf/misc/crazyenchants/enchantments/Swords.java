package cc.stormworth.hcf.misc.crazyenchants.enchantments;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import org.bukkit.event.Listener;

public class Swords implements Listener {

  private final EnchantmentsManager enchantmentsManager = Main.getInstance().getEnchantmentsManager();

  public Swords() {
    Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
  }

  /*@EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDamage(final EntityDamageByEntityEvent event) {
    if (CustomTimerCreateCommand.sotwday || Main.getInstance().getServerHandler().isEOTW()) {
      return;
    }

    if (!event.isCancelled()) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
        Player damager = (Player) event.getDamager();
        ItemStack It = damager.getItemInHand();

        if (!event.getEntity().isDead()) {
          if (enchantmentsManager.hasEnchantments(It)) {
            if (enchantmentsManager.hasEnchantment(It, CEnchantments.NUTRITION) && CEnchantments.NUTRITION.isActivated() && Methods.randomPicker(8)) {
              if (damager.getSaturation() + 2 * enchantmentsManager.getLevel(It, CEnchantments.NUTRITION) <= 20.0f) {
                damager.setSaturation(damager.getSaturation() + 2 * enchantmentsManager.getLevel(It, CEnchantments.NUTRITION));
              }
              if (damager.getSaturation() + 2 * enchantmentsManager.getLevel(It, CEnchantments.NUTRITION) >= 20.0f) {
                damager.setSaturation(20.0f);
              }
            }
          }
        }
      }
    }
  }*/

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            final Player damager = event.getEntity().getKiller();
            final Player player = event.getEntity();
            final ItemStack item = damager.getItemInHand();
            if (ce.hasEnchantments(item) && ce.hasEnchantment(item, CEnchantments.HEADLESS) && CEnchantments.HEADLESS.isActivated()) {
                final int power = ce.getLevel(item, CEnchantments.HEADLESS);
                if (Methods.randomPicker(11 - power)) {
                    final ItemStack head = ItemUtil.createSkull(CC.YELLOW + player.getName() + "'s Skull", 1, player.getName(), (List) null);
                    event.getDrops().add(head);
                }
            }
        }
    }*/
}