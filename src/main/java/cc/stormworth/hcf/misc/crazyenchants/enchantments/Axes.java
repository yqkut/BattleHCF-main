package cc.stormworth.hcf.misc.crazyenchants.enchantments;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Axes implements Listener {

  private final EnchantmentsManager enchantmentsManager = Main.getInstance().getEnchantmentsManager();

/*  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDamage(EntityDamageByEntityEvent event) {
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    if (event.getEntity() instanceof LivingEntity) {
      LivingEntity en = (LivingEntity) event.getEntity();
      if (event.getDamager() instanceof Player) {
        Player damager = (Player) event.getDamager();
        ItemStack item = damager.getItemInHand();
        if (!event.getEntity().isDead()) {
          List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
          if (CEnchantments.GLUTTONY.isActivated() && enchantments.contains(
              CEnchantments.GLUTTONY.getEnchantment()) && CEnchantments.GLUTTONY.chanceSuccessful(
              item) && damager.getFoodLevel() < 20) {
            int food = 2 * ce.getLevel(item, CEnchantments.GLUTTONY);
            if (damager.getFoodLevel() + food < 20) {
              damager.setFoodLevel((int) (damager.getSaturation() + food));
            }
            if (damager.getFoodLevel() + food > 20) {
              damager.setFoodLevel(20);
            }
          }
        }
      }
    }
  }*/

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (CustomTimerCreateCommand.sotwday) {
      return;
    }

    Player player = event.getEntity();
    if (event.getEntity().getKiller() != null) {
      Player damager = event.getEntity().getKiller();
      ItemStack item = damager.getItemInHand();

      if (enchantmentsManager.hasEnchantment(item, CEnchantments.DECAPITATION)) {
        event.getDrops().add(new ItemBuilder(Material.SKULL_ITEM).data((short) 3).name(CC.YELLOW + player.getName() + "'s Skull").setSkullOwner(player.getName()).build());
      }
    }
  }
}