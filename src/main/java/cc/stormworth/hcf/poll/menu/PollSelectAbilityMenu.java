package cc.stormworth.hcf.poll.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.poll.GlobalPoll;
import cc.stormworth.hcf.poll.PollHandler;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@RequiredArgsConstructor
public class PollSelectAbilityMenu extends Menu {

  private final PollHandler pollHandler = Main.getInstance().getPollHandler();

  @Override
  public String getTitle(Player player) {
    return "&eSelect Ability";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    for (int i = 0; i < 9; i++) {
      buttons.put(i,
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    for (Ability ability : Ability.getAbilities()) {
      GlobalPoll poll = pollHandler.getGlobalPoll(ability.getName());

      if (poll == null) {
        continue;
      }

      buttons.put(buttons.size(), new AbilityButton(ability, poll));
    }

    return buttons;
  }

  @RequiredArgsConstructor
  public class AbilityButton extends Button {

    private final Ability ability;
    private final GlobalPoll poll;

    @Override
    public String getName(Player player) {
      return ability.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
      List<String> lore = Lists.newArrayList(ability.getDescription());

      lore.add("&eClick to select");

      return lore;
    }

    @Override
    public Material getMaterial(Player player) {
      return ability.getItem().getType();
    }

    @Override
    public byte getDamageValue(Player player) {
      return (byte) ability.getItem().getDurability();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      new PollConfirmMenu(ability.getName(), ChatColor.GOLD, poll, ability,
          new PollSelectAbilityMenu()).openMenu(player);
    }
  }
}