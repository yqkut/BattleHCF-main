package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamFriendlyFireCommand {

  @Command(names = {"team ff", "t ff", "f ff", "faction ff", "fac ff"}, permission = "")
  public static void teamKick(final Player sender) {
    final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }
    if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())
        && !team.isCaptain(sender.getUniqueId())) {
      sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
      return;
    }
    team.setFriendlyFire(!team.isFriendlyFire());
    String watcherName = ChatColor.DARK_GREEN + sender.getName();
    if (team.isOwner(sender.getUniqueId())) {
      watcherName = watcherName + ChatColor.GRAY + "**";
    } else if (team.isCoLeader(sender.getUniqueId())) {
      watcherName = watcherName + ChatColor.GRAY + "**";
    } else if (team.isCaptain(sender.getUniqueId())) {
      watcherName = watcherName + ChatColor.GRAY + "*";
    }
    final String ffstatus = team.isFriendlyFire() ? "&aEnabled" : "&cDisabled";
    team.sendMessage(
        CC.translate(watcherName + " &ehas " + ffstatus + " &ethe friendly fire of your faction!"));
  }
}