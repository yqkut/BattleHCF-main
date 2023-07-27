package cc.stormworth.hcf.bounty.menu;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BountyMenu extends Menu {

  private final BountyPlayer bountyPlayer;

  @Override
  public String getTitle(Player player) {
    return "Bounty Player: " + bountyPlayer.getTarget().getName();
  }

  public BountyMenu(BountyPlayer bountyPlayer) {
    this.bountyPlayer = bountyPlayer;
    setAutoUpdate(false);
    setUpdateAfterClick(false);
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 9 * 3;
  }

  @Override
  public void onClose(Player player) {
    if (isClosedByMenu()) {
      return;
    }

    BountyPlayer.getBounties().remove(bountyPlayer.getUuid());

    player.sendMessage(CC.translate("&cBounty has been cancelled!"));
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

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(11, Button.fromItem(new ItemBuilder(Material.GOLD_INGOT)
            .name("&6&lBalance &7(Reward)")
            .addToLore(
                "&7Select an amount of economy you",
                "&7want to put on your target.",
                "",
                "&eClick to set the bounty!"
            ).build(),
        (other) -> {

          Button.playNeutral(other);
          setClosedByMenu(true);
          new BountyBalanceMenu(bountyPlayer).openMenu(other);
        }));

    buttons.put(13, Button.fromItem(new ItemBuilder(Material.EMERALD)
        .name("&a&lConfirm &7(Bounty) &8[&a✔&8]")
        .setGlowing(true)
        .addToLore(
            "&7After selecting all preferences.",
            "",
            "&eClick to confirm the Bounty!")
        .build(), (other) -> {
      setClosedByMenu(true);

      if (!bountyPlayer.isCompleted()) {
        other.sendMessage(
            CC.translate("&cYou must complete one of rewards before you can confirm a bounty!"));
        openMenu(other);
        return;
      }

      bountyPlayer.setReady(true);
      player.sendMessage(CC.translate("&aSuccessfully created Bounty!"));
      player.closeInventory();

      CorePlugin.getInstance().getNametagEngine().reloadPlayer(bountyPlayer.getTarget());
      CorePlugin.getInstance().getNametagEngine().reloadOthersFor(bountyPlayer.getTarget());
      other.playSound(other.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    }));

    buttons.put(15, Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
        .name("&6&lLoot &7(Reward)")
        .addToLore(
            "&7Select a loot which is obtained",
            "&7by killing your target.",
            "",
            "&eClick to set the bounty!"
        ).build(), (other) -> {

      Inventory inventory = Bukkit.createInventory(null, 9 * 6, CC.translate("&eAdd Bounty items"));

      int size = inventory.getSize();

      ItemBuilder glassPlaceholder = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
          .name(" ")
          .setGlowing(true);

      int rowCount = size / 9;

      for (int index = 0; index < size; index++) {
        int row = index / 9;
        int column = (index % 9) + 1;

        if (row == 0 || row == rowCount - 1 || column == 1 || column == 9) {
          inventory.setItem(index,
              glassPlaceholder.data(NumberUtils.isEven(index) ? orangeData : yellowData).build());
        }
      }

      inventory.setItem(4, new ItemBuilder(Material.STAINED_GLASS)
          .name("&a&lConfirm &7(Loot) &8[&a✔&8]")
          .setGlowing(true)
          .addToLore(
              "&7Are you sure about the items you selected?",
              "",
              "&eClick to confirm selection!")
          .data((short) 5)
          .build());

      inventory.setItem(0, new ItemBuilder(Material.BED)
          .name("&cBack")
          .addToLore(
              "",
              "&7Click to go back to the main menu.")
          .build());

      int slot = 10;
      int index = 0;

      List<ItemStack> items = bountyPlayer.getRewards();

      while (slot < 54 - 10 && items.size() > index) {
        ItemStack itemStack = items.get(index);

        inventory.setItem(slot++, itemStack);

        index++;

        if ((slot - 8) % 9 == 0) {
          slot += 2;
        }
      }

      Button.playNeutral(other);

      setClosedByMenu(true);
      other.openInventory(inventory);
    }));

    return buttons;
  }
}