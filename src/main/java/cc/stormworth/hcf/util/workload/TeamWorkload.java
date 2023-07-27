package cc.stormworth.hcf.util.workload;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This is the basic implementation of a WorkloadRunnable.
 * It processes as many Workloads every tick as the given
 * field MAX_MILLIS_PER_TICK allows.
 */
@Getter
public class TeamWorkload extends ScheduleWorkLoad {

    private static final double MAX_MILLIS_PER_TICK = 2.5;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);
    private final Team team;
    private final TeamWorkdLoadType type;
    private final Consumer<Team> onStart;
    private final Consumer<Team> onFinish;
    private final LinkedList<Workload> workloadDeque = Lists.newLinkedList();
    private final List<Workload> completeWorkloads = new ArrayList<>();
    private boolean paused = false;

    public TeamWorkload(Team team, TeamWorkdLoadType type, Location location) {
        this.team = team;
        this.type = type;

        onStart = (t) -> {
            if (type == TeamWorkdLoadType.BASE) {
                team.sendMessage(CC.translate("&eStarting to create your base"));

                if (location != null) {
                    Hologram hologram = Holograms.newHologram()
                            .at(location.add(0.5, 1, 0.5))
                            .addLines("&eBase Progress:&6 " + getPercentage())
                            .updates()
                            .onUpdate(hologram1 -> {
                                if (workloadDeque.isEmpty()) {
                                    hologram1.setLine(0, "&eBase Progress:&6 Complete");
                                } else {
                                    hologram1.setLine(0, "&eBase Progress:&6 " + getPercentage());
                                }
                            }).build();


                    hologram.send();

                    team.setRegenBaseHologram(hologram);
                }
            } else if (type == TeamWorkdLoadType.REGEN) {
                team.sendMessage(CC.translate("&eStarting to regen your base"));

                if (location != null) {
                    Hologram hologram = Holograms.newHologram()
                            .at(location.add(0.5, 1, 0.5))
                            .addLines("&eRegen Progress:&6 " + getPercentage())
                            .updates()
                            .onUpdate(hologram1 -> {
                                if (workloadDeque.isEmpty()) {
                                    hologram1.setLine(0, "&eRegen Progress:&6 Complete");
                                } else {
                                    hologram1.setLine(0, "&eRegen Progress:&6 " + getPercentage());
                                }
                            }).build();


                    hologram.send();

                    team.setRegenBaseHologram(hologram);
                }
            } else if (type == TeamWorkdLoadType.FALL_TRAP) {
                team.sendMessage(CC.translate("&eStarting to create your falltrap"));

                if (location != null) {
                    Hologram hologram = Holograms.newHologram()
                            .at(location.add(0.5, 1, 0.5))
                            .addLines("&eFallTrap Progress:&6 " + getPercentage())
                            .updates()
                            .onUpdate(hologram1 -> {
                                if (workloadDeque.isEmpty()) {
                                    hologram1.setLine(0, "&eFallTrap Progress:&6 Complete");
                                } else {
                                    hologram1.setLine(0, "&eFallTrap Progress:&6 " + getPercentage());
                                }
                            }).build();


                    hologram.send();

                    team.setRegenBaseHologram(hologram);
                }
            }
    };

    onFinish = (t) -> {
        if (type == TeamWorkdLoadType.REGEN) {
            team.sendMessage(CC.translate("&eYour team base has been regenerated"));

            TaskUtil.runLater(Main.getInstance(), () -> {
                if (team.getRegenBaseHologram() != null) {
                    team.getRegenBaseHologram().destroy();
                    team.setRegenBaseHologram(null);
                }
            }, 20L);
        } else if (type == TeamWorkdLoadType.BASE) {

            team.setUseBase(true);
            team.sendMessage(CC.translate("&eYour team base has been created"));

            TaskUtil.runLater(Main.getInstance(), () -> {
                if (team.getRegenBaseHologram() != null) {
                    team.getRegenBaseHologram().destroy();
                    team.setRegenBaseHologram(null);
                }
            }, 20L);
        } else if (type == TeamWorkdLoadType.FALL_TRAP) {
            team.sendMessage(CC.translate("&eYour falltrap has been created"));

            TaskUtil.runLater(Main.getInstance(), () -> {
                if (team.getRegenBaseHologram() != null) {
                    team.getRegenBaseHologram().destroy();
                    team.setRegenBaseHologram(null);
                }
            }, 20L);
        }

        team.getWorkloadRunnables().remove(type);
    };
  }

  public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }

    @Override
    public void run() {

        if (paused) {
            return;
        }

        for (int i = 0; i < 3; i++) {

            if (workloadDeque.isEmpty()) {
                break;
            }

            Workload workload = workloadDeque.poll();

            if (workload != null) {
                workload.compute();
                completeWorkloads.add(workload);
            }
        }
    }

    @Override
    public void compute() {
        runTaskTimer(Main.getInstance(), 0, 1L);

        onStart.accept(team);
    }

    @Override
    public boolean isFinished() {

        if (workloadDeque.isEmpty()) {
            onFinish.accept(team);

            cancel();

            completeWorkloads.clear();
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

    public String getPercentage() {
        int total = workloadDeque.size() + completeWorkloads.size();

        int percentaje = ((100 * completeWorkloads.size()) / total);

        return percentaje + "%";
    }
}