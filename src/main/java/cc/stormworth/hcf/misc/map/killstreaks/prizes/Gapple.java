package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gapple extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return ItemBuilder.of(Material.GOLDEN_APPLE).build();
  }

  @Override
  public String getName() {
    return "OP Apple";
  }

  @Override
  public int[] getKills() {
    return new int[]{25};
  }

  @Override
  public void apply(final Player player) {
    this.give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
  }
}