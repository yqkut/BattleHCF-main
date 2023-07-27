package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.InteractAbility;
import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SecondChance extends InteractAbility {

  public SecondChance() {
    super("SecondChance",
        "&8SecondChance",
        Lists.newArrayList(
            "",
            "&7Take care of your hearts!",
            "&7 Use it to regenerate to the maximum",
            "&7if you have less than 3 hearts.",
            ""
        ),
        new ItemStack(Material.REDSTONE),
        TimeUtil.parseTimeLong("1m"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {

    Player player = event.getPlayer();

    if (player.getHealth() <= 6) {
      player.setHealth(player.getMaxHealth());
      player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 0));
    } else {
      player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 0));
      player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1));
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 0));
    }

    super.onInteract(event);
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 3),
        new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 3),
        new PotionEffect(PotionEffectType.ABSORPTION, 20 * 5, 0)
    );
  }
}