package cc.stormworth.hcf.commands.comunitychest;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ComunityChestCommand {

  @Command(names = {"communitychest", "cc"}, permission = "DEFAULT")
  public static void onCommunityChestCommand(Player player) {

    Team team = LandBoard.getInstance().getTeam(player.getLocation());

    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) && (team == null || !team.isMember(player.getUniqueId()))) {
      player.sendMessage(CC.translate("&cYou can only use this command in your claim or in &aSpawn&e."));
      return;
    }

    if(SpawnTagHandler.isTagged(player)) {
      player.sendMessage(CC.translate("&cYou can't use this command while you are tagged."));
      return;
    }

    player.openInventory(Main.getInstance().getComunityChest().getInventory());
  }
}