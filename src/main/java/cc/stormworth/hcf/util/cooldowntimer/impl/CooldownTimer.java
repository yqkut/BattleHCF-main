package cc.stormworth.hcf.util.cooldowntimer.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CooldownTimer extends PlayerTimer {
 
  private final Table<UUID, String, ScheduledFuture<?>> cooldowns;

  public CooldownTimer(ScheduledExecutorService executor) {
    super(executor, "CooldownTimer", 0);

    this.cooldowns = HashBasedTable.create();
    this.setFormat(TimeUtil.FormatType.MILLIS_TO_SECONDS);
  }

  @Override
  public void disable() {
    this.cooldowns.values().forEach(future -> future.cancel(true));
    this.cooldowns.clear();
  }

  public void activate(Player player, String cooldown, int delay, String message) {
    this.activate(player.getUniqueId(), cooldown, delay, message);
  }

  private void activate(UUID uuid, String cooldown, int delay, String message) {
    if (delay <= 0 || this.isActive(uuid, cooldown)) {
      return;
    }
    this.cooldowns.put(uuid, cooldown, this.scheduleExpiry(uuid, cooldown, delay, message));
  }

  public void activate(Player player, String cooldown, int delay, String message,
      Callable callable) {
    this.activate(player.getUniqueId(), cooldown, delay, message, callable);
  }

  private void activate(UUID uuid, String cooldown, int delay, String message, Callable callable) {
    if (delay <= 0 || this.isActive(uuid, cooldown)) {
      return;
    }
    this.cooldowns.put(uuid, cooldown,
        this.scheduleExpiry(uuid, cooldown, delay, message, callable));
  }

  public void cancel(Player player, String cooldown) {
    this.cancel(player.getUniqueId(), cooldown);
  }

  public void forcecancel(UUID uuid, String cooldown) {
    if (cooldowns.contains(uuid, cooldown)) {
      this.cooldowns.remove(uuid, cooldown).cancel(true);
    }
  }

  public void cancel(UUID uuid, String cooldown) {
    if (!this.isActive(uuid)) {
      return;
    }
    this.cooldowns.remove(uuid, cooldown).cancel(true);
  }

  public boolean isActive(Player player, String cooldown) {
    return this.isActive(player.getUniqueId(), cooldown);
  }

  private boolean isActive(UUID uuid, String cooldown) {
    return this.cooldowns.contains(uuid, cooldown);
  }

  public long getCooldown(Player player, String cooldown) {
    return this.getCooldown(player.getUniqueId(), cooldown);
  }

  private long getCooldown(UUID uuid, String cooldown) {
    return this.cooldowns.get(uuid, cooldown).getDelay(TimeUnit.MILLISECONDS);
  }

  public String getTimeLeft(Player player, String cooldown) {
    return TimeUtil.formatTime(this.getCooldown(player, cooldown), this.format);
  }

  public String getDynamicTimeLeft(Player player, String cooldown) {
    long remaining = this.getCooldown(player, cooldown);

    if (remaining < 3_600_000L) {
      return TimeUtil.formatTime(remaining, TimeUtil.FormatType.MILLIS_TO_MINUTES);
    } else {
      return TimeUtil.formatTime(remaining, TimeUtil.FormatType.MILLIS_TO_HOURS);
    }
  }

  private ScheduledFuture<?> scheduleExpiry(UUID uuid, String cooldown, int delay, String message) {
    return this.executor.schedule(() -> {
      try {
        this.cooldowns.remove(uuid, cooldown);

        if (message == null) {
          return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
          player.sendMessage(CC.translate(message));
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }, delay, TimeUnit.SECONDS);
  }

  private ScheduledFuture<?> scheduleExpiry(UUID uuid, String cooldown, int delay, String message,
      Callable callable) {
    return this.executor.schedule(() -> {
      try {
        this.cooldowns.remove(uuid, cooldown);
        callable.call();

        if (message == null) {
          return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
          player.sendMessage(CC.translate(message));
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }, delay, TimeUnit.SECONDS);
  }
}
