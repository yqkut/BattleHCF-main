package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamStartRegenCommand {

  @Command(names = {"setdtrregen"}, permission = "SENIORMOD")
  public static void setDtrRegen(final CommandSender sender,
      @Param(name = "team") final Team team) {
    team.setDTRCooldown(System.currentTimeMillis() + (
        team.isRaidable() ? Main.getInstance().getMapHandler().getRegenTimeRaidable()
            : Main.getInstance().getMapHandler().getRegenTimeDeath()));

    DTRHandler.markOnDTRCooldown(team);
    sender.sendMessage(
        ChatColor.YELLOW + "Put " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW
            + " on dtr freeze.");
  }
}