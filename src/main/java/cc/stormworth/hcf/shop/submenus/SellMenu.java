package cc.stormworth.hcf.shop.submenus;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.shop.ShopUtils;
import cc.stormworth.hcf.shop.buttons.SellItemButton;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellMenu extends Menu {

  final int amount = Main.getInstance().getMapHandler().isKitMap() ? 64 : 16;

  @Override
  public String getTitle(Player player) {
    return "&c&lSell items";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {

    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    short orangeData = 1;
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
        new SellItemButton("&6Diamond Block", 250, amount, new ItemStack(Material.DIAMOND_BLOCK)));
    buttons.put(getSlot(4, 2),
        new SellItemButton("&6Gold Block", 210, amount, new ItemStack(Material.GOLD_BLOCK)));
    buttons.put(getSlot(5, 2),
        new SellItemButton("&6Iron Block", 230, amount, new ItemStack(Material.IRON_BLOCK)));

    buttons.put(getSlot(2, 3),
        new SellItemButton("&6Coal Block", 230, amount, new ItemStack(Material.COAL_BLOCK)));
    buttons.put(getSlot(3, 3),
        new SellItemButton("&6Lapis Block", 250, amount, new ItemStack(Material.LAPIS_BLOCK)));
    buttons.put(getSlot(4, 3),
        new SellItemButton("&6Emerald Block", 210, amount, new ItemStack(Material.EMERALD_BLOCK)));
    buttons.put(getSlot(5, 3),
        new SellItemButton("&6Redstone Block", 250, amount,
            new ItemStack(Material.REDSTONE_BLOCK)));
    buttons.put(getSlot(6, 3),
        new SellItemButton("&6Cobblestone Block", 50, 64, new ItemStack(Material.COBBLESTONE)));

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

    buttons.put(getSlot(5, 5), Button.fromItem(new ItemBuilder(Material.ANVIL)
            .name("&4Sell Inventory")
            .addToLore("&7Sells anything in your inventory that", "&7can be sold in the shop menu.", "",
                "&4Click to sell your inventory.")
            .build(),
        (other) -> new SellConfirmationMenu((player2) -> {
          Material[] toSell = new Material[]{
              Material.DIAMOND_BLOCK,
              Material.GOLD_BLOCK,
              Material.IRON_BLOCK,
              Material.COAL_BLOCK,
              Material.LAPIS_BLOCK,
              Material.EMERALD_BLOCK,
              Material.REDSTONE_BLOCK,
              Material.COBBLESTONE
          };

          int price = 0;

          int itemsSold = 0;

          List<ItemStack> toSellList = new ArrayList<>();

          for (Material item : toSell) {
            if (player2.getInventory().all(item).size() > 0) {
              HashMap<Integer, ? extends ItemStack> map = player2.getInventory().all(item);

              for (ItemStack stack : map.values()) {
                price += ShopUtils.getPrice(stack, amount);
                itemsSold += stack.getAmount();
                toSellList.add(stack);
              }
            }
          }

          if (price == 0) {
            player2.sendMessage(ChatColor.RED + "You don't have anything to sell!");
            Button.playFail(player2);
            return;
          }

          HCFProfile.get(player2).getEconomyData().addBalance(price);

          for (ItemStack item : toSellList) {
            player2.getInventory().removeItem(item);
          }

          player2.sendMessage(
              CC.translate("&aYou sold &e" + itemsSold + " &aitems for &e$" + price + ""));
          Button.playSuccess(player2);
        }).openMenu(other)));

    return buttons;
  }
}