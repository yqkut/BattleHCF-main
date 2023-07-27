package cc.stormworth.hcf.bounty.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.bounty.prompt.BountySelectBalancePrompt;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.chat.ChatUtils;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

public class BountyBalanceMenu extends Menu {

  private final BountyPlayer bountyPlayer;

  public BountyBalanceMenu(BountyPlayer bountyPlayer) {
    this.bountyPlayer = bountyPlayer;

    setUpdateAfterClick(true);
    setAutoUpdate(false);
  }

  @Override
  public String getTitle(Player player) {
    return "Bounty Money for: " + bountyPlayer.getTarget().getName();
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

    buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
        .name("&cBack")
        .addToLore(
            "",
            "&7Click to go back to the main menu.")
        .build(), (other) -> {
      Button.playNeutral(other);
      setClosedByMenu(true);
      new BountyMenu(bountyPlayer).openMenu(player);
    }));

    buttons.put(4, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS)
        .name("&a&lConfirm &7(Balance) &8[&a✔&8]")
        .setGlowing(true)
        .addToLore(
            "&7Are you sure of the amount you entered?",
            "",
            "&8[&6\uD83E\uDE99&8] &7Current Balance: &e" + NumberUtils.addComma(
                bountyPlayer.getBalance()),
            "&eClick to confirm selection!")
        .data((short) 5)
        .build(), (other) -> {
      Button.playNeutral(other);
      setClosedByMenu(true);
      new BountyMenu(bountyPlayer).openMenu(player);
    }));

    buttons.put(11, new BalanceMenu(Material.WOOL, 1000, (byte) 5));
    buttons.put(12, new BalanceMenu(Material.WOOL, 3000, (byte) 4));
    buttons.put(13, new BalanceMenu(Material.WOOL, 5000, (byte) 1));
    buttons.put(14, new BalanceMenu(Material.WOOL, 10000, (byte) 14));

    buttons.put(15, Button.fromItem(new ItemBuilder(Material.WOOL)
            .name("&6&lCustom &7(Balance)")
            .addToLore(
                "&7Select an custom balance to earn,",
                "",
                "&eClick to select!")
            .data((byte) 15)
            .build(),
        (other) -> {
          Button.playNeutral(other);
          setClosedByMenu(true);
          ChatUtils.beginPrompt(other, new BountySelectBalancePrompt(bountyPlayer));
        }));

    return buttons;
  }

  @RequiredArgsConstructor
  public class BalanceMenu extends Button {

    private final Material type;
    private final int balance;
    private final byte data;

    @Override
    public String getName(Player player) {
      return "&6&l$&a" + NumberUtils.addComma(balance);
    }

    @Override
    public List<String> getDescription(Player player) {
      return Lists.newArrayList(
          "&7Earn $" + NumberUtils.addComma(balance) + " for killing the bounty.",
          "",
          "&eClick to select!"
      );
    }

    @Override
    public byte getDamageValue(Player player) {
      return data;
    }

    @Override
    public Material getMaterial(Player player) {
      return type;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      setClosedByMenu(true);

      if (bountyPlayer.getBalance() > 0) {

        HCFProfile profile = HCFProfile.get(player);

        profile.getEconomyData().addBalance(bountyPlayer.getBalance());

        bountyPlayer.setBalance(0);
      }

      HCFProfile profile = HCFProfile.get(player);

      if (profile.getEconomyData().getBalance() < balance) {
        player.sendMessage(CC.translate("&cYou do not have enough money to do this."));
        return;
      }

      profile.getEconomyData().subtractBalance(balance);
      bountyPlayer.setBalance(balance);

      player.sendMessage(CC.translate(
          "&aAdded a total of $" + balance + " for " + bountyPlayer.getTarget()
              .getName() + " Head."));

      Button.playNeutral(player);
    }
  }
}