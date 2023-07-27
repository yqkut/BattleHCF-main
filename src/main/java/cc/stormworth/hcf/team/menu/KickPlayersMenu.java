package cc.stormworth.hcf.team.menu;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.menu.button.KickPlayerButton;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.*;

public class KickPlayersMenu extends Menu {
    @NonNull
    Team team;

    public KickPlayersMenu(@NonNull final Team team) {
        if (team == null) {
            throw new NullPointerException("team");
        }
        this.team = team;
    }

    public String getTitle(final Player player) {
        return CC.YELLOW + "Players in " + this.team.getName();
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        int index = 0;
        final ArrayList<UUID> uuids = new ArrayList<UUID>();
        uuids.addAll(this.team.getMembers());
        Collections.sort(uuids, (u1, u2) -> UUIDUtils.name(u1).toLowerCase().compareTo(UUIDUtils.name(u2).toLowerCase()));
        for (final UUID uuid : uuids) {
            buttons.put(index, new KickPlayerButton(uuid, this.team));
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
