package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamKickCommand {

  @Command(names = {"team kick", "t kick", "f kick", "faction kick", "fac kick"}, permission = "")
  public static void teamKick(Player sender, @Param(name = "player") UUID uuid) {
    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }

    if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())
        || team.isCaptain(sender.getUniqueId()))) {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
      return;
    }

    if (!team.isMember(uuid)) {
      sender.sendMessage(ChatColor.RED + UUIDUtils.name(uuid) + " isn't on your team!");
      return;
    }

    if (team.isOwner(uuid)) {
      sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
      return;
    }

    if (team.isCoLeader(uuid) && (!team.isOwner(sender.getUniqueId()))) {
      sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
      return;
    }

    if (team.isCaptain(uuid) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(
        sender.getUniqueId())) {
      sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
      return;
    }

    if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
      sender.sendMessage(CC.RED + "You cannot kick while your team is banned.");
      return;
    }

    if (DTRHandler.isOnCooldown(team) && !Main.getInstance().getServerHandler().isPreEOTW()) {
      sender.sendMessage(
          CC.RED + "You cannot kick members while are regenerating DTR! You can forcibly kick "
              + UUIDUtils.name(uuid) + " by using '" + ChatColor.YELLOW + "/f forcekick "
              + UUIDUtils.name(uuid) + ChatColor.RED + "' which will cost your team 1 DTR.");
      return;
    }

    Player player = Bukkit.getPlayer(uuid);
    if (SpawnTagHandler.isTagged(player)) {
      sender.sendMessage(ChatColor.RED + UUIDUtils.name(uuid)
          + " is currently combat-tagged! You can forcibly kick " + UUIDUtils.name(uuid)
          + " by using '" + ChatColor.YELLOW + "/f forcekick " + UUIDUtils.name(uuid)
          + ChatColor.RED + "' which will cost your team 1 DTR.");
      return;
    }

        /*if (CombatLoggerListener.hasCombatLogger(offlinePlayer.getName())) {
            sender.sendMessage(ChatColor.RED + targetName + " is currently combat-logged! You can forcibly kick " + targetName + " by using '" + ChatColor.YELLOW + "/f forcekick " + targetName + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }*/

    team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(uuid) + " was kicked by " + sender.getName() + "!");

    TeamTrackerManager.logAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
            "playerId", uuid.toString(),
            "kickedById", sender.getUniqueId().toString(),
            "kickedByName", sender.getName(),
            "usedForceKick", "false",
            "date", System.currentTimeMillis()
    ));

    if (team.removeMember(uuid)) {
      team.disband();
    } else {
      team.flagForSave();
    }

    Main.getInstance().getTeamHandler().setTeam(uuid, null);

    Player target = Bukkit.getPlayer(uuid);
    if (target != null) {
      CorePlugin.getInstance().getNametagEngine().reloadPlayer(target);
      CorePlugin.getInstance().getNametagEngine().reloadOthersFor(target);
      WaypointManager.updatePlayerFactionChange(target);
      team.getActiveEffects().keySet().forEach(target::removePotionEffect);
    }
  }
}