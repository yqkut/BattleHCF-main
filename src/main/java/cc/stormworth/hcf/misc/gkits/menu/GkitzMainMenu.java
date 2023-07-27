package cc.stormworth.hcf.misc.gkits.menu;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.GlowGlassButton;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class GkitzMainMenu extends Menu {

  int[] glassslots = new int[]{0, 1, 7, 8, 9, 17, 18, 19, 25, 26};

  @Override
  public boolean isAutoUpdate() {
    return false;
  }

  @Override
  public boolean isUpdateAfterClick() {
    return false;
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    for (int slot : glassslots) {
      buttons.put(slot, new GlowGlassButton((byte) 1));
    }

/*        if (Main.getInstance().getMapHandler().isKitMap()) {
            buttons.put(12, new MainMenuButton(CC.YELLOW + "Gkits", Material.EMERALD, new KitsMenu(false, false), Arrays.asList("&7Click to open purchasable kits!")));
            buttons.put(14, new MainMenuButton(CC.YELLOW + "Vip Gkits", Material.REDSTONE, new KitsMenu(false, true), Arrays.asList("&7Click to open vip kits!")));
        } else {
            buttons.put(11, new MainMenuButton(CC.YELLOW + "Vip Gkits", Material.REDSTONE, new KitsMenu(false, true), Arrays.asList("&7Click to open vip kits!")));
            buttons.put(13, new MainMenuButton(CC.YELLOW + "Gkits", Material.EMERALD, new KitsMenu(false, false), Arrays.asList("&7Click to open purchasable kits!")));
            buttons.put(15, new MainMenuButton(CC.YELLOW + "Free Gkits", Material.RED_ROSE, new KitsMenu(true, false), Arrays.asList("&7Click to open playtime kits!")));
        }*/
    return buttons;
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 27;
  }

  @Override
  public String getTitle(Player player) {
    return CC.YELLOW + "Battle Gkits";
  }
}