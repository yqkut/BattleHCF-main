package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KeyBlood extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemBuilder(Material.TRIPWIRE_HOOK).setGlowing(true).build();
  }

  @Override
  public String getName() {
    return "Blood Key";
  }

  @Override
  public int[] getKills() {
    return new int[]{9};
  }

  @Override
  public void apply(Player player) {
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 10");
  }
}