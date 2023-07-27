package cc.stormworth.hcf.commands.reclaimitems;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.entity.Player;

public class ReclaimItemsCommand {

  @Command(names = {"reclaimitems", "keys"}, permission = "")
  public static void reclaimItems(Player player) {
    HCFProfile profile = HCFProfile.get(player);

    if (profile.getNoReclaimedItems().isEmpty()) {
      player.sendMessage(CC.translate("&cYou have no items to reclaim."));
      return;
    }

    profile.claimItems(player);
  }

}