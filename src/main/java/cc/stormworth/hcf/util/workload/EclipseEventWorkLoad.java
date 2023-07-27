package cc.stormworth.hcf.util.workload;

import cc.stormworth.hcf.Main;

import java.util.LinkedList;

public class EclipseEventWorkLoad extends ScheduleWorkLoad{

    private static final double MAX_MILLIS_PER_TICK = 2.5;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    private final LinkedList<Workload> workloadQueue;
    private boolean paused = false;

    public EclipseEventWorkLoad(LinkedList<Workload> workloadQueue) {
        this.workloadQueue = workloadQueue;
    }

    public void addWorkload(Workload workload) {
        this.workloadQueue.add(workload);
    }

    @Override
    public void run() {

        if (paused) {
            return;
        }

        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

        Workload workload;

        while (System.nanoTime() < stopTime && (workload = workloadQueue.poll()) != null) {
            workload.compute();
        }
    }

    @Override
    public void compute() {
        runTaskTimer(Main.getInstance(), 0, 1L);
    }

    @Override
    public boolean isFinished() {

        if (workloadQueue.isEmpty()) {
            cancel();
            return true;
        }

        return false;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }
}
