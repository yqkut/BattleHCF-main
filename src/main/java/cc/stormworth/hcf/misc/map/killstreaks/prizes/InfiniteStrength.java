package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfiniteStrength extends PersistentKillstreak {

  public InfiniteStrength() {
    super("Strength Infinite + Resistance Infinite", 300,
        ItemBuilder.of(Material.BLAZE_POWDER).build());
  }

  @Override
  public void apply(final Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
  }
}