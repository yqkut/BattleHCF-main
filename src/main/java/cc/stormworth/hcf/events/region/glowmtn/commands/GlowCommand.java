package cc.stormworth.hcf.events.region.glowmtn.commands;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.region.glowmtn.GlowHandler;
import cc.stormworth.hcf.events.region.glowmtn.GlowMountain;
import cc.stormworth.hcf.events.region.glowmtn.GlowRespawnTask;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GlowCommand {

  @Command(names = "glow scan", permission = "op")
  public static void glowScan(Player sender) {
    Team team = Main.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

    if (team == null) {
      sender.sendMessage(
          ChatColor.RED + "You must first create the team (" + GlowHandler.getGlowTeamName()
              + ") and claim it!");
      return;
    }

    if (team.getClaims().isEmpty()) {
      sender.sendMessage(ChatColor.RED + "You must claim land for '" + GlowHandler.getGlowTeamName()
          + "' before scanning it!");
      return;
    }

    if (!Main.getInstance().getGlowHandler().hasGlowMountain()) {
      Main.getInstance().getGlowHandler().setGlowMountain(new GlowMountain());
    }

    Main.getInstance().getGlowHandler().getGlowMountain().scan();
    Main.getInstance().getGlowHandler().save();

    sender.sendMessage(
        GREEN + "[Glowstone Mountain] Scanned all glowstone and saved glowstone mountain to file!");
  }

  @Command(names = "glow reset", permission = "op")
  public static void glowReset(Player sender) {
    Team team = Main.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

    // Make sure we have a team, claims, and a mountain!
    if (team == null || team.getClaims().isEmpty() || !Main.getInstance().getGlowHandler()
        .hasGlowMountain()) {
      sender.sendMessage(RED + "Create the team '" + GlowHandler.getGlowTeamName()
          + "', then make a claim for it, finally scan it! (/glow scan)");
      return;
    }

    // Check, check, check, LIFT OFF! (reset the mountain)
    GlowRespawnTask.makeReset();
  }

  @Command(names = {"glow set"}, permission = "op", description = "set glowstone location")
  public static void set(final Player sender) {
    final Team team = Main.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());
    team.setHQ(sender.getLocation());
    sender.sendMessage(CC.translate("&aSuccessfully updated glowstone location!"));
  }

  @Command(names = {"glow start"}, permission = "op")
  public static void start(final Player sender) {
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lGlowstone")) {
      sender.sendMessage(CC.RED + "Glowstone Event is already running.");
      return;
    }
    String[] messages = new String[]{
        ChatColor.RED + "███████",
        ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD
            + "[Glowstone Event]",
        ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " "
            + ChatColor.YELLOW + "Glowstone will reset every time",
        ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " "
            + ChatColor.YELLOW + "that remaining blocks equals 0.",
        ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.GOLD
            + "Every 10 glowstone equals 1 fac point.",
        ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
        ChatColor.RED + "███████"
    };

    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
      for (String message : messages) {
        Bukkit.broadcastMessage(message);
      }
      return;
    });
    CustomTimerCreateCommand.getCustomTimers()
        .put("&6&lGlowstone", System.currentTimeMillis() + 900 * 1000);
    sender.sendMessage(CC.translate("&aSuccessfully started Glowstone Event."));
  }

  @Command(names = {"glow stop"}, permission = "op")
  public static void stop(final Player sender) {
    if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&6&lGlowstone")) {
      sender.sendMessage(CC.RED + "Glowstone Event is already running.");
      return;
    }
    CustomTimerCreateCommand.getCustomTimers().remove("&6&lGlowstone");
    sender.sendMessage(CC.translate("&aSuccessfully stopped Glowstone Event."));
  }
}