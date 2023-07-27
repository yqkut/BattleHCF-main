package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.kt.util.Callback;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.team.menu.button.BooleanButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {
    private final String title;
    private final Callback<Boolean> response;

    public ConfirmMenu(final String title, final Callback<Boolean> response) {
        this.title = title;
        this.response = response;
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
            }
        }
        return buttons;
    }

    public String getTitle(final Player player) {
        return this.title;
    }
}
