package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInviteCommand {

  @Command(names = {"team invite", "t invite", "f invite", "faction invite", "fac invite",
      "team inv", "t inv", "f inv", "faction inv", "fac inv"}, permission = "", async = true)
  public static void teamInvite(Player sender, @Param(name = "player") UUID player) {

    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }

    if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
      return;
    }


    if (team.isMember(player)) {
      sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is already on your team.");
      return;
    }

    if (team.getInvitations().contains(player)) {
      sender.sendMessage(ChatColor.RED + "That player has already been invited.");
      return;
    }

    if (team.isRaidable() && !Main.getInstance().getServerHandler().isPreEOTW()) {
      sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
      return;
    }

    Profile profile = Profile.getByUuid(team.getOwner());

    if (!canBypassMembersSize(profile)){
      if (team.getMembers().size() >= Main.getInstance().getMapHandler().getTeamSize()) {
        sender.sendMessage(ChatColor.RED + "The max team size is " + Main.getInstance().getMapHandler().getTeamSize() + "!");
        return;
      }
    }

    TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
            "playerId", player.toString(),
            "invitedById", sender.getUniqueId().toString(),
            "date", System.currentTimeMillis()
    ));

    team.getInvitations().add(player);
    team.flagForSave();

    Player bukkitPlayer = Main.getInstance().getServer().getPlayer(player);

    if (bukkitPlayer != null) {
        
      bukkitPlayer.sendMessage(
          ChatColor.YELLOW + sender.getDisplayName() + ChatColor.YELLOW + " invited you to join '" +
              ChatColor.GOLD + team.getName() + ChatColor.YELLOW + "'.");

      FormatingMessage clickToJoin = new FormatingMessage("Type '")
          .color(ChatColor.YELLOW)
          .then("/team join " + team.getName())
          .color(ChatColor.GOLD);

      clickToJoin.then("' or ").color(ChatColor.YELLOW);

      clickToJoin.then("click here")
          .color(ChatColor.AQUA)
          .command("/team join " + team.getName())
          .tooltip("§aJoin " + team.getName());

      clickToJoin.then(" to join.")
          .color(ChatColor.YELLOW);

      clickToJoin.send(bukkitPlayer);

      team.sendMessage(bukkitPlayer.getDisplayName() + ChatColor.YELLOW + " has been invited to the team!");
      return;
    }

    team.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " has been invited to the team!");
  }

  public static boolean canBypassMembersSize(Profile profile){

    long diff = (CustomTimerCreateCommand.getSOTWremaining() - System.currentTimeMillis()) / 1000;

    return profile.getRank().isAboveOrEqual(Rank.PARTNER) && (CustomTimerCreateCommand.sotwday && diff > 3600);
  }
}