package cc.stormworth.hcf.misc.kills;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class KillsMenu extends PaginatedMenu {

  private final List<KillInfo> killList;
  private final String name;

  @Override
  public String getPrePaginatedTitle(Player player) {
    return "Kills of " + name;
  }

  @Override
  public Map<Integer, Button> getAllPagesButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (KillInfo kill : killList) {
      buttons.put(buttons.size(), Button.fromItem(
          new ItemBuilder(Material.PAPER)
              .name("&6" + kill.getFormattedDate())
              .addToLore(
                  "",
                  "&eKilled: &6" + UUIDUtils.name(kill.getVictim())
              )
              .build()
      ));
    }

    return buttons;
  }
}