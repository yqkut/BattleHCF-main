package cc.stormworth.hcf.commands.dimesionalteleport;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.teleport.DimentionalTeleportMenu;
import org.bukkit.entity.Player;

public class DimentionalTeleport {

  @Command(names = {"dimentionalteleport"}, permission = "DEFAULT")
  public static void dimesionalTeleport(Player player) {
    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
      player.sendMessage(CC.translate("&cYou must be in the safe-zone to use this command."));
      return;
    }

    new DimentionalTeleportMenu().openMenu(player);
  }

}