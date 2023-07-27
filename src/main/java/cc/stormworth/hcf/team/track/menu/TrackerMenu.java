package cc.stormworth.hcf.team.track.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import cc.stormworth.hcf.team.track.menu.sub.*;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class TrackerMenu extends Menu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Teams logs";
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
            buttons.put(i,
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        for (int i = 0; i < 9; i++) {
            buttons.put(getSlot(i, 4),
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        for (int i = 0; i < 4; i++) {
            buttons.put(getSlot(0, i),
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        for (int i = 0; i < 4; i++) {
            buttons.put(getSlot(8, i),
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        buttons.put(getSlot(1, 1), Button.fromItem(new ItemBuilder(Material.DIRT)
                .name("&6General logs")
                        .addToLore(
                                "",
                                        "&7Click to view general logs of team")
                .build(), (other) -> new GeneralLogsMenu(team).openMenu(other)));

        buttons.put(getSlot(3, 1), Button.fromItem(new ItemBuilder(Material.PAPER).name("&6Chat history")
                .addToLore(
                "",
                        "&7Click to view all chat logs of team").build(), (other) -> new ChatHistoryMenu(team).openMenu(other)));

        buttons.put(getSlot(5, 1), Button.fromItem(new ItemBuilder(Material.GOLD_INGOT).addToLore(
                "",
                        "&7Click to view all financial & land logs of team").name("&6Financial & Land").build(), (other) -> new FinancialLandMenu(team).openMenu(other)));

        buttons.put(getSlot(7, 1), Button.fromItem(new ItemBuilder(Material.COMMAND).name("&6Connections").addToLore(
                "",
                        "&7Click to view all player connections of team").build(), (other) -> new ConnectionMenu(team).openMenu(other)));

        buttons.put(getSlot(1, 3), Button.fromItem(new ItemBuilder(Material.SKULL_ITEM).data((short) 3).name("&6Ranks logs").addToLore(
                "",
                        "&7Click to view all members logs of team").build(), (other) -> new RankLogsMenu(team).openMenu(other)));

        buttons.put(getSlot(3, 3), Button.fromItem(new ItemBuilder(Material.SKULL_ITEM).name("&6Deaths").addToLore(
                "",
                        "&7Click to view members deaths logs of team").build(), (other) -> new DeathLogsMenu(team).openMenu(other)));

        buttons.put(getSlot(5, 3), Button.fromItem(new ItemBuilder(Material.DIAMOND_SWORD).name("&6Kills").addToLore(
                "",
                        "&7Click to view all members kills logs of team").build(), (other) -> {
            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP), new TrackerMenu(team)).openMenu(other));
        }));

        buttons.put(getSlot(7, 3), Button.fromItem(new ItemBuilder(Material.BOOK).name("&6All logs").addToLore(
                "",
                        "&7Click to view all logs of team").build(), (other) -> new AllLogsMenu(team).openMenu(other)));

        return buttons;
    }
}
