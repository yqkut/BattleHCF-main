package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthResistance extends PersistentKillstreak {

  public StrengthResistance() {
    super(" Strength 12m + Resistance I", 130, ItemBuilder.of(Material.BREWING_STAND_ITEM).build());
  }

  @Override
  public void apply(final Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 12, 0, true));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60 * 12, 0, true));
  }
}