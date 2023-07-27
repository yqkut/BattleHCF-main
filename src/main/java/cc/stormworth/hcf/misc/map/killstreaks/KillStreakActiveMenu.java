package cc.stormworth.hcf.misc.map.killstreaks;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KillStreakActiveMenu extends Menu {

  public KillStreakActiveMenu() {
    this.setUpdateAfterClick(false);
    this.setAutoUpdate(false);
  }

  public static LinkedHashMap<Player, Integer> getSortedPlayers() {
    final Map<Player, Integer> playerKillstreaks = new HashMap<>();
    for (final Player player : Bukkit.getOnlinePlayers()) {
      final StatsEntry stats = Main.getInstance().getMapHandler().getStatsHandler()
          .getStats(player.getUniqueId());
      if (stats.getKillstreak() > 0) {
        playerKillstreaks.put(player, stats.getKillstreak());
      }
    }
    return sortByValues(playerKillstreaks);
  }

  public static LinkedHashMap<Player, Integer> sortByValues(final Map<Player, Integer> map) {
    final LinkedList<Map.Entry<Player, Integer>> list = new LinkedList<>(map.entrySet());
    Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    final LinkedHashMap<Player, Integer> sortedHashMap = new LinkedHashMap<Player, Integer>();
    for (final Map.Entry<Player, Integer> entry : list) {
      sortedHashMap.put(entry.getKey(), entry.getValue());
    }
    return sortedHashMap;
  }

  public String getTitle(final Player player) {
    return CC.YELLOW + "Active KillStreaks";
  }

  public int size(final Map<Integer, Button> buttons) {
    return 36;
  }

  public Map<Integer, Button> getButtons(final Player player) {
    Map<Integer, Button> buttons = new HashMap<>();
    Map<Player, Integer> sorted = getSortedPlayers();

    if (!sorted.isEmpty()) {
      int index = 0;
      for (final Map.Entry<Player, Integer> entry : getSortedPlayers().entrySet()) {
        if (index > 10) {
          break;
        }

        ++index;

        Team team = Main.getInstance().getTeamHandler().getTeam(entry.getKey());

        String finalIndex = (index == 1 ? ChatColor.GREEN.toString()
            : index == 2 ? ChatColor.YELLOW.toString()
                : index == 3 ? ChatColor.RED.toString() : ChatColor.GRAY.toString()) + index;

        ItemStack item = new ItemBuilder(Material.SKULL_ITEM).data((short) 3).name(entry.getKey().getDisplayName()).setSkullOwner(entry.getKey().getName()).build();

        ItemUtil.renameItem(item, CC.translate(
            finalIndex + ". " + (team != null && team.isMember(player.getUniqueId())
                ? Main.getInstance().getMapHandler().getTeamRelationColor()
                : (team == null && entry.getKey() == player) ? Main.getInstance().getMapHandler()
                    .getTeamRelationColor()
                    : Main.getInstance().getMapHandler().getDefaultRelationColor()) + entry.getKey()
                .getName()));
        ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(Arrays.asList(CC.SEPARATOR,
            "&eAmount: &c" + entry.getValue() + " killstreak" + (entry.getValue() < 1 ? "" : "s"),
            CC.SEPARATOR)));
        item.setItemMeta(meta);

        buttons.put(buttons.size(), new Button() {
          @Override
          public String getName(Player player) {
            return null;
          }

          @Override
          public List<String> getDescription(Player player) {
            return null;
          }

          @Override
          public Material getMaterial(Player player) {
            return null;
          }

          @Override
          public ItemStack getButtonItem(Player player) {
            return item;
          }

          @Override
          public void clicked(Player player, int slot, ClickType clickType) {
          }
        });
      }
    }
    buttons.put(35, new Button() {
      @Override
      public String getName(Player player) {
        return ChatColor.GOLD + "KillStreak Prizes";
      }

      @Override
      public List<String> getDescription(Player player) {
        return CC.translate(
            Arrays.asList(CC.SEPARATOR, "&aClick to see the killstreak prizes", CC.SEPARATOR));
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.GOLD_NUGGET;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        new KillStreakMenu().openMenu(player);
        playNeutral(player);
      }
    });
    return buttons;
  }
}