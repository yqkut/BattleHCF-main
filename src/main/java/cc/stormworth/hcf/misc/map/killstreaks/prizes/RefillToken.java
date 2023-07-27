package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RefillToken extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemStack(Material.NETHER_STAR);
  }

  @Override
  public String getName() {
    return "Potion Refill Token";
  }

  @Override
  public int[] getKills() {
    return new int[]{15};
  }

  @Override
  public void apply(Player player) {
    this.give(player,
        new ItemBuilder(Material.NETHER_STAR).name("&c&lPotion Refill Token").build());
  }
}