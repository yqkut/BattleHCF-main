package cc.stormworth.hcf.commands.archerupgrades;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.archerupgrade.ArcherUpgradeMenu;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.entity.Player;

public class ArcherUpgrades {

  @Command(names = {"archerupgrades"}, permission = "")
  public static void test(Player player) {

    if (!Main.getInstance().getMapHandler().isKitMap()) {
      player.sendMessage(CC.RED + "You can only use this command in a kits.");
      return;
    }

    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
      player.sendMessage(CC.translate("&cYou can only use this command in the Safe Zone."));
      return;
    }

    new ArcherUpgradeMenu().openMenu(player);
  }
}