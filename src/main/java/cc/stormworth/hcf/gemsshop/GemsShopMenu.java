package cc.stormworth.hcf.gemsshop;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.gemsshop.submenus.AbilitiesShopMenu;
import cc.stormworth.hcf.gemsshop.submenus.GKitsMenu;
import cc.stormworth.hcf.gemsshop.submenus.PrivateChestShopMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class GemsShopMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&a&lMerchant";
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 9 * 6;
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder itemBuilder = new ItemBuilder(Material.STAINED_GLASS_PANE).setGlowing(true)
        .name(" ");

    for (int x = 0; x < 9; x++) {
      for (int y = 0; y < 6; y++) {
        buttons.put(getSlot(x, y), Button.fromItem(itemBuilder.data((short) 1).build()));
      }
    }

    buttons.put(0, Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(1, 0), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(0, 1), Button.fromItem(itemBuilder.data((short) 2).build()));

    buttons.put(getSlot(0, 5), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(1, 5), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(0, 4), Button.fromItem(itemBuilder.data((short) 2).build()));

    buttons.put(getSlot(8, 0), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(7, 0), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(8, 1), Button.fromItem(itemBuilder.data((short) 2).build()));

    buttons.put(getSlot(8, 4), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(7, 5), Button.fromItem(itemBuilder.data((short) 2).build()));
    buttons.put(getSlot(8, 5), Button.fromItem(itemBuilder.data((short) 2).build()));

    buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.BOOK)
        .name("&6&lGems Shop")
        .addToLore("",
            "&6&l| &fBalance: &e" + HCFProfile.get(player).getGems(),
            " ",
            "&7Purchase gems at &6&nstore.battle.rip")
        .build()));

    if (Main.getInstance().getMapHandler().isKitMap()) {
      buttons.put(getSlot(2, 3), Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
          .name("&6&lPrivate Chest")
          .addToLore("",
              "&7In this section you will be able to get private chests.",
              " ",
              "&eClick to view.")
          .build(), (other) -> new PrivateChestShopMenu(other).openMenu(other)));
    }

    buttons.put(getSlot(Main.getInstance().getMapHandler().isKitMap() ? 4 : 3, 3),
        Button.fromItem(new ItemBuilder(Material.GOLD_CHESTPLATE)
            .name("&6&lGkits")
            .addToLore("",
                "&6&l| &eCategories&7: ",
                "",
                "&e➤ &fDiamond",
                "&e➤ &fBard",
                "&e➤ &fRouge",
                "&e➤ &fBuilder",
                "",
                "&eClick to view.")
            .build(), (other) -> new GKitsMenu(other).openMenu(other)));

    buttons.put(getSlot(Main.getInstance().getMapHandler().isKitMap() ? 6 : 5, 3),
        Button.fromItem(new ItemBuilder(Material.BLAZE_POWDER)
            .name("&6&lAbilities")
            .addToLore("",
                "&7In this section you will able to buy special abilities.",
                "",
                "&eClick to view.")
            .build(), (other) -> new AbilitiesShopMenu(other).openMenu(other)));

    return buttons;
  }
}