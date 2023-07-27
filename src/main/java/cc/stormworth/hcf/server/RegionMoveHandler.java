package cc.stormworth.hcf.server;

import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;

public interface RegionMoveHandler {

  RegionMoveHandler ALWAYS_TRUE = event -> (true);

  RegionMoveHandler PVP_TIMER = event -> {
    Team team = LandBoard.getInstance().getTeam(event.getTo());
    if (team.getOwner() != null && event.getPlayer().getGameMode() != GameMode.CREATIVE
        && CustomTimerCreateCommand.areClaimsLocked() && team.isClaimsLocked()) {
      if (team.isMember(event.getPlayer().getUniqueId())) {
        return (true);
      }
      event.getPlayer()
          .sendMessage(ChatColor.RED + "This claim is locked, you can't join during sotw.");
      event.setTo(event.getFrom());
      return (false);
    }
    if (HCFProfile.get(event.getPlayer()).hasPvPTimer() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.getPlayer()
          .sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
      event.getPlayer().sendMessage(
          ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED
              + "' to remove your timer.");
      event.setTo(event.getFrom());
      return (false);
    }

    return (true);
  };

  boolean handleMove(PlayerMoveEvent event);
}