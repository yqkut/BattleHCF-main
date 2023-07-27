package cc.stormworth.hcf.events.conquest.commands.conquestadmin;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminStopCommand {

  @Command(names = {"conquestadmin stop"}, permission = "op")
  public static void conquestAdminStop(CommandSender sender) {
    ConquestGame game = Main.getInstance().getConquestHandler().getGame();

    if (game == null) {
      sender.sendMessage(ChatColor.RED + "Conquest is not active.");
      return;
    }

    Main.getInstance().getEventHandler().setScheduleEnabled(true);
    game.endGame(null, null);
  }
}