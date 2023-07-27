package cc.stormworth.hcf.gemsshop;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.entity.Player;

public class GemsShopCommand {

  @Command(names = {"merchant"}, permission = "")
  public static void gemsshop(Player player) {

    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
      player.sendMessage(CC.translate("&cYou can only use this command in the Safe Zone."));
      return;
    }

    new GemsShopMenu().openMenu(player);
  }

}