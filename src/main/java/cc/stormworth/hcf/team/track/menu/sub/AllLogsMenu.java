package cc.stormworth.hcf.team.track.menu.sub;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import cc.stormworth.hcf.team.track.menu.TrackerEntryMenu;
import cc.stormworth.hcf.team.track.menu.TrackerMenu;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class AllLogsMenu extends Menu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "All logs";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
                .name("&cBack")
                .addToLore(
                        "",
                        "&7Click to go back to the main menu.")
                .build(), (other) -> new TrackerMenu(team).openMenu(other)));

        int slot = 1;
        int index = 0;

        while (slot < 54 - 10 && TeamActionType.values().length > index) {
            TeamActionType trackEntry = TeamActionType.values()[index];

            buttons.put(slot++, Button.fromItem(new ItemBuilder(Material.BOOK).name(ChatColor.YELLOW + trackEntry.getName()).build(), (other) -> {
                player.closeInventory();
                other.sendMessage(CC.translate("&eLoading logs..."));

                TaskUtil.runAsync(Main.getInstance(), () -> {
                    new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, trackEntry), new AllLogsMenu(team)).openMenu(other);
                });
            }));

            index++;

            if ((slot - 8) % 9 == 0) {
                slot += 2;
            }
        }

        return buttons;
    }
}
