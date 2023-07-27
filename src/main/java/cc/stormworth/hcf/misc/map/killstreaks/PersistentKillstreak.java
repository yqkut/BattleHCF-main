package cc.stormworth.hcf.misc.map.killstreaks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PersistentKillstreak {

  private final String name;
  private final int killsRequired;
  private final ItemStack item;

  public PersistentKillstreak(final String name, final int killsRequired, ItemStack build) {
    this.name = name;
    this.killsRequired = killsRequired;
    item = build;
  }

  public boolean matchesExactly(final int kills) {
    return kills == this.killsRequired;
  }

  public boolean check(final int count) {
    return this.killsRequired <= count;
  }

  public void apply(final Player player) {
  }

  public String getName() {
    return this.name;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public int getKillsRequired() {
    return this.killsRequired;
  }
}