package cc.stormworth.hcf.misc.shops.block;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.shops.MainMenuButton;
import cc.stormworth.hcf.misc.shops.block.categories.ClayBlocksMenu;
import cc.stormworth.hcf.misc.shops.block.categories.DesertBlocksMenu;
import cc.stormworth.hcf.misc.shops.block.categories.EndBlocksMenu;
import cc.stormworth.hcf.misc.shops.block.categories.GlassBlocskMenu;
import cc.stormworth.hcf.misc.shops.block.categories.NetherBlocksMenu;
import cc.stormworth.hcf.misc.shops.block.categories.StoneBlocksMenu;
import cc.stormworth.hcf.misc.shops.block.categories.WoodBlocksMenu;
import cc.stormworth.hcf.misc.shops.block.categories.WoolBlocksMenu;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockShopMainMenu extends Menu {

  public BlockShopMainMenu() {
    this.setAutoUpdate(false);
    this.setUpdateAfterClick(false);
  }

  @Override
  public String getTitle(Player player) {
    return "Block Shop";
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
        new MainMenuButton(CC.RED + "Nether Blocks", Material.NETHERRACK, new NetherBlocksMenu(),
            null));
    buttons.put(13,
        new MainMenuButton(CC.WHITE + "Glass Blocks", Material.STAINED_GLASS, new GlassBlocskMenu(),
            null));
    buttons.put(14,
        new MainMenuButton(CC.GRAY + "Stone Blocks", Material.STONE, new StoneBlocksMenu(), null));

    buttons.put(21,
        new MainMenuButton(CC.GRAY + "Wool Blocks", Material.WOOL, new WoolBlocksMenu(), null));
    buttons.put(23,
        new MainMenuButton(CC.RED + "Clay Blocks", Material.STAINED_CLAY, new ClayBlocksMenu(),
            null));

    buttons.put(30,
        new MainMenuButton(CC.GREEN + "Wood Blocks", Material.LOG, new WoodBlocksMenu(), null));
    buttons.put(31,
        new MainMenuButton(CC.DARK_PURPLE + "End Blocks", Material.ENDER_STONE, new EndBlocksMenu(),
            null));
    buttons.put(32,
        new MainMenuButton(CC.YELLOW + "Desert Blocks", Material.SANDSTONE, new DesertBlocksMenu(),
            null));

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
    return buttons;
  }
}