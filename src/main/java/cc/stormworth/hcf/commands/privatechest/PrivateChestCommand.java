package cc.stormworth.hcf.commands.privatechest;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.entity.Player;

public class PrivateChestCommand {

  @Command(names = {"privatechest", "pv", "pchest"}, permission = "")
  public static void privateChest(Player player,
      @Param(name = "number", defaultValue = "1") String numberString) {
    HCFProfile profile = HCFProfile.get(player);

    /*if (!Main.getInstance().getMapHandler().isKitMap()) {
      player.sendMessage(CC.RED + "You can only use this command in a kits.");
      return;
    }*/

    Team team = LandBoard.getInstance().getTeam(player.getLocation());


    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) && (team == null || !team.isMember(player.getUniqueId()))) {
      player.sendMessage(CC.translate("&cYou can only use this command in your claim or in &aSpawn&e."));
      return;
    }


    if(SpawnTagHandler.isTagged(player)) {
    	player.sendMessage(CC.translate("&cYou can't use this command while you are tagged."));
    	return;
    }

    if (profile.getPrivateChests().isEmpty()) {
      player.sendMessage(CC.translate("&cYou don't have any private chests."));
      return;
    }

    int number;

    try {
      number = Integer.parseInt(numberString);

      if (profile.getPrivateChests().size() < (number)) {
        player.sendMessage(CC.translate("&cYou do not have that many private chests."));
        return;
      }

      player.openInventory(profile.getPrivateChests().get(number - 1).getInventory());

    } catch (NumberFormatException e) {
      player.sendMessage(CC.translate("&cInvalid number."));
    }
  }

}