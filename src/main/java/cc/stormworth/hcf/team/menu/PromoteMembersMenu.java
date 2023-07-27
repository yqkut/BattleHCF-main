package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.button.ChangePromotionStatusButton;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PromoteMembersMenu extends Menu {
    @NonNull
    Team team;

    public PromoteMembersMenu(@NonNull final Team team) {
        if (team == null) {
            throw new NullPointerException("team");
        }
        this.team = team;
    }

    public String getTitle(final Player player) {
        return CC.YELLOW + "Members of " + this.team.getName();
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        int index = 0;
        for (final UUID uuid : this.team.getMembers()) {
            if (!this.team.isOwner(uuid) && !this.team.isCoLeader(uuid)) {
                buttons.put(index, new ChangePromotionStatusButton(uuid, this.team, true));
                ++index;
            }
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
