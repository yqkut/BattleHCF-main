package cc.stormworth.hcf.team.upgrades.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.ConfirmMenu;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TeamPassiveEffectMenu extends Menu {

  private final Team team;

  @Override
  public String getTitle(Player player) {
    return "&6" + team.getName() + "'s Passive Effects";
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 3 * 9;
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

    buttons.put(getSlot(1, 1),
        new UpgradePassiveEffectButton(PotionEffectType.SPEED, "Speed", Material.SUGAR, 2,
            Main.getInstance().getMapHandler().isKitMap() ? 100 : 150));
    buttons.put(getSlot(3, 1),
        new UpgradePassiveEffectButton(PotionEffectType.DAMAGE_RESISTANCE, "Resistance",
            Material.IRON_INGOT, 1, Main.getInstance().getMapHandler().isKitMap() ? 100 : 150));
    buttons.put(getSlot(5, 1),
        new UpgradePassiveEffectButton(PotionEffectType.INCREASE_DAMAGE, "Strength",
            Material.BLAZE_POWDER, 1, Main.getInstance().getMapHandler().isKitMap() ? 100 : 400));
    buttons.put(getSlot(7, 1),
        new UpgradePassiveEffectButton(PotionEffectType.JUMP, "Jump Boost",
            Material.FEATHER, 2, Main.getInstance().getMapHandler().isKitMap() ? 100 : 150));

    return buttons;
  }

  @RequiredArgsConstructor
  public class UpgradePassiveEffectButton extends Button {

    private final PotionEffectType effect;
    private final String name;
    private final Material material;
    private final int maxLevel;
    private final int price;

    @Override
    public String getName(Player player) {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + name;
    }

    @Override
    public List<String> getDescription(Player player) {
      List<String> description = Lists.newArrayList();

      description.add("&7Upgrade the " + name + " effect in your claim.");
      description.add("");
      description.add("&7Buy: &e" + price + " Gems");
      description.add("");
      description.add(
          "&7Level: &7(" + getLevelString((team.getEffectLevel(effect)), maxLevel) + "&7)");
      description.add("");
      description.add("&7Status: " + ((team.getEffectLevel(effect)) > 0 ? "&a✔" : "&c✖"));
      description.add("");

      if(team.getActiveEffects().containsKey(effect)){
        description.add("&fLeft Click&e to upgrade this feature.");
      }else{
        description.add("&fLeft Click&e to add this feature.");
      }

      description.add("&fRemove Click&e to remove this feature.");

      return description;
    }

    @Override
    public Material getMaterial(Player player) {
      return material;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      HCFProfile profile = HCFProfile.get(player);

      if (clickType == ClickType.LEFT){
        if (profile.getGems() < price) {
          player.sendMessage(CC.translate("&cYou do not have enough gems to purchase this upgrade."));
          return;
        }

        if (team.getEffectLevel(effect) >= maxLevel) {
          player.sendMessage(CC.translate("&cYou cannot upgrade this feature any further."));
          Button.playFail(player);
          return;
        }

        profile.setGems(profile.getGems() - price);

        if (team.isRaidable()){
          player.sendMessage(CC.translate("&cYou can't purchase faction perks while being raidable."));
          Button.playFail(player);
          return;
        }

        if(team.getActiveEffects().containsKey(effect)){
          player.sendMessage(CC.translate("&eYou have successfully upgraded your &6" + name + "&e effect."));
        }else {
          player.sendMessage(CC.translate("&eYou have successfully added &6" + name + "&e effect to your claim."));
        }

        team.upgradeEffect(effect);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.closeInventory();
      }else{

        if (team.getActiveEffects() == null){
          player.sendMessage(CC.translate("&cYou cannot remove this feature."));
          Button.playFail(player);
          return;
        }

        if(!team.getActiveEffects().containsKey(effect)){
          player.sendMessage(CC.translate("&cYou cannot remove this feature."));
          return;
        }

        new ConfirmMenu(ChatColor.RED + "Remove " + name + " effect?", remove -> {

          if (remove){
            int finalPrice = price / 2;

            profile.addGems(finalPrice);

            if(team.getActiveEffects().get(effect) > 1){
              team.removeEffect(effect);
              team.getActiveEffects().put(effect, team.getActiveEffects().get(effect) - 1);
              player.sendMessage(CC.translate("&eYou have successfully removed upgrade &6&l" + name + " &eeffect, &6" + finalPrice + " &egems have been returned."));
              team.giveEffectsToAllInClaim();
            }else{
              team.removeEffect(effect);
              team.getActiveEffects().remove(effect);
              team.flagForSave();
              player.sendMessage(CC.translate("&eYou have successfully removed &6&l" + name + " &eeffect, &6" + finalPrice + " &egems have been returned."));
            }
          }

          new TeamPassiveEffectMenu(team).open(player);

        }).open(player);
      }
    }
  }

  private String getLevelString(int level, int maxLevel) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < maxLevel; i++) {
      if (i < level) {
        builder.append(" &6&l▊ ");
      } else {
        builder.append(" &7&l▊ ");
      }
    }

    return builder.toString();
  }
}