package cc.stormworth.hcf.misc.partnerpackages;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @Author NulledCode
 * @Plugin BattleHCF
 * @Date 2022-04
 */
public class PackagesMenu extends Menu {

  public PackagesMenu() {
    this.setAutoUpdate(false);
    this.setUpdateAfterClick(false);
  }

  @Override
  public String getTitle(Player player) {
    return CC.GOLD + "PPItems Perks";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    for (ItemStack item : EditPackageListener.PPItems) {
      buttons.put(buttons.size(), new Button() {
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
          return item;
        }
      });
    }

    return buttons;
  }
}