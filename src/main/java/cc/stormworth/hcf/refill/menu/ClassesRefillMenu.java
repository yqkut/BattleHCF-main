package cc.stormworth.hcf.refill.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ClassesRefillMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return ChatColor.GOLD.toString() + ChatColor.BOLD + "Classes Refill";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE)
        .data((short) 1)
        .name(" ").setGlowing(true)
        .build();

    for (int i = 0; i < 27; i++) {
      buttons.put(i, Button.fromItem(placeholder));
    }

    buttons.put(4, Button.fromItem(new ItemBuilder(Material.BED)
        .name("&cBack")
        .addToLore(
            "",
            "&7Click to go back to the main menu.")
        .build(), (other) -> new RefillTypesMenu().openMenu(other)));

    buttons.put(0, new ItemButton(new ItemStack(Material.BLAZE_POWDER, 64)));
    buttons.put(1, new ItemButton(new ItemStack(Material.SUGAR, 64)));
    buttons.put(7, new ItemButton(new ItemStack(Material.MAGMA_CREAM, 64)));
    buttons.put(8, new ItemButton(new ItemStack(Material.FEATHER, 64)));

    buttons.put(22, new ItemButton(new ItemStack(Material.GOLD_SWORD)));

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

}