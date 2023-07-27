package cc.stormworth.hcf.team.event;

import cc.stormworth.hcf.team.Team;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class TeamRaidEvent extends Event {

  private final Team team;

  @Getter
  private static HandlerList handlerList = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }
}