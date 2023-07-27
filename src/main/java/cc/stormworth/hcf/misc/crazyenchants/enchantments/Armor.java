package cc.stormworth.hcf.misc.crazyenchants.enchantments;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Armor implements Listener {

  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();

    if (player.getKiller() == null) {
      return;
    }

    Player killer = player.getKiller();
    if (CEnchantments.RECOVER.isActivated()) {
      for (ItemStack item : Objects.requireNonNull(killer.getEquipment()).getArmorContents()) {
        if (enchantmentsManager.hasEnchantments(item) && enchantmentsManager.hasEnchantment(item, CEnchantments.RECOVER)) {
          killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 2));
          killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
        }
      }
    }
  }
}