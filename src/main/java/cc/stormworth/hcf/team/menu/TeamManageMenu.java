package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.button.DisbandTeamButton;
import cc.stormworth.hcf.team.menu.button.OpenKickMenuButton;
import cc.stormworth.hcf.team.menu.button.OpenMuteMenuButton;
import cc.stormworth.hcf.team.menu.button.RenameButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeamManageMenu extends Menu {
    private final Team team;

    public TeamManageMenu(final Team team) {
        this.team = team;
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int i = 0; i < 9; ++i) {
            if (i == 1) {
                buttons.put(i, new RenameButton(this.team));
            } else if (i == 3) {
                buttons.put(i, new OpenMuteMenuButton(this.team));
            } else if (i == 5) {
                buttons.put(i, new OpenKickMenuButton(this.team));
            } else if (i == 7) {
                buttons.put(i, new DisbandTeamButton(this.team));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
            }
        }
        return buttons;
    }

    public String getTitle(final Player player) {
        return CC.YELLOW + "Manage " + this.team.getName();
    }
}
