package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.button.ChangePromotionStatusButton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class DemoteMembersMenu extends Menu {

  @NonNull
  Team team;

  public DemoteMembersMenu(@NonNull final Team team) {
    if (team == null) {
      throw new NullPointerException("team");
    }
    this.team = team;
  }

  public String getTitle(final Player player) {
    return CC.YELLOW + "Demote captains/co-leaders";
  }

  public Map<Integer, Button> getButtons(final Player player) {
    Map<Integer, Button> buttons = new HashMap<Integer, Button>();
    int index = 0;
    for (final UUID uuid : this.team.getCoLeaders()) {
      buttons.put(index, new ChangePromotionStatusButton(uuid, this.team, false));
      ++index;
    }
    for (final UUID uuid : this.team.getCaptains()) {
      buttons.put(index, new ChangePromotionStatusButton(uuid, this.team, false));
      ++index;
    }
    return buttons;
  }

  public boolean isAutoUpdate() {
    return true;
  }

  @NonNull
  public Team getTeam() {
    return this.team;
  }
}
