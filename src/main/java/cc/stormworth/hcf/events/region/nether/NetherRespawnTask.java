package cc.stormworth.hcf.events.region.nether;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class NetherRespawnTask implements Runnable {

    static NetherHandler handler;
    @Getter
    private static long nextRespawn;
    private final BukkitTask task;

    public NetherRespawnTask(NetherHandler handler) {
        NetherRespawnTask.handler = handler;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), this, 0L, 20L);
    }

    public static void makeReset() {
        nextRespawn = System.currentTimeMillis() + (600 * 1000L);
        TaskUtil.run(Main.getInstance(), () -> handler.getArea().reset());
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
            String message = CC.translate("&c[Nether] &awill be reset in &l" + this.getNextRespawnString() + "&a.");
            Bukkit.getConsoleSender().sendMessage(message);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getName().equalsIgnoreCase("void") && !HCFProfile.get(player).isDeathBanned()).forEach(player -> player.sendMessage(message));
        }
    }
}