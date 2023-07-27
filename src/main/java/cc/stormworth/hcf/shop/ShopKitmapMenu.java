package cc.stormworth.hcf.shop;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.misc.shops.block.BlockShopMainMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.shop.submenus.ItemsShopMenu;
import cc.stormworth.hcf.shop.submenus.PotionShopMenu;
import cc.stormworth.hcf.shop.submenus.SellMenu;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShopKitmapMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&6Shop";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {

    final Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    short orangeData = 1;
    short yellowData = 4;

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

    buttons.put(getSlot(4, 2),
        Button.fromItem(
            new ItemBuilder(Material.POTION)
                .name("&9&lPotion Shop")
                .addToLore("&7Buy potions with money")
                .build(),
            (other) -> new PotionShopMenu().openMenu(other)));

    buttons.put(getSlot(3, 3),
        Button.fromItem(new ItemBuilder(Material.DIAMOND_ORE)
                .name("&c&lSell Shop")
                .addToLore("&7Sell shop for money.")
                .build(),
            (other) -> new SellMenu().openMenu(other)));

    buttons.put(getSlot(4, 3),
        Button.fromItem(new ItemBuilder(Material.DIRT)
                .name("&a&lBlock Shop")
                .addToLore("&7Buy blocks in this shop.")
                .build(),
            (other) -> new BlockShopMainMenu().openMenu(other)));

    buttons.put(getSlot(5, 3),
        Button.fromItem(new ItemBuilder(Material.FISHING_ROD)
                .name("&c&lItems Shop")
                .addToLore("&7Buy items on the shop.").build(),
            (other) -> new ItemsShopMenu().openMenu(other)));

    return buttons;
  }
}