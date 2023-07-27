package cc.stormworth.hcf.team.notes;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class TeamNotesMenu extends PaginatedMenu {

    private final Team team;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("&6&l" + team.getName() + "'s Notes &7(&f" + team.getNotes().size() + "&7)");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        int index = 0;
        for (TeamNote note : team.getNotes()) {
            buttons.put(index, Button.fromItem(new ItemBuilder(Material.PAPER)
                    .name("&6&lNote: &e#" + (index + 1))
                    .addToLore(
                            "",
                            "&7- &eIssued on: &f" + Team.DATE_FORMAT.format(note.getIssuedOn()),
                            "&7- &eBy: &f" + note.getStaff(),
                            "&7- &eReason: &f" + note.getReason(),
                            ""
                    )
                    .build()));
        }

        return buttons;
    }
}
