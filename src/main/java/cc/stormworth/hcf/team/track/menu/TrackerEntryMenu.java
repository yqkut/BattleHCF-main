package cc.stormworth.hcf.team.track.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.menu.pagination.PageButton;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.hcf.team.track.TrackEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TrackerEntryMenu extends PaginatedMenu {

    private final List<TrackEntry> trackEntries;
    private final Menu backMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Team logs";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int slot = 19;
        int index = getPage() * 21 - 21;

        while (slot < 54 - 10 && trackEntries.size() > index) {
            TrackEntry trackEntry = trackEntries.get(index);

            buttons.put(slot++, new TrackerEntryButton(trackEntry));

            index++;

            if ((slot - 8) % 9 == 0) {
                slot += 2;
            }
        }

        return buttons;
    }

    @Override
    public int getPages(Player player) {
        return (trackEntries.size() - 1) / 27 + 1;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 21;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        Map<Integer, Button> global = getGlobalButtons(player);

        if (global != null) {
            buttons.putAll(global);
        }

        buttons.put(4, Button.fromItem(new ItemBuilder(Material.BED)
                .name("&cBack")
                .addToLore(
                        "",
                        "&7Click to go back to the main menu.")
                .build(), backMenu::openMenu));

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        buttons.putAll(getAllPagesButtons(player));

        return buttons;
    }


    @RequiredArgsConstructor
    public class TrackerEntryButton extends Button{

        private final TrackEntry trackEntry;

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + trackEntry.getType().getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = Lists.newArrayList();

            trackEntry.getFormattedData().forEach((key, value) -> description.add(ChatColor.YELLOW + key + ": " + ChatColor.GOLD + value));

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.PAPER;
        }
    }
}
