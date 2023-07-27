package cc.stormworth.hcf.commands.dropset;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.util.misc.InventoryUtil;
import cc.stormworth.hcf.util.misc.PotionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ViewPlayerMenu extends Menu {

  private final Player target;

  public ViewPlayerMenu(Player target) {
    this.target = target;
    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return ChatColor.GOLD + this.target.getName() + "'s Inventory";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    final Map<Integer, Button> buttons = new HashMap<>();

    if (player == null) {
      return buttons;
    }

    final ItemStack[] fixedContents =
        InventoryUtil.fixInventoryOrder(this.target.getInventory().getContents());

    for (int i = 0; i < fixedContents.length; i++) {
      final ItemStack itemStack = fixedContents[i];

      if (itemStack == null || itemStack.getType() == Material.AIR) {
        continue;
      }

      buttons.put(i, Button.fromItem(itemStack));
    }

    for (int i = 36; i < 54; i++) {
      final ItemStack itemStack =
          new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).build();

      buttons.put(i, Button.fromItem(itemStack));
    }

    for (int i = 0; i < this.target.getInventory().getArmorContents().length; i++) {
      ItemStack itemStack = this.target.getInventory().getArmorContents()[i];

      if (itemStack != null && itemStack.getType() != Material.AIR) {
        buttons.put(48 - i, Button.fromItem(itemStack));
      } else {
        buttons.put(48 - i, Button.fromItem(new ItemBuilder(Material.AIR).build()));
      }
    }

    return buttons;
  }

  @Override
  public boolean isAutoUpdate() {
    return true;
  }

  @AllArgsConstructor
  private class HealthButton extends Button {

    private int health;

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
      return new ItemBuilder(Material.MELON)
          .name(
              ChatColor.YELLOW.toString()
                  + ChatColor.BOLD
                  + "Health: "
                  + ChatColor.LIGHT_PURPLE
                  + this.health
                  + "/10 "
                  + CC.UNICODE_HEART)
          .amount(this.health == 0 ? 1 : this.health)
          .build();
    }
  }

  @AllArgsConstructor
  private class HungerButton extends Button {

    private int hunger;

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
      return new ItemBuilder(Material.COOKED_BEEF)
          .name(
              ChatColor.YELLOW.toString()
                  + ChatColor.BOLD
                  + "Hunger: "
                  + ChatColor.LIGHT_PURPLE
                  + this.hunger
                  + "/20")
          .amount(this.hunger == 0 ? 1 : this.hunger)
          .build();
    }
  }


  @AllArgsConstructor
  private class EffectsButton extends Button {

    private Collection<PotionEffect> effects;

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
      final ItemBuilder builder =
          new ItemBuilder(Material.POTION)
              .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Potion Effects");

      if (this.effects.isEmpty()) {
        builder.addToLore(ChatColor.GRAY + "No effects");
      } else {
        final List<String> lore = new ArrayList<>();

        this.effects.forEach(
            effect -> {
              final String name =
                  PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
              final String duration =
                  " (" + TimeUtil.millisToRoundedTime((effect.getDuration() / 20) * 1000L) + ")";

              lore.add(ChatColor.LIGHT_PURPLE + name + duration);
            });

        builder.setLore(lore);
      }

      return builder.build();
    }
  }
}
