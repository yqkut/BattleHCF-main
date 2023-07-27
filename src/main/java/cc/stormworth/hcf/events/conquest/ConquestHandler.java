package cc.stormworth.hcf.events.conquest;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.conquest.game.ConquestGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

public class ConquestHandler {

    public static final String PREFIX = ChatColor.YELLOW + "[Conquest]";

    public static final int POINTS_DEATH_PENALTY = 20;
    public static final String KOTH_NAME_PREFIX = "conquest-";
    public static final int TIME_TO_CAP = 30;

    @Getter
    @Setter
    private ConquestGame game = null;

    public static int getPointsToWin() {
        return Main.getInstance().getMapHandler().getConquestWinPoints();
    }
}