package cc.stormworth.hcf.profile;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.listener.CombatLoggerListener;
import cc.stormworth.hcf.util.countdown.Countdown;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Setter
@Getter
public class Teleport {

  private final Player player;
  private final Location startLocation;
  private final Location to;
  private final int countdownTime;
  private int maxMoveDistance = 3;
  private Ability ability;
  private boolean cancelOnMove = true;
  private Consumer<Player> onTeleport = null;
  private UUID uuid;
  private boolean cancelledOnDamage = true;
  private boolean showParticles = true;
  private Countdown countdown;

  public Countdown start() {
    return countdown = Countdown.of(countdownTime, TimeUnit.SECONDS)
            .players(Lists.newArrayList(player))
            .withMessage(
                    "&6&l[&e✷&6&l] &eTeleporting in &6&l{time}&e." + (cancelOnMove ? "&c&l Do not move or teleport will be cancelled!" : ""))
            .onTick(() -> {
              if (cancelOnMove
                      && player.getLocation().distanceSquared(startLocation) > maxMoveDistance) {
                player.sendMessage(CC.translate("&cTeleport cancelled. You moved too far."));
                HCFProfile profile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());

                if (profile == null) {
                  return;
                }

                profile.getCountdown().cancel();
                return;
              }

              if (showParticles) {
                //ParticleEffect.PORTAL.sphere(player, player.getLocation().clone().add(0, 1, 0), 2);
              }

              if (uuid != null){
                Player target = Bukkit.getPlayer(uuid);

                if(target == null){
                  player.sendMessage(CC.translate("&cTeleport cancelled. &cTarget player is no longer online."));
                  countdown.cancel();

                  if(ability != null){
                    ability.handleAbilityRefund(player, null, true);
                  }

                  return;
                }

                if(target.hasMetadata("deathAt")){
                  long deathAt = target.getMetadata("deathAt").get(0).asLong();

                  int secondsElapsed = (int) (System.currentTimeMillis() - deathAt) / 1000;

                  if(secondsElapsed <= 10){
                      player.sendMessage(CC.translate("&cTeleport cancelled. &cTarget player is no longer online."));
                      countdown.cancel();

                    if(ability != null){
                      ability.handleAbilityRefund(player, null, true);
                    }
                  }else{
                    target.removeMetadata("deathAt", Main.getInstance());
                  }
                }

                if (ability != null && ability.getName().equalsIgnoreCase("Sacrifice")){
                  target.setHealth(target.getHealth() - 2);
                  target.damage(0);
                }
              }
            })
            .onBroadcast(() -> player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1))
            .onFinish(() -> {

              if(player.getWorld() != to.getWorld()){
                player.sendMessage(CC.translate("&cTeleport cancelled. Target is in a different world."));
                if(ability != null){
                  ability.handleAbilityRefund(player, null, true);
                }
                return;
              }

              Player target = Bukkit.getPlayer(uuid);

              if(target != null && target.getWorld() != player.getWorld()){
                player.sendMessage(CC.translate("&cTeleport cancelled. Target is in a different world."));
                if(ability != null){
                  ability.handleAbilityRefund(player, null, true);
                }
                return;
              }

              player.setFallDistance(0);
              if (target != null) {
                player.teleport(target.getLocation());
              } else {

                if(uuid != null){
                  LivingEntity villager = CombatLoggerListener.getCombatLogger(uuid);

                  if(villager != null){
                    player.teleport(villager.getLocation());
                  }else{
                    player.sendMessage(CC.translate("&cTeleport cancelled. Target is offline."));
                  }
                }else{
                  player.teleport(to);
                }
              }

              player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
              player.sendMessage(CC.translate("&eYou have been teleported!"));

              HCFProfile profile = HCFProfile.get(player);
              profile.setCountdown(null);
              profile.setTeleport(null);

              if (onTeleport != null) {
                onTeleport.accept(player);
              }

            }).start();
  }

}