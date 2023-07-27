package cc.stormworth.hcf.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthFixListener implements Listener {

  @EventHandler
  public void StrengthFix(final EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player) {
      final Player player = (Player) event.getDamager();
      if (player.getItemInHand().getType().name().contains("SWORD")) {
        event.setDamage(event.getDamage() + 1.86);
      }
      if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
        for (final PotionEffect Effect : player.getActivePotionEffects()) {
          if (Effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
            final double DamagePercentage = (Effect.getAmplifier() + 1) * 1.3 + 1.0;
            int NewDamage;
            if (event.getDamage() / DamagePercentage <= 1.0) {
              NewDamage = (Effect.getAmplifier() + 1) * 3 + 1;
            } else {
              NewDamage =
                  (int) (event.getDamage() / DamagePercentage)
                      + (Effect.getAmplifier() + 1) * 3;
            }
            event.setDamage(NewDamage);
            break;
          }
        }
      }
    }
  }

  @EventHandler
  public void onArrowHit(final EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (!(event.getDamager() instanceof Arrow)) {
      return;
    }

    Arrow arrow = (Arrow) event.getDamager();

    if (!(arrow.getShooter() instanceof Player)) {
      return;
    }

    Player damager = (Player) arrow.getShooter();

    if (damager.getItemInHand().getType() != Material.BOW) {
      return;
    }

    if (!damager.getItemInHand().hasItemMeta()) {
      return;
    }

    if (!damager.getItemInHand().getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)) {
      return;
    }

    event.setDamage(event.getDamage() / 2);
  }
}