package cc.stormworth.hcf.util.timer;

import cc.stormworth.core.util.time.TimeUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timer {

  private long endAt;
  private long currentTime;
  private boolean pause;
  private long pauseTime;
  private String timeDefault;
  private boolean announce;
  private Runnable runnable;

  public Timer(String timeDefault) {
    endAt = System.currentTimeMillis() + TimeUtil.parseTime(timeDefault);
    pause = false;
    this.timeDefault = timeDefault;
    //new TimerRunnable(this).runTaskTimer(Main.getInstance(), 0, 20);
  }

  public void add(long time) {
    endAt += time;
    currentTime += time;
  }

  public void decrease(long time) {
    endAt -= time;
    currentTime -= time;
  }

  public void set(long time) {
    endAt = time;
    currentTime = time;
  }

  public long getTime() {

    if (System.currentTimeMillis() == currentTime) {
      return 0;
    }

    if (System.currentTimeMillis() > endAt) {
      return 0;
    }

    if (pause) {
      return currentTime;
    }

    return currentTime = endAt - System.currentTimeMillis();
  }

  public boolean isEnded() {
    return getTime() == 0;
  }

/*  public String getReadableTime() {
    return TimeUtil.millisToTimer(getTime());
  }*/

  public void pause() {
    pause = true;
    pauseTime = System.currentTimeMillis();
  }

  public void resume() {
    long pauseEnd = System.currentTimeMillis() - pauseTime;

    endAt += pauseEnd;
    currentTime += pauseEnd;
    pause = false;
  }

  public void reset() {
    endAt = System.currentTimeMillis() + TimeUtil.parseTime(timeDefault);
  }

  public void start() {

  }

  public void onEnd(Runnable runnable) {
    this.runnable = runnable;
  }

  public void end() {

    if (endAt < System.currentTimeMillis()) {
      return;
    }

    if (runnable != null) {
      runnable.run();
    }
  }


}