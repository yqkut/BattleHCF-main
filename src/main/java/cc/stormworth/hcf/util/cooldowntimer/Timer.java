package cc.stormworth.hcf.util.cooldowntimer;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Timer {

  protected final String name;

  protected final ScheduledExecutorService executor;
  protected final int delay;

  @Setter
  protected TimeUtil.FormatType format;
  @Setter
  protected String expiryMessage;
  @Setter
  protected CooldownType lunarCooldownType;

  protected Timer(ScheduledExecutorService executor, String name, int delay, boolean persistable) {
    this.name = name;

    this.executor = executor;
    this.delay = delay;
  }

  public abstract void disable();
}