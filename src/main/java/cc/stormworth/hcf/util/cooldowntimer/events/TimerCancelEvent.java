package cc.stormworth.hcf.util.cooldowntimer.events;

import cc.stormworth.hcf.util.cooldowntimer.Timer;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class TimerCancelEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final UUID uuid;
  private final Timer timer;

  public TimerCancelEvent(UUID uuid, Timer timer) {
    this.uuid = uuid;
    this.timer = timer;

    Bukkit.getPluginManager().callEvent(this);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
} 