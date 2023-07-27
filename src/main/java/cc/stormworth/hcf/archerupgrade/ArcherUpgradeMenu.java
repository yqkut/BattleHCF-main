package cc.stormworth.hcf.archerupgrade;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.pvpclasses.pvpclasses.ArcherClass;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ArcherUpgradeMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return CC.translate("&6Buy archer Upgrade");
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

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(1, 1),
        new UpgradeButton(Color.GRAY, "Medusa", ChatColor.DARK_GREEN, "Poison II"));
    buttons.put(getSlot(3, 1),
        new UpgradeButton(Color.BLUE, "Fainter", ChatColor.DARK_GREEN, "Weakness II"));
    buttons.put(getSlot(5, 1),
        new UpgradeButton(Color.WHITE, "Phantom", ChatColor.WHITE, "Blindness II"));
    buttons.put(getSlot(7, 1),
        new UpgradeButton(Color.PURPLE, "Venom", ChatColor.LIGHT_PURPLE, "Wither II"));

    return buttons;
  }

  @RequiredArgsConstructor
  public class UpgradeButton extends Button {

    private final Color color;
    private final String name;
    private final ChatColor chatColor;
    private final String effect;
    private final int cost = 100;

    @Override
    public String getName(Player player) {
      return chatColor + ChatColor.BOLD.toString() + name + " Archer";
    }

    @Override
    public List<String> getDescription(Player player) {
      return Lists.newArrayList(
          "",
          "&7Every shot has a &e50% &7of chances",
          "&7of giving " + chatColor + effect + " &7to the target.",
          "",
          "&eCost: &a" + cost + " Gems",
          "",
          "&eClick to apply this upgrade!"
      );
    }

    @Override
    public Material getMaterial(Player player) {
      return Material.LEATHER_HELMET;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
      return new ItemBuilder(super.getButtonItem(player)).setGlowing(true).color(color).build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      HCFProfile profile = HCFProfile.get(player);

      if (profile.getGems() < cost) {
        player.sendMessage(CC.translate("&cYou don't have enough gems to buy this upgrade!"));
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
        player.closeInventory();
        return;
      }

      if (!(PvPClassHandler.getPvPClass(player) instanceof ArcherClass)) {
        player.sendMessage(CC.translate("&cYou must be a archer class to buy this upgrade!"));
        return;
      }

      profile.setGems(profile.getGems() - cost);
      player.sendMessage(
          CC.translate("&aYou have successfully bought the &e" + name + "&a upgrade!"));
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

      player.getInventory()
          .setHelmet(new ItemBuilder(player.getInventory().getHelmet()).color(color).build());

      player.getInventory()
          .setChestplate(
              new ItemBuilder(player.getInventory().getChestplate()).color(color).build());

      player.getInventory()
          .setLeggings(new ItemBuilder(player.getInventory().getLeggings()).color(color).build());

      player.getInventory()
          .setBoots(new ItemBuilder(player.getInventory().getBoots()).color(color).build());

      player.closeInventory();
    }
  }
}