package cc.stormworth.hcf.poll.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.poll.Poll;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PollQuickMenu extends Menu {

  private final Poll poll;

  public PollQuickMenu(Poll poll) {
    this.poll = poll;
    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&bPoll Quick";
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 9 * 3;
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    buttons.put(getSlot(2, 1), new Button() {
      @Override
      public String getName(Player player) {
        return "&c&lNO &7[&f" + poll.getNo() + "&7 Votes]";
      }

      @Override
      public List<String> getDescription(Player player) {
        return Lists.newArrayList(
            "",
            poll.hasVoted(player) ? "&c&lYou have already vote." : "&eClick to vote!"
        );
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.WOOL;
      }

      @Override
      public byte getDamageValue(Player player) {
        return 14;
      }

      @Override
      public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(super.getButtonItem(player))
            .setGlowing(poll.hasVoted(player))
            .build();
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        if (poll.hasVoted(player)) {
          player.sendMessage(CC.translate("&cYou have already voted in this poll."));
          player.closeInventory();
          player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
          return;
        }

        player.sendMessage(CC.translate("&eThanks for helping us to improve."));
        poll.voteNo(player);
      }
    });

    buttons.put(getSlot(6, 1), new Button() {
      @Override
      public String getName(Player player) {
        return "&a&lYES &7[&f" + poll.getYes() + "&7 Votes]";
      }

      @Override
      public List<String> getDescription(Player player) {
        return Lists.newArrayList(
            "",
            poll.hasVoted(player) ? "&c&lYou have already vote." : "&eClick to vote!"
        );
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.WOOL;
      }

      @Override
      public byte getDamageValue(Player player) {
        return 5;
      }

      @Override
      public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(super.getButtonItem(player))
            .setGlowing(poll.hasVoted(player))
            .build();
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        if (poll.hasVoted(player)) {
          player.sendMessage(CC.translate("&cYou have already voted in this poll."));
          player.closeInventory();
          player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
          return;
        }

        player.sendMessage(CC.translate("&eThanks for helping us to improve."));
        poll.voteYes(player);
      }
    });

    buttons.put(getSlot(4, 1), Button.fromItem(
        new ItemBuilder(Material.BOOK_AND_QUILL)
            .name("&6&lPoll")
            .addToLore(
                "",
                "&6&l• &f" + poll.getQuestion(),
                "&eSelect a option"
            ).build()
    ));

    return buttons;
  }
}