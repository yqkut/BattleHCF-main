package cc.stormworth.hcf.bounty.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BountiesMenu extends PaginatedMenu {

  public BountiesMenu() {
    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&b&lBounties";
  }

  @Override
  public String getPrePaginatedTitle(Player player) {
    return "&b&lBounties";
  }


  @Override
  public int getMaxItemsPerPage(Player player) {
    return 27;
  }

  @Override
  public Map<Integer, Button> getAllPagesButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (BountyPlayer bountyPlayer : BountyPlayer.getBounties().values()) {
      if (bountyPlayer.isReady()) {
        buttons.put(buttons.size(), new BountyButton(bountyPlayer));
      }
    }

    return buttons;
  }

  @RequiredArgsConstructor
  public class BountyButton extends Button {

    private final BountyPlayer bountyPlayer;

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
      String name = bountyPlayer.getTarget().getName();

      List<String> lore = new ArrayList<>();

      lore.add("");
      lore.add(CC.translate("&7Bounty:"));
      lore.add("   ");

      if (bountyPlayer.getBalance() > 0) {
        lore.add(CC.translate("&7- &2$&a" + NumberUtils.addComma(bountyPlayer.getBalance())));
      }

      bountyPlayer.getRewards().forEach(itemStack -> {
        String displayName = (
            itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null
                ? itemStack.getItemMeta().getDisplayName()
                : WordUtils.capitalize(itemStack.getType().name().toLowerCase().replace("_", " ")));

        lore.add(CC.translate("&7- &f" + displayName));
      });

      lore.add("     ");
      lore.add("&eLeft Click to track &6" + name + "&e.");
      lore.add("&eRight click to see items rewards.");
      lore.add("&eUse &6/bounty " + name + " &eto add more rewards.");

      return new ItemBuilder(Material.SKULL_ITEM).name("&7" + name).data((short) 3)
          .setLore(lore)
          .setSkullOwner(name).build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      player.closeInventory();

      if (clickType.isLeftClick()) {
        Button.playSuccess(player);
        player.performCommand(
            "focus " + bountyPlayer.getTarget().getName());
        return;
      }

      player.closeInventory();

      if (bountyPlayer.getTarget().isOnline()) {
        Button.playNeutral(player);
        new BountyRewardsMenu(bountyPlayer).openMenu(player);
        return;
      }

      player.sendMessage(CC.translate("&cThis player is not online."));
    }

  }
}