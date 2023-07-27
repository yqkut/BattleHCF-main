package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamLeaveCommand {

  @Command(names = {"team leave", "t leave", "f leave", "faction leave",
      "fac leave"}, permission = "")
  public static void teamLeave(final Player sender) {

    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

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
    if (SpawnTagHandler.isTagged(sender)) {
      sender.sendMessage(
          ChatColor.RED + "You are combat-tagged! You can only leave your faction by using '"
              + ChatColor.YELLOW + "/f forceleave" + ChatColor.RED
              + "' which will cost your team 1 DTR.");
      return;
    }
    if (Main.getInstance().getEventHandler().getBannedTeams().contains(team)) {
      sender.sendMessage(CC.RED + "You cannot leave while your team is banned.");
      return;
    }
    if (team.removeMember(sender.getUniqueId())) {
      team.disband();
      Main.getInstance().getTeamHandler().setTeam(sender, null);
      sender.sendMessage(CC.translate("&eSuccessfully &6left &eand &6disbanded &eteam!"));
      Bukkit.broadcastMessage(CC.translate("&eTeam " + ChatColor.BLUE + team.getName() + " &ehas been &cdisbanded&e by " + sender.getDisplayName() + "&e."));
    } else {
      Main.getInstance().getTeamHandler().setTeam(sender, null);
      team.flagForSave();
      team.sendMessage(sender.getDisplayName() + ChatColor.YELLOW + " has left the team.");
      sender.sendMessage(CC.translate("&eSuccessfully &6left&e the team!"));
    }
    CorePlugin.getInstance().getNametagEngine().reloadPlayer(sender);
    CorePlugin.getInstance().getNametagEngine().reloadOthersFor(sender);
    WaypointManager.updatePlayerFactionChange(sender);
    team.getActiveEffects().keySet().forEach(sender::removePotionEffect);
  }
}