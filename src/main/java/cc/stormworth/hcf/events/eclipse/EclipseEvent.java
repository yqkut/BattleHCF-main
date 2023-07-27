package cc.stormworth.hcf.events.eclipse;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.util.workload.*;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class EclipseEvent {

    private long endAt;
    private boolean active;
    private int radiusInfected = 1000;
    private BukkitTask task;
    private long lastUpdate;
    private int currentRadius = 0;

    private ScheduleWorkLoad scheduleWorkLoad;
    private final LinkedList<Workload> changeBlocks = Lists.newLinkedList();

    private int stage = 1;
    private int lastStagePercentageAnnounce = 0;

    public void start(){
        endAt = TimeUtil.parseTimeLong("2h") + System.currentTimeMillis();

        active = true;

        radiusInfected = 1000;

        Bukkit.broadcastMessage(CC.translate("&7&m---------------------------------"));
        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&6&lEclipse Event &ehas &6&nStarted."));
        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&4&lWARNING! &cAll fence gates, hoppers, chests, doors, furnaces, "));
        Bukkit.broadcastMessage(CC.translate("&cetc... can be opened on any claim."));
        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&4&l¡CUIDADO! &cTodas las vallas, tolvas, cofres, hornos, "));
        Bukkit.broadcastMessage(CC.translate("&cetc... podrán ser abiertos en cualquier claim."));
        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&7&m---------------------------------"));

        for (Player player : Bukkit.getOnlinePlayers()){
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
        }

        Main.getInstance().getWorKLoadQueue().addWorkload(scheduleWorkLoad = new EclipseEventSearchLocationsWorkLoad(this,1000, 100));
        setLastUpdate(System.currentTimeMillis());

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new EclipseRunnable(this), 0, 20L);

        Main.getInstance().getEventHandler().setScheduleEnabled(false);

        Bukkit.broadcastMessage(CC.translate("&e&l▐ &6&lEclipse &7» &eThe &6Stage " + stage + " &ehas &aStarted&e."));
        setLastStagePercentageAnnounce(getStagePercentage());

        KOTH event = (KOTH) Main.getInstance().getEventHandler().getEvent("Eclipse");
        event.setCapTime(720);
        event.activate();
    }

    public void end(){
        active = false;

        Bukkit.broadcastMessage(CC.translate("&7&m---------------------------------"));
        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&6&lEclipse Event &ehas &6&nEnded."));
        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&7&m---------------------------------"));
        task.cancel();

        EclipseEventWorkLoad eclipseEventWorkLoad = new EclipseEventWorkLoad(changeBlocks);

        Main.getInstance().getWorKLoadQueue().addWorkload(eclipseEventWorkLoad);

        KOTH event = (KOTH) Main.getInstance().getEventHandler().getEvent("Eclipse");

        if (event.isActive()){
            event.deactivate();
        }
    }

    public String getFormattedTimeLeft(){
        int seconds = (int) ((endAt - System.currentTimeMillis()) / 1000);
        return ScoreFunction.TIME_FANCY.apply((float) seconds);
    }

    public List<String> getScoreboardScore(Player player){
        List<String> lines = Lists.newArrayList();

        lines.add("&7&m----------------------");
        lines.add("&6&lEclipse");
        lines.add("");
        lines.add("&eTime Left: &f" + getFormattedTimeLeft());
        lines.add("&eRadius Infected: &f" + radiusInfected);

        return lines;
    }

    public void addChangeBlock(Location location, Material material, Byte aByte){
        changeBlocks.add(new PlacableBlock(location, material, aByte, false));
    }

    public boolean isInRadius(Location location){
        return Math.abs(location.getX()) <= currentRadius && Math.abs(location.getZ()) <= currentRadius;
    }

    public String getNextUpdate(){
        long nextUpdate = lastUpdate + 300000;

        int seconds = (int) ((nextUpdate - System.currentTimeMillis()) / 1000);

        return ScoreFunction.TIME_FANCY.apply((float) seconds);
    }

    public String getFormattedStagePercentage(){
        return getStagePercentage() + "%";
    }

    public int getStagePercentage(){

        int maxRadius = 1000;
        int currentRadius = this.currentRadius;

        switch (stage){
            case 2:
                currentRadius = this.currentRadius - 1000;
                maxRadius = 250;
                break;
            case 3:
                currentRadius = this.currentRadius - 1250;
                maxRadius = 250;
                break;
            case 4:
                currentRadius = this.currentRadius - 1500;
                maxRadius = 250;
                break;
            case 5:
                currentRadius = this.currentRadius - 1750;
                maxRadius = 250;
                break;
        }


        return (currentRadius * 100) / maxRadius;
    }
}
