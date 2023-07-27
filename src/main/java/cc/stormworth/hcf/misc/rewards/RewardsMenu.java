package cc.stormworth.hcf.misc.rewards;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardsMenu extends Menu {

  private final ItemStack[] rewards = Main.getInstance().getRewardsManager().getRewards();

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 9 * 3;
  }

  @Override
  public String getTitle(Player player) {
    return CC.translate("&e&lRewards");
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (ItemStack reward : rewards) {
      buttons.put(buttons.size(), Button.fromItem(reward));
    }

    return buttons;
  }
}