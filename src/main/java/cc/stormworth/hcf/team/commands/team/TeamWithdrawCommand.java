package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamWithdrawCommand {

  @Command(names = {"team withdraw", "t withdraw", "f withdraw", "faction withdraw", "fac withdraw",
      "team w", "t w", "f w", "faction w", "fac w"}, permission = "", async = true)
  public static void teamWithdraw(final Player sender, @Param(name = "amount") final int amount) {
    final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }
    if (team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())
        || team.isOwner(sender.getUniqueId())) {
      if (team.getBalance() < amount) {
        sender.sendMessage(ChatColor.RED + "The team doesn't have enough money to do this!");
        return;
      }
      if (amount <= 0.0f) {
        sender.sendMessage(ChatColor.RED + "You cannot withdraw $0 (or less)!");
        return;
      }

      HCFProfile profile = HCFProfile.get(sender);

      profile.getEconomyData().addBalance(amount);
      sender.sendMessage(ChatColor.YELLOW + "You have withdrawn " + ChatColor.GOLD + amount + ChatColor.YELLOW + " from the team balance!");

      TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_WITHDRAW_MONEY, ImmutableMap.<String, Object>builder()
              .put("playerId", sender.getUniqueId().toString())
              .put("amount", amount)
              .put("oldBalance", team.getBalance())
              .put("newBalance", team.getBalance() - amount)
              .put("date", System.currentTimeMillis())
              .build()
      );

      team.setBalance(team.getBalance() - amount);
      team.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.GOLD + "$"
              + amount + ChatColor.YELLOW + " from the team balance.");


    } else {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
    }
  }

  @Command(names = {"team withdraw all", "t withdraw all", "f withdraw all", "faction withdraw all",
      "fac withdraw all",
      "team w all", "t w all", "f w all", "faction w all",
      "fac w all"}, permission = "", async = true)
  public static void teamWithdrawAll(final Player sender) {
    final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }
    int amount = team.getBalance();
    if (team.isCoLeader(sender.getUniqueId())
        || team.isOwner(sender.getUniqueId())) {

      if (team.getBalance() <= 0.0f) {
        sender.sendMessage(ChatColor.RED + "The team doesn't have enough money to do this!");
        return;
      }

      HCFProfile profile = HCFProfile.get(sender);

      profile.getEconomyData().addBalance(amount);

      team.setBalance(0);
      sender.sendMessage(
          ChatColor.YELLOW + "You have withdrawn " + ChatColor.GOLD + amount + ChatColor.YELLOW
              + " from the team balance!");
      team.sendMessage(
          ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.GOLD + "$"
              + amount + ChatColor.YELLOW + " from the team balance.");
    } else {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team co leader can do this.");
    }
  }
}