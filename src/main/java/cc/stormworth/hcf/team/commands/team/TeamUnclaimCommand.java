package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamUnclaimCommand {

  @Command(names = {"team unclaim", "t unclaim", "f unclaim", "faction unclaim",
      "fac unclaim"}, permission = "")
  public static void teamUnclaim(final Player sender, @Param(name = "all?", defaultValue = "not_all?") final String all) {
    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }

    if (!team.isOwner(sender.getUniqueId()) && !team.isOwner(sender.getUniqueId())) {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders can do this.");
      return;
    }

    if (team.isRaidable()) {
      sender.sendMessage(
          ChatColor.RED + "You may not unclaim land while your faction is raidable!");
      return;
    }

    if (DTRHandler.isOnCooldown(team)) {
      sender.sendMessage(CC.RED + "You cannot unclaim while are regenerating DTR!");
      return;
    }

    if (all.equalsIgnoreCase("all")) {
      int claims = team.getClaims().size();
      int refund = 0;

      for (final Claim claim : team.getClaims()) {
        refund += Claim.getPrice(claim, team, false);

        Location minLoc = claim.getMinimumPoint();
        Location maxLoc = claim.getMaximumPoint();

        TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.<String, Object>builder()
                        .put("playerId", sender.getUniqueId().toString())
                        .put("refund", Claim.getPrice(claim, team, false))
                        .put("point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ())
                .put("point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ())
                .put("date", System.currentTimeMillis())
                .build()
        );
      }

      team.setBalance(team.getBalance() + refund);
      team.removeAllEffects();
      LandBoard.getInstance().clear(team);
      team.getClaims().clear();
      team.setHQ(null);
      team.flagForSave();

      for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
        if (team.isMember(player.getUniqueId())) {
          player.sendMessage(
              ChatColor.YELLOW + sender.getName() + " has unclaimed all of your team's claims. ("
                  + ChatColor.GOLD + claims + " total" + ChatColor.YELLOW + ")");
        }
      }
      return;
    }

    if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && team.ownsLocation(sender.getLocation())) {

      Claim claim = LandBoard.getInstance().getClaim(sender.getLocation());

      team.getActiveEffects().keySet().forEach(effect -> {
        for (Player player : team.getOnlineMembers()) {
          if (claim.contains(player.getLocation())) {
            player.removePotionEffect(effect);
          }
        }
      });

      int refund = Claim.getPrice(claim, team, false);

      team.setBalance(team.getBalance() + refund);
      team.getClaims().remove(claim);
      team.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed " + ChatColor.GOLD + claim.getFriendlyName() + ChatColor.YELLOW + ".");
      team.flagForSave();

      LandBoard.getInstance().setTeamAt(claim, null);

      Location minLoc = claim.getMinimumPoint();
      Location maxLoc = claim.getMaximumPoint();

      TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.<String, Object>builder()
              .put("playerId", sender.getUniqueId().toString())
              .put("refund", Claim.getPrice(claim, team, false))
              .put("point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ())
              .put("point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ())
              .put("date", System.currentTimeMillis())
              .build());

      if (team.getHQ() != null && claim.contains(team.getHQ())) {
        team.setHQ(null);
        sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
      }

      return;
    }

    sender.sendMessage(ChatColor.RED + "You don't own this claim.");
    sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/team unclaim all" + ChatColor.RED + ".");
  }

  @Command(names = {"team unclaimfor", "t unclaimfor", "f unclaimfor", "faction unclaimfor", "fac unclaimfor"}, permission = "op")
  public static void teamUnclaimFor(Player sender, @Param(name = "all?", defaultValue = "not_all?") String all, @Param(name = "team") Team team) {

    if (all.equalsIgnoreCase("all")) {

      int claims = team.getClaims().size();

      LandBoard.getInstance().clear(team);
      team.getClaims().clear();
      team.setHQ(null);
      team.flagForSave();

      for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
        if (team.isMember(player.getUniqueId())) {
          player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed all of your team's claims. (" + ChatColor.GOLD + claims + " total" + ChatColor.YELLOW + ")");
        }
      }

      return;
    }
    if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && team.ownsLocation(sender.getLocation())) {

      Claim claim2 = LandBoard.getInstance().getClaim(sender.getLocation());

      team.getClaims().remove(claim2);
      team.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed " + ChatColor.GOLD + claim2.getFriendlyName() + ChatColor.YELLOW + ".");
      team.flagForSave();
      LandBoard.getInstance().setTeamAt(claim2, null);

      if (team.getHQ() != null && claim2.contains(team.getHQ())) {
        team.setHQ(null);
        sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
      }

      return;
    }

    sender.sendMessage(ChatColor.RED + "You don't own this claim.");
    sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/team unclaimfor all" + ChatColor.RED + ".");
  }
}