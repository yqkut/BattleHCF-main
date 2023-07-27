package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gold extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemStack(Material.GOLD_INGOT);
  }

  @Override
  public String getName() {
    return "50 Gold";
  }

  @Override
  public int[] getKills() {
    return new int[]{270};
  }

  @Override
  public void apply(Player player) {
    Profile profile = Profile.getByUuid(player.getUniqueId());
    profile.addGoldCoin(50);

    player.sendMessage(CC.translate("&eYou have received &650 gold coins&e."));
  }
}