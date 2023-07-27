package cc.stormworth.hcf.misc.shops.block.categories;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.shops.PurchaseBalButton;
import cc.stormworth.hcf.misc.shops.block.BlockShopMainMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.shop.ShopUtils;
import cc.stormworth.hcf.shop.submenus.BuyConfirmation;
import cc.stormworth.hcf.util.number.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NetherBlocksMenu extends Menu {

  int[] glassslots = new int[]{0, 1, 7, 8, 9, 17, 27, 36, 37, 35, 43, 44};

  public NetherBlocksMenu() {
    this.setAutoUpdate(false);
    this.setUpdateAfterClick(false);
  }

  @Override
  public String getTitle(Player player) {
    return "Nether Blocks";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    short orangeData = 4;
    short yellowData = 6;

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

    buttons.put(12,
        new PurchaseBalButton(ItemBuilder.of(Material.SOUL_SAND).amount(64).build(), 800));
    buttons.put(13,
        new PurchaseBalButton(ItemBuilder.of(Material.NETHERRACK).amount(64).build(), 64));
    buttons.put(14,
        new PurchaseBalButton(ItemBuilder.of(Material.NETHER_BRICK).amount(64).build(), 64));
    buttons.put(20,
        new PurchaseBalButton(ItemBuilder.of(Material.QUARTZ_ORE).amount(64).build(), 64));
    buttons.put(24,
        new PurchaseBalButton(ItemBuilder.of(Material.OBSIDIAN).amount(64).build(), 500));
    buttons.put(30,
        new PurchaseBalButton(ItemBuilder.of(Material.QUARTZ_BLOCK).amount(64).build(), 64));
    buttons.put(31, new PurchaseBalButton(
        ItemBuilder.of(Material.QUARTZ_BLOCK).amount(64).data((short) 2).build(), 64));
    buttons.put(32, new PurchaseBalButton(
        ItemBuilder.of(Material.QUARTZ_BLOCK).amount(64).data((short) 1).build(), 64));

    buttons.put(getSlot(3, 5), Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
            .name("&5Open EnderChest")
            .addToLore("&7If you want keep something", "&7on your enderchest.", "", "&5Click to open!")
            .build(),
        (other) -> other.openInventory(other.getEnderChest())));

    buttons.put(getSlot(4, 5), Button.fromItem(new ItemBuilder(Material.BED)
            .name("&cGo back")
            .addToLore("&7Click to return to previous page.")
            .build(),
        (other) -> new BlockShopMainMenu().openMenu(other)));

    HCFProfile profile = HCFProfile.get(player);

    EconomyData economyData = profile.getEconomyData();

    buttons.put(getSlot(5, 5), Button.fromItem(new ItemBuilder(Material.EMERALD)
            .name("&aBuy All")
            .addToLore("&7Buy all the items in the", "&7shop for a certain price", "",
                "&a(Shift+Click) to buy all items.")
            .build(),
        (other) -> new BuyConfirmation((player1) -> {
          int totalPrice = 1684;

          int items = 8;

          if (ShopUtils.getEmptySlots(player1.getInventory()) < items) {
            player1.sendMessage(ChatColor.RED + "You don't have enough space in your inventory!");
            Button.playFail(player1);
            return;
          }

          if (economyData.getBalance() < totalPrice) {
            player1.sendMessage(ChatColor.RED + "You don't have enough money!");
            player1.closeInventory();
            Button.playFail(player1);
            return;
          }

          economyData.subtractBalance(totalPrice);

          player1.getInventory().addItem(ItemBuilder.of(Material.SOUL_SAND).amount(64).build());
          player1.getInventory().addItem(ItemBuilder.of(Material.NETHERRACK).amount(64).build());
          player1.getInventory().addItem(ItemBuilder.of(Material.NETHER_BRICK).amount(64).build());
          player1.getInventory().addItem(ItemBuilder.of(Material.QUARTZ_ORE).amount(64).build());
          player1.getInventory().addItem(ItemBuilder.of(Material.OBSIDIAN).amount(64).build());
          player1.getInventory().addItem(ItemBuilder.of(Material.QUARTZ_BLOCK).amount(64).build());
          player1.getInventory()
              .addItem(ItemBuilder.of(Material.QUARTZ_BLOCK).amount(64).data((short) 2).build());
          player1.getInventory()
              .addItem(ItemBuilder.of(Material.QUARTZ_BLOCK).amount(64).data((short) 1).build());

          player1.sendMessage(
              CC.translate(
                  "&eYou bought &6" + items + " &eitems for &6" + NumberUtils.addComma(totalPrice)
                      + "&e!"));
          Button.playSuccess(player1);
        }, this).openMenu(other)));

    return buttons;
  }
}