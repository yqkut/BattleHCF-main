package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.command.annotations.Type;
import cc.stormworth.core.util.command.parameter.filter.NormalFilter;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.commands.staff.ModCommand;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.regex.Pattern;

public class TeamCreateCommand {

  public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
  public static final Set<String> disallowedTeamNames = ImmutableSet.of("list", "glowstone", "dtc",
      "eotw", "none", "null", "self", "nether");

  @Command(names = {"team create", "t create", "f create", "faction create", "fac create"}, permission = "", async = true)
  public static void teamCreate(final Player sender, @Param(name = "team") @Type(NormalFilter.class) final String team) {

    if (Main.getInstance().getTeamHandler().getTeam(sender) != null) {
      sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
      return;
    }

    if (team.length() > 12) {
      sender.sendMessage(ChatColor.RED + "Maximum team name size is 12 characters!");
      return;
    }

    if (team.length() < 3) {
      sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
      return;
    }

    if (Main.getInstance().getTeamHandler().getTeam(team) != null) {
      sender.sendMessage(ChatColor.GRAY + "That team already exists!");
      return;
    }

    if (disallowedTeamNames.contains(team.toLowerCase())) {
      sender.sendMessage(ChatColor.RED + "Team name disabled!");
      return;
    }
    if (ALPHA_NUMERIC.matcher(team).find()) {
      sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
      return;
    }
    if ((EOTWCommand.isFfaEnabled() || CustomTimerCreateCommand.ffasotw) && !sender.isOp()) {
      sender.sendMessage(ChatColor.RED + "You cannot create teams right now.");
      return;
    }

    HCFProfile profile = HCFProfile.get(sender);

    if (profile.hasTeamDelay() && !sender.isOp()) {
      long createmillis = profile.getTeamDelay();
      long left = createmillis - System.currentTimeMillis();
      if (left >= 0L) {
        sender.sendMessage(
            ChatColor.RED + "You still have " + ScoreFunction.TIME_FANCY.apply(left / 1000.0f)
                + " on your faction create cooldown.");
        return;
      }
    }

    sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

    Team createdTeam = new Team(team);
    createdTeam.setUniqueId(new ObjectId());
    createdTeam.setOwner(sender.getUniqueId());
    createdTeam.setName(team);
    createdTeam.setCreateAt(System.currentTimeMillis());
    createdTeam.setDTR(CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") ? createdTeam.getMaxDTR() : 1.0);

    if (Main.getInstance().getServerHandler().isEOTW()) {
      createdTeam.setDTR(-0.99);
    } else {
      createdTeam.setDTR(1.0);
    }

    TeamTrackerManager.logAsync(createdTeam, TeamActionType.PLAYER_CREATE_TEAM, ImmutableMap.of(
            "playerId", sender.getUniqueId().toString(),
            "date", System.currentTimeMillis()
    ));

    Main.getInstance().getTeamHandler().setupTeam(createdTeam);
    if (ModCommand.createFactionsMessage) {
      Bukkit.broadcastMessage(
          ChatColor.YELLOW + "Team " + ChatColor.BLUE + createdTeam.getName() + ChatColor.YELLOW
              + " has been " + ChatColor.GREEN + "created" + ChatColor.YELLOW + " by "
              + sender.getDisplayName());
    }

    profile.setTeamDelay(System.currentTimeMillis() + 60 * 1000L);
    CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
    WaypointManager.updatePlayerFactionChange(sender);
  }

  @Command(names = {"team silentcreate", "t silentcreate", "f silentcreate", "faction silentcreate", "fac silentcreate"}, permission = "op", hidden = true, async = true)
  public static void teamSilentCreate(final Player sender, @Param(name = "team") @Type(NormalFilter.class) final String team) {
    if (Main.getInstance().getTeamHandler().getTeam(sender) != null) {
      sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
      return;
    }
    if (team.length() > 16) {
      sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
      return;
    }
    if (team.length() < 3) {
      sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
      return;
    }
    if (Main.getInstance().getTeamHandler().getTeam(team) != null) {
      sender.sendMessage(ChatColor.GRAY + "That team already exists!");
      return;
    }
    if (disallowedTeamNames.contains(team.toLowerCase())) {
      sender.sendMessage(ChatColor.RED + "Team name disabled!");
      return;
    }
    if (ALPHA_NUMERIC.matcher(team).find()) {
      sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
      return;
    }
    if ((EOTWCommand.isFfaEnabled() || CustomTimerCreateCommand.ffasotw) && !sender.isOp()) {
      sender.sendMessage(ChatColor.RED + "You cannot create teams right now.");
      return;
    }
    sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

    Team createdTeam = new Team(team);
    createdTeam.setUniqueId(new ObjectId());
    createdTeam.setOwner(sender.getUniqueId());
    createdTeam.setName(team);
    if (Main.getInstance().getServerHandler().isEOTW()) {
      createdTeam.setDTR(-0.99);
    } else {
      createdTeam.setDTR(1.0);
    }

    Main.getInstance().getTeamHandler().setupTeam(createdTeam);
    CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
    WaypointManager.updatePlayerFactionChange(sender);
  }
}