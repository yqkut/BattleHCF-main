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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamForceKickCommand {

  @Command(names = {"team forcekick", "t forcekick", "f forcekick", "faction forcekick",
      "fac forcekick"}, permission = "")
  public static void teamForceKick(Player sender, @Param(name = "player") UUID player) {
    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }

    if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
      return;
    }

    if (!team.isMember(player)) {
      sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't on your team!");
      return;
    }

    if (team.isOwner(player)) {
      sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
      return;
    }

    if (team.isCoLeader(player) && !team.isOwner(sender.getUniqueId())) {
      sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
      return;
    }

    if (team.isCaptain(player) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(
        sender.getUniqueId())) {
      sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
      return;
    }

    if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
      sender.sendMessage(CC.RED + "You cannot kick while your team is banned.");
      return;
    }

    if (team.removeMember(player)) {
      team.disband();
    } else {
      team.flagForSave();
    }

    TeamTrackerManager.logAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
            "playerId", player.toString(),
            "kickedById", sender.getUniqueId().toString(),
            "usedForceKick", "true",
            "date", System.currentTimeMillis()
    ));

    Main.getInstance().getTeamHandler().setTeam(player, null);
    Player bukkitPlayer = Main.getInstance().getServer().getPlayer(player);
    // || CombatLoggerListener.hasCombatLogger(UUIDUtils.name(player))

    if (SpawnTagHandler.isTagged(bukkitPlayer) || (DTRHandler.isOnCooldown(team)
        && !Main.getInstance().getServerHandler().isPreEOTW())) {
      team.setDTR(team.getDTR() - 1.0);
      team.sendMessage(
          ChatColor.RED + UUIDUtils.name(player) + " was force kicked by " + sender.getName() + " and your team lost 1 DTR!");
      long dtrCooldown;
      if (team.isRaidable()) {

        int newpoints = team.getRemovedPoints() + (team.getPoints() * 10 / 100);

        if (newpoints < 1) {
          team.setPoints(0);
          return;
        }

        team.setRemovedPoints(newpoints);

        dtrCooldown = System.currentTimeMillis() + (team.isDtrRegenFaster() ?
            Main.getInstance().getMapHandler().getRegenTimeRaidable() / 2
            : Main.getInstance().getMapHandler().getRegenTimeRaidable());
      } else {
        dtrCooldown = System.currentTimeMillis() + (team.isDtrRegenFaster() ?
                Main.getInstance().getMapHandler().getRegenTimeDeath() / 2
            : Main.getInstance().getMapHandler().getRegenTimeDeath());
      }

      team.setDTRCooldown(dtrCooldown);
      DTRHandler.markOnDTRCooldown(team);
    } else {
      team.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " was force kicked by " + sender.getName() + "!");
    }

    if (bukkitPlayer != null) {
      CorePlugin.getInstance().getNametagEngine().reloadPlayer(bukkitPlayer);
      CorePlugin.getInstance().getNametagEngine().reloadOthersFor(bukkitPlayer);
      WaypointManager.updatePlayerFactionChange(bukkitPlayer);
      team.getActiveEffects().keySet().forEach(bukkitPlayer::removePotionEffect);
    }
  }
}