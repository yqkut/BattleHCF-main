package cc.stormworth.hcf.battleplayers.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.date.DateUtils;
import cc.stormworth.hcf.util.number.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class BattlePlayersCommand {

  @Command(names = {"battleplayers start"}, permission = "DEVELOPER")
  public static void start(CommandSender sender) {
    Main.getInstance().getBattlePlayers().startMap();

    sender.sendMessage(CC.translate("&aBattlePlayers count started!"));
  }

  @Command(names = {"battleplayers atm"}, permission = "DEVELOPER")
  public static void atm(CommandSender sender) {
    sender.sendMessage(CC.translate("&6&lPlayers Joined: &7" + NumberUtils.addComma(
        Main.getInstance().getBattlePlayers().getMap().getTotalPlayers().size())));
  }

  @Command(names = {"battleplayers stop"}, permission = "DEVELOPER")
  public static void stop(CommandSender sender) {
    Main.getInstance().getBattlePlayers().stopMap();

    sender.sendMessage(CC.translate("&aBattlePlayers count stopped!"));
  }

  @Command(names = {"battleplayers results"}, permission = "DEVELOPER")
  public static void results(CommandSender sender) {
    sender.sendMessage(CC.translate("&6&lTotal Players:"));
    sender.sendMessage("");
    Main.getInstance().getBattlePlayers().getMaps().forEach((number, map) -> {

      if (map.getEndedAt() == 0) {
        return;
      }

      String startAt = DateUtils.formatDate(map.getStartedAt(), 'e', '7');
      String endedAt = DateUtils.formatDate(map.getEndedAt(), 'e', '7');

      String size = NumberUtils.addComma(map.getTotalPlayers().size());

      sender.sendMessage(
          CC.translate("&6&l| " + startAt + " &f- " + endedAt + " &6&l(" + size + ")"));
    });
  }

}