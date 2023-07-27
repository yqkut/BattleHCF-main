package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.button.MuteButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MuteMenu extends Menu {
    private final Team team;

    public MuteMenu(final Team team) {
        this.team = team;
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int i = 0; i < 9; ++i) {
            if (i == 1) {
                buttons.put(i, new MuteButton(5, this.team));
            } else if (i == 3) {
                buttons.put(i, new MuteButton(15, this.team));
            } else if (i == 5) {
                buttons.put(i, new MuteButton(30, this.team));
            } else if (i == 7) {
                buttons.put(i, new MuteButton(60, this.team));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
            }
        }
        return buttons;
    }

    public String getTitle(final Player player) {
        return CC.YELLOW + "Mute " + this.team.getName();
    }
}
