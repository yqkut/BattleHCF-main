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
import cc.stormworth.hcf.team.track.TrackEntry;
import cc.stormworth.hcf.team.track.menu.TrackerEntryMenu;
import cc.stormworth.hcf.team.track.menu.TrackerMenu;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class GeneralLogsMenu extends Menu {

    private final Team team;

    public GeneralLogsMenu(Team team) {
        this.team = team;
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&cGeneral Logs");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        short orangeData = 1;
        short yellowData = 2;

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

        TrackEntry entry = TeamTrackerManager.loadEntry(team, TeamActionType.PLAYER_CREATE_TEAM).get(0);

        List<String> description = Lists.newArrayList();

        entry.getFormattedData().forEach((key, value) -> description.add(ChatColor.YELLOW + key + ": " + ChatColor.GOLD + value));

        buttons.put(getSlot(1, 1), Button.fromItem(
                new ItemBuilder(Material.SIGN)
                        .name(CC.translate("&e&lCreate At"))
                        .setLore(description)
                        .build(), (other)-> {

        }));

        buttons.put(getSlot(3, 1), Button.fromItem(new ItemBuilder(Material.EYE_OF_ENDER).name("&e&lRaid logs")
                .addToLore(
                        "&7",
                        "&7Click to view raid logs."
                ).build(), (other)-> {

            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, TeamActionType.TEAM_NOW_RAIDABLE), new GeneralLogsMenu(team)).openMenu(other));

        }));

        buttons.put(getSlot(5, 1), Button.fromItem(new ItemBuilder(Material.ENDER_PEARL).name("&e&lUnRaid logs")
                .addToLore(
                        "&7",
                        "&7Click to view unraid logs."
                ).build(), (other)-> {

            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> new TrackerEntryMenu(TeamTrackerManager.loadEntry(team, TeamActionType.TEAM_NO_LONGER_RAIDABLE), new GeneralLogsMenu(team)).openMenu(other));

        }));

        buttons.put(getSlot(7, 1), Button.fromItem(new ItemBuilder(Material.EXP_BOTTLE).name("&e&lPoints logs")
                .addToLore(
                        "&7",
                        "&7Click to view points logs."
                ).build(), (other)-> new PointLogsMenu(team).openMenu(other)));

        return buttons;
    }
}
