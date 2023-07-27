package cc.stormworth.hcf.events.eclipse;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.workload.EclipseEventSearchLocationsWorkLoad;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class EclipseRunnable implements Runnable{

    private final EclipseEvent eclipseEvent;

    @Override
    public void run() {
        if(eclipseEvent.isActive()){
            if(eclipseEvent.getEndAt() <= System.currentTimeMillis()){
                eclipseEvent.end();
                return;
            }

            for (Player player : Bukkit.getOnlinePlayers()){
                if (eclipseEvent.isInRadius(player.getLocation())){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 0), true);
                }

                //BossBar.display(player, CC.translate("&eNext Expansion In:&f " + eclipseEvent.getNextUpdate()));
            }

            if (Main.getInstance().getEventHandler().getEvent("Eclipse") != null){
                Team team = Main.getInstance().getTeamHandler().getTeam("Eclipse");

                team.getClaims().forEach(coordinates ->
                        coordinates.getPlayers().forEach(other ->
                                other.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 0), true)));
            }

            if (eclipseEvent.getStagePercentage() == 100 && eclipseEvent.getLastStagePercentageAnnounce() != eclipseEvent.getStagePercentage() && eclipseEvent.getStage() == 5){
                Bukkit.broadcastMessage(CC.translate(""));
                Bukkit.broadcastMessage(CC.translate("&e&l▐ &6&lEclipse &7» &a&lAll the Map &ehas been &6INFECTED &cGood Luck!"));
                Bukkit.broadcastMessage(CC.translate(""));
                eclipseEvent.setLastStagePercentageAnnounce(eclipseEvent.getStagePercentage());
            }else if (eclipseEvent.getStagePercentage() % 10 == 0 && eclipseEvent.getLastStagePercentageAnnounce() != eclipseEvent.getStagePercentage()){
                Bukkit.broadcastMessage(CC.translate(""));
                Bukkit.broadcastMessage(CC.translate("&e&l▐ &6&lEclipse &7» &a&l" + eclipseEvent.getStagePercentage() + "% &eof the &6Stage " + eclipseEvent.getStage() + " &ehas been &6&ninfected&e."));
                Bukkit.broadcastMessage(CC.translate(""));
                eclipseEvent.setLastStagePercentageAnnounce(eclipseEvent.getStagePercentage());
            }

            if (eclipseEvent.getRadiusInfected() >= 2000) {
                return;
            }

            //Update eclipsevent radius to 250 blocks every 5 minutes
            if((eclipseEvent.getLastUpdate() + 300000) <= System.currentTimeMillis()){
                eclipseEvent.setStage(eclipseEvent.getStage() + 1);
                eclipseEvent.setLastStagePercentageAnnounce(eclipseEvent.getStagePercentage());
                Bukkit.broadcastMessage(CC.translate(""));
                Bukkit.broadcastMessage(CC.translate("&e&l▐ &6&lEclipse &7» &eThe &6Stage " + eclipseEvent.getStage() + " &ehas &aStarted&e."));
                Bukkit.broadcastMessage(CC.translate(""));

                int radius = eclipseEvent.getRadiusInfected();
                eclipseEvent.setRadiusInfected(eclipseEvent.getRadiusInfected() + 250);
                EclipseEventSearchLocationsWorkLoad workLoad = new EclipseEventSearchLocationsWorkLoad(eclipseEvent, eclipseEvent.getRadiusInfected(), radius);
                Main.getInstance().getWorKLoadQueue().addWorkload(workLoad);
                eclipseEvent.setScheduleWorkLoad(workLoad);
                eclipseEvent.setLastUpdate(System.currentTimeMillis());
            }
        }
    }
}
