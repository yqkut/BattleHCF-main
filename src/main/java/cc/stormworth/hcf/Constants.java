package cc.stormworth.hcf;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class Constants {

  public static String teamChatFormat(final Player player, final String message) {
    return CC.DARK_AQUA + "(Team) " + player.getName() + ": " + CC.YELLOW + message;
  }

  public static String officerChatFormat(final Player player, final String message) {
    return CC.LIGHT_PURPLE + "(Officer) " + player.getName() + ": " + CC.YELLOW + message;
  }

  public static String teamChatSpyFormat(final Team team, final Player player,
      final String message) {
    return (CC.GOLD + "[" + CC.DARK_AQUA + "TC: " + CC.YELLOW + team.getName() + CC.GOLD + "]"
        + CC.DARK_AQUA + player.getName() + ": " + message);
  }

  public static String allyChatFormat(final Player player, final String message) {
    return Main.getInstance().getMapHandler().getAllyRelationColor() + "(Ally) " + player.getName()
        + ": " + CC.YELLOW + message;
  }

  public static String allyChatSpyFormat(final Team team, final Player player,
      final String message) {
    return (CC.GOLD + "[" + Main.getInstance().getMapHandler().getAllyRelationColor() + "AC: "
        + CC.YELLOW + team.getName() + CC.GOLD + "]" + ChatColor.RESET + Main.getInstance().getMapHandler()
        .getAllyRelationColor() + player.getName() + ": " + message);
  }

  public static String publicChatFormat(final Player player, final Team team,
      final String rankPrefix) {
    String starting = "";
    if (team != null) {
      starting = CC.GOLD + "[" + Main.getInstance().getMapHandler().getDefaultRelationColor()
          + team.getName() + CC.GOLD + "] " + ChatColor.RESET;
    }
    return starting + CC.translate(
        rankPrefix + Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getColor()
            + player.getName()) + CC.GRAY + ": " + CC.WHITE + "%s";
  }

  public static String publicChatFormat(final Player player, final Player recipient,
      final Team team, final String rankPrefix) {
    String starting = "";
    if (team != null) {
      starting = CC.GOLD + "[" + team.getName(recipient) + CC.GOLD + "] " + ChatColor.RESET;
    }
    return starting + CC.translate(
        rankPrefix + Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getColor()
            + player.getName()) + CC.GRAY + ": " + CC.WHITE + "%s";
  }
}