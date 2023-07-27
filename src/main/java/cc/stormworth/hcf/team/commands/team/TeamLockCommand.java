package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamLockCommand {

  @Command(names = {"team lock", "t lock", "f lock", "faction lock",
      "fac lock"}, permission = "BATTLE")
  public static void teamKick(Player sender) {
    if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
      sender.sendMessage(ChatColor.RED + "This command is available just during sotw timer.");
      return;
    }
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
    team.setClaimsLocked(!team.isClaimsLocked());
    sender.sendMessage(CC.translate(
        "&eYou have " + (team.isClaimsLocked() ? "&cLocked" : "&aUnlocked") + " &eyour claims."));
  }
}