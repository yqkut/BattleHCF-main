package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TwoHundredGems extends Killstreak {

  @Override
  public ItemStack getItemStack() {
    return new ItemStack(Material.EMERALD);
  }

  @Override
  public String getName() {
    return "200 Gems + 15 Koth key";
  }

  @Override
  public int[] getKills() {
    return new int[230];
  }

  @Override
  public void apply(Player player) {
    HCFProfile profile = HCFProfile.get(player);
    profile.addGems(200);
    player.sendMessage(CC.translate("&eYou have been awarded &a200 &egems."));
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " koth 15");
  }
}