package cc.stormworth.hcf.util.workload;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.eclipse.EclipseEvent;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorKLoadQueue extends Thread {

    @Getter
    @Setter
    private boolean paused = false;

    @Getter private final LinkedList<ScheduleWorkLoad> queueWorkLoads = new LinkedList<>();

    public WorKLoadQueue(){
        this.start();
        setName("Workload-Queue-Thread");
    }

    public int addWorkload(ScheduleWorkLoad workload) {
        this.queueWorkLoads.add(workload);

        return this.queueWorkLoads.size();
    }

    @Getter
    private final List<ScheduleWorkLoad> currentWorkloads = new CopyOnWriteArrayList<>();

    private int itemsCurrentlyProcessing = 0;

    @Override
    public void run() {

        while (true) {
            try {

                tick();

                sleep(20L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void tick(){
        if (paused) {
            Bukkit.getLogger().info("Workload queue is paused.");
            return;
        }

        EclipseEvent eclipseEvent = Main.getInstance().getEventHandler().getEclipseEvent();

        if (eclipseEvent.isActive()) {
            if (queueWorkLoads.contains(eclipseEvent.getScheduleWorkLoad()) && !currentWorkloads.contains(eclipseEvent.getScheduleWorkLoad())) {
                ScheduleWorkLoad workLoad = eclipseEvent.getScheduleWorkLoad();

                if (queueWorkLoads.remove(workLoad)) {
                    currentWorkloads.add(workLoad);
                    workLoad.compute();
                    itemsCurrentlyProcessing++;
                }

                return;
            }
        }

        if (itemsCurrentlyProcessing < 4) {
            if (queueWorkLoads.size() > 0) {
                /*if (getInQueueByType(TeamWorkdLoadType.FALL_TRAP) > 0 && getCurrentByType(TeamWorkdLoadType.FALL_TRAP) < 2) {
                    ScheduleWorkLoad workload = getWorkLoadByType(TeamWorkdLoadType.FALL_TRAP);
                    if (queueWorkLoads.remove(workload)) {
                        currentWorkloads.add(workload);
                        workload.compute();
                        itemsCurrentlyProcessing++;
                    }
                } else {
                    ScheduleWorkLoad workload = queueWorkLoads.poll();
                    if (workload != null) {
                        currentWorkloads.add(workload);
                        workload.compute();
                        itemsCurrentlyProcessing++;
                    }
                }*/
                if (getCurrentByType(TeamWorkdLoadType.BASE) < 2 && getInQueueByType(TeamWorkdLoadType.BASE) > 0) {
                    ScheduleWorkLoad workload = getWorkLoadByType(TeamWorkdLoadType.BASE);
                    if (workload != null) {
                        queueWorkLoads.remove(workload);
                        currentWorkloads.add(workload);

                        workload.compute();
                        itemsCurrentlyProcessing++;
                    }
                }
                else if (getCurrentByType(TeamWorkdLoadType.FALL_TRAP) < 2 && getInQueueByType(TeamWorkdLoadType.FALL_TRAP) > 0) {
                    ScheduleWorkLoad workload = getWorkLoadByType(TeamWorkdLoadType.FALL_TRAP);
                    if (workload != null) {
                        queueWorkLoads.remove(workload);
                        currentWorkloads.add(workload);

                        workload.compute();
                        itemsCurrentlyProcessing++;
                    }
                } else {
                    ScheduleWorkLoad workload = getWorkLoadByType(TeamWorkdLoadType.FALL_TRAP);
                    if (workload == null) workload = getWorkLoadByType(TeamWorkdLoadType.BASE);
                    if (workload != null) {
                        queueWorkLoads.remove(workload);
                        currentWorkloads.add(workload);

                        workload.compute();
                        itemsCurrentlyProcessing++;
                    }
                }
            }
        }

        if (currentWorkloads.size() > 0) {
            for (ScheduleWorkLoad workload : currentWorkloads) {
                if (workload.isFinished()) {
                    currentWorkloads.remove(workload);
                    itemsCurrentlyProcessing--;
                }
            }
        }
    }

    public int getInQueueByType(TeamWorkdLoadType type) {
        int count = 0;
        for (ScheduleWorkLoad workload : queueWorkLoads) {
            if (workload instanceof TeamWorkload){
                TeamWorkload runnable = (TeamWorkload) workload;
                if (runnable.getType() == type){
                    count++;
                }
            }
        }
        return count;
    }

    public int getCurrentByType(TeamWorkdLoadType type) {
        int count = 0;
        for (ScheduleWorkLoad workload : currentWorkloads) {
            if (workload instanceof TeamWorkload){
                TeamWorkload runnable = (TeamWorkload) workload;
                if (runnable.getType() == type){
                    count++;
                }
            }
        }
        return count;
    }

    public ScheduleWorkLoad getWorkLoadByType(TeamWorkdLoadType type) {
        for (ScheduleWorkLoad workload : queueWorkLoads) {
            if (workload instanceof TeamWorkload){
                TeamWorkload runnable = (TeamWorkload) workload;
                if (runnable.getType() == type){
                    return runnable;
                }
            }
        }
        return null;
    }

    public void shutdown() {
        stop();
        currentWorkloads.clear();
        queueWorkLoads.forEach(ScheduleWorkLoad::compute);
    }

    public int getQueuePosition(ScheduleWorkLoad scheduleWorkLoad){
        return queueWorkLoads.indexOf(scheduleWorkLoad);
    }

    public boolean removeFromQueue(ScheduleWorkLoad scheduleWorkLoad){
        return queueWorkLoads.remove(scheduleWorkLoad);
    }
}
