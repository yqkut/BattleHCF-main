package cc.stormworth.hcf.team.event;

import cc.stormworth.hcf.team.Team;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerRaidTeamEvent extends PlayerEvent {

  @Getter
  private static HandlerList handlerList = new HandlerList();

  private final Team raidTeam;

  public PlayerRaidTeamEvent(Player who, Team raidTeam) {
    super(who);
    this.raidTeam = raidTeam;
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }
}