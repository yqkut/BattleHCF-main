package cc.stormworth.hcf.commands.lff;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.lff.LFFMenu;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.entity.Player;

public class LffCommand {

  @Command(names = {"lff"}, permission = "DEFAULT")
  public static void lff(Player player) {

    if (CooldownAPI.hasCooldown(player, "LFF")) {
      player.sendMessage(CC.translate("&cYou must wait &e" +
          TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "LFF"))
          + " &cto use this command again."));

      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team != null) {
      player.sendMessage(CC.translate("&cYou must not be in a team to use this command."));
      return;
    }

    new LFFMenu().openMenu(player);
  }

}