package cc.stormworth.hcf.gemsshop.submenus;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.gemsshop.GemsShopMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class PrivateChestShopMenu extends Menu {

  private final HCFProfile hcfProfile;

  public PrivateChestShopMenu(Player player) {
    this.hcfProfile = HCFProfile.get(player);
    setUpdateAfterClick(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&6Buy Private Chest Upgrade";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    buttons.put(getSlot(0, 0),
        Button.fromItem(
            glass
                .data((short) 3)
                .build()));

    buttons.put(getSlot(1, 0),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(0, 1),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(8, 0),
        Button.fromItem(
            glass
                .data((short) 3)
                .build()));

    buttons.put(getSlot(7, 0),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(8, 1),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(8, 3),
        Button.fromItem(
            glass
                .data((short) 3)
                .build()));

    buttons.put(getSlot(7, 3),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(8, 2),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(0, 3),
        Button.fromItem(
            glass
                .data((short) 3)
                .build()));

    buttons.put(getSlot(1, 3),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(0, 2),
        Button.fromItem(
            glass
                .data((short) 2)
                .build()));

    buttons.put(getSlot(4, 1),
        Button.fromItem(new ItemBuilder(Material.CHEST).name("&6&lPrivate Chest")
            .addToLore(
                "",
                "&7Buy this perk to get access on a full private vault",
                "&7to deposit your favorite items inside.",
                "",
                "&6➤&f Level 1&7:&e 100",
                "&6➤&f Level 2&7:&e 180",
                "&6➤&f Level 3&7:&e 240",
                "",
                "&6Your Chest Level&7: " + hcfProfile.getChestLevel(),
                "",
                "&6[&7Level " + hcfProfile.getChestLevel() + " / " + getLevelString(
                    hcfProfile.getChestLevel()) + " &6]",
                "",
                "&6&l. &fCurrent Slots&7: &e" + hcfProfile.getChestLevel(),
                "",
                "&eClick to upgrade!"
            )
            .build(), (other) -> {
          int chestLevel = hcfProfile.getChestLevel();

          if (chestLevel == 3) {
            other.sendMessage(CC.translate("&cYou have reached the maximum level!"));
            Button.playFail(other);
            return;
          }

          int price;

          if (chestLevel == 0) {
            price = 100;
          } else if (chestLevel == 1) {
            price = 180;
          } else if (chestLevel == 2) {
            price = 240;
          } else {
            price = 0;
          }

          if (hcfProfile.getGems() < price) {
            other.sendMessage(CC.translate("&cYou do not have enough gems!"));
            Button.playFail(other);
            return;
          }

          hcfProfile.setGems(hcfProfile.getGems() - price);

          hcfProfile.upgradeChest(chestLevel + 1);
          player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
          other.sendMessage(CC.translate(
              "&eYou have upgraded your private chest to level &e" + (chestLevel + 1)
                  + "&3!"));
          other.sendMessage("");
          other.sendMessage(CC.translate(
              "&6&lHey! &eYou have &6&l" + (chestLevel + 1)
                  + " &eprivate chests&e. &7Don't forget to Use &f/pv " + (
                  chestLevel + 1)));
        }));

    buttons.put(getSlot(4, 3), Button.fromItem(new ItemBuilder(Material.BED)
            .name("&cGo back")
            .addToLore("&7Click to return to previous page.")
            .build(),
        (other) -> new GemsShopMenu().openMenu(player)));

    return buttons;
  }

  private String getLevelString(int level) {
    if (level == 1) {
      return "&e&l: &7&l: &7&l:";
    } else if (level == 2) {
      return "&e&l: &e&l: &7&l:";
    } else if (level == 3) {
      return "&e&l: &e&l: &e&l:";
    }
    return "&7&l: &7&l: &7&l:";
  }
}