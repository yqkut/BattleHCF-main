package cc.stormworth.hcf.giveaway.runnable;

import cc.stormworth.hcf.giveaway.GiveAway;
import cc.stormworth.hcf.giveaway.GiveAwayType;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class GiveAwayRunnable extends BukkitRunnable {

  private final GiveAway giveaway;

  @Override
  public void run() {
    if (giveaway.getTime() <= System.currentTimeMillis()) {
      if (giveaway.getType() == GiveAwayType.RAFFLE) {
        giveaway.selectRandomWinner();
      }else{
        giveaway.end();
      }

      cancel();
    }
  }
}