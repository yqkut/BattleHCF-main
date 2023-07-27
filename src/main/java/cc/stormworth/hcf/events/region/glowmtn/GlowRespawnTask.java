package cc.stormworth.hcf.events.region.glowmtn;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class GlowRespawnTask implements Runnable {

    static GlowHandler handler;
    @Getter
    private static long nextRespawn;
    private final BukkitTask task;

    public GlowRespawnTask(GlowHandler handler) {
        GlowRespawnTask.handler = handler;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), this, 0L, 20L);
    }

    public static void makeReset() {
        nextRespawn = System.currentTimeMillis() + (CustomTimerCreateCommand.isSOTWTimer() ? 600 * 1000L : 3600 * 1000L);
        TaskUtil.run(Main.getInstance(), () -> handler.getGlowMountain().reset());
    }

    private int getNextRespawnInSeconds() {
        return (int) ((nextRespawn - System.currentTimeMillis()) / 1000);
    }

    public String getNextRespawnString() {
        final String time = TimeUtils.formatIntoDetailedString(getNextRespawnInSeconds());
        return time;
    }

    @Override
    public void run() {
        if (Main.getInstance().getServerHandler().isEOTW()) task.cancel();
        int nextInSeconds = this.getNextRespawnInSeconds();

        if (nextInSeconds <= 0) {
            makeReset();
            return;
        }

        int messageInterval = 900;

        if (messageInterval != 0 && nextInSeconds % messageInterval == 0) {
            Bukkit.broadcastMessage(CC.translate("&6[Glowstone Mountain] &awill be reset in &l" + this.getNextRespawnString() + "&a."));
        }
    }
}