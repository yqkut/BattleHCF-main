package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.glowmtn.GlowRespawnTask;
import cc.stormworth.hcf.events.region.oremountain.OreMountainRespawnTask;
import cc.stormworth.hcf.listener.FFASOTWListener;
import cc.stormworth.hcf.pvpclasses.pvpclasses.MinerClass;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.utils.LunarTeammatesHandler;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CustomTimerCreateCommand {

  @Getter
  private static final Map<String, Long> customTimers = new HashMap<>();
  private static final Set<UUID> sotwEnabled = Sets.newHashSet();
  public static boolean itemsInSpawn = true;
  public static boolean sotwday = false;
  public static boolean ffasotw = false;
  public static boolean clearItems = true;
  static BukkitTask sotwTask = null;

  private static boolean kickedPlayers = false;

  @Command(names = {"createtimer", "customtimer"}, permission = "op", hidden = true)
  public static void customTimerCreate(final CommandSender sender,
      @Param(name = "time") final String time,
      @Param(name = "title", wildcard = true) final String title) {
    final int seconds = TimeUtils.parseTime(time);
    if (seconds < 0) {
      sender.sendMessage(ChatColor.RED + "Invalid time!");
      return;
    }

    customTimers.put(title, System.currentTimeMillis() + seconds * 1000L);
  }

  @Command(names = "clearitems", permission = "op", hidden = true)
    public static void clearItems(final CommandSender sender) {
        clearItems = !clearItems;
        sender.sendMessage(ChatColor.GREEN + "Items will " + (clearItems ? "now" : "no longer") + " be cleared in spawn");
    }

  @Command(names = {"removetimer", "customtimer remove"}, permission = "op", hidden = true)
  public static void customTimerDelete(final CommandSender sender,
      @Param(name = "title", wildcard = true) final String title) {
    final Long removed = customTimers.remove(title);
    if (removed != null && System.currentTimeMillis() < removed) {
      sender.sendMessage(ChatColor.GREEN + "Deactivated the " + title + " timer.");
      return;
    }
    sender.sendMessage(ChatColor.RED + title + "timer is not active.");
  }

  @Command(names = {"sotw spawnitems"}, hidden = true, permission = "op")
  public static void toggleitems(final CommandSender sender) {
    itemsInSpawn = !itemsInSpawn;
    sender.sendMessage(CC.translate(
        "&eYou have " + (itemsInSpawn ? "&aEnabled" : "&cDisabled") + " &ethe items in spawn."));
  }

  @Command(names = {"sotw enable"}, permission = "")
  public static void sotwEnable(final Player sender) {
    if (FFASOTWListener.ffaTask != null) {
      sender.sendMessage(CC.RED + "You cannot use this command during eotw.");
      return;
    }
    if (!isSOTWTimer()) {
      sender.sendMessage(ChatColor.RED + "This command is available just during sotw timer.");
      return;
    }

        /*nt playtimeTime = (int) Main.getInstance().getPlaytimeMap().getPlaytime(sender.getUniqueId());
        final Player bukkitPlayer = Main.getInstance().getServer().getPlayer(sender.getUniqueId());
        if (bukkitPlayer != null) {
            playtimeTime += (int) (Main.getInstance().getPlaytimeMap().getCurrentSession(bukkitPlayer.getUniqueId()) / 1000L);
        }
        if (playtimeTime < 1800) {
            sender.sendMessage(CC.RED + "You must have minimum 30 minutes of playtime to use this command.");
            sender.sendMessage(CC.YELLOW + "Your playtime is " + CC.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + CC.YELLOW + ".");
            return;
        }*/
    if (sotwEnabled.add(sender.getUniqueId())) {
      sender.sendMessage(ChatColor.GREEN + "Successfully disabled your SOTW timer.");
    } else {
      sender.sendMessage(ChatColor.RED + "Your SOTW timer was already disabled...");
    }
    CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
  }

  @Command(names = {"sotw cancel", "sotw end"}, permission = "op")
  public static void sotwCancel(final CommandSender sender) {
    final Long removed = customTimers.remove("&a&lSOTW Timer");
    if (removed != null && System.currentTimeMillis() < removed) {
      sender.sendMessage(ChatColor.GREEN + "Deactivated the SOTW timer.");
      return;
    }
    sender.sendMessage(ChatColor.RED + "SOTW timer is not active.");
  }

  @Command(names = {"sotw start"}, permission = "op")
  public static void sotwStart(final CommandSender sender,
      @Param(name = "time") final String time) {
    final int seconds = TimeUtils.parseTime(time);
    if (seconds < 0) {
      sender.sendMessage(ChatColor.RED + "Invalid time!");
      return;
    }
    customTimers.put("&a&lSOTW Timer", System.currentTimeMillis() + seconds * 1000);
    SpigotConfig.forcedisablemobai = true;
    if (MinerClass.minertask != null) {
      MinerClass.minertask.cancel();
      MinerClass.minertask = null;
    }

    sotwday = true;

    if(seconds >= 1200){ //20 minutes
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "createtimer 1h &4&lOP Keyall");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "createtimer 2h &6&lAirDropAll");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "createtimer 6h &d&lx3 Keys");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "createtimer 6h &c&l40% OFF");
    }


    TaskUtil.run(Main.getInstance(), () -> {
      //HandlerList.unregisterAll(Main.getInstance().getTradeListener());
      HandlerList.unregisterAll(Main.getInstance().getSpectatorListener());
      HandlerList.unregisterAll(Main.getInstance().getPickAxes());
      HandlerList.unregisterAll(Main.getInstance().getSwords());
      HandlerList.unregisterAll(Main.getInstance().getInvisibilityListener());
      HandlerList.unregisterAll(Main.getInstance().getEventHandler().getDtcListener());
    });

    Main.getInstance().getEventHandler().setScheduleEnabled(false);

    sender.sendMessage(ChatColor.GREEN + "Started the SOTW timer for " + time);

    CorePlugin.getInstance().getRedisManager().setChatSilenced(true);
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      OreMountainRespawnTask.makeReset();
      GlowRespawnTask.makeReset();
    }

    String timer = DurationFormatUtils.formatDurationWords(seconds * 1000, true, true);

    for (Player player : Bukkit.getOnlinePlayers()) {
      TitleBuilder title = new TitleBuilder("&6&lSOTW Timer", "&6&l" + timer + " remaining", 20, 60,
          20);
      title.send(player);
    }

    sotwTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
      if (!isSOTWTimer()) {
        if (sotwTask != null) {

          sotwday = false;
          sotwTask.cancel();
          sotwTask = null;

          /*TaskUtil.run(Main.getInstance(), () -> {
            for (Player player : Bukkit.getWorld("void").getPlayers()) {

              if (Utils.isDeathbanned(player.getUniqueId())) {
                continue;
              }

              player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
            }
          });*/

          for (Player player : Bukkit.getOnlinePlayers()) {
            TitleBuilder title = new TitleBuilder("&6&lSOTW Timer", "&cEnded",
                    20,
                    60,
                    20);
            title.send(player);
          }

          Main.getInstance().getEventHandler().setScheduleEnabled(true);
        }
        CorePlugin.getInstance().getRedisManager().setChatSilenced(false);
        return;
      }
      long diff = (CustomTimerCreateCommand.getSOTWremaining() - System.currentTimeMillis()) / 1000;

      //30 minutes
      /*if (diff == 1800) {

        String remaining = DurationFormatUtils.formatDurationWords(diff * 1000, true, true);

        for (Player player : Bukkit.getOnlinePlayers()) {
          TitleBuilder title = new TitleBuilder("&6&lSOTW Timer", "&6&l" + remaining + " remaining",
              20,
              60,
              20);
          title.send(player);
        }
      }*/

      //Every 30 minutes
        if (diff % 1800 == 0 && diff != 0) {
            String remaining = DurationFormatUtils.formatDurationWords(diff * 1000, true, true);

            for (Player player : Bukkit.getOnlinePlayers()) {
            TitleBuilder title = new TitleBuilder("&6&lSOTW Timer", "&6&l" + remaining + " remaining",
                20,
                60,
                20);
            title.send(player);
            }
        }

      //15 minutes
      if (diff == 900) {
        Bukkit.broadcastMessage(CC.RED + "All claims has been unlocked.");
      }

      //1 hour
      if (diff <= 3600 && !kickedPlayers) {

        kickedPlayers = true;
        for (Team team : Main.getInstance().getTeamHandler().getTeams()){

          int kicked = 0;
          if (team.getMembers().size() >  Main.getInstance().getMapHandler().getTeamSize()){
            for (UUID uuid : team.getMembers()){

              if (kicked == Main.getInstance().getMapHandler().getTeamSize()){
                break;
              }

              if (team.getOwner() != uuid){
                team.removeMember(uuid);

                Player player = Bukkit.getPlayer(uuid);

                if (player != null && team.isInClaim(player)){
                  player.teleport(player.getWorld().getSpawnLocation());
                }

                kicked++;
              }
            }

            team.setClaimsLocked(true);
          }
        }
      }

    }, 20, 20);
  }

  @Command(names = {"sotw toggleteamview"}, hidden = true, permission = "op")
  public static void toggleteamview(final CommandSender sender) {
    LunarTeammatesHandler.teamview = !LunarTeammatesHandler.teamview;
    if (LunarTeammatesHandler.teamview) {
      sender.sendMessage(CC.GREEN + "You have enabled the lunar teamview.");
    } else {
      sender.sendMessage(CC.RED + "You have disable the lunar teamview.");
    }
  }

  @Command(names = {"sotw extend"}, permission = "op")
  public static void sotwExtend(final CommandSender sender,
      @Param(name = "time") final String time) {
    int seconds;
    try {
      seconds = TimeUtils.parseTime(time);
    } catch (Exception e) {
      sender.sendMessage(ChatColor.RED + "Invalid time!");
      return;
    }
    if (seconds < 0) {
      sender.sendMessage(ChatColor.RED + "Invalid time!");
      return;
    }
    if (!isSOTWTimer()) {
      sender.sendMessage(ChatColor.RED + "There is currently no active SOTW timer.");
      return;
    }
    customTimers.put("&a&lSOTW Timer", customTimers.get("&a&lSOTW Timer") + seconds * 1000);
    sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time);
  }

  public static boolean areClaimsLocked() {
    if (!isSOTWTimer()) {
      return false;
    }
    long diff = (CustomTimerCreateCommand.getSOTWremaining() - System.currentTimeMillis()) / 1000;
    return !(diff <= 900);
  }

  public static boolean isSOTWTimer() {
    return customTimers.containsKey("&a&lSOTW Timer");
  }

  public static boolean hasSOTWEnabled(final UUID uuid) {
    return sotwEnabled.contains(uuid);
  }

  public static boolean hasSOTWEnabled(final Player player) {
    return sotwEnabled.contains(player.getUniqueId());
  }

  public static Long getSOTWremaining() {
    if (!customTimers.containsKey("&a&lSOTW Timer")) {
      return 0L;
    }
    return getCustomTimers().get("&a&lSOTW Timer");
  }
}