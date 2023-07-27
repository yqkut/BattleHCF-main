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
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BrewerShopMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&e&lBrewer Shop";
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

    buttons.put(getSlot(2, 1),
        new BuyItemButton("Glistering Melon", 570, 16, new ItemStack(Material.SPECKLED_MELON)));

    buttons.put(getSlot(3, 1),
        new BuyItemButton("Fermented Spider Eye", 510, 16,
            new ItemStack(Material.FERMENTED_SPIDER_EYE)));

    buttons.put(getSlot(4, 1),
        new BuyItemButton("Magma Cream", 560, 16,
            new ItemStack(Material.MAGMA_CREAM)));

    buttons.put(getSlot(5, 1),
        new BuyItemButton("Glass Bottle", 750, 4,
            new ItemStack(Material.GLASS_BOTTLE)));

    buttons.put(getSlot(6, 1),
        new BuyItemButton("Blaze Rod", 1300, 8,
            new ItemStack(Material.BLAZE_ROD)));

    buttons.put(getSlot(2, 2),
        new BuyItemButton("Slimeball", 340, 16,
            new ItemStack(Material.SLIME_BALL)));

    buttons.put(getSlot(3, 2),
        new BuyItemButton("Ghast Tear", 510, 16,
            new ItemStack(Material.GHAST_TEAR)));

    buttons.put(getSlot(4, 2),
        new BuyItemButton("Golden Carrot", 550, 16,
            new ItemStack(Material.GOLDEN_CARROT)));

    buttons.put(getSlot(5, 2),
        new BuyItemButton("Nether Wart", 540, 16,
            new ItemStack(372)));

    buttons.put(getSlot(6, 2),
        new BuyItemButton("Redstone", 520, 16,
            new ItemStack(Material.REDSTONE)));

    buttons.put(getSlot(3, 3),
        new BuyItemButton("Sugar Cane", 570, 16,
            new ItemStack(Material.SUGAR_CANE)));

    buttons.put(getSlot(4, 3),
        new BuyItemButton("Feather", 530, 16,
            new ItemStack(Material.FEATHER)));

    buttons.put(getSlot(5, 3),
        new BuyItemButton("Spider Eyes", 580, 16,
            new ItemStack(Material.SPIDER_EYE)));

    buttons.put(40,
        new BuyItemButton("Milk", 200, 2,
            new ItemStack(Material.MILK_BUCKET)));

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
          int totalPrice = 7830;

          int items = 13;

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

          player1.getInventory().addItem(new ItemStack(Material.SPECKLED_MELON, 16));
          player1.getInventory().addItem(new ItemStack(Material.FERMENTED_SPIDER_EYE, 16));
          player1.getInventory().addItem(new ItemStack(Material.MAGMA_CREAM, 16));

          player1.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE, 4));

          player1.getInventory().addItem(new ItemStack(Material.BLAZE_ROD, 8));

          player1.getInventory().addItem(new ItemStack(Material.SLIME_BALL, 16));
          player1.getInventory().addItem(new ItemStack(Material.GHAST_TEAR, 16));
          player1.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 16));
          player1.getInventory().addItem(new ItemStack(Material.NETHER_WARTS, 16));
          player1.getInventory().addItem(new ItemStack(Material.REDSTONE, 16));
          player1.getInventory().addItem(new ItemStack(Material.SUGAR_CANE, 16));
          player1.getInventory().addItem(new ItemStack(Material.FEATHER, 16));
          player1.getInventory().addItem(new ItemStack(Material.SPIDER_EYE, 16));

          player1.sendMessage(
              CC.translate(
                  "&eYou bought &6" + items + " &eitems for &6" + NumberUtils.addComma(totalPrice)
                      + "&e!"));
          Button.playSuccess(player1);
        }, this).openMenu(other)));

    return buttons;
  }
}