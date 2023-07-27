package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthTwoResistance extends PersistentKillstreak {

  public StrengthTwoResistance() {
    super("Strength II 2m + Resistance I 10m", 200,
        ItemBuilder.of(Material.BREWING_STAND_ITEM).build());
  }

  @Override
  public void apply(final Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 2, 1, true));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60 * 10, 0, true));
  }
}