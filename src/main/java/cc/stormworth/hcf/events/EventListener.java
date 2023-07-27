package cc.stormworth.hcf.events;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.conquest.ConquestHandler;
import cc.stormworth.hcf.events.events.EventActivatedEvent;
import cc.stormworth.hcf.events.events.EventCapturedEvent;
import cc.stormworth.hcf.events.events.EventDeactivatedEvent;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.koth.events.KOTHControlLostEvent;
import cc.stormworth.hcf.events.mad.MadGame;
import cc.stormworth.hcf.listener.FFASOTWListener;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

  @EventHandler
  public void onKOTHActivated(EventActivatedEvent event) {

    if (event.getEvent().isHidden()) {
      return;
    }

    if(event.getEvent().getName().equals(MadGame.EVENT_NAME)){
      return;
    }

    String[] messages;

    switch (event.getEvent().getName()) {
      case "EOTW":
        messages = new String[]{
                ChatColor.RED + "███████",
                ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█"
                        + " " + ChatColor.DARK_RED + "[EOTW]",
                ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████"
                        + " " + ChatColor.RED + ChatColor.BOLD + "The cap point at spawn",
                ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██"
                        + " " + ChatColor.RED + ChatColor.BOLD + "is now active.",
                ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████"
                        + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD
                        + "can be contested now.",
                ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED
                        + "█",
                ChatColor.RED + "███████"
        };

        for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
          player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
        }
        break;
      case "Citadel":
        messages = new String[]{
                ChatColor.GRAY + "███████",
                ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY
                        + "█",
                ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY
                        + "█████ " + ChatColor.GOLD + "[Citadel]",
                ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY
                        + "█████ " + ChatColor.DARK_PURPLE + event.getEvent().getName(),
                ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY
                        + "█████ " + ChatColor.GOLD + "can be contested now.",
                ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY
                        + "█████",
                ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY
                        + "█",
                ChatColor.GRAY + "███████"
        };
        break;

      default:
        messages = new String[]{
                ChatColor.GRAY + "███████",
                ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY
                        + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██"
                        + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " "
                        + ChatColor.GOLD + "[KingOfTheHill]",
                ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY
                        + "███" + " " + ChatColor.YELLOW + event.getEvent().getName()
                        + " KOTH",
                ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██"
                        + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " "
                        + ChatColor.GOLD + "can be contested now.",
                ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY
                        + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY
                        + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                ChatColor.GRAY + "███████"
        };
        break;
    }

    if (event.getEvent() instanceof KOTH && !event.getEvent().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
      WaypointManager.updateKoTHWaypoint((KOTH) event.getEvent(), true);
    }

    if (event.getEvent().getType() == EventType.DTC) {
      messages = new String[]{
              ChatColor.RED + "███████",
              ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " "
                      + ChatColor.GOLD + "[Event]",
              ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " "
                      + ChatColor.YELLOW + "DTC",
              ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " "
                      + ChatColor.GOLD + "can be contested now.",
              ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████",
              ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
              ChatColor.RED + "███████"
      };
    }

    String[] messagesFinal = messages;

    String displayName = TeamUtils.getEventName(event.getEvent().getName());

    TitleBuilder titleBuilder = new TitleBuilder(displayName + " &6&lKoth", "&7has been &aactivated&7.", 20, 60, 20);

    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
      player.sendMessage(messagesFinal);

      titleBuilder.send(player);

      player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1F, 1F);
    }
  }

  @EventHandler
  public void onKOTHCaptured(final EventCapturedEvent event) {
    if (event.getEvent().isHidden()) {
      return;
    }

    if(event.getEvent().getName().equals(MadGame.EVENT_NAME)){
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());
    String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

    if (team != null) {
      teamName =
          ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
    }

    final String[] filler = {"", "", "", "", "", ""};
    String[] messages;

    if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
      messages = new String[]{
          ChatColor.GRAY + "███████",
          ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
          ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ "
              + ChatColor.GOLD + "[Citadel]",
          ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ "
              + ChatColor.YELLOW + "controlled by",
          ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ "
              + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
          ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
          ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
          ChatColor.GRAY + "███████"
      };

      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + event.getPlayer().getName() + " Citadel 10");
      Main.getInstance().getEventHandler().getBannedTeams().clear();
    } else if (event.getEvent().getName().equalsIgnoreCase("EOTW")) {
      messages = new String[]{
          ChatColor.RED + "███████",
          ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " "
              + ChatColor.DARK_RED + "[EOTW]",
          ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " "
              + ChatColor.RED + ChatColor.BOLD + "EOTW has been",
          ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " "
              + ChatColor.RED + ChatColor.BOLD + "controlled by",
          ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " "
              + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
          ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
          ChatColor.RED + "███████",
      };
      for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0f, 1.0f);
      }
      TaskUtil.runLater(Main.getInstance(), new FFASOTWListener(), 150L);
    } else if (event.getEvent().getType() == EventType.DTC) {
      messages = new String[]{
          ChatColor.RED + "███████",
          ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " "
              + ChatColor.GOLD + "[Event]",
          ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " "
              + ChatColor.YELLOW + ChatColor.BOLD + "DTC has been",
          ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " "
              + ChatColor.YELLOW + ChatColor.BOLD + "controlled by",
          ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " "
              + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
          ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
          ChatColor.RED + "███████",
      };

      Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (playerTeam != null) {
        playerTeam.addPoints(Main.getInstance().getMapHandler().isKitMap() ? 500 : 100);
      }

      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + event.getPlayer().getName() + " DTC 20");
      ItemStack kothSign = Main.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(),
              team == null ? event.getPlayer().getName() : team.getName(), EventType.DTC);

      event.getPlayer().getInventory().addItem(kothSign);

      if (!event.getPlayer().getInventory().contains(kothSign)) {
        event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
      }
    } else {
      messages = new String[]{
          ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + event.getEvent().getName()
              + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE
              + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!",
          ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Awarded" + ChatColor.GOLD
              + " KOTH Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE
              + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "."
      };

      KOTH koth = (KOTH) event.getEvent();
      int amount = 3;
      if (Bukkit.getWorld(koth.getWorld()).getEnvironment() != World.Environment.NORMAL) {
        amount = 5;
      }

      if (event.getEvent().getName().equalsIgnoreCase("Eclipse") && Main.getInstance().getEventHandler().getEclipseEvent().isActive()){
        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (playerTeam != null) {
          playerTeam.addPoints(250);
        }
        HCFProfile profile = HCFProfile.get(event.getPlayer());

        profile.addGems(1000);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + event.getPlayer().getName() + " Koth " + 10);
      }else{
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + event.getPlayer().getName() + " Koth " + amount);

        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (playerTeam != null) {
          if (koth.getName().equalsIgnoreCase("conquest-mid")) {
            playerTeam.addPoints(5);
          }

          if (koth.getName().equalsIgnoreCase("End") || koth.getName().equalsIgnoreCase("Nether")) {
            playerTeam.addPoints(15);
          }
          playerTeam.setKothCaptures(playerTeam.getKothCaptures() + 1);
        }
      }

      ItemStack kothSign = Main.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(),
              team == null ? event.getPlayer().getName() : team.getName(), EventType.KOTH);

      event.getPlayer().getInventory().addItem(kothSign);

      if (!event.getPlayer().getInventory().contains(kothSign)) {
        TaskUtil.run(Main.getInstance(), ()-> {
          event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
        });
      }
    }

    final String[] messagesFinal = messages;

    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
      player.sendMessage(filler);
      player.sendMessage(messagesFinal);
    }

    if (event.getEvent() instanceof KOTH && !event.getEvent().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
      WaypointManager.updateKoTHWaypoint((KOTH) event.getEvent(), false);
      Main.getInstance().getEventHandler().getBannedTeams().clear();
    }
  }

  @EventHandler
  public void onKOTHControlLost(KOTHControlLostEvent event) {
    if (event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
      return;
    }

    if(event.getKOTH().getName().equals(MadGame.EVENT_NAME)){
      return;
    }

    if (event.getKOTH().getRemainingCapTime() <= (event.getKOTH().getCapTime() - 30)) {
      Bukkit.broadcastMessage(
          ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Control of " + ChatColor.YELLOW + event.getKOTH().getName() + ChatColor.GOLD + " lost.");
    }
  }

  @EventHandler
  public void onCappingEvent(EventCapturedEvent event) {
    Player player = event.getPlayer();

    HCFProfile profile = HCFProfile.get(player);

    if(event.getEvent().getName().equals(MadGame.EVENT_NAME)){
      return;
    }

    if (!event.getEvent().getName().contains("conquest")) {
      profile.addGems(30);

      player.sendMessage(CC.translate(
          "&eYou have been awarded 30 &6Gems &efor capturing the &6" + event.getEvent().getName() + " &eevent!"));
    }
  }

  @EventHandler
  public void onKOTHDeactivated(EventDeactivatedEvent event) {
    if (event.getEvent() instanceof KOTH && !event.getEvent().getName()
        .startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
      WaypointManager.updateKoTHWaypoint((KOTH) event.getEvent(), false);
      Main.getInstance().getEventHandler().getBannedTeams().clear();
    }

    if(event.getEvent().getName().equals(MadGame.EVENT_NAME)){
      return;
    }

    // activate koths every 10m on the kitmap
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      return;
    }


    /*Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
      cc.stormworth.hcf.events.EventHandler eventHandler = Main.getInstance().getEventHandler();
      List<Event> localEvents = new ArrayList<>(eventHandler.getEvents());

      if (localEvents.isEmpty()) {
        return;
      }

      List<KOTH> koths = new ArrayList<>();
      // don't start a koth while another is active
      for (Event localEvent : localEvents) {
        if (localEvent.getName().equalsIgnoreCase("citadel") || localEvent.isHidden()) {
          continue;
        }
        if (localEvent.isActive()) {
          return;
        } else if (localEvent.getType() == EventType.KOTH) {
          koths.add((KOTH) localEvent);
        }
      }

      KOTH selected = koths.get(CorePlugin.RANDOM.nextInt(koths.size()));
      selected.activate();
    }, 10 * 60 * 20);*/
  }
}