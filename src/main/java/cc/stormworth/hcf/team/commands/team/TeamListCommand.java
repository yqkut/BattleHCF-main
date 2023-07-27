package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.utils.FilterType;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamListCommand {

  @Command(names = {"team list", "t list", "f list", "faction list", "fac list", "tlist",
      "flist"}, permission = "", async = true)
  public static void teamList(final Player sender,
      @Param(name = "page", defaultValue = "1") final int page) {
    if (page < 1) {
      sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
      return;
    }

    HCFProfile profile = HCFProfile.getByUUID(sender.getUniqueId());
    FilterType filtertype = profile.getFilterType();
    Map<Team, Integer> teamPlayerCount = new HashMap<>();
    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
      if (player.hasMetadata("invisible")) {
        continue;
      }

      Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);

      if (playerTeam != null) {
        if (teamPlayerCount.containsKey(playerTeam)) {
          teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
        } else {
          teamPlayerCount.put(playerTeam, 1);
        }
      }
    }

    Map<Team, Double> teamDTRCount = new HashMap<>();
    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
      if (player.hasMetadata("invisible")) {
        continue;
      }

      Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);

      if (playerTeam != null) {
        teamDTRCount.put(playerTeam, playerTeam.getDTR());
      }
    }

    int maxPages =
        ((filtertype == FilterType.HIGHEST_DTR || filtertype == FilterType.LOWEST_DTR)
            ? teamDTRCount.size() : teamPlayerCount.size() / 10) + 1;
    int currentPage = FastMath.min(page, maxPages);

    LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount,
        filtertype);
    LinkedHashMap<Team, Double> sortedDTRCount = sortByDValues(teamDTRCount, filtertype);

    int start = (currentPage - 1) * 10;
    int index = 0;

    sender.sendMessage(Team.GRAY_LINE);
    sender.sendMessage(
        ChatColor.GOLD + "Team List " + ChatColor.GRAY + "(Page " + currentPage + "/" + maxPages
            + ")");

    if (filtertype == FilterType.HIGHEST_DTR || filtertype == FilterType.LOWEST_DTR) {
      for (Map.Entry<Team, Double> teamEntry : sortedDTRCount.entrySet()) {
        index++;

        if (index < start) {
          continue;
        }

        if (index > start + 10) {
          break;
        }

        FormatingMessage teamMessage = new FormatingMessage();

        teamMessage.text(
                index + ". " + (teamEntry.getKey().isDisqualified() ? CC.BD_RED + "✘ " : ""))
            .color(ChatColor.GRAY).then();
        teamMessage.text(teamEntry.getKey().getName()).color(ChatColor.YELLOW).tooltip(
                ChatColor.YELLOW + "DTR: " + teamEntry.getKey().getDTRColor()
                    + Team.DTR_FORMAT.format(
                    teamEntry.getKey().getDTR()) + ChatColor.YELLOW + " / " + teamEntry.getKey()
                    .getMaxDTR() + "\n" +
                    (teamEntry.getKey().isDisqualified() ? CC.RED + "This team is disqualified"
                        + "\n"
                        + ChatColor.GREEN + "Click to view team info"
                        : ChatColor.GREEN + "Click to view team info"))
            .command("/t who " + teamEntry.getKey().getName()).then();
        teamMessage.text(
            " (" + teamEntry.getKey().getDTRString() + ChatColor.GREEN + "/"
                + teamEntry.getKey()
                .getMaxDTR() + ChatColor.GREEN + ")").color(ChatColor.GREEN);

        if (!teamEntry.getKey().isMember(sender.getUniqueId())) {
          teamMessage.then().text(
              CC.translate(" - ")).color(ChatColor.GRAY);

          teamMessage.then().text(
                  CC.translate("&7[&6Focus&7]"))
              .tooltip("Click to focus this faction").color(ChatColor.GREEN)
              .command("/t focus " + teamEntry.getKey().getName());
        }

        teamMessage.send(sender);
      }
    } else {
      for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
        index++;

        if (index < start) {
          continue;
        }

        if (index > start + 10) {
          break;
        }

        FormatingMessage teamMessage = new FormatingMessage();

        teamMessage.text(
                index + ". " + (teamEntry.getKey().isDisqualified() ? CC.BD_RED + "✘ " : ""))
            .color(ChatColor.GRAY).then();
        teamMessage.text(teamEntry.getKey().getName()).color(ChatColor.YELLOW).tooltip(
                ChatColor.YELLOW + "DTR: " + teamEntry.getKey().getDTRColor()
                    + Team.DTR_FORMAT.format(
                    teamEntry.getKey().getDTR()) + ChatColor.YELLOW + " / " + teamEntry.getKey()
                    .getMaxDTR() + "\n" +
                    (teamEntry.getKey().isDisqualified() ? CC.RED + "This team is disqualified"
                        + "\n"
                        + ChatColor.GREEN + "Click to view team info"
                        : ChatColor.GREEN + "Click to view team info"))
            .command("/t who " + teamEntry.getKey().getName()).then();
        teamMessage.text(
                " (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")")
            .color(ChatColor.GREEN);

        if (!teamEntry.getKey().isMember(sender.getUniqueId())) {
          teamMessage.then().text(
              CC.translate(" - ")).color(ChatColor.GRAY);

          teamMessage.then().text(
                  CC.translate("&7[&6Focus&7]"))
              .tooltip("Click to focus this faction").color(ChatColor.GREEN)
              .command("/t focus " + teamEntry.getKey().getName());
        }

        teamMessage.send(sender);
      }
    }
    sender.sendMessage(
        ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + currentPage + "/"
            + maxPages + ChatColor.GRAY + ".");
    sender.sendMessage(
        ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/t list <page#>"
            + ChatColor.GRAY + ".");
    sender.sendMessage(Team.GRAY_LINE);
  }

  public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map,
      FilterType filterType) {
    LinkedList<java.util.Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

    Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

    if (filterType == FilterType.LOWEST_DTR || filterType == FilterType.LOWEST_ONLINE) {
      Collections.reverse(list);
    }

    LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

    for (Map.Entry<Team, Integer> entry : list) {
      sortedHashMap.put(entry.getKey(), entry.getValue());
    }

    return (sortedHashMap);
  }

  public static LinkedHashMap<Team, Double> sortByDValues(Map<Team, Double> map,
      FilterType filterType) {
    LinkedList<java.util.Map.Entry<Team, Double>> list = new LinkedList<>(map.entrySet());

    Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

    if (filterType == FilterType.LOWEST_DTR || filterType == FilterType.LOWEST_ONLINE) {
      Collections.reverse(list);
    }

    LinkedHashMap<Team, Double> sortedHashMap = new LinkedHashMap<>();

    for (Map.Entry<Team, Double> entry : list) {
      sortedHashMap.put(entry.getKey(), entry.getValue());
    }

    return (sortedHashMap);
  }
}