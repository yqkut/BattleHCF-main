package cc.stormworth.hcf.gemsshop.submenus;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.gemsshop.GemsShopMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

public class GKitsMenu extends Menu {

  private final HCFProfile hcfProfile;

  public GKitsMenu(Player player) {
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

    for (int i = 0; i < 9; i++) {
      buttons.put(i,
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? yellowData : orangeData).build()));
    }

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(yellowData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? yellowData : orangeData).build()));
    }

    buttons.put(getSlot(4, 0), Button.fromItem(new ItemBuilder(Material.BOOK)
        .name("&6&lGems Shop")
        .addToLore("",
            "&6&l| &fBalance: &e" + HCFProfile.get(player).getGems(),
            " ",
            "&7Purchase gems at &6&nstore.battle.rip")
        .build()));

    if (Main.getInstance().getMapHandler().isKitMap()) {
      buttons.put(getSlot(1, 1), new GKitButton("Donor", Material.DIAMOND_CHESTPLATE, 150));
      buttons.put(getSlot(3, 1), new GKitButton("Bard premium", Material.GOLD_CHESTPLATE, 150));
      buttons.put(getSlot(5, 1),
          new GKitButton("Archer premium", Material.LEATHER_CHESTPLATE, 150));
      buttons.put(getSlot(7, 1),
          new GKitButton("Rogue premium", Material.CHAINMAIL_CHESTPLATE, 150));
    } else {
      buttons.put(getSlot(1, 1), new GKitButton("Donor", Material.DIAMOND_CHESTPLATE, 300));
      buttons.put(getSlot(3, 1), new GKitButton("Bard", Material.GOLD_CHESTPLATE, 300));
      buttons.put(getSlot(5, 1), new GKitButton("Archer", Material.LEATHER_CHESTPLATE, 300));
      buttons.put(getSlot(7, 1), new GKitButton("Rogue", Material.CHAINMAIL_CHESTPLATE, 300));
    }

    buttons.put(getSlot(4, 2), Button.fromItem(new ItemBuilder(Material.BED)
            .name("&cGo back")
            .addToLore("&7Click to return to previous page.")
            .build(),
        (other) -> new GemsShopMenu().openMenu(player)));

    return buttons;
  }

  @RequiredArgsConstructor
  public class GKitButton extends Button {

    private final String name;
    private final Material material;
    private final int price;

    @Override
    public String getName(Player player) {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + name;
    }

    @Override
    public List<String> getDescription(Player player) {
      return Lists.newArrayList(
          "",
          "&6&l. &fUses: &e1",
          "",
          "&6&l. &fPrice: &e" + price,
          ""
      );
    }

    @Override
    public Material getMaterial(Player player) {
      return material;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

      if (hcfProfile.getGems() < price) {
        player.sendMessage(CC.RED + "You do not have enough gems to buy this.");
        return;
      }

      hcfProfile.setGems(hcfProfile.getGems() - price);

      player.sendMessage(CC.GREEN + "You have successfully bought the GKit Diamond.");

      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          "voucher give " + name.toLowerCase().replace(" ", "") + " " + player.getName() + " 1");
    }
  }

}