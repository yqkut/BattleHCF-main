package cc.stormworth.hcf.team.upgrades.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TeamUpgradesMenu extends Menu {

  private final Team team;

  public TeamUpgradesMenu(Team team) {
    this.team = team;
  }

  @Override
  public String getTitle(Player player) {
    return "&6&lTeam Upgrades";
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 3 * 9;
  }

  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

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

    buttons.put(getSlot(5, 1), new DtrRegenButton(100));

    buttons.put(getSlot(3, 1), Button.fromItem(
        new ItemBuilder(Material.BEACON)
            .name("&6&lClaims Effects")
            .addToLore(
                "",
                "&7Select the effects that will be in your claim.",
                "",
                "&eClick to view."
            ).build(),
        (other) -> new TeamPassiveEffectMenu(team).openMenu(other)));

    return buttons;
  }

  @RequiredArgsConstructor
  public class DtrRegenButton extends Button {

    private final int price;

    @Override
    public String getName(Player player) {
      return "&6&lDTR Reducer";
    }

    @Override
    public List<String> getDescription(Player player) {

      List<String> description = Lists.newArrayList();

      if (!team.isDtrRegenFaster()) {
        description.add("");
        description.add("&7Reduce a 50% of your active DTR Freeze.");
        description.add("");
        description.add("&7Buy:&e " + price + " Gems");
        description.add("");
        description.add("&eClick to purchase!");
      } else {
        description.add("");
        description.add("&cThis upgrade was already");
        description.add("&cpurchased for your faction.");
        if (team.hasDtrRegenCooldown()) {
          description.add(CC.translate(
              "&cYou must wait &c&l" + TimeUtil.millisToRoundedTime(team
                  .getDtrRegenFasterEndAt() - System.currentTimeMillis())
                  + " &cbefore reducing your DTR Freeze timer again."));
        }
      }

      return description;
    }

    @Override
    public Material getMaterial(Player player) {
      return Material.FIREBALL;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

      if (Main.getInstance().getConquestHandler().getGame() != null) {
        player.sendMessage(
            CC.translate("&cThis upgrade is currently deactivated due to &6&lConquest &cevent."));
        return;
      }

      if (team.hasDtrRegenCooldown()) {
        player.sendMessage(CC.translate(
            "&cThis upgrade is currently on cooldown for &c&l" + TimeUtil.millisToRoundedTime(team
                .getDtrRegenFasterEndAt() - System.currentTimeMillis()) + "&c."));
        playFail(player);
        return;
      }

      if (team.isDtrRegenFaster()) {
        player.sendMessage(CC.translate("&cThis upgrade was already purchased for your faction!"));

        playFail(player);
        return;
      }

      HCFProfile profile = HCFProfile.get(player);

      if (profile.getGems() < price) {
        player.sendMessage(CC.translate("&cYou don't have enough gems to purchase this upgrade!"));
        playFail(player);
        return;
      }

      if (DTRHandler.isOnCooldown(team)) {

        int cooldown = (int) ((team.getDTRCooldown() - System.currentTimeMillis()) / 1000);

        if (cooldown < TimeUnit.MINUTES.toSeconds(15)) {
          player.sendMessage(
              CC.translate(
                  "&cYou cannot purchase this upgrade when dtr regen is less than 15 minutes."));
          playFail(player);
          return;
        }

        profile.setGems(profile.getGems() - price);

        team.setDtrRegenFaster(true);
        team.setDTRCooldown((System.currentTimeMillis() + (cooldown * 1000 / 2)));
        team.setDtrRegenFasterEndAt(System.currentTimeMillis() + TimeUtil.parseTimeLong("3h"));

        player.sendMessage(CC.translate("&aYou have successfully purchased this upgrade!"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
      } else {
        player.sendMessage(CC.translate("&cYou can't purchase this upgrade yet!"));
      }
    }
  }
}
