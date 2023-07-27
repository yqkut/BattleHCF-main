package cc.stormworth.hcf.misc.gkits.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.misc.gkits.KitType;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class SelectKitTypeMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&6&lSelect kit type";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    for (int i = 0; i < 9; i++) {
      buttons.put(i, Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(3, 1), Button.fromItem(new ItemBuilder(Material.IRON_INGOT)
            .name("&a&lFree &egKits").build(),
        (other) -> new KitsMenu(KitType.FREE).openMenu(other)));

    buttons.put(getSlot(5, 1), Button.fromItem(new ItemBuilder(Material.DIAMOND)
            .name("&6&lPremium &egKits").build(),
        (other) -> new KitsMenu(KitType.PREMIUM).openMenu(other)));

    return buttons;
  }
}