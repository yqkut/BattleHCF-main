package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldenApples extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return ItemBuilder.of(Material.GOLDEN_APPLE, 3).build();
  }

  @Override
  public String getName() {
    return "8 Golden Apples";
  }

  @Override
  public int[] getKills() {
    return new int[]{3};
  }

  @Override
  public void apply(final Player player) {
    this.give(player, new ItemStack(Material.GOLDEN_APPLE, 8));
  }
}