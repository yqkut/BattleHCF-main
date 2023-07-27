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

public class TeamDepositCommand {

  @Command(names = {"team deposit", "t deposit", "f deposit", "faction deposit", "fac deposit",
      "team d", "t d", "f d", "faction d", "fac d", "team m d", "t m d", "f m d", "faction m d",
      "fac m d"}, permission = "", async = true)
  public static void teamDeposit(final Player sender, @Param(name = "amount") final int amount) {
    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }

    if (amount <= 0) {
      sender.sendMessage(ChatColor.RED + "You cannot deposit $0 (or less)!");
      return;
    }

    HCFProfile profile = HCFProfile.get(sender);

    if (profile.getEconomyData().getBalance() < amount) {
      sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
      return;
    }

    profile.getEconomyData().subtractBalance(amount);
    sender.sendMessage(
        ChatColor.YELLOW + "You have added " + ChatColor.GOLD + amount + ChatColor.YELLOW
            + " to the team balance!");

    TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_DEPOSIT_MONEY, ImmutableMap.<String, Object>builder()
            .put("playerId", sender.getUniqueId().toString())
            .put("amount", amount)
            .put( "oldBalance", team.getBalance())
            .put( "newBalance", team.getBalance() + amount)
            .put("date", System.currentTimeMillis()).build());

    team.setBalance(team.getBalance() + amount);
    team.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.GOLD + amount
        + ChatColor.YELLOW + " into the team balance.");
  }

  @Command(names = {"team deposit all", "t deposit all", "f deposit all", "faction deposit all",
      "fac deposit all", "team d all", "t d all", "f d all", "faction d all", "fac d all",
      "team m d all", "t m d all", "f m d all", "faction m d all",
      "fac m d all"}, permission = "", async = true)
  public static void teamDepositAll(final Player sender) {
    teamDeposit(sender, (int) HCFProfile.get(sender).getEconomyData().getBalance());
  }
}