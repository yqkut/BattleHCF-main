package cc.stormworth.hcf.poll.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.poll.GlobalPoll;
import cc.stormworth.hcf.poll.PollHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PollMenu extends Menu {

  private final PollHandler pollHandler = Main.getInstance().getPollHandler();

  public PollMenu() {
    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&6&lPolls";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {

    Map<Integer, Button> buttons = Maps.newHashMap();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    buttons.put(getSlot(0, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(0, 4), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(0, 5), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(1, 5), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(7, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 0), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 4), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(7, 5), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 5), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(4, 1),
        Button.fromItem(new ItemBuilder(Material.BOOK_AND_QUILL)
            .name("&6&lPolls")
            .addToLore(
                "",
                "&6&l• &fGive us your opinion about",
                "&fchanges you'd like to see in the server.",
                "",
                "&eChoose a poll!"
            ).build()));

    int x = 2;
    for (Entry<String, GlobalPoll> entry : pollHandler.getGlobalPolls().entrySet()) {
      String name = entry.getKey();
      GlobalPoll poll = entry.getValue();

      if (name.equalsIgnoreCase("Knockback") || name.equalsIgnoreCase("Cross Pearl")) {
        buttons.put(getSlot(x, 3),
            new PollButton(name, ChatColor.GOLD, poll));
      }

      x += 4;
    }

    buttons.put(getSlot(4, 3), new Button() {
      @Override
      public String getName(Player player) {
        return "&6&lAbilities";
      }

      @Override
      public List<String> getDescription(Player player) {
        return Lists.newArrayList(
            "",
            "&6&l• &fGive us your opinion",
            "&fabout ender pearls.",
            "",
            "&eClick to select ability!"
        );
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.BLAZE_POWDER;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        new PollSelectAbilityMenu().openMenu(player);
      }
    });

    return buttons;
  }

  @RequiredArgsConstructor
  public class PollButton extends Button {

    private final String name;
    private final ChatColor color;
    private final GlobalPoll poll;

    @Override
    public String getName(Player player) {
      return color.toString() + ChatColor.BOLD + name + " ";
    }

    @Override
    public List<String> getDescription(Player player) {
      return CC.translate(
          Lists.newArrayList(
              "",
              "&6&l• &f" + poll.getQuestion().get(0),
              ChatColor.WHITE + poll.getQuestion().get(0),
              "",
              "&eClick to vote!"
          )
      );
    }


    @Override
    public ItemStack getButtonItem(Player player) {
      return super.getButtonItem(player);
    }

    @Override
    public Material getMaterial(Player player) {
      if (name.equalsIgnoreCase("Knockback")) {
        return Material.PAPER;
      } else if (name.equalsIgnoreCase("Abilities")) {
        return Material.BLAZE_POWDER;
      } else {
        return Material.ENDER_PEARL;
      }
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      if (name.equalsIgnoreCase("Knockback") || name.equalsIgnoreCase("Cross Pearl")) {
        new PollConfirmMenu(name, color, poll, null, new PollMenu()).openMenu(player);
      }
    }
  }
}