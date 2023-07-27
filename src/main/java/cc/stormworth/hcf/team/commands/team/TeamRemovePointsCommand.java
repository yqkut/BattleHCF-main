package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRemovePointsCommand {

  @Command(names = {"team removepoints", "t removepoints", "f removepoints",
      "faction removepoints", "fac removepoints"}, permission = "op")
  public static void teamAddPoints(final Player sender, @Param(name = "team") final Team team,
      @Param(name = "amount") final String amount) {
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
      return;
    }

    if (amount.endsWith("%")) {
      final int percent = Integer.parseInt(amount.substring(0, amount.length() - 1));
      final int points = team.getPoints();

      final int newPoints = (int) (points - (points * (percent / 100.0)));

      team.removePoints(newPoints);

      sender.sendMessage(CC.translate(
          "&eYou have successfully &cremoved &6" + amount + "&e points from &a" + team.getName()
              + "&e."));
    } else {
      try {
        final int points = Integer.parseInt(amount);

        final int newPoints = team.getPoints() - points;

        team.removePoints(newPoints);
        sender.sendMessage(CC.translate(
            "&eYou have successfully &cremoved &6" + amount + "&e points from &a" + team.getName()
                + "&e."));
      } catch (NumberFormatException e) {
        sender.sendMessage(ChatColor.GRAY + "That is not a valid amount!");
      }
    }
  }

  @Command(names = {"team resetpoints", "t resetpoints", "f resetpoints", "faction resetpoints",
      "fac resetpoints"}, permission = "op")
  public static void reset(final Player sender, @Param(name = "team") final Team team) {
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
      return;
    }

    team.removePoints(team.getPoints());
    sender.sendMessage(
        CC.translate("&eYou have successfully &creset &6" + team.getName() + "&e's points."));
  }

  @Command(names = {"team setremovePoints", "t setremovePoints", "f setremovePoints", "faction setremovePoints",
      "fac setremovePoints"}, permission = "op")
  public static void setremovePoints(final Player sender, @Param(name = "team") final Team team, @Param(name = "amount") final int amount) {
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
      return;
    }

    team.setRemovedPoints(amount);
    sender.sendMessage(
        CC.translate("&eYou have successfully &creset &6" + team.getName() + "&e's points."));
  }

  /*@Command(names = {"team setpoints", "t setpoints", "f setpoints", "faction setpoints",
      "fac setpoints"}, permission = "op")
  public static void setpoints(final Player sender, @Param(name = "team") final Team team,
      @Param(name = "amount") final int amount) {
    if (team == null) {
      sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
      return;
    }

    team.setPoints(amount);
    sender.sendMessage(CC.translate(
        "&eYou have successfully &cset &6" + team.getName() + "&e's points to &6" + amount
            + "&e."));
  }*/
}