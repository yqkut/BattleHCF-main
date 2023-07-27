package cc.stormworth.hcf.events.region.oremountain.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.region.oremountain.OreMountain;
import cc.stormworth.hcf.events.region.oremountain.OreMountainHandler;
import cc.stormworth.hcf.events.region.oremountain.OreMountainRespawnTask;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

public class MountainCommand {

  static BukkitTask mountainTask;

  @Command(names = "mountain scan", permission = "op")
  public static void scan(Player sender) {
    Team team = Main.getInstance().getTeamHandler().getTeam(OreMountainHandler.getOreTeamName());

    // Make sure we have a team
    if (team == null) {
      sender.sendMessage(
          ChatColor.RED + "You must first create the team (" + OreMountainHandler.getOreTeamName()
              + ") and claim it!");
      return;
    }

    // Make sure said team has a claim
    if (team.getClaims().isEmpty()) {
      sender.sendMessage(
          ChatColor.RED + "You must claim land for '" + OreMountainHandler.getOreTeamName()
              + "' before scanning it!");
      return;
    }

    // We have a claim, and a team, now do we have a ore?
    if (!Main.getInstance().getOreHandler().hasOreMountain()) {
      Main.getInstance().getOreHandler().setOreMountain(new OreMountain());
    }

    // We have a ore now, we're gonna scan and save the area
    Main.getInstance().getOreHandler().getOreMountain().scan();
    Main.getInstance().getOreHandler().save(); // save to file :D

    sender.sendMessage(AQUA + "[Ore Mountain] Scanned all ore and saved ore mountain to file!");
  }

  @Command(names = "mountain reset", permission = "op")
  public static void reset(Player sender) {
    Team team = Main.getInstance().getTeamHandler().getTeam(OreMountainHandler.getOreTeamName());

    // Make sure we have a team, claims, and a mountain!
    if (team == null || team.getClaims().isEmpty() || !Main.getInstance().getOreHandler()
        .hasOreMountain()) {
      sender.sendMessage(RED + "Create the team '" + OreMountainHandler.getOreTeamName()
          + "', then make a claim for it, finally scan it! (/mountain scan)");
      return;
    }

    // Check, check, check, LIFT OFF! (reset the mountain)
    OreMountainRespawnTask.makeReset();
  }

  @Command(names = {"mountain set"}, permission = "op", description = "set ore mountain location")
  public static void set(final Player sender) {
    final Team team = Main.getInstance().getTeamHandler()
        .getTeam(OreMountainHandler.getOreTeamName());
    team.setHQ(sender.getLocation());
    sender.sendMessage(CC.translate("&aSuccessfully updated ore location!"));
  }

  @Command(names = {"mountain start"}, permission = "op")
  public static void start(final Player sender, @Param(name = "time") final String time) {
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lDouble Ores")) {
      sender.sendMessage(CC.RED + "Ore Event is already running.");
      return;
    }
    String[] messages = new String[]{
        ChatColor.RED + "███████",
        ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD
            + "[Ore Event]",
        ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " "
            + ChatColor.YELLOW + "Ores will reset every time",
        ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " "
            + ChatColor.YELLOW + "that remaining blocks equals 0.",
        ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.GOLD
            + "Every block you break will be duplicated",
        ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
        ChatColor.RED + "███████"
    };

    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
      for (String message : messages) {
        Bukkit.broadcastMessage(CC.translate(message));
      }
      return;
    });
    final int seconds = TimeUtils.parseTime(time);
    if (seconds < 0) {
      sender.sendMessage(ChatColor.RED + "Invalid time!");
      return;
    }
    CustomTimerCreateCommand.getCustomTimers()
        .put("&6&lDouble Ores", System.currentTimeMillis() + seconds * 1000);
    sender.sendMessage(CC.translate("&aSuccessfully started Ore Mountain Event."));
    SpigotConfig.oremultiplier = 2;
    mountainTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
      if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lDouble Ores")) {
        SpigotConfig.oremultiplier = 1;
        if (mountainTask != null)
          mountainTask.cancel();
        return;
      }
    }, 20, 20);
  }

  @Command(names = {"mountain stop"}, permission = "op")
  public static void stop(final Player sender) {
    if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lDouble Ores")) {
      sender.sendMessage(CC.RED + "Ore Mountain Event is already running.");
      return;
    }
    CustomTimerCreateCommand.getCustomTimers().remove("&6&lDouble Ores");
    sender.sendMessage(CC.translate("&aSuccessfully stopped Ore Mountain Event."));
  }
}