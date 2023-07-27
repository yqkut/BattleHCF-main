package cc.stormworth.hcf.ability.impl.merge;

import cc.stormworth.core.util.time.TimeUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class Merge {

  private static final List<Merge> merges = Lists.newArrayList();

  private final List<UUID> mergedplayers;
  private final long expiredAt;

  public Merge(Player player1, Player player2) {
    this.mergedplayers = Lists.newArrayList(player1.getUniqueId(), player2.getUniqueId());
    this.expiredAt = System.currentTimeMillis() + TimeUtil.parseTimeLong("20s");
    merges.add(this);
  }

  public static void removeMerge(Merge merge) {
    merges.remove(merge);
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > expiredAt;
  }

  public void remove() {
    merges.remove(this);
  }

  public UUID getOtherPlayer(Player player) {
    return mergedplayers.get(0) == player.getUniqueId() ? mergedplayers.get(1)
        : mergedplayers.get(0);
  }

  public static boolean isMerged(Player player) {
    return merges.stream()
        .anyMatch(
            merge -> merge.getMergedplayers().contains(player.getUniqueId()) && !merge.isExpired());
  }

  public static Merge getMerge(Player player) {
    return merges.stream()
        .filter(
            merge -> merge.getMergedplayers().contains(player.getUniqueId()) && !merge.isExpired())
        .findFirst()
        .orElse(null);
  }

}