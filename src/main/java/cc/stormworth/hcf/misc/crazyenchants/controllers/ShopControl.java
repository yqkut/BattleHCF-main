package cc.stormworth.hcf.misc.crazyenchants.controllers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.processors.Methods;
import cc.stormworth.hcf.misc.crazyenchants.utils.CurrencyAPI;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.ShopOption;
import cc.stormworth.hcf.misc.crazyenchants.utils.managers.ShopManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEBook;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.Category;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.LostBook;
import cc.stormworth.hcf.misc.gkits.menu.SelectKitTypeMenu;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopControl implements Listener {

  private static final EnchantmentsManager ce = Main.getInstance().getEnchantmentsManager();
  private static final ShopManager shopManager = ce.getShopManager();

  public static void openGUI(Player player) {
    player.openInventory(shopManager.getShopInventory(player));
  }

  @EventHandler
  public void onInvClick(InventoryClickEvent event) {
    ItemStack item = event.getCurrentItem();
    Inventory inventory = event.getInventory();
    Player player = (Player) event.getWhoClicked();
    if (inventory != null && event.getView().getTitle().equals(shopManager.getInventoryName())) {
      event.setCancelled(true);
      if (event.getRawSlot() >= inventory.getSize()) {
        return;
      }
      if (item != null) {
        for (Category category : ce.getCategories()) {
          if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {
            if (Methods.isInventoryFull(player)) {
              player.sendMessage(CC.translate("&cInventory full."));
              return;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
              if (CurrencyAPI.canBuy(player, category)) {
                CurrencyAPI.takeCurrency(player, category);
              } else {
                String needed = (category.getCost() - CurrencyAPI.getCurrency(player)) + "";
                player.sendMessage(CC.translate("&cYou need &6" + needed + " &cmore xp level."));
                return;
              }
            }
            CEBook book = ce.getRandomEnchantmentBook(category);
            if (book != null) {
              player.getInventory().addItem(book.buildBook());
            } else {
              player.sendMessage(Methods.getPrefix("&cThe category &6" + category.getName()
                  + " &chas no enchantments assigned to it."));
            }
            return;
          }
          LostBook lostBook = category.getLostBook();
          if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build())) {
            if (Methods.isInventoryFull(player)) {
              player.sendMessage(CC.translate("&cInventory full."));
              return;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
              if (CurrencyAPI.canBuy(player, lostBook)) {
                CurrencyAPI.takeCurrency(player, lostBook);
              } else {
                String needed = (lostBook.getCost() - CurrencyAPI.getCurrency(player)) + "";
                player.sendMessage(CC.translate("&cYou need &6" + needed + " &cmore xp level."));
                return;
              }
            }
            player.getInventory().addItem(lostBook.getLostBook(category));
            return;
          }
        }
        for (ShopOption option : ShopOption.values()) {
          if (option.isInGUI() && item.isSimilar(option.getItem())) {
            //If the option is buyable then it check to see if they player can buy it and take the money.
            if (option.isBuyable()) {
              if (Methods.isInventoryFull(player)) {
                player.sendMessage(CC.translate("&cInventory full."));
                return;
              }
              if (player.getGameMode() != GameMode.CREATIVE) {
                if (CurrencyAPI.canBuy(player, option)) {
                  CurrencyAPI.takeCurrency(player, option);
                } else {
                  String needed = (option.getCost() - CurrencyAPI.getCurrency(player)) + "";
                  player.sendMessage(CC.translate("&cYou need &6" + needed + " &cmore xp level."));
                  return;
                }
              }
            }
            switch (option) {
              case GKITZ:
                new SelectKitTypeMenu().openMenu(player);
                break;
              case INFO:
                ce.getInfoMenuManager().openInfoMenu(player);
                break;
            }
            return;
          }
        }
      }
    }
  }
}