package cc.stormworth.hcf.server;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Deathban {

  private static final Map<Rank, Integer> deathban = new LinkedHashMap<>();
  private static final int defaultMinutes = 30;

  static {
    deathban.put(Rank.WARRIOR, 25);
    deathban.put(Rank.KNIGHT, 20);
    deathban.put(Rank.SOLDIER, 15);
    deathban.put(Rank.HERO, 10);
    deathban.put(Rank.MASTER, 15);
    deathban.put(Rank.KING, 10);
    deathban.put(Rank.KING_PLUS, 5);
    deathban.put(Rank.BATTLE, 1);
    deathban.put(Rank.MINI_YT, 10);
    deathban.put(Rank.YOUTUBER, 5);
    deathban.put(Rank.FAMOUS, 5);
    deathban.put(Rank.FAMOUSPLUS, 5);
    deathban.put(Rank.PARTNER, 1);
  }

  public static int getDeathbanSeconds(Player player) {
    int minutes = defaultMinutes;
    for (Map.Entry<Rank, Integer> entry : deathban.entrySet()) {
      if (Profile.getByUuid(player.getUniqueId()).getRank() == entry.getKey()
          && entry.getValue() < minutes) {
        minutes = entry.getValue();
      }
    }
    return (int) TimeUnit.MINUTES.toSeconds(minutes);
  }
}