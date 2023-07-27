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
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class ChatHistoryMenu extends Menu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Chat History";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        short orangeData = 2;
        short yellowData = 5;

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
                .name(" ")
                .setGlowing(true);

        for (int i = 0; i < 9; i++) {
            buttons.put(i, Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));

        buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

        for (int i = 0; i < 9; i++) {
            buttons.put(getSlot(i, 2), Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
                .name("&cBack")
                .addToLore(
                        "",
                        "&7Click to go back to the main menu.")
                .build(), (other) -> new TrackerMenu(team).openMenu(other)));

        buttons.put(getSlot(2, 1), Button.fromItem(new ItemBuilder(Material.INK_SACK).data((short) 10).name("&e&lTeam Chat History").build(), other -> {
            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, TeamActionType.TEAM_CHAT_MESSAGE), new ChatHistoryMenu(team)).openMenu(other));
        }));


        buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.INK_SACK).data((short) 12).name("&e&lAlly Chat History").build(), other -> {
            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, TeamActionType.ALLY_CHAT_MESSAGE), new ChatHistoryMenu(team)).openMenu(other));
        }));


        buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.INK_SACK).data((short) 2).name("&e&lOfficer Chat History").build(), other -> {
            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, TeamActionType.OFFICER_CHAT_MESSAGE), new ChatHistoryMenu(team)).openMenu(other));
        }));

        return buttons;
    }
}
