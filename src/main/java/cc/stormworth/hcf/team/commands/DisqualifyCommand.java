package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisqualifyCommand {

  @Command(names = {"descalificateteam"}, permission = "op")
  public static void Disqualify(final CommandSender sender,
      @Param(name = "team") final Team team) {
    team.setDisqualified(!team.isDisqualified());
    for (Player online : team.getOnlineMembers()) {
      CorePlugin.getInstance().getNametagEngine().reloadPlayer(online);
    }
    sender.sendMessage(
        CC.YELLOW + team.getName() + " has been " + (team.isDisqualified() ? CC.RED
            + "Disqualified"
            : CC.GREEN + "Qualified") + CC.YELLOW + " from the events.");
  }

  @Command(names = {"descalificateteamevent"}, permission = "op")
  public static void DisqualifyFrom(final CommandSender sender,
      @Param(name = "team") final Team team) {

    Event activeEvent = Main.getInstance().getEventHandler().getActiveEvent();

    if (activeEvent == null && Main.getInstance().getConquestHandler().getGame() == null) {
      sender.sendMessage(CC.translate("&cThere is no event currently running."));
      return;
    }

    if (activeEvent instanceof KOTH) {
      KOTH koth = (KOTH) activeEvent;

      if (team.getEventDisqualified().contains(koth.getName())) {
        team.getEventDisqualified().remove(koth.getName());
        sender.sendMessage(CC.translate("&6" + team.getName() + " &ewill now be able to " + CC.GREEN
            + "participate" + CC.YELLOW + " in the event."));
      } else {
        team.getEventDisqualified().add(koth.getName());

        sender.sendMessage(CC.translate("&6" + team.getName() + " &ewill now be able to " + CC.RED
            + "participate" + CC.YELLOW + " in the event."));
      }

      if (team.getEventDisqualified().contains(koth.getName())) {

        String displayName;
        switch (activeEvent.getName()) {
          case "EOTW": {
            displayName = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
            break;
          }
          case "Citadel": {
            displayName = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
            break;
          }
          case "Hell":
          case "Nether": {
            displayName = ChatColor.RED.toString() + ChatColor.BOLD + "Hell";
            break;
          }
          case "Conquest-Mid": {
            displayName = ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Conquest Mid";
            break;
          }
          case "Palace": {
            displayName = ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Palace";
            break;
          }
          case "End": {
            displayName = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "End";
            break;
          }
          default: {
            displayName = ChatColor.GOLD.toString() + ChatColor.BOLD + activeEvent.getName();
            break;
          }
        }

        Bukkit.broadcastMessage(
            CC.translate(
                "&6&l" + team.getName() + " &ehas been &cremoved &efrom the " + displayName
                    + "&e event."));
      }
    } else {
      String displayName = "Conquest";

      if (team.getEventDisqualified().contains(displayName)) {
        team.getEventDisqualified().remove(displayName);
      } else {
        team.getEventDisqualified().add(displayName);
      }

      if (team.getEventDisqualified().contains(displayName)) {
        Bukkit.broadcastMessage(CC.translate(
            "&6&l" + team.getName() + " &ehas been &cremoved &efrom the &6&lConquest&e."));
      }
    }
  }

  @Command(names = {"bypassevents"}, permission = "op")
  public static void bypasseventsteam(final CommandSender sender,
      @Param(name = "team") final Team team) {
    team.setBypassEvent(!team.isBypassEvent());

    sender.sendMessage(CC.translate(
        "&6" + team.getName() + " &ewill now be able to " + (team.isBypassEvent() ? CC.GREEN
            + "bypass"
            : CC.RED + "not bypass") + " &eevents."));
  }
}