package cc.stormworth.hcf.misc.crazyenchants.controllers;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.utils.managers.InfoMenuManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.EnchantmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InfoGUIControl implements Listener {

  private final InfoMenuManager manager = Main.getInstance().getEnchantmentsManager()
      .getInfoMenuManager();

  @EventHandler
  public void infoClick(InventoryClickEvent event) {
    if (event.getInventory() != null && event.getView().getTitle()
        .equals(manager.getInventoryName())) {
      event.setCancelled(true);
      if (event.getCurrentItem() != null) {
        ItemStack item = event.getCurrentItem();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
          Player player = (Player) event.getWhoClicked();
          if (item.isSimilar(manager.getBackLeftButton()) || item.isSimilar(
              manager.getBackRightButton())) {
            manager.openInfoMenu(player);
            return;
          }
          for (EnchantmentType enchantmentType : manager.getEnchantmentTypes()) {
            if (item.isSimilar(enchantmentType.getDisplayItem())) {
              manager.openInfoMenu(player, enchantmentType);
              return;
            }
          }
        }
      }
    }
  }
}