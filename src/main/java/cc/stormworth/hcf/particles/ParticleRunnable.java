package cc.stormworth.hcf.particles;

import cc.stormworth.hcf.profile.HCFProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ParticleRunnable implements Runnable {

  @Setter
  @Getter
  private static boolean enable = true;

  @Override
  public void run() {

    if (!ParticleRunnable.isEnable()) {
      return;
    }

    for (Player player : Bukkit.getOnlinePlayers()) {

      HCFProfile profile = HCFProfile.get(player);

      if (!profile.isParticles()) {
        continue;
      }
      
      /*ParticleEffect.HAPPY_VILLAGER.display(0.0, 0.0, 0.0, 1, 10,
          player.getLocation().clone().add(0, 0.5, 0), true, player,
          (other) -> HCFProfile.get(other).isParticlesBattle());*/
    }
  }
}