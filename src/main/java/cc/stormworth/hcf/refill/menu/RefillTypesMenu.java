package cc.stormworth.hcf.refill.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class RefillTypesMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return ChatColor.GOLD.toString() + ChatColor.BOLD + "Choose a Refill Type";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    buttons.put(12, new CombatRefillButton());
    buttons.put(14, new ClassesRefillButton());

    Button placeholder = Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
        .data((short) 1)
        .name(" ")
        .enchant(Enchantment.DURABILITY, 10)
        .build());

    for (int i = 0; i < 9; i++) {
      buttons.put(i, placeholder);
    }

    for (int i = 18; i < 27; i++) {
      buttons.put(i, placeholder);
    }

    buttons.put(9, placeholder);
    buttons.put(17, placeholder);

    return buttons;
  }

  final class CombatRefillButton extends Button {

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
      return new ItemBuilder(Material.DIAMOND_SWORD)
          .name("&d&lCombat &7(&dRefill&7)")
          .addToLore(
              "",
              "&7Refill all combat resources that",
              "&7could be useful in combat.",
              "",
              "&eClick to view it!"
          )
          .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      new CombatRefillMenu().openMenu(player);

      playNeutral(player);
    }
  }

  final class ClassesRefillButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
      return new ItemBuilder(Material.GOLD_SWORD)
          .name("&a&lClasses &7(&2Refill&7)")
          .addToLore(
              "",
              "&7Refill all classes resources that",
              "&7could be useful in combat.",
              "",
              "&eClick to view it!"
          )
          .build();
    }

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
    public void clicked(Player player, int slot, ClickType clickType) {
      new ClassesRefillMenu().openMenu(player);

      playNeutral(player);
    }
  }
}