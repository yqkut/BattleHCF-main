package cc.stormworth.hcf.util.misc;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtil {

  public static String getName(PotionEffectType potionEffectType) {
    if (potionEffectType.getName().equalsIgnoreCase("fire_resistance")) {
      return "Fire Resistance";
    } else if (potionEffectType.getName().equalsIgnoreCase("speed")) {
      return "Speed";
    } else if (potionEffectType.getName().equalsIgnoreCase("weakness")) {
      return "Weakness";
    } else if (potionEffectType.getName().equalsIgnoreCase("slowness")) {
      return "Slowness";
    } else {
      return "Unknown";
    }
  }

  public static int getPotionEffectLevel(Player player, PotionEffectType potionEffectType) {
    return player.getActivePotionEffects().stream()
        .filter(potionEffect -> potionEffect.getType().equals(potionEffectType)).mapToInt(PotionEffect::getAmplifier).sum();
  }
}
