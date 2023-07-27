package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CowebGapples extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemStack(Material.CHEST);
  }

  @Override
  public String getName() {
    return "25 Cobweb + 32 Crapples";
  }

  @Override
  public int[] getKills() {
    return new int[]{40};
  }

  @Override
  public void apply(Player player) {
    give(player, new ItemStack(Material.WEB, 25));
    give(player, new ItemStack(Material.GOLDEN_APPLE, 34));
  }
}