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
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RankLogsMenu extends Menu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Rank Logs";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        short orangeData = 4;
        short yellowData = 5;

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
                .name(" ")
                .setGlowing(true);

        for (int i = 0; i < 9; i++) {
            buttons.put(i,
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
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

        buttons.put(getSlot(2, 1), Button.fromItem(
                new ItemBuilder(Material.SIGN)
                        .name(CC.translate("&e&lMember logs"))
                        .addToLore("", "&7Click to view member logs.")
                        .build(), (other)-> {

                    player.closeInventory();
                    other.sendMessage(CC.translate("&eLoading logs..."));

                    TaskUtil.runAsync(Main.getInstance(), () -> {

                        List<TrackEntry> track = Lists.newArrayList();

                        track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.PLAYER_JOINED));
                        track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.MEMBER_KICKED));
                        track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.MEMBER_REMOVED));

                        new TrackerEntryMenu(track, new RankLogsMenu(team)).openMenu(other);
                    });
                }));

        buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.IRON_INGOT).name("&e&lCaptain logs")
                .addToLore(
                        "&7",
                        "&7Click to view captain logs."
                ).build(), (other)-> {

            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> {

                List<TrackEntry> track = Lists.newArrayList();

                track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.PROMOTED_TO_CAPTAIN));
                track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.DEMOTED_FROM_CAPTAIN));

                new TrackerEntryMenu(track, new RankLogsMenu(team)).openMenu(other);
            });

        }));

        buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.DIAMOND_SWORD).name("&e&lCo-Leader logs")
                .addToLore(
                        "&7",
                        "&7Click to view Co-Leader logs."
                ).build(), (other)-> {

            player.closeInventory();
            other.sendMessage(CC.translate("&eLoading logs..."));

            TaskUtil.runAsync(Main.getInstance(), () -> {

                List<TrackEntry> track = Lists.newArrayList();

                track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.PROMOTED_TO_CO_LEADER));
                track.addAll(TeamTrackerManager.loadEntry(team, TeamActionType.DEMOTED_FROM_CO_LEADER));

                new TrackerEntryMenu(track, new RankLogsMenu(team)).openMenu(other);
            });
        }));

        return buttons;
    }
}
