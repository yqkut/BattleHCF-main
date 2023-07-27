package cc.stormworth.hcf.team.upgrades;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.player.Players;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;

public class UpgradeListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    Team team = LandBoard.getInstance().getTeam(player.getLocation());

    if (team == null) {
      return;
    }

    if (team.isMember(player.getUniqueId()) && !team.isRaidable()) {
      giveEffects(team, player);
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    Team team = LandBoard.getInstance().getTeam(player.getLocation());

    if (team != null && team.isMember(player.getUniqueId())) {
      removeEffects(team, player);
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Location to = event.getTo();
    Location from = event.getFrom();

    Player player = event.getPlayer();

    if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
      return;
    }

    Team teamTo = LandBoard.getInstance().getTeam(to);
    Team teamFrom = LandBoard.getInstance().getTeam(from);

    if (teamFrom != teamTo) {

      if (teamTo == null) {
        if (teamFrom.isMember(player.getUniqueId()) && teamFrom.getActiveEffects() != null) { //Leave from your claim to the wilderness
          removeEffects(teamFrom, player);
        }
      } else if (teamTo.isMember(player.getUniqueId()) && teamTo.getActiveEffects() != null && !teamTo.isRaidable()) { //Enter your claim

        giveEffects(teamTo, player);

      } else if (!teamTo.isMember(player.getUniqueId()) && teamFrom != null
              && teamFrom.isMember(player.getUniqueId()) && teamFrom.getActiveEffects() != null) { //Leave your claim to another claim

        removeEffects(teamFrom, player);
      }
    }
  }

  @EventHandler
  public void onTeleport(PlayerTeleportEvent event) {
    onPlayerMove(event);
  }

  @EventHandler
  public void onDimensionChange(PlayerChangedWorldEvent event) {
    Player player = event.getPlayer();
    if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
      Team team = Main.getInstance().getTeamHandler().getTeam(player);

      if (team != null) {
        team.getActiveEffects().keySet().forEach(player::removePotionEffect);
      }
    } else {
      Team team = LandBoard.getInstance().getTeam(player.getLocation());

      if (team == null) {
        return;
      }

      if (team.isMember(player.getUniqueId()) && !team.isRaidable()) {
        giveEffects(team, player);
      }
    }
  }

  private void giveEffects(Team team, Player player){
    team.getActiveEffects().forEach((potionEffectType, level) ->
            Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(potionEffectType, Integer.MAX_VALUE, level - 1)));
  }

  private void removeEffects(Team team, Player player){
    team.getActiveEffects().forEach((potionEffectType, level) -> {

      PotionEffect activeEffect = Players.getActivePotionEffect(player, potionEffectType);

      if (activeEffect != null) {
        if (activeEffect.getAmplifier() > (level - 1)) {
          Main.getInstance().getEffectRestorer().getRestores().remove(player.getUniqueId(), new Effect(potionEffectType, level - 1));
          return;
        }
      }

      player.removePotionEffect(potionEffectType);
    });
  }

  @EventHandler
  public void onRemovePotionEffect(PotionEffectRemoveEvent event){
    if(!(event.getEntity() instanceof Player)){
      return;
    }

    Player player = (Player) event.getEntity();

    PotionEffect expiredEffect = event.getEffect();
    TaskUtil.runLater(Main.getInstance(), () -> {
      Team team = LandBoard.getInstance().getTeam(player.getLocation());


      if(team == null){
        return;
      }

      if(team.isMember(player.getUniqueId()) && !team.isRaidable()){
        team.getActiveEffects().forEach((potionEffectType, level) -> {
          if (expiredEffect.getType() == potionEffectType){

              player.addPotionEffect(new PotionEffect(potionEffectType, Integer.MAX_VALUE, level - 1));
          }
        });
      }
    }, 3L);

  }

}