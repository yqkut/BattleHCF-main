package cc.stormworth.hcf.events.conquest.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.events.conquest.ConquestHandler;
import cc.stormworth.hcf.events.conquest.enums.ConquestCapzone;
import cc.stormworth.hcf.events.events.EventCapturedEvent;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.koth.events.EventControlTickEvent;
import cc.stormworth.hcf.events.koth.events.KOTHControlLostEvent;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConquestGame implements Listener {

  @Getter
  public static LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

  public ConquestGame() {
    teamPoints.clear();

    Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

    for (Event event : Main.getInstance().getEventHandler().getEvents()) {
      if (event.getType() != EventType.KOTH) {
        continue;
      }
      KOTH koth = (KOTH) event;
      if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
        if (!koth.isHidden()) {
          koth.setHidden(true);
        }

        if (koth.getCapTime() != ConquestHandler.TIME_TO_CAP) {
          koth.setCapTime(ConquestHandler.TIME_TO_CAP);
        }

        koth.activate();
        WaypointManager.updateKoTHWaypoint(koth, true);
      }
    }
    Bukkit.broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has started! Use /conquest for more information.");
    Main.getInstance().getConquestHandler().setGame(this);
  }

  public static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
    LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
    Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();
    Iterator<Map.Entry<ObjectId, Integer>> iterator = list.iterator();

    while (iterator.hasNext()) {
      Map.Entry<ObjectId, Integer> entry = iterator.next();
      sortedHashMap.put(entry.getKey(), entry.getValue());
    }

    return sortedHashMap;
  }

  public void endGame(final Team winner, Player capper) {
    if (winner == null) {
      Bukkit.broadcastMessage(
          ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has ended.");
    } else {
      Bukkit.broadcastMessage(
          ConquestHandler.PREFIX + " " + ChatColor.GOLD + ChatColor.BOLD + winner.getName()
              + ChatColor.GOLD + " has won Conquest!");
    }

    if (winner != null) {
      winner.setConquestsCapped(winner.getConquestsCapped() + 1);

      Player leader = Bukkit.getPlayer(winner.getOwner());

      if (leader != null) {
        HCFProfile profile = HCFProfile.get(leader);

        profile.addGems(400);

        leader.sendMessage(CC.translate(
            "&eYou have been awarded 400 &6Gems &efor capturing the &6&lConquest&e."));
      } else {
        HCFProfile profile = HCFProfile.get(capper);

        profile.addGems(400);

        capper.sendMessage(CC.translate(
            "&eYou have been awarded 400 &6Gems &efor capturing the &6&lConquest&e."));
      }

      Bukkit.broadcastMessage(CC.translate("&6&l" + winner.getName() + " &ehas received &6&l+250 Points &efor capping the"));
    }

    for (Event koth : Main.getInstance().getEventHandler().getEvents()) {
      if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
        koth.deactivate();
        WaypointManager.updateKoTHWaypoint((KOTH) koth, false);
      }
    }
    Main.getInstance().getEventHandler().setScheduleEnabled(true);
    HandlerList.unregisterAll(this);
    Main.getInstance().getConquestHandler().setGame(null);
  }

  @EventHandler
  public void onKOTHCaptured(final EventCapturedEvent event) {
    if (!event.getEvent().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());
    if (team == null) {
      return;
    }

    if (teamPoints.containsKey(team.getUniqueId())) {
      teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
    } else {
      teamPoints.put(team.getUniqueId(), 1);
    }

    teamPoints = sortByValues(teamPoints);

    ConquestCapzone capzone = ConquestCapzone.valueOf(
        event.getEvent().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());
    Bukkit.broadcastMessage(
        ConquestHandler.PREFIX + " " + ChatColor.GOLD + team.getName() + ChatColor.GOLD
            + " captured " + capzone.getColor() + capzone.getName() + ChatColor.GOLD
            + " and earned a point!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId())
            + "/" + ConquestHandler.getPointsToWin() + ")");

    if (teamPoints.get(team.getUniqueId()) >= ConquestHandler.getPointsToWin()) {
      endGame(team, event.getPlayer());

      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givekey " + event.getPlayer().getName() + " Conquest 10");

      //team.setPoints(team.getPoints() + 250);
    } else {
      new BukkitRunnable() {
        public void run() {
          if (Main.getInstance().getConquestHandler().getGame() != null) {
            event.getEvent().activate();
          }
        }
      }.runTaskLaterAsynchronously(Main.getInstance(), 10L);
    }
  }

  @EventHandler
  public void onKOTHControlLost(KOTHControlLostEvent event) {
    if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
      return;
    }

    ConquestCapzone capzone = ConquestCapzone.valueOf(
        event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

    if (event.getKOTH().getCurrentCapper() == null) {
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
    if (team == null) {
      return;
    }

    team.sendMessage(
        ConquestHandler.PREFIX + ChatColor.GOLD + " " + event.getKOTH().getCurrentCapper()
            + " was knocked off of " + capzone.getColor() + capzone.getName() + ChatColor.GOLD
            + "!");
  }

  @EventHandler
  public void onKOTHControlTick(EventControlTickEvent event) {

    if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)
        || event.getKOTH().getRemainingCapTime() % 5 != 0) {
      return;
    }

    ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());
    Player capper = Main.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());

    if (capper != null) {
      capper.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Attempting to capture " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!" + ChatColor.AQUA + " (" + event.getKOTH().getRemainingCapTime() + "s)");
    }
  }
}