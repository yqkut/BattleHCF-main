package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamForceLeaveCommand {

  @Command(names = {"team forceleave", "t forceleave", "f forceleave", "faction forceleave",
      "fac forceleave", "t fl", "team fl"}, permission = "")
  public static void forceLeave(final Player sender) {
    final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      return;
    }
    if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
      sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
      return;
    }
    if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
      sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
      return;
    }
    if (team.removeMember(sender.getUniqueId())) {
      team.disband();
      Main.getInstance().getTeamHandler().setTeam(sender, null);
      sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
    } else {
      Main.getInstance().getTeamHandler().setTeam(sender, null);
      team.flagForSave();
      if (SpawnTagHandler.isTagged(sender)) {
        team.setDTR(team.getDTR() - 1.0);
        team.sendMessage(ChatColor.RED + sender.getName()
            + " forcibly left the team. Your team has lost 1 DTR.");
        sender.sendMessage(
            ChatColor.RED + "You have forcibly left your team. Your team lost 1 DTR.");
      } else {
        team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");
        sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
      }
    }
    CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
    CorePlugin.getInstance().getNametagEngine().reloadOthersFor(sender);
    WaypointManager.updatePlayerFactionChange(sender);
    team.getActiveEffects().keySet().forEach(sender::removePotionEffect);
  }
}