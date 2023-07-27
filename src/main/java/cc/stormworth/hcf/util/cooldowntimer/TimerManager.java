package cc.stormworth.hcf.util.cooldowntimer;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.util.cooldowntimer.impl.CooldownTimer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Getter;

@Getter
public class TimerManager {

  @Getter
  private static TimerManager instance;

  private final ScheduledThreadPoolExecutor executor;
  private final CooldownTimer cooldownTimer;

  public TimerManager() {
    instance = this;

    this.executor = new ScheduledThreadPoolExecutor(1,
        TaskUtil.newThreadFactory("Timer Thread - %d"));
    this.executor.setRemoveOnCancelPolicy(true);

    this.cooldownTimer = new CooldownTimer(this.executor);
  }

  public void disable() {
    this.cooldownTimer.disable();
    this.executor.shutdownNow();
  }
} 