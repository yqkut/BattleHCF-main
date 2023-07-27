package cc.stormworth.hcf.poll;

import cc.stormworth.core.util.chat.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class PollRunnable extends BukkitRunnable {

  private final Poll poll;

  @Override
  public void run() {

    if (poll.isFinished()) {
      Bukkit.broadcastMessage("");
      Bukkit.broadcastMessage(CC.translate("&7[&6&l✪&7] &6&lPoll &ehas ended!"));
      Bukkit.broadcastMessage("");
      for (Player player : Bukkit.getOnlinePlayers()) {
        poll.sendResults(player);
      }
      
      poll.setFinished(true);
      this.cancel();
    }
  }
}