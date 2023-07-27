package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LongStrength extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return ItemBuilder.of(Material.BLAZE_POWDER).build();
  }

  @Override
  public String getName() {
    return "8m Strength";
  }

  @Override
  public int[] getKills() {
    return new int[100];
  }

  @Override
  public void apply(final Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 8, 0, true));
  }
}