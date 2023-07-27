package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.button.DTRButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DTRMenu extends Menu {
    Team team;

    public DTRMenu(final Team team) {
        this.team = team;
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new DTRButton(this.team, false));
            } else if (i == 5) {
                buttons.put(i, new DTRButton(this.team, true));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
            }
        }
        return buttons;
    }

    public String getTitle(final Player player) {
        return CC.YELLOW + "Manage DTR";
    }
}