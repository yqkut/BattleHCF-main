package cc.stormworth.hcf.gemsshop.submenus;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.gemsshop.GemsShopMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AbilitiesShopMenu extends Menu {

  private final HCFProfile hcfProfile;

  public AbilitiesShopMenu(Player player) {
    this.hcfProfile = HCFProfile.get(player);
    setUpdateAfterClick(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&6Buy Abilities";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    buttons.put(getSlot(0, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(0, 2), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(0, 3), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(0, 5), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(8, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 2), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 3), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 5), Button.fromItem(glass.data(yellowData).build()));

    for (int i = 9; i < 18; i++) {
      buttons.put(i,
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? yellowData : orangeData).build()));
    }

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 4),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(4, 0), Button.fromItem(new ItemBuilder(Material.BOOK)
        .name("&6&lGems Shop")
        .addToLore("",
            "&6&l| &fBalance: &e" + HCFProfile.get(player).getGems(),
            " ",
            "&7Purchase gems at &6&nstore.battle.rip")
        .build()));

    buttons.put(getSlot(2, 2), new AbilityButton(Ability.getByName("Strength")));
    buttons.put(getSlot(3, 2), new AbilityButton(Ability.getByName("Resistance")));
    buttons.put(getSlot(4, 2), new AbilityButton(Ability.getByName("MedKit")));
    buttons.put(getSlot(5, 2), new AbilityButton(Ability.getByName("Grenade")));
    buttons.put(getSlot(6, 2), new AbilityButton(Ability.getByName("Freezer")));

    buttons.put(getSlot(3, 3), new AbilityButton(Ability.getByName("Camouflage")));
    buttons.put(getSlot(4, 3), new AbilityButton(Ability.getByName("HelmetDisarmer")));

    buttons.put(getSlot(4, 5), Button.fromItem(new ItemBuilder(Material.BED)
            .name("&cGo back")
            .addToLore("&7Click to return to previous page.")
            .build(),
        (other) -> new GemsShopMenu().openMenu(player)));

    return buttons;
  }

  @RequiredArgsConstructor
  public class AbilityButton extends Button {

    private final Ability ability;

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

      int amount = 1;

      if (ability.getName().equalsIgnoreCase("Strength")) {
        amount = 5;
      } else if (ability.getName().equalsIgnoreCase("Resistance") || ability.getName()
          .equalsIgnoreCase("MedKit")) {
        amount = 3;
      }

      return new ItemBuilder(ability.getItem().clone()).setLore(Lists.newArrayList(
          "&7Type &e/abilities &7to see how it works.",
          "",
          "&7Buy: &6&l♦&e50",
          "",
          "&eClick to purchase!"
      )).amount(amount).build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      int price = 50;

      if (hcfProfile.getGems() < price) {
        player.sendMessage(CC.RED + "You do not have enough gems to buy this.");
        Button.playFail(player);
        return;
      }

      hcfProfile.setGems(hcfProfile.getGems() - price);

      if (ability.getName().equalsIgnoreCase("Strength")) {
        for (int i = 0; i < 5; i++) {
          player.getInventory().addItem(ability.getItem().clone());
        }
      } else if (ability.getName().equalsIgnoreCase("Resistance") || ability.getName()
          .equalsIgnoreCase("MedKit")) {
        for (int i = 0; i < 3; i++) {
          player.getInventory().addItem(ability.getItem().clone());
        }
      } else {
        player.getInventory().addItem(ability.getItem().clone());
      }
    }
  }
}