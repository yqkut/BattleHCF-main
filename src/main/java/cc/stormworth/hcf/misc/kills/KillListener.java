package cc.stormworth.hcf.misc.kills;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.onedoteight.ActionBarUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillListener implements Listener {

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {

    if (event.getEntity().getKiller() == null) {
      return;
    }

    if(Main.getInstance().getTeamHandler().getTeam(event.getEntity()) != null &&
            Main.getInstance().getFactionDuelManager().isInMatch(Main.getInstance().getTeamHandler().getTeam(event.getEntity()))){
      return;
    }

    Player killer = event.getEntity().getKiller();
    Player victim = event.getEntity();

    Team playerTeam = Main.getInstance().getTeamHandler().getTeam(victim);
    Team killerTeam = Main.getInstance().getTeamHandler().getTeam(killer);

    Location deathLoc = event.getEntity().getLocation();
    int deathX = deathLoc.getBlockX();
    int deathY = deathLoc.getBlockY();
    int deathZ = deathLoc.getBlockZ();

    if (killerTeam != null) {
      TeamTrackerManager.logAsync(killerTeam, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP, ImmutableMap.<String, Object>builder()
                      .put("playerId", killer.getUniqueId().toString())
                      .put("killedId", event.getEntity().getUniqueId().toString())
              .put("coordinates", deathX + ", " + deathY + ", " + deathZ)
              .put("date", System.currentTimeMillis())
              .build());
    }

    if (playerTeam != null) {
      TeamTrackerManager.logAsync(playerTeam, TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP, ImmutableMap.<String, Object>builder()
              .put("playerId", event.getEntity().getUniqueId().toString())
              .put("killerId", killer.getUniqueId().toString())
              .put( "coordinates", deathX + ", " + deathY + ", " + deathZ)
              .put( "date", System.currentTimeMillis())
              .build()
      );
    }

    ActionBarUtils.sendActionBarMessage(killer, "&4✗ &eYou have killed &6&l" + victim.getName() + " &4✗");
    killer.playSound(killer.getLocation(), Sound.IRONGOLEM_DEATH, 1.0f, 1.0f);

    TaskUtil.runAsync(Main.getInstance(),
        () -> KillsManager.saveKill(killer.getUniqueId(), victim.getUniqueId(),
            killer.getItemInHand()));

    HCFProfile killerProfile = HCFProfile.get(killer);

    if (killerProfile != null && killerProfile.isRandomEffects()){

      PotionEffectType[] types = PotionEffectType.values();

      PotionEffectType randomType = types[(int) (Math.random() * types.length)];

      killer.addPotionEffect(new PotionEffect(randomType, 20 * 10, 0));

    }
  }

}