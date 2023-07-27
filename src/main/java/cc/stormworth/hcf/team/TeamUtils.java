package cc.stormworth.hcf.team;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class TeamUtils {

  public String getEventName(String eventName) {
    switch (eventName) {
      case "EOTW":
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
      case "Citadel":
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
      case "hell":
      case "Hell":
      case "Nether":
        return ChatColor.RED.toString() + ChatColor.BOLD + "Hell";
      case "Conquest-Mid":
        return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Conquest Mid";
      case "Palace":
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Palace";
      case "End":
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "End";
      case "Mad":
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Mad";
      default:
        return ChatColor.BLUE.toString() + ChatColor.BOLD + eventName;
    }
  }

}