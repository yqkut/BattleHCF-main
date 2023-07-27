package cc.stormworth.hcf.misc.map.leaderboards;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.misc.map.stats.command.StatsTopCommand.StatsObjective;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.team.TeamTopCommand;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&6&lLeaderboards";
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 9 * 3;
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {

    Map<Integer, Button> buttons = Maps.newHashMap();

    buttons.put(getSlot(1, 1), new KillsButton());

    buttons.put(getSlot(3, 1), new DeathsButton());

    buttons.put(getSlot(5, 1), new KDRButton());

    buttons.put(getSlot(7, 1), new TeamPointsButton());

    return buttons;
  }

  public class KillsButton extends Button {

    @Override
    public String getName(Player player) {
      return "&e&lKills Leaderboard";
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

      List<String> description = Lists.newArrayList();

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      String name = "";

      int index = 1;
      for (final Map.Entry<StatsEntry, String> entry : Main.getInstance().getMapHandler()
          .getStatsHandler().getLeaderboards(StatsObjective.KILLS, 10).entrySet()) {

        if (index == 1) {
          name = UUIDUtils.name(entry.getKey().getOwner());
        }

        description.add(
            "&7" + index++ + ". &6" + UUIDUtils.name(entry.getKey().getOwner()) + " &7[&e"
                + entry.getValue() + "&7]");
      }

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      return new ItemBuilder(Material.SKULL_ITEM)
          .data((short) 3)
          .name("&6&lKills Leaderboard")
          .setSkullOwner(name)
          .setLore(description).build();
    }
  }

  public class DeathsButton extends Button {

    @Override
    public String getName(Player player) {
      return "&c&lDeaths Leaderboard";
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

      List<String> description = Lists.newArrayList();

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      String name = "";

      int index = 1;
      for (final Map.Entry<StatsEntry, String> entry : Main.getInstance().getMapHandler()
          .getStatsHandler().getLeaderboards(
              StatsObjective.DEATHS, 10).entrySet()) {

        if (index == 1) {
          name = UUIDUtils.name(entry.getKey().getOwner());
        }

        description.add(
            "&7" + index++ + ". &4" + UUIDUtils.name(entry.getKey().getOwner()) + " &7[&c"
                + entry.getValue() + "&7]");
      }

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      return new ItemBuilder(Material.SKULL_ITEM)
          .data((short) 3)
          .name("&c&lDeaths Leaderboard")
          .setSkullOwner(name)
          .setLore(description).build();
    }
  }


  public class KDRButton extends Button {

    @Override
    public String getName(Player player) {
      return "&a&lKDR Leaderboard";
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

      List<String> description = Lists.newArrayList();

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      String name = "";

      int index = 1;
      for (final Map.Entry<StatsEntry, String> entry : Main.getInstance().getMapHandler()
          .getStatsHandler().getLeaderboards(
              StatsObjective.KD, 10).entrySet()) {

        if (index == 1) {
          name = UUIDUtils.name(entry.getKey().getOwner());
        }

        description.add(
            "&7" + index++ + ". &a" + UUIDUtils.name(entry.getKey().getOwner()) + " &7[&2"
                + entry.getValue() + "&7]");
      }

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM)
          .data((short) 3)
          .name("&a&lKDR Leaderboard")
          .setLore(description);

      if (name.equals("")) {
        itemBuilder.setSkullOwner("MHF_Question");
      }

      return itemBuilder.build();
    }
  }

  public class TeamPointsButton extends Button {

    @Override
    public String getName(Player player) {
      return "&d&lTeams Leaderboard";
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

      List<String> description = Lists.newArrayList();

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamTopCommand.getTopTeams();

      String name = "";

      int index = 1;
      for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

        if (teamEntry.getKey().getOwner() == null) {
          continue;
        }

        if (11 <= index) {
          break;
        }

        if (index == 1) {
          name = UUIDUtils.name(teamEntry.getKey().getOwner());
        }

        description.add(
            "&7" + index++ + ". &d" + teamEntry.getKey().getName() + " &7[&5"
                + teamEntry.getValue() + "&7]");
      }

      description.add(
          ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));

      ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM)
          .data((short) 3)
          .name("&d&lTeams Leaderboard")
          .setLore(description);

      if (name.equals("")) {
        itemBuilder.setSkullOwner("MHF_Question");
      }

      return itemBuilder.build();
    }
  }
}