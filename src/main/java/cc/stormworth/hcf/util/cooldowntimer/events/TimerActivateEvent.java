package cc.stormworth.hcf.util.cooldowntimer.events;

import cc.stormworth.hcf.util.cooldowntimer.Timer;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class TimerActivateEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private final UUID uuid;
  private final Timer timer;
  private final int delay;
  @Setter
  private boolean cancelled;

  public TimerActivateEvent(UUID uuid, Timer timer, int delay) {
    this.uuid = uuid;
    this.timer = timer;
    this.delay = delay;

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