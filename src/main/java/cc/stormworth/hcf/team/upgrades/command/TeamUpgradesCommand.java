package cc.stormworth.hcf.team.upgrades.command;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.upgrades.menu.TeamUpgradesMenu;
import org.bukkit.entity.Player;

public class TeamUpgradesCommand {

  @Command(names = {"f upgrades", "t upgrades", "faction upgrades",
      "team upgrades", "f upgrade", "t upgrade", "faction upgrade",
      "team upgrade"}, permission = "")
  public static void upgrade(Player player) {

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team == null) {
      player.sendMessage(CC.translate("&cTo use this command you must be in a faction."));
      player.updateInventory();
      return;
    }

    new TeamUpgradesMenu(team).openMenu(player);
  }

  @Command(names = "f resetupgrades", permission = "op")
  public static void resetupgrades(Player player) {

  }

  @Command(names = {"f resetupgradecooldown", "t resetupgradecooldown",
      "resetupgradecooldown upgrades",
      "team resetupgradecooldown", "f resetupgradecooldown", "t resetupgradecooldown",
      "faction resetupgradecooldown",
      "team resetupgradecooldown"}, permission = "")
  public static void resetupgradecooldown(Player player) {

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team == null) {
      player.sendMessage(CC.translate("&cTo use this command you must be in a faction."));
      player.updateInventory();
      return;
    }

    team.setDtrRegenFasterEndAt(0);

    player.sendMessage(
        CC.translate("&aYou have reset the upgrade cooldown for &e" + team.getName() + "&a."));
  }
}
