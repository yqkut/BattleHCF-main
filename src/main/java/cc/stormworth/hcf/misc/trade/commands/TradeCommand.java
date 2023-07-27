package cc.stormworth.hcf.misc.trade.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.misc.request.Request;
import org.bukkit.entity.Player;

public class TradeCommand {

  @Command(names = {"trade"}, permission = "DEFAULT")
  public static void trade(Player player, @Param(name = "target") Player target) {

    player.sendMessage(CC.translate("&cThis command is currently disabled."));

    /*

    if (player == target) {
      player.sendMessage(CC.translate("&cYou cannot trade with yourself."));
      return;
    }

    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
      player.sendMessage(CC.translate("&cYou can only use this command in the Safe Zone."));
      return;
    }

    if (player.getLocation().distance(target.getLocation()) > 6) {
      player.sendMessage(
          CC.translate("&cYou are too far away from " + target.getName() + " to trade."));
      return;
    }

    Request request = new Request(
        new Clickable("&6" + player.getName() + " &ehas sent a trade request, &6&l[Click here] &eto accept.",
            "&bClick to accept",
            "/trade accept"),
        player.getUniqueId(), target.getUniqueId(),
        System.currentTimeMillis() + TimeUtil.parseTimeLong("20s"));

    request.addAction((players) -> {

      Player player1 = players.get(0);
      Player player2 = players.get(1);

      new TradeMenu(player2, player1).openMenu(player1);
    });

    request.send();

    player.sendMessage(
        CC.translate("&aSuccessfully sent a trade request to &6&l" + target.getName() + "&a."));*/
  }

  @Command(names = {"trade accept"}, permission = "DEFAULT")
  public static void tradeAccept(Player player) {
    if (Request.hasRequest(player)) {
      Request request = Request.getRequest(player);

      request.execute();
    } else {
      player.sendMessage(CC.translate("&cYou do not have a pending trade request."));
    }
  }

}