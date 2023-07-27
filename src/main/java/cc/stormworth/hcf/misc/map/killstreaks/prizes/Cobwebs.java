package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cobwebs extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return ItemBuilder.of(Material.WEB, 4).build();
  }

  @Override
  public String getName() {
    return "4 Cobwebs";
  }

  @Override
  public int[] getKills() {
    return new int[]{6};
  }

  @Override
  public void apply(final Player player) {
    this.give(player, new ItemStack(Material.WEB, 4));
  }
}