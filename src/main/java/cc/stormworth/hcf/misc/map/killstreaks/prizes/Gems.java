package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gems extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemStack(Material.EMERALD);
  }

  @Override
  public String getName() {
    return "20 Gems";
  }

  @Override
  public int[] getKills() {
    return new int[]{30};
  }

  @Override
  public void apply(Player player) {
    HCFProfile profile = HCFProfile.get(player);
    profile.addGems(20);
    player.sendMessage(CC.translate("&eYou have been awarded &a20 &egems."));
  }
}