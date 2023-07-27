package cc.stormworth.hcf.misc.crazyenchants.enchantments;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

public class EnchantmentsTask implements Runnable {

  private final EnchantmentsManager enchantmentsManager = Main.getInstance().getEnchantmentsManager();
  private final int potionTime = 5 * 20;

  @Override
  public void run() {
    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
      ItemStack item = player.getItemInHand();

      if (item == null) {
        continue;
      }

      if (enchantmentsManager.hasEnchantments(item)) {
        List<CEnchantment> enchantments = enchantmentsManager.getEnchantmentsOnItem(item);
        if (enchantments.contains(CEnchantments.OXYGENATE.getEnchantment())) {
          player.removePotionEffect(PotionEffectType.WATER_BREATHING);
          player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, potionTime, 5));
        }
      }

      for (final ItemStack armor : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
        if (CEnchantments.IMPLANTS.isActivated() && enchantmentsManager.hasEnchantment(armor, CEnchantments.IMPLANTS)
            && CEnchantments.IMPLANTS.chanceSuccessful(armor) && player.getFoodLevel() < 20) {
          player.setFoodLevel(20);
          return;
        }
      }
    }
  }
}