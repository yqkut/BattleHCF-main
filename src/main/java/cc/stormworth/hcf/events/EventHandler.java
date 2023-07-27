package cc.stormworth.hcf.events;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.dtc.DTC;
import cc.stormworth.hcf.events.dtc.DTCListener;
import cc.stormworth.hcf.events.eclipse.EclipseEvent;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.mad.MadGame;
import cc.stormworth.hcf.team.Team;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EventHandler {

  @Getter
  private final Set<Event> events = new HashSet<>();
  @Getter
  private final Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();

  @Getter
  private final Set<Team> bannedTeams = new HashSet<>();

  @Getter
  @Setter
  private boolean scheduleEnabled = true;
  private transient File eventSchedule;
  @Getter
  private final DTCListener dtcListener;
  @Getter
  private final EclipseEvent eclipseEvent;

  public EventHandler() {
    loadEvents();
    loadSchedules();

    eclipseEvent = new EclipseEvent();

    dtcListener = new DTCListener();
    Main.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), Main.getInstance());
    rCommandHandler.registerParameterType(Event.class, new EventParameterType());

    new MadGame();

    new BukkitRunnable() {
      public void run() {
        for (Event event : events) {
          if (event.isActive()) {
            event.tick();
          }
        }
      }
    }.runTaskTimer(Main.getInstance(), 5L, 20L);

    Main.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
      terminateKOTHs();
      activateKOTHs();
    }, 0L, 20L);
  }

  public Event getActiveEvent() {
    for (final Event event : Main.getInstance().getEventHandler().getEvents()) {
      if (event.isActive()) {
        if (event.isHidden()) {
          continue;
        }
        return event;
      }
    }
    return null;
  }

  public void loadEvents() {
    try {
      File eventsBase = new File(Main.getInstance().getDataFolder(), "events");

      if (!eventsBase.exists()) {
        eventsBase.mkdir();
      }

      for (EventType eventType : EventType.values()) {
        File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

        if (!subEventsBase.exists()) {
          subEventsBase.mkdir();
        }

        for (File eventFile : subEventsBase.listFiles()) {
          if (eventFile.getName().endsWith(".json")) {
            events.add(CorePlugin.GSON.fromJson(FileUtils.readFileToString(eventFile),
                eventType == EventType.KOTH ? KOTH.class : DTC.class));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // look for a previously active Event, if present deactivate and start it after 15 seconds
    events.stream().filter(Event::isActive).findFirst().ifPresent((event) -> {
      event.setActive(false);
      Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
        // if anyone had started a Event within the last 15 seconds,
        // don't activate previously active one
        for (Event otherKoth : getEvents()) {
          if (otherKoth.isActive() && !otherKoth.getName().contains("conquest")) {
           /* Bukkit.getServer().getLogger()
                .info(ChatColor.RED + otherKoth.getName() + " is currently active.");*/
            return;
          }
        }
        //.filter(event1 -> !event1.getName().equalsIgnoreCase("citadel"))
        if (events.stream().filter(event1 -> !event1.isHidden()).noneMatch(Event::isActive)) {
          event.activate();
        }
      }, 300L);
    });
  }

  public void fillSchedule() {
    List<String> allevents = new ArrayList<>();

    for (Event event : getEvents()) {
      if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName()
          .equalsIgnoreCase("Citadel") || event.getName().equalsIgnoreCase("Mad")) {
        continue;
      }

      allevents.add(event.getName());
    }

    for (int minute = 0; minute < 60; minute++) {
      for (int hour = 0; hour < 24; hour++) {
        this.EventSchedule.put(
            new EventScheduledTime(Calendar.getInstance().get(Calendar.DAY_OF_YEAR), (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + hour) % 24, minute),
            allevents.get(0));
      }
    }
  }

  public void loadSchedules() {
    EventSchedule.clear();

    try {
      eventSchedule = new File(Main.getInstance().getDataFolder(), "eventSchedule.json");

      if (!eventSchedule.exists()) {
        eventSchedule.createNewFile();

        BasicDBObject dbObject = getDefaults();

        FileUtils.write(eventSchedule, CorePlugin.GSON.toJson(new JsonParser().parse(dbObject.toString())));
      } else {
        // basically check for any new keys in the defaults which aren't contained in the actual file
        // if there are any, add them to the file.
        BasicDBObject file = (BasicDBObject) JSON.parse(FileUtils.readFileToString(eventSchedule));

        BasicDBObject defaults = getDefaults();

        defaults.keySet().stream().filter(key -> !file.containsKey(key))
            .forEach(key -> file.put(key, defaults.get(key)));

        FileUtils.write(eventSchedule,
            CorePlugin.GSON.toJson(new JsonParser().parse(file.toString())));
      }

      BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(eventSchedule));

      if (dbo != null) {
        for (Map.Entry<String, Object> entry : dbo.entrySet()) {
          EventScheduledTime scheduledTime = EventScheduledTime.parse(entry.getKey());
          this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private BasicDBObject getDefaults() {
    BasicDBObject dbObject = new BasicDBObject();

    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    List<String> allevents = new ArrayList<>();

    for (Event event : getEvents()) {
      if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")
              || event.getName().equalsIgnoreCase("Mad")
              || event.getName().equalsIgnoreCase("Conquest")) {
        continue;
      }

      allevents.add(event.getName());
    }

    for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
      int day = (currentDay + dayOffset) % 365;
      EventScheduledTime[] times = new EventScheduledTime[]{
          new EventScheduledTime(day, 0, 0),
          new EventScheduledTime(day, 5, 30),
          new EventScheduledTime(day, 7, 0),
          new EventScheduledTime(day, 8, 30),
          new EventScheduledTime(day, 10, 0),
          new EventScheduledTime(day, 11, 30),

          new EventScheduledTime(day, 13, 0),
          new EventScheduledTime(day, 14, 30),
          new EventScheduledTime(day, 16, 0),
          new EventScheduledTime(day, 17, 30),
          new EventScheduledTime(day, 19, 0),
          new EventScheduledTime(day, 20, 30),
          new EventScheduledTime(day, 22, 0),
      };

      if(Main.getInstance().getMapHandler().isKitMap()){
        times = new EventScheduledTime[144];

        int i = 0;
        //Every 10 minutes
        for(int hours = 0; hours < 24; hours++){
          for (int minute = 0; minute < 60; minute += 10) {
            times[i++] = new EventScheduledTime(day, hours, minute);
          }
        }
      }

      Collections.shuffle(allevents);

      if (!allevents.isEmpty()) {
        for (int eventTimeIndex = 0; eventTimeIndex < times.length; eventTimeIndex++) {
          EventScheduledTime eventTime = times[eventTimeIndex];
          String eventName = allevents.get(eventTimeIndex % allevents.size());

          dbObject.put(eventTime.toString(), eventName);
        }
      }
    }

    //dbObject.put(new EventScheduledTime(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).getDayOfYear(), 16, 00).toString(), "Citadel");

    /*if (!Main.getInstance().getMapHandler().isKitMap()) {
      dbObject.put(new EventScheduledTime(
          LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfYear(), 15,
          00).toString(), "eotwTimer");
      dbObject.put(new EventScheduledTime(
          LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfYear(), 16,
          00).toString(), "EOTW");
    }*/

  /*  dbObject.put(new EventScheduledTime(
        LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).getDayOfYear(), 16,
        00).toString(), "Glowstone");*/

    /*dbObject.put(new EventScheduledTime(
        LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).getDayOfYear(), 16,
        00).toString(), "Conquest");*/

    return dbObject;
  }

  public void saveEvents() {
    try {
      File eventsBase = new File(Main.getInstance().getDataFolder(), "events");

      if (!eventsBase.exists()) {
        eventsBase.mkdir();
      }

      for (EventType eventType : EventType.values()) {

        File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

        if (!subEventsBase.exists()) {
          subEventsBase.mkdir();
        }

        for (File eventFile : subEventsBase.listFiles()) {
          eventFile.delete();
        }
      }

      for (Event event : events) {
        File eventFile = new File(new File(eventsBase, event.getType().name().toLowerCase()),
            event.getName() + ".json");
        FileUtils.write(eventFile, CorePlugin.GSON.toJson(event));
        //Bukkit.getLogger().info("Writing " + event.getName() + " to " + eventFile.getAbsolutePath());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Event getEvent(String name) {
    for (Event event : events) {
      if (event.getName().equalsIgnoreCase(name)) {
        return (event);
      }
    }

    return (null);
  }

  public void deactivateOthers() {
    for (Event otherKoth : Main.getInstance().getEventHandler().getEvents()) {
      if (otherKoth.isActive() && !otherKoth.getName().contains("conquest")) {
        otherKoth.deactivate();
        return;
      }
    }
  }

  private void activateKOTHs() {
    // Don't start a KOTH during EOTW & SOTW.
    if (Main.getInstance().getServerHandler().isPreEOTW() || CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
      return;
    }

    if (!scheduleEnabled) {
      return;
    }

    if (Bukkit.getOnlinePlayers().size() < 10) {
      return;
    }

    EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());

    if (Main.getInstance().getEventHandler().getEventSchedule().containsKey(scheduledTime)) {
      String resolvedName = Main.getInstance().getEventHandler().getEventSchedule().get(scheduledTime);

      if (resolvedName.equalsIgnoreCase("eotwTimer")) {
        if (!Main.getInstance().getMapHandler().isKitMap() && !CustomTimerCreateCommand.getCustomTimers().containsKey("&4&lEOTW In")) {
          CustomTimerCreateCommand.customTimerCreate(Bukkit.getConsoleSender(), "1h",
              "&4&lEOTW In");
        }
        return;
      } else if (resolvedName.equalsIgnoreCase("eotw")) {
        if (!Main.getInstance().getMapHandler().isKitMap()) {
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth activate eotw");
        }
        return;
      } else if (resolvedName.equalsIgnoreCase("glowstone")) {
        if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lGlowstone")
            && !Main.getInstance().getMapHandler().isKitMap()) {
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "glow start 15m");
        }
        return;
      } else if (resolvedName.equalsIgnoreCase("conquest")) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "conquestadmin start");
        return;
      } else if (resolvedName.equalsIgnoreCase("OPKeyAll")) {
        if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&4&lOpKeyAll")) {
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
              Main.getInstance().getMapHandler().isKitMap() ? "customtimer 10h15m &4&lOpKeyAll"
                  : "customtimer 10h05m &4&lOpKeyAll");
          return;
        }
      } else if (resolvedName.equalsIgnoreCase("citadel")) {
        for (Event event : Main.getInstance().getEventHandler().getEvents()) {
          event.deactivate();
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth activate citadel");
        return;
      }

      for (Event otherKoth : Main.getInstance().getEventHandler().getEvents()) {
        if (otherKoth.isActive()) {
          Main.getInstance().getLogger().warning(otherKoth.getName() + " is currently active.");
          return;
        }
      }

      Event resolved = Main.getInstance().getEventHandler().getEvent(resolvedName);

      if (resolved == null) {
        Main.getInstance().getLogger().warning(
            "The event scheduler has a schedule for an event named " + resolvedName
                + ", but the event does not exist.");
        return;
      }

      if (resolved.isHidden() | resolved.getName().contains("conquest")) {
        return;
      }
      resolved.activate();
    }
  }

  private void terminateKOTHs() {
    EventScheduledTime nextScheduledTime = EventScheduledTime.parse(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)));

    if(Main.getInstance().getMapHandler().isKitMap()){
      return;
    }

    if (Main.getInstance().getEventHandler().getEventSchedule().containsKey(nextScheduledTime)) {
      // We have a KOTH about to start. Prepare for it.
      for (Event activeEvent : Main.getInstance().getEventHandler().getEvents()) {
        if (activeEvent.getType() != EventType.KOTH) {
          continue;
        }
        KOTH activeKoth = (KOTH) activeEvent;

        if (activeKoth.getCurrentCapper() != null) {
          return;
        }

        if (activeKoth.getName().contains("conquest")) {
          continue;
        }
        if (!activeKoth.isHidden() && activeKoth.isActive() && !activeKoth.getName()
            .equals("Citadel") && !activeKoth.getName().equals("EOTW")) {
          if (activeKoth.getCurrentCapper() != null && !activeKoth.isTerminate()) {
            activeKoth.setTerminate(true);
            Bukkit.broadcastMessage(
                ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + activeKoth.getName()
                    + ChatColor.YELLOW + " will be terminated if knocked.");
          } else {
            activeKoth.deactivate();
            Bukkit.broadcastMessage(
                ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + activeKoth.getName() + ChatColor.YELLOW + " has been terminated.");
          }
        }
      }
    }
  }
}