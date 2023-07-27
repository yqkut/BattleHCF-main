package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ForceDisbandAllCommand {

  private static Runnable confirmRunnable;

  @Command(names = {"forcedisbandall"}, permission = "op")
  public static void forceDisbandAll(CommandSender sender) {
    confirmRunnable = () -> {

      List<Team> teams = new ArrayList<>(Main.getInstance().getTeamHandler().getTeams());

      for (Team team : teams) {
        team.disband();
      }
      Bukkit.broadcastMessage(
          ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
    };
    sender.sendMessage(
        ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED
            + "/forcedisbandall confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN
            + "/forcedisbandall cancel" + ChatColor.RED + " to cancel.");
  }

  @Command(names = {"forcedisbandall confirm"}, permission = "op")
  public static void confirm(final CommandSender sender) {
    if (ForceDisbandAllCommand.confirmRunnable == null) {
      sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
      return;
    }
    sender.sendMessage(ChatColor.GREEN + "If you're sure...");
    ForceDisbandAllCommand.confirmRunnable.run();
  }

  @Command(names = {"forcedisbandall cancel"}, permission = "op")
  public static void cancel(final CommandSender sender) {
    if (ForceDisbandAllCommand.confirmRunnable == null) {
      sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
      return;
    }
    sender.sendMessage(ChatColor.GREEN + "Cancelled.");
    ForceDisbandAllCommand.confirmRunnable = null;
  }
}