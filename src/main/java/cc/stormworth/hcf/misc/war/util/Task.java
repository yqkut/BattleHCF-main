package cc.stormworth.hcf.misc.war.util;

import cc.stormworth.hcf.Main;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;

public class Task {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(Callable callable) {
        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Main.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Main.getInstance(), callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }
}

