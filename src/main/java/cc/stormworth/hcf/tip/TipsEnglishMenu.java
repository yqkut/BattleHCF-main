package cc.stormworth.hcf.tip;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PageButton;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TipsEnglishMenu extends PaginatedMenu {

  @Override
  public String getTitle(Player player) {
    return "&6&lTips";
  }

  @Override
  public String getPrePaginatedTitle(Player player) {
    return "&6&lTips";
  }

  @Override
  public int getPages(Player player) {
    return (Main.getInstance().getTipManager().getTips().size() - 1) / 20 + 1;
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

    buttons.put(0, new PageButton(-1, this));
    buttons.put(8, new PageButton(1, this));

    buttons.putAll(getAllPagesButtons(player));

    return buttons;
  }

  @Override
  public Map<Integer, Button> getGlobalButtons(Player player) {

    Map<Integer, Button> buttons = Maps.newHashMap();

    buttons.put(4,
        Button.fromItem(new ItemBuilder(Material.ENCHANTED_BOOK).name("&eEnglish").build()));

    return buttons;
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

    List<Tip> tips = Main.getInstance().getTipManager().getTips().stream().filter(Tip::isEnable).collect(Collectors.toList());

    while (slot < 54 - 10 && tips.size() > index) {
      Tip tip = tips.get(index);

      buttons.put(slot++, Button.fromItem(tip.getEnglishItem(),
          (other) -> {
            player.closeInventory();
            player.sendMessage("");
            CC.translate(tip.getMessages_english()).forEach(player::sendMessage);
            player.sendMessage("");
          }));

      index++;

      if ((slot - 8) % 9 == 0) {
        slot += 2;
      }
    }

    return buttons;
  }
}