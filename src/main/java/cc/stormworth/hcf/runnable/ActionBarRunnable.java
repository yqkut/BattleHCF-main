package cc.stormworth.hcf.runnable;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.onedoteight.ActionBarUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.listener.GoldenAppleListener;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import cc.stormworth.hcf.util.workload.TeamWorkload;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class ActionBarRunnable implements Runnable {

  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {

      HCFProfile profile = HCFProfile.get(player);

      if (profile != null && profile.getTeam() != null){
        Team team = profile.getTeam();

        if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)){
          TeamWorkload runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE);

          if (Main.getInstance().getWorKLoadQueue().getCurrentWorkloads().contains(runnable)){
            ActionBarUtils.sendActionBarMessage(player, CC.translate("&aYour base is building!"));
          }else{
            ActionBarUtils.sendActionBarMessage(player, CC.translate("&aYour are in position &e" + Main.getInstance().getWorKLoadQueue().getQueuePosition(runnable) + " &aof the queue!"));
          }
        }else if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.FALL_TRAP)){
          TeamWorkload runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP);

          if (Main.getInstance().getWorKLoadQueue().getCurrentWorkloads().contains(runnable)){
            ActionBarUtils.sendActionBarMessage(player, CC.translate("&aYour falltrap is building!"));
          }else{
            ActionBarUtils.sendActionBarMessage(player, CC.translate("&aYour are in position &e" + Main.getInstance().getWorKLoadQueue().getQueuePosition(runnable) + " &aof the queue!"));
          }
        }

      }

      ItemStack hand = player.getItemInHand();

      if (hand == null || hand.getType() == Material.AIR) {
        continue;
      }

      if (hand.getType() == Material.GOLDEN_APPLE){
        if (hand.getDurability() == 0){
          if(GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId())){
            long millisRemaining = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();
            double value = millisRemaining / 1000.0;
            double sec = (value > 0.1) ? (FastMath.round(10.0 * value) / 10.0) : 0.1;

            long totalCooldown = 15000L;

            if (GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) > System.currentTimeMillis()) {
              ActionBarUtils.sendActionBarMessage(player,
                      CC.translate( "&6Golden Apple " + getTimeLeft(millisRemaining,  totalCooldown) +
                              " &7" + ScoreFunction.TIME_FANCY.apply((millisRemaining / 1000F))));
            }
          }
        }else{
          if(Main.getInstance().getOppleMap().isOnCooldown(player.getUniqueId())){
            long cooldownUntil = Main.getInstance().getOppleMap().getCooldown(player.getUniqueId());
            long totalCooldown = Main.getInstance().getMapHandler().isKitMap() ? TimeUnit.MINUTES.toSeconds(15L) : TimeUnit.HOURS.toSeconds(2L);

            if (cooldownUntil > System.currentTimeMillis()) {

              long millisLeft = cooldownUntil - System.currentTimeMillis();

              ActionBarUtils.sendActionBarMessage(player,
                      CC.translate( "&6&lGApple " + getTimeLeft(millisLeft,  totalCooldown) +
                              " &7" + ScoreFunction.TIME_FANCY.apply((millisLeft / 1000F))));
            }
          }
        }
      }

      Ability ability = Ability.getByItem(hand);

      if (ability == null) {
        continue;
      }

      if (CooldownAPI.hasCooldown(player, ability.getName())) {
        ActionBarUtils.sendActionBarMessage(player,
            CC.translate(ability.getDisplayName() + " &eAbility " + getTimeLeft(
                CooldownAPI.getCooldown(player, ability.getName()), ability.getCooldown()) +
                    " &7" + ScoreFunction.TIME_FANCY.apply((CooldownAPI.getCooldown(player, ability.getName()) / 1000F))));
      }
    }
  }

  private String getTimeLeft(long timeLeft, long cooldown) {
    String icon = "▊";

    int maxIcons = 12;

    long totalTime = cooldown / 1000;
    int icons = (int) Math.ceil((double) (timeLeft / 1000) / (double) totalTime * maxIcons);

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < maxIcons; i++) {
      if (i < icons) {
        builder.append("&c&l").append(icon);
      } else {
        builder.append("&7&l").append(icon);
      }
    }

    return builder.toString();
  }
}