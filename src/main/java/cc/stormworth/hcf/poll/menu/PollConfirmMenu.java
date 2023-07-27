package cc.stormworth.hcf.poll.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.poll.GlobalPoll;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PollConfirmMenu extends Menu {

  private final String name;
  private final ChatColor color;
  private final GlobalPoll poll;
  private final Ability ability;
  private final Menu menu;

  public PollConfirmMenu(String name, ChatColor color, GlobalPoll poll, Ability ability,
      Menu menu) {
    this.name = name;
    this.color = color;
    this.poll = poll;
    this.ability = ability;
    this.menu = menu;

    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&6Select an option";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {

    Map<Integer, Button> buttons = Maps.newHashMap();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(0, 2), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 0), Button.fromItem(glass.data(orangeData).build()));
    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(yellowData).build()));
    buttons.put(getSlot(8, 2), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(0, new Button() {

      @Override
      public String getName(Player player) {
        return "&cGo Back";
      }

      @Override
      public List<String> getDescription(Player player) {
        return Lists.newArrayList("&7Click to return to the previous page.");
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.BED;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        menu.openMenu(player);
      }
    });

    buttons.put(4, Button.fromItem(
        new ItemBuilder(
            ability == null ?
                name.equalsIgnoreCase("Knockback") ? Material.PAPER : Material.ENDER_PEARL
                : ability.getItem().getType())
            .data(ability == null ? (short) 0 : ability.getItem().getDurability())
            .name(color + name)
            .addToLore("", "&6&l• &f" + poll.getQuestion().get(0),
                ChatColor.WHITE + poll.getQuestion().get(1), "",
                "&eSelect an option!")
            .build()));

    buttons.put(getSlot(2, 1), new Button() {
      @Override
      public String getName(Player player) {
        return "&c&lPoop";
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
      public void clicked(Player player, int slot, ClickType clickType) {
        if (poll.hasVoted(player)) {
          player.sendMessage(CC.translate("&cYou have already voted in this poll."));
          player.closeInventory();
          player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
          return;
        }

        player.sendMessage(CC.translate("&eThanks for helping us to improve."));
        poll.vote(player, 1);
      }
    });

    buttons.put(getSlot(4, 1), new Button() {
      @Override
      public String getName(Player player) {
        return "&a&lOk";
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
      public void clicked(Player player, int slot, ClickType clickType) {
        if (poll.hasVoted(player)) {
          player.sendMessage(CC.translate("&cYou have already voted in this poll."));
          player.closeInventory();
          player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
          return;
        }

        player.sendMessage(CC.translate("&eThanks for helping us to improve."));
        poll.vote(player, 2);
      }
    });

    buttons.put(getSlot(6, 1), new Button() {
      @Override
      public String getName(Player player) {
        return "&2&lGreat";
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
        return 13;
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
        poll.vote(player, 3);
      }

    });

    return buttons;
  }
}