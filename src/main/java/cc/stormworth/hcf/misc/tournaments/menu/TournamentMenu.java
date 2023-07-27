package cc.stormworth.hcf.misc.tournaments.menu;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.misc.tournaments.TournamentType;
import cc.stormworth.hcf.misc.tournaments.menu.button.TournamentButton;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public class TournamentMenu extends Menu {

    public TournamentMenu() {
        super("Host a event");
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();
        int integer = 0;
        for (TournamentType type : TournamentType.values()) {
            buttonMap.put(integer, new TournamentButton(type));
            integer++;
        }
        return buttonMap;
    }
}
