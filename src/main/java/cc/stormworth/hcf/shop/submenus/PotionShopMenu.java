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

import java.util.Map;

public class PotionShopMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&bPotion Shop";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    short orangeData = 10;
    short yellowData = 14;

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

    HCFProfile profile = HCFProfile.get(player);
    EconomyData economyData = profile.getEconomyData();

    buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.SKULL_ITEM)
        .setSkullOwner(player.getName())
        .data((short) 3)
        .name("&6" + player.getName() + "'s Balance")
        .addToLore(
            "",
            "&7Balance: &e" + economyData.getFormattedBalance(),
            "",
            "&e&nstore.battle.rip"
        ).build()));

    buttons.put(getSlot(3, 2),
        new BuyItemButton("Fire Resistance I", 800, 1, new ItemBuilder(Material.POTION)
            .data((short) 8259).build()));
    buttons.put(getSlot(4, 2),
        new BuyItemButton("Invisibility I", 2500, 1, new ItemBuilder(Material.POTION)
            .data((short) 8238).build()));
    buttons.put(getSlot(5, 2),
        new BuyItemButton("Speed II", 200, 1, new ItemBuilder(Material.POTION)
            .data((short) 8226).build()));

    buttons.put(getSlot(3, 3),
        new BuyItemButton("Poison II", 1250, 1, new ItemBuilder(Material.POTION)
            .data((short) 16420).build()));
    buttons.put(getSlot(4, 3),
        new BuyItemButton("Weakness I", 1250, 1, new ItemBuilder(Material.POTION)
            .data((short) 16424).build()));
    buttons.put(getSlot(5, 3),
        new BuyItemButton("Slowness I", 1250, 1, new ItemBuilder(Material.POTION)
            .data((short) 16426).build()));

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

    buttons.put(getSlot(5, 5), Button.fromItem(new ItemBuilder(Material.EMERALD)
            .name("&aBuy All")
            .addToLore("&7Buy all the items in the", "&7shop for a certain price", "",
                "&a(Shift+Click) to buy all items.")
            .build(),
        (other) -> {
          new BuyConfirmation((player1) -> {
            int totalPrice = 39000;

            int items = 6;

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

            player1.getInventory().addItem(new ItemBuilder(Material.POTION)
                .data((short) 8259).amount(1).build());
            player1.getInventory().addItem(new ItemBuilder(Material.POTION)
                .data((short) 8238).amount(1).build());
            player1.getInventory().addItem(new ItemBuilder(Material.POTION)
                .data((short) 8226).amount(1).build());

            player1.getInventory().addItem(new ItemBuilder(Material.POTION)
                .data((short) 16420).amount(1).build());
            player1.getInventory().addItem(new ItemBuilder(Material.POTION)
                .data((short) 16424).amount(1).build());
            player1.getInventory().addItem(new ItemBuilder(Material.POTION)
                .data((short) 16426).amount(1).build());

            player1.sendMessage(
                CC.translate(
                    "&eYou bought &6" + items + " &eitems for &6" + NumberUtils.addComma(totalPrice)
                        + "&e!"));
            Button.playSuccess(player1);
          }, this).openMenu(other);
        }));
    return buttons;
  }
}