package cc.stormworth.hcf.providers.tab;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.tab.LayoutProvider;
import cc.stormworth.core.kt.tab.TabLayout;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventScheduledTime;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.TeamUtils;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.commands.team.TeamListCommand;
import cc.stormworth.hcf.team.utils.FilterType;
import cc.stormworth.hcf.util.Utils;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TabProvider implements LayoutProvider {

  private final String titleColor = CC.PRIMARY + CC.BOLD;
  long cacheLastUpdated;
  @Getter private static LinkedHashMap<Team, Integer> cachedTeamOnlineList = Maps.newLinkedHashMap();

  @NotNull
  @Override
  public TabLayout provide(@NotNull Player player) {

    final TabLayout layout = TabLayout.create(player);

    layout.setHeaderText(CC.B_PRIMARY + "Battle Network &7┃ &f" + CorePlugin.getInstance().getCache().getOnlineCount() + " / 5,000");

    if (CorePlugin.getInstance().getServerId().toLowerCase().contains("hcf")) {
      layout.setFooterText("&fYou are playing " + CC.PRIMARY + "HCF &fon " + CC.PRIMARY + "battle.rip");
    }else{
      layout.setFooterText("&fYou are playing " + CC.PRIMARY + "Kits" + " &fon " + CC.PRIMARY + "battle.rip");
    }


    this.mapInfo(layout);
    this.playerInfo(player, layout);
    this.eventInfo(layout);

    layout.set(1, 0, titleColor + "Battle");

    Team team = Main.getInstance().getTeamHandler().getTeam(player);
    int y = -1;

    this.teamInfo(team, layout);

    if (team == null) {
      y = 0;
    }

    ++y;

    /*Set<ObjectId> cappers = Main.getInstance().getCitadelHandler().getCappers();

    if (!cappers.isEmpty()) {
      Set<String> capperNames = new HashSet<>();

      for (ObjectId capper : cappers) {
        Team capperTeam = Main.getInstance().getTeamHandler().getTeam(capper);

        if (capperTeam != null) {
          capperNames.add(capperTeam.getName());
        }
      }
      if (!capperNames.isEmpty()) {
        ++y;
        layout.set(2, ++y, titleColor + "Citadel Cappers:");

        for (String capper2 : capperNames) {
          layout.set(2, ++y, infoColor + capper2);
        }
      }
    }*/

    boolean shouldReloadCache = cachedTeamOnlineList == null || System.currentTimeMillis() - this.cacheLastUpdated > 2000L;

    y = 1;

    Map<Team, Integer> teamPlayerCount = new HashMap<>();

    if (shouldReloadCache) {

      for (Team teamOnline : Main.getInstance().getTeamHandler().getTeams()){
        if (teamOnline.getOnlineMemberAmount() > 0){
          teamPlayerCount.put(teamOnline, teamOnline.getOnlineMemberAmount());
        }
      }
    }

    if (shouldReloadCache) {
      cachedTeamOnlineList = TeamListCommand.sortByValues(teamPlayerCount, FilterType.HIGHEST_ONLINE);
      this.cacheLastUpdated = System.currentTimeMillis();
    }

    int index = 1;
    boolean title = false;

    for (Map.Entry<Team, Integer> teamEntry : cachedTeamOnlineList.entrySet()) {

      if (++index > 20) break;

      if (!title) {
        title = true;
        layout.set(3, 0, ChatColor.GOLD + "Faction List");
      }

      String teamName = teamEntry.getKey().getName();
      String teamColor;

      if (team == null) {
        teamColor = ChatColor.GRAY.toString();
      } else if (teamEntry.getKey().isMember(player.getUniqueId())) {
        teamColor = ChatColor.GREEN.toString();
      } else if (team.getAllies().contains(teamEntry.getKey().getUniqueId())) {
        teamColor = ChatColor.BLUE.toString();
      } else {
        teamColor = ChatColor.RED.toString();
      }

      int n = 3;
      int n2 = y++;

      layout.set(n, n2,
          teamColor + teamName + ChatColor.GRAY + " (" + teamEntry.getValue() + ") "
              + (teamEntry.getKey().isRaidable() ? ChatColor.DARK_RED : ChatColor.GREEN)
              + Team.DTR_FORMAT.format(teamEntry.getKey().getDTR()));
    }

    return layout;
  }

  private void playerInfo(Player player, TabLayout layout) {
    HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());


    if(hcfProfile == null){
      TaskUtil.run(Main.getInstance(), () -> {
        player.kickPlayer(ChatColor.RED + "Error: Profile not found");
      });
      return;
    }

    layout.set(0, 8, "&6Player Info");
    layout.set(0, 9, ChatColor.GRAY + "Kills: " + hcfProfile.getKills());
    layout.set(0, 10, ChatColor.GRAY + "Deaths: " + hcfProfile.getDeaths());

    layout.set(0, 12, "&6Your Location");

    Location loc = player.getLocation();
    Team ownerTeam = LandBoard.getInstance().getTeam(loc);

    String location = ChatColor.GRAY + "None";

    if (!player.getWorld().getName().equalsIgnoreCase("void")) {
      if (ownerTeam != null) {
        location = ownerTeam.getName(player.getPlayer());
      } else if (!Main.getInstance().getServerHandler().isWarzone(loc)) {
        location = ChatColor.GRAY + "Wilderness";
      } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance()
          .getTeam(loc).getName().equalsIgnoreCase("citadel")) {
        location = ChatColor.DARK_PURPLE + "Citadel";
      } else {
        location = ChatColor.RED + "Warzone";
      }
    }

    layout.set(0, 13, location);
    String direction = Utils.getCardinalDirection(player);

    if (direction != null) {
      layout.set(0, 14, ChatColor.GRAY.toString() + loc.getBlockX() + ", " + loc.getBlockZ() + " [" + direction + "]");
    } else {
      layout.set(0, 14, ChatColor.GRAY.toString() + loc.getBlockX() + ", " + loc.getBlockZ());
    }
  }

  private void eventInfo(TabLayout layout) {
    KOTH activeKOTH = null;

    for (final Event event : Main.getInstance().getEventHandler().getEvents()) {
      if (!(event instanceof KOTH)) {
        continue;
      }

      KOTH koth = (KOTH) event;

      if (koth.isActive() && !koth.isHidden()) {
        activeKOTH = koth;
        break;
      }
    }

    if (activeKOTH == null) {

      Date now = new Date();

      String nextKothName = null;
      Date nextKothDate = null;
      String displayName = "";


      for (final Map.Entry<EventScheduledTime, String> entry : Main.getInstance().getEventHandler().getEventSchedule().entrySet()) {

        if (entry.getKey().toDate().after(now) && (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime())) {
          nextKothName = entry.getValue();
          nextKothDate = entry.getKey().toDate();

          displayName = TeamUtils.getEventName(nextKothName);
        }
      }

      if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
        layout.set(2, 10, "&6Next KOTH");
        layout.set(2, 11, "&7None");

        for (int i = 12; i < 16; i++) {
          layout.set(2, i, "");
        }
      } else {
        if (nextKothName != null) {


          layout.set(2, 10, "&6Next KOTH");
          layout.set(2, 11, displayName);

          Event event2 = Main.getInstance().getEventHandler().getEvent(nextKothName);

          if (event2 instanceof KOTH) {
            KOTH koth2 = (KOTH) event2;

            layout.set(2, 12, ChatColor.GRAY.toString() + koth2.getCapLocation().getBlockX() + ", " +
                    koth2.getCapLocation().getBlockY() + ", " + koth2.getCapLocation().getBlockZ());

            int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000L);

            layout.set(2, 13, ChatColor.GRAY + "Goes active in:");

            String time = TimeUtils.formatIntoHHMMSS(seconds);

            layout.set(2, 14,  ChatColor.WHITE + time);
          }
        }
      }
    } else {
      String displayName = TeamUtils.getEventName(activeKOTH.getName());

      layout.set(2, 10, displayName);
      layout.set(2, 11,
          "&7(" + activeKOTH.getCapLocation().getBlockX()
              + ", "
              + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation()
              .getBlockZ() + ")");
      layout.set(2, 12, ChatColor.WHITE + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime()));

      for (int i = 13; i < 17; i++) {
        layout.set(2, i, "");
      }
    }
  }

  private void teamInfo(Team team, TabLayout layout) {
    if (team != null) {

      if (team.getHQ() != null) {
        String homeLocation =
                ChatColor.GRAY.toString() + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", "
                        + team.getHQ().getBlockZ();
        layout.set(0, 0, "&6Hq");
        layout.set(0, 1, homeLocation);
      } else {
        layout.set(0, 0, "&6HQ");
        layout.set(0, 1, "&7Not Set");
      }

      layout.set(0, 3,  "&6Faction Info");
      layout.set(0, 4, ChatColor.GRAY + "DTR: " + (team.isRaidable() ? ChatColor.DARK_RED : ChatColor.GREEN) + Team.DTR_FORMAT.format(team.getDTR()));

      layout.set(0, 5, ChatColor.GRAY + "Online: " + team.getOnlineMemberAmount() + "/" + team.getMembers().size());

      layout.set(0, 6, ChatColor.GRAY + "Points: " + team.getPoints());

/*      String watcherName = ChatColor.GREEN + player.getName();
      if (team.isOwner(player.getUniqueId())) {
        watcherName = watcherName + ChatColor.WHITE + "**";
      } else if (team.isCoLeader(player.getUniqueId())) {
        watcherName = watcherName + ChatColor.WHITE + "**";
      } else if (team.isCaptain(player.getUniqueId())) {
        watcherName = watcherName + ChatColor.WHITE + "*";
      }*/

      //layout.set(1, 3, watcherName, ((CraftPlayer) player).getHandle().ping);

      layout.set(1, 2,  ChatColor.GREEN.toString() + ChatColor.BOLD + team.getName());

      int y = 2;

      for (final Player member : team.getOrderOnlineList()) {

        if (y >= 20) break;

        if (team.isOwner(member.getUniqueId())) {
          layout.set(1, ++y, ChatColor.GREEN + member.getName() + "**", ((CraftPlayer) member).getHandle().ping);
        } else if (team.isCoLeader(member.getUniqueId())) {
          layout.set(1, ++y, ChatColor.GREEN + member.getName() + "**", ((CraftPlayer) member).getHandle().ping);
        } else if (team.isCaptain(member.getUniqueId())) {
          layout.set(1, ++y, ChatColor.GREEN + member.getName() + "*", ((CraftPlayer) member).getHandle().ping);
        } else {
          layout.set(1, ++y, ChatColor.GREEN + member.getName(), ((CraftPlayer) member).getHandle().ping);
        }
      }

    } else {
      layout.set(0, 0, "&6HQ");
      layout.set(0, 1, "&7Not Set");
      layout.set(0, 3,  "&6Faction Info");
      layout.set(0, 4, ChatColor.GRAY + "You can create");
      layout.set(0, 5, ChatColor.GRAY + "a faction using");
      layout.set(0, 6, ChatColor.GRAY + "/f create <name>");
    }
  }

  private void mapInfo(TabLayout layout) {

    int border = CorePlugin.getInstance().getConfigFile().getConfig().getInt("borders.world");

    layout.set(2, 0, ChatColor.GOLD + "Players Online");
    layout.set(2, 1, ChatColor.GRAY.toString() + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers());

    layout.set(2, 3, ChatColor.GOLD + "Map Kit");
    layout.set(2, 4, "&7Protection " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel());
    layout.set(2, 5, "&7Sharpness " + Enchantment.DAMAGE_ALL.getMaxLevel());

    layout.set(2, 7, "&6Border Info ");
    layout.set(2, 8, "&7" + border + " x " + border);

    /*String endPortalLocation = Main.getInstance().getMapHandler().getEndPortalLocation();
    if (endPortalLocation != null && !endPortalLocation.equals("N/A")
        && !endPortalLocation.isEmpty()) {

      layout.set(2, 11, titleColor + "End Portals");
      layout.set(2, 12, infoColor + endPortalLocation);
      layout.set(2, 13, ChatColor.GRAY + "in each quadrant");
    }*/

    /*int y = 8;
    KOTH activeKOTH = null;
    for (final Event event : Main.getInstance().getEventHandler().getEvents()) {
      if (!(event instanceof KOTH)) {
        continue;
      }
      final KOTH koth = (KOTH) event;
      if (koth.isActive() && !koth.isHidden()) {
        activeKOTH = koth;
        break;
      }
    }
    if (activeKOTH == null) {
      final Date now = new Date();
      String nextKothName = null;
      Date nextKothDate = null;
      for (final Map.Entry<EventScheduledTime, String> entry : Main.getInstance()
          .getEventHandler().getEventSchedule().entrySet()) {
        if (entry.getKey().toDate().after(now) && (nextKothDate == null
            || nextKothDate.getTime() > entry.getKey().toDate().getTime())) {
          nextKothName = entry.getValue();
          nextKothDate = entry.getKey().toDate();
        }
      }
      if (nextKothName != null) {
        layout.set(0, ++y, titleColor + "Next KOTH:");
        layout.set(0, ++y, infoColor + nextKothName);
        final Event event2 = Main.getInstance().getEventHandler().getEvent(nextKothName);
        if (event2 != null && event2 instanceof KOTH) {
          final KOTH koth2 = (KOTH) event2;
          layout.set(0, ++y, infoColor + koth2.getCapLocation().getBlockX() + ", "
              + koth2.getCapLocation().getBlockY() + ", " + koth2.getCapLocation()
              .getBlockZ());
          final int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis())
              / 1000L);
          layout.set(0, ++y, titleColor + "Goes active in:");
          final String time = formatIntoDetailedString(seconds).replace("minutes", "min")
              .replace("minute", "min").replace("seconds", "sec")
              .replace("second", "sec");
          layout.set(0, ++y, infoColor + time);
        }
      }
    } else {
      layout.set(0, ++y, titleColor + activeKOTH.getName());
      layout.set(0, ++y,
          infoColor + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime()));
      layout.set(0, ++y, infoColor + activeKOTH.getCapLocation().getBlockX() + ", "
          + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation()
          .getBlockZ());
    }
    layout.set(1, 2, titleColor + "Online: &f" + Bukkit.getOnlinePlayers().size() + "/"
        + Bukkit.getMaxPlayers());
    layout.set(1, 4, titleColor + "Team Info");
    final Team team = Main.getInstance().getTeamHandler().getTeam(player);
    if (team != null) {
      layout.set(1, 5, "&fName: " + team.getName());
      if (team.getHQ() != null) {
        final String homeLocation =
            infoColor + "HQ " + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY()
                + ", " + team.getHQ().getBlockZ();
        layout.set(1, 6, homeLocation);
      } else {
        layout.set(1, 6, infoColor + "HQ Not Set");
      }
      layout.set(1, 7, "&fBalance: $" + team.getBalance());
    } else {
      layout.set(1, 5, "&fNone");
    }
    layout.set(2, 4, titleColor + "Statistics");
    layout.set(2, 5, "&fKills: " + hcfProfile.getKills());
    layout.set(2, 6, "&fDeaths: " + hcfProfile.getDeaths());
    layout.set(2, 7,
        "&fBalance: $" + Main.getInstance().getBalanceMap().getBalance(player.getUniqueId()));
    layout.set(2, 9, titleColor + "Your Location");
    final Location loc = player.getLocation();
    final Team ownerTeam = LandBoard.getInstance().getTeam(loc);
    String location;
    if (ownerTeam != null) {
      location = ownerTeam.getName(player.getPlayer());
    } else if (!Main.getInstance().getServerHandler().isWarzone(loc)) {
      location = ChatColor.GRAY + "Wilderness";
    } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance()
        .getTeam(loc).getName().equalsIgnoreCase("citadel")) {
      location = titleColor + "Citadel";
    } else {
      location = ChatColor.DARK_RED + "Warzone";
    }
    layout.set(2, 11, location);
    final String direction = Utils.getCardinalDirection(player);
    if (direction != null) {
      layout.set(2, 10,
          ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ") [" + direction
              + "]");
    } else {
      layout.set(2, 10,
          ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
    }

    final boolean shouldReloadCache = this.cachedTeamOnlineList == null
        || System.currentTimeMillis() - this.cacheLastUpdated > 2000L;
    y = 1;
    final Map<Team, Integer> teamPlayerCount = new HashMap<Team, Integer>();
    if (shouldReloadCache) {
      for (final Player other : Main.getInstance().getServer().getOnlinePlayers()) {
        if (!CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(other)) {
          if (CorePlugin.getInstance().getStaffModeManager().getVanishedPlayers()
              .contains(other.getUniqueId())) {
            continue;
          }
          final Team playerTeam = Main.getInstance().getTeamHandler().getTeam(other);
          if (playerTeam == null) {
            continue;
          }
          if (teamPlayerCount.containsKey(playerTeam)) {
            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
          } else {
            teamPlayerCount.put(playerTeam, 1);
          }
        }
      }
    }
    LinkedHashMap<Team, Integer> sortedTeamPlayerCount;
    if (shouldReloadCache) {
      sortedTeamPlayerCount = TeamListCommand.sortByValues(teamPlayerCount,
          FilterType.HIGHEST_ONLINE);
      this.cachedTeamOnlineList = sortedTeamPlayerCount;
      this.cacheLastUpdated = System.currentTimeMillis();
    } else {
      sortedTeamPlayerCount = this.cachedTeamOnlineList;
    }
    int index = 0;
    boolean title = false;
    for (final Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
      if (++index > 19) {
        break;
      }
      if (!title) {
        title = true;
        layout.set(3, 0, titleColor + "Team List:");
      }
      String teamName = teamEntry.getKey().getName();
      final String teamColor =
          teamEntry.getKey().isMember(player.getUniqueId()) ? ChatColor.GREEN.toString()
              : infoColor;
      if (teamName.length() > 8) {
        teamName = teamName.substring(0, 8);
      }
      final int n = 3;
      final int n2 = y++;
      layout.set(n, n2,
          teamColor + teamName + ChatColor.WHITE + " (" + teamEntry.getValue() + ") &7┃ &f"
              + teamEntry.getKey().getDTRString());
    }*/
  }
}