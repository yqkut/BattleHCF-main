package cc.stormworth.hcf.bounty.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BountyRewardsMenu extends Menu {

  private final BountyPlayer bountyPlayer;

  @Override
  public String getTitle(Player player) {
    return "&6Rewards of &f" + bountyPlayer.getTarget().getName();
  }

  public BountyRewardsMenu(BountyPlayer bountyPlayer) {
    this.bountyPlayer = bountyPlayer;

    setUpdateAfterClick(false);
    setAutoUpdate(false);
  }


  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    buttons.put(4, Button.fromItem(new ItemBuilder(Material.DOUBLE_PLANT)
        .name("&a&lBounty Balance")
        .addToLore(
            "",
            "&7Bounty Balance: &a" + NumberUtils.addComma(bountyPlayer.getBalance()))
        .build()));

    buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
        .name("&cGo back")
        .addToLore(
            "",
            "&7Click to go back")
        .build(), (other) -> {
      Button.playNeutral(other);
      new BountiesMenu().openMenu(other);
    }));

    int slot = 18;

    for (ItemStack itemStack : bountyPlayer.getRewards()) {
      buttons.put(slot++, Button.fromItem(itemStack));
    }

    return buttons;
  }
}