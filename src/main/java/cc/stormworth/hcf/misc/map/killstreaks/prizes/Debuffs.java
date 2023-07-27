package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Debuffs extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return ItemBuilder.of(Material.SPIDER_EYE).build();
  }

  @Override
  public String getName() {
    return "Debuffs";
  }

  @Override
  public int[] getKills() {
    return new int[]{12};
  }

  @Override
  public void apply(final Player player) {
    final Potion poison = new Potion(PotionType.POISON);
    poison.setSplash(true);
    final Potion slowness = new Potion(PotionType.SLOWNESS);
    slowness.setSplash(true);
    this.give(player, poison.toItemStack(1));
    this.give(player, slowness.toItemStack(1));
  }
}