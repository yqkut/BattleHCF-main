package cc.stormworth.hcf.bounty.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.bounty.menu.BountiesMenu;
import cc.stormworth.hcf.bounty.menu.BountyMenu;
import org.bukkit.entity.Player;

public class BountyCommand {

  @Command(names = {"bounty"}, permission = "")
  public static void bounty(Player player, @Param(name = "player") Player target) {

    if (!Main.getInstance().getMapHandler().isKitMap()) {
      player.sendMessage(CC.RED + "You can only use this command in a kits.");
      return;
    }

    if (target == player) {
      player.sendMessage(CC.translate("&cYou cannot bounty yourself."));
      return;
    }

    if (BountyPlayer.get(target) != null) {
      player.sendMessage(CC.translate("&cThat player is already on a bounty."));
      return;
    }

    BountyPlayer bountyPlayer = new BountyPlayer(target.getUniqueId(), player.getName(),
        player.getUniqueId());

    BountyPlayer.getBounties().put(target.getUniqueId(), bountyPlayer);

    new BountyMenu(bountyPlayer).openMenu(player);
  }

  @Command(names = {"bounties"}, permission = "")
  public static void bounties(Player player) {
    new BountiesMenu().openMenu(player);
  }

}