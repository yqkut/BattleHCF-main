package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreGems extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemStack(Material.EMERALD);
  }

  @Override
  public String getName() {
    return "50 Gems";
  }

  @Override
  public int[] getKills() {
    return new int[]{50};
  }

  @Override
  public void apply(Player player) {
    HCFProfile profile = HCFProfile.get(player);
    profile.addGems(50);
    player.sendMessage(CC.translate("&eYou have been awarded &a50 gems&e."));
  }
}