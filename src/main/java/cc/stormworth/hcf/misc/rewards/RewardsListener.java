package cc.stormworth.hcf.misc.rewards;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RewardsListener implements Listener {

  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();
    Inventory inventory = event.getInventory();

    if (inventory.getTitle().equals(CC.translate("&6Rewards Edit"))) {
      ItemStack[] items = inventory.getContents();

      Main.getInstance().getRewardsManager().setRewards(items);

      player.sendMessage(CC.translate("&6Rewards updated!"));
    }
  }

}