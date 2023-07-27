package cc.stormworth.hcf.misc.map.leaderboards;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.misc.map.stats.command.StatsTopCommand.StatsObjective;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LeaderboardRunnable implements Runnable {

  private final Map<StatsObjective, UUID> tops = Maps.newHashMap();

  @Override
  public void run() {
    for (StatsObjective statsObjective : StatsObjective.values()) {
      Optional<StatsEntry> entry = Main.getInstance().getMapHandler()
          .getStatsHandler().getLeaderboards(statsObjective, 1).keySet().stream().findFirst();

      if (entry.isPresent()) {
        if (tops.containsKey(statsObjective)) {
          if (tops.get(statsObjective).equals(entry.get().getOwner())) {
            Player player = Bukkit.getPlayer(entry.get().getOwner());
            if (player != null) {
              Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                  "gems give " + player.getName() + " 20");
            }
          }
        } else {
          tops.put(statsObjective, entry.get().getOwner());
        }
      }
    }

  }
}