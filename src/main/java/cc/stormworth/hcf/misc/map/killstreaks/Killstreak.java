package cc.stormworth.hcf.misc.map.killstreaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public abstract class Killstreak {

  public abstract ItemStack getItemStack();

  public abstract String getName();

  public abstract int[] getKills();

  public abstract void apply(final Player p0);

  public boolean check(final Player player, final int kills) {
    if (this.shouldApply(kills)) {
      this.apply(player);
      return true;
    }
    return false;
  }

  private boolean shouldApply(final int kills) {
    for (final int k : this.getKills()) {
      if (k == kills) {
        return true;
      }
    }
    return false;
  }

  public void give(final Player player, final ItemStack item) {
    for (int i = 0; i < player.getInventory().getSize(); ++i) {
      final ItemStack current = player.getInventory().getItem(i);
      if (current == null || current.getType() == Material.AIR) {
        player.getInventory().setItem(i, item);
        return;
      }
    }
    for (int i = 0; i < player.getInventory().getSize(); ++i) {
      final ItemStack current = player.getInventory().getItem(i);
      if (current != null && current.getType() == Material.POTION) {
        final Potion potion = Potion.fromItemStack(current);
        if (potion.getType() == PotionType.INSTANT_HEAL && potion.isSplash()) {
          player.getInventory().setItem(i, item);
          return;
        }
      }
    }
  }
}
