package cc.stormworth.hcf.misc.crazyenchants.controllers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager.Files;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEBook;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantmentControl implements Listener {

  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  @EventHandler
  public void addEnchantment(InventoryClickEvent event) {
    if (event.getInventory() != null && event.getCursor() != null
        && event.getCurrentItem() != null) {
      ItemStack item = event.getCurrentItem();
      if (enchantmentsManager.isEnchantmentBook(event.getCursor())) {
        CEBook ceBook = enchantmentsManager.getCEBook(event.getCursor());
        CEnchantment enchantment = ceBook.getEnchantment();
        if (enchantment != null && enchantment.canEnchantItem(item) && ceBook.getAmount() == 1) {
          Player player = (Player) event.getWhoClicked();
          if (enchantmentsManager.isEnchantStackedItems() || item.getAmount() == 1) {
            int bookLevel = ceBook.getLevel();
            boolean hasEnchantment = false;
            boolean isLowerLevel = false;
            if (enchantmentsManager.hasEnchantment(item, enchantment)) {
              hasEnchantment = true;
              if (enchantmentsManager.getLevel(item, enchantment) < bookLevel) {
                isLowerLevel = true;
              }
            }
            if (hasEnchantment) {
              if (Files.CUSTOMENCHANTS.getFile()
                  .getBoolean("Settings.EnchantmentOptions.Armor-UpgradenchantmentsManager.Toggle")
                  && isLowerLevel) {
                event.setCancelled(true);
                event.setCurrentItem(
                    enchantmentsManager.addEnchantment(item, enchantment, bookLevel));
                player.setItemOnCursor(new ItemStack(Material.AIR));
                player.sendMessage(CC.translate(
                    "&7You have just upgraded &6" + enchantment.getCustomName() + "&7 to level &6"
                        + bookLevel + "&7."));
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                return;
              }
              return;
            }
            event.setCancelled(true);
            ItemStack newItem = enchantmentsManager.addEnchantment(item, enchantment,
                ceBook.getLevel());
            event.setCurrentItem(newItem);
            player.setItemOnCursor(new ItemStack(Material.AIR));
            player.sendMessage(CC.translate("&aYour item loved this book and accepted it."));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
          }
        }
      }
    }
  }

}