package cc.stormworth.hcf.shop.submenus;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.shop.ShopUtils;
import cc.stormworth.hcf.shop.buttons.BuyItemButton;
import cc.stormworth.hcf.util.number.NumberUtils;
import cc.stormworth.hcf.util.player.InventoryUtils;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemsHCFShopMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&6Items Shop";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    short orangeData = 1;
    short yellowData = 12;

    buttons.put(getSlot(0, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(2, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 1), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(0, 4), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(0, 5), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 5), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(6, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(7, 1), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(7, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 4), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(7, 5), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 5), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(6, 5), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(7, 4), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(0, 2), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(0, 3), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(2, 5), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 4), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(8, 2), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 3), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(4, 1),
        new BuyItemButton("Eye of Ender", 500, 9, new ItemStack(Material.EYE_OF_ENDER)));

    buttons.put(getSlot(4, 2),
        new BuyItemButton("End Portal", 1300, 1, new ItemStack(Material.ENDER_PORTAL_FRAME)));

    buttons.put(getSlot(3, 3),
        new BuyItemButton("Fishing Rod", 1300, 1, new ItemStack(Material.FISHING_ROD)));
    buttons.put(getSlot(4, 3),
        new BuyItemButton("Beacon", 20000, 1, new ItemStack(Material.BEACON)));
    buttons.put(getSlot(5, 3),
        new BuyItemButton("Sheers", 3500, 1, new ItemStack(Material.SHEARS)));

    buttons.put(getSlot(4, 4),
              new BuyItemButton("Crowbar", 10000, 1, InventoryUtils.CROWBAR));

    buttons.put(getSlot(3, 5), Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
            .name("&5Open EnderChest")
            .addToLore("&7If you want keep something", "&7on your enderchest.", "", "&5Click to open!")
            .build(),
        (other) -> other.openInventory(other.getEnderChest())));

    buttons.put(getSlot(4, 5), Button.fromItem(new ItemBuilder(Material.BED)
            .name("&cGo back")
            .addToLore("&7Click to return to previous page.")
            .build(),
        (other) -> {
          other.closeInventory();
          other.performCommand("shop");
        }));

      HCFProfile profile = HCFProfile.get(player);
      EconomyData economyData = profile.getEconomyData();

    buttons.put(getSlot(5, 5), Button.fromItem(new ItemBuilder(Material.EMERALD)
            .name("&aBuy All")
            .addToLore("&7Buy all the items in the", "&7shop for a certain price", "",
                "&a(Shift+Click) to buy all items.")
            .build(),
        (other) -> new BuyConfirmation((player1) -> {
          int totalPrice = 24800;

          int items = 3;

          if (ShopUtils.getEmptySlots(player1.getInventory()) < items) {
            player1.sendMessage(ChatColor.RED + "You don't have enough space in your inventory!");
            Button.playFail(player1);
            return;
          }

          if (economyData.getBalance() < totalPrice) {
            player1.sendMessage(ChatColor.RED + "You don't have enough money!");
            Button.playFail(player1);
            return;
          }

          economyData.subtractBalance(totalPrice);

          player1.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
          player1.getInventory().addItem(new ItemStack(Material.BEACON));
          player1.getInventory().addItem(new ItemStack(Material.SHEARS));

          player1.sendMessage(
              CC.translate(
                  "&eYou bought &6" + items + " &eitems for &6" + NumberUtils.addComma(totalPrice)
                      + "&e!"));
          Button.playSuccess(player1);
        }, this).openMenu(other)));
    return buttons;
  }
}