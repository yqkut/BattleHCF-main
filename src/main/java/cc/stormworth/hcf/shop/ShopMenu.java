package cc.stormworth.hcf.shop;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.misc.shops.block.BlockShopMainMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.shop.submenus.BrewerShopMenu;
import cc.stormworth.hcf.shop.submenus.ItemsHCFShopMenu;
import cc.stormworth.hcf.shop.submenus.SellMenu;
import cc.stormworth.hcf.shop.submenus.SpawnerShopMenu;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShopMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&6HCF Shop";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    short orangeData = 1;
    short yellowData = 4;

    buttons.put(getSlot(0, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(0, 4), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(0, 5), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 5), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(7, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 4), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(7, 5), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 5), Button.fromItem(glass.data(yellowData).build()));

    HCFProfile profile = HCFProfile.get(player);

    EconomyData economyData = profile.getEconomyData();

    buttons.put(4, Button.fromItem(new ItemBuilder(Material.SKULL_ITEM)
            .setSkullOwner(player.getName())
            .data((short) 3)
            .name("&6" + player.getName() + " Balance")
            .addToLore(
                "",
                "&7Balance: &e" + economyData.getFormattedBalance(),
                "",
                "&eClick to sell something!"
            ).build(),
        (other) -> {

        }));

    buttons.put(getSlot(3, 2),
        Button.fromItem(new ItemBuilder(Material.DIAMOND_ORE)
                .name("&c&lSell Shop")
                .addToLore("&7Sell shop for money.")
                .build(),
            (other) -> new SellMenu().openMenu(other)));

    buttons.put(getSlot(4, 2),
        Button.fromItem(new ItemBuilder(Material.DIRT)
                .name("&a&lBlock Shop")
                .addToLore("&7Buy blocks in this shop.")
                .build(),
            (other) -> new BlockShopMainMenu().openMenu(other)));

    buttons.put(getSlot(5, 2),
        Button.fromItem(new ItemBuilder(Material.FISHING_ROD)
                .name("&c&lItems Shop")
                .addToLore("&7Buy items on the shop.").build(),
            (other) -> new ItemsHCFShopMenu().openMenu(other)));

    buttons.put(getSlot(3, 3), Button.fromItem(new ItemBuilder(Material.COAL)
            .name("&8&lSpawner Shop").addToLore("&7Buy spawners to farm").build(),
        (other) -> new SpawnerShopMenu().openMenu(other)));

    buttons.put(getSlot(4, 3), Button.fromItem(new ItemBuilder(Material.BREWING_STAND_ITEM)
            .name("&6&lBrewer Shop").addToLore("&7Buy all brewing items that you need").build(),
        (other) -> new BrewerShopMenu().openMenu(other)));

    buttons.put(getSlot(4, 5), Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
            .name("&5Open EnderChest")
            .addToLore("&7If you want keep something", "&7on your enderchest.", "", "&5Click to open!")
            .build(),
        (other) -> other.openInventory(other.getEnderChest())));

    return buttons;
  }
}