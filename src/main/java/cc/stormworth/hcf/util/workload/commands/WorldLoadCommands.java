package cc.stormworth.hcf.util.workload.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.workload.ScheduleWorkLoad;
import cc.stormworth.hcf.util.workload.TeamWorkload;
import cc.stormworth.hcf.util.workload.WorKLoadQueue;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldLoadCommands {

    private static final WorKLoadQueue queue = Main.getInstance().getWorKLoadQueue();

    @Command(names = {"workload pause", "workload resume"}, permission = "op")
    public static void pause(Player player){
        queue.setPaused(!queue.isPaused());

        if (queue.isPaused()){
            queue.getCurrentWorkloads().forEach(ScheduleWorkLoad::pause);
        } else {
            queue.getCurrentWorkloads().forEach(ScheduleWorkLoad::resume);
        }

        player.sendMessage(CC.translate("&eWork load queue " + (queue.isPaused() ? "&cpaused" : "&aresumed")));
    }

    @Command(names = {"queue base"}, permission = "")
    public static void base(Player player){
        HCFProfile profile = HCFProfile.get(player);

        if (profile != null && profile.getTeam() != null){
            Team team = profile.getTeam();

            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)){
                TeamWorkload runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE);

                if (Main.getInstance().getWorKLoadQueue().getCurrentWorkloads().contains(runnable)){
                    player.sendMessage(CC.translate("&aYour base is building!"));
                }else{
                    player.sendMessage(CC.translate("&aYour are in position &e" + Main.getInstance().getWorKLoadQueue().getQueuePosition(runnable) + " &aof the queue!"));
                }
            }

        }else{
            player.sendMessage(CC.translate("&cYou are not in a team!"));
        }
    }

    @Command(names = {"queue falltrap"}, permission = "")
    public static void falltrap(Player player){
        HCFProfile profile = HCFProfile.get(player);

        if (profile != null && profile.getTeam() != null){
            Team team = profile.getTeam();

            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.FALL_TRAP)){
                TeamWorkload runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP);

                if (Main.getInstance().getWorKLoadQueue().getCurrentWorkloads().contains(runnable)){
                    player.sendMessage(CC.translate("&aYour falltrap is building!"));
                }else{
                    player.sendMessage(CC.translate("&aYour are in position &e" + Main.getInstance().getWorKLoadQueue().getQueuePosition(runnable) + " &aof the queue!"));
                }
            }

        }else{
            player.sendMessage(CC.translate("&cYou are not in a team!"));
        }
    }

    @Command(names = {"queue size"}, permission = "op")
    public static void size(Player player){
        player.sendMessage(CC.translate("&aThere are &e" + Main.getInstance().getWorKLoadQueue().getQueueWorkLoads().size() + " &aitems in the queue!"));
        player.sendMessage(CC.translate("&aThere are &e" + Main.getInstance().getWorKLoadQueue().getCurrentWorkloads().size() + " &aitems in the current queue!"));
    }

    @Command(names = {"queue view"}, permission = "op")
    public static void view(Player player){
        List<ScheduleWorkLoad> currentWorkload = Main.getInstance().getWorKLoadQueue().getCurrentWorkloads();

        player.sendMessage(CC.translate("&6&lTeams in currently active: "));

        for (ScheduleWorkLoad scheduleWorkLoad : currentWorkload) {
            if (scheduleWorkLoad instanceof TeamWorkload){
                TeamWorkload teamWorkload = (TeamWorkload) scheduleWorkLoad;

                player.sendMessage(CC.translate("&e" + teamWorkload.getTeam().getName()));
            }
        }
    }

    @Command(names = {"queue cancel", "base cancel", "falltrap cancel", "queue leave"}, permission = "")
    public static void cancel(Player player){
        HCFProfile profile = HCFProfile.get(player);

        if (profile != null && profile.getTeam() != null){
            Team team = profile.getTeam();

            TeamWorkload runnable;
            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.FALL_TRAP)){
                runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP);
            }else if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)) {
                runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE);
            }else{
                if (queue.getCurrentWorkloads().contains(team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE))) {
                    player.sendMessage(CC.translate("&aYou base is currently being built!"));
                    return;
                }else if (queue.getCurrentWorkloads().contains(team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP))) {
                    player.sendMessage(CC.translate("&aYou falltrap is currently being built!"));
                    return;
                }

                player.sendMessage(CC.translate("&cYou are not in a queue!"));
                return;
            }

            if (Main.getInstance().getWorKLoadQueue().getQueueWorkLoads().contains(runnable)) {
                Main.getInstance().getWorKLoadQueue().getQueueWorkLoads().remove(runnable);
                team.getWorkloadRunnables().remove(runnable.getType());
                player.sendMessage(CC.translate("&aYou have left the queue!"));
            }

        }else{
            player.sendMessage(CC.translate("&cYou are not in a team!"));
        }

    }

}
