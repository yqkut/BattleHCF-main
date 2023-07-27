package cc.stormworth.hcf.refill.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CombatRefillMenu extends Menu {

  public CombatRefillMenu() {
    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return ChatColor.GOLD.toString() + ChatColor.BOLD + "Combat Refill";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE)
        .data((short) 1)
        .name(" ")
        .setGlowing(true)
        .build();

    for (int i = 0; i < 27; i++) {
      buttons.put(i, Button.fromItem(placeholder));
    }

    buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
        .name("&cBack")
        .addToLore(
            "",
            "&7Click to go back to the main menu.")
        .build(), (other) -> new RefillTypesMenu().openMenu(other)));

    buttons.put(9, new ItemButton(new ItemStack(Material.ENDER_PEARL, 16)));
    buttons.put(11, new ItemButton(new ItemStack(Material.POTION, 1, (short) 8226)));
    buttons.put(13, Button.fromItem(new ItemBuilder(Material.POTION)
        .data((short) 16421)
        .name("&d&lPots &7(&5Refill&7)")
        .addToLore(
            "",
            "&7Click once and all your empty",
            "&7slots will be refilled.",
            "",
            "&7» " + (getEmptySlots(player) == 0 ? "&cYou don't have &lempty slots &cavailable."
                : "&aYou have &l" + getEmptySlots(player) + " &aempty slots."),
            "",
            "&eClick to fill your inventory!"
        )
        .build(), (other) -> {
      ItemStack healthPotion = new ItemStack(Material.POTION, 1, (short) 16421);

      for (ItemStack ignored : player.getInventory()) {
        if (player.getInventory().firstEmpty() == -1) {
          break;
        }

        player.getInventory().addItem(healthPotion);
      }
    }));
    buttons.put(15, new ItemButton(new ItemStack(Material.GOLDEN_CARROT, 64)));
    buttons.put(17, new ItemButton(new ItemStack(Material.ARROW, 16)));

    return buttons;
  }

  @RequiredArgsConstructor
  public class ItemButton extends Button {

    private final ItemStack itemStack;

    @Override
    public String getName(Player player) {
      return null;
    }

    @Override
    public List<String> getDescription(Player player) {
      return null;
    }

    @Override
    public Material getMaterial(Player player) {
      return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
      return itemStack;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      player.getInventory().addItem(itemStack);
    }
  }

  private int getEmptySlots(Player player) {
    return (int) Stream.of(player.getInventory().getContents())
        .filter(item -> item == null || item.getType() == Material.AIR).count();
  }
}