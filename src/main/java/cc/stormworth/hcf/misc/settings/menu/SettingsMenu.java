package cc.stormworth.hcf.misc.settings.menu;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.misc.settings.Setting;
import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.entity.Player;

public class SettingsMenu extends Menu {

  public String getTitle(final Player player) {
    return "Options";
  }

  @Override
  public boolean isUpdateAfterClick() {
    return true;
  }

  @Override
  public boolean isAutoUpdate() {
    return false;
  }

  public Map<Integer, Button> getButtons(final Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();
    buttons.put(1, Setting.TEAM_NAMETAGS.toButton());
    buttons.put(3, Setting.DEATH_MESSAGES.toButton());
    buttons.put(5, Setting.PUBLIC_CHAT.toButton());
    buttons.put(7, Setting.ENDERPEARL_COOLDOWN_CHAT.toButton());

    buttons.put(getSlot(2, 2), Setting.PARTICLES_BATTLE.toButton());
    buttons.put(getSlot(4, 2), Setting.LANG.toButton());
    buttons.put(getSlot(6, 2), Setting.PAY.toButton());
    return buttons;
  }
}