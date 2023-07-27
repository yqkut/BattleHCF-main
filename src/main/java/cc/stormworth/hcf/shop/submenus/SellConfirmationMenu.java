package cc.stormworth.hcf.shop.submenus;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SellConfirmationMenu extends Menu {

  private final Consumer<Player> onConfirm;

  @Override
  public String getTitle(Player player) {
    return "&c&lSell Confirmation";
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
      buttons.put(i,
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(7, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(1, 1),
        Button.fromItem(new ItemBuilder(Material.WOOL).data((short) 5).name("&a&lYES!").build(),
            onConfirm));

    buttons.put(getSlot(4, 1),
        Button.fromItem(new ItemBuilder(Material.REDSTONE_LAMP_ON).setGlowing(true)
            .name("&c&lWarning")
            .addToLore(
                "&7[&c⚠&7] &cAre you sure you want to sell",
                "&call your selleable items from your inventory?",
                "",
                "&eSelect an option!"
            )
            .build()));

    buttons.put(getSlot(7, 1),
        Button.fromItem(new ItemBuilder(Material.WOOL).data((short) 14).name("&c&lNOPE!").build(),
            (other) -> new SellMenu().openMenu(other)));

    return buttons;
  }
}