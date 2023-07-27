package cc.stormworth.hcf.bounty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class BountyPlayer {

  @Getter
  private static final Map<UUID, BountyPlayer> bounties = Maps.newHashMap();

  private final UUID uuid;
  private int balance;
  private final List<ItemStack> rewards = Lists.newArrayList();

  private final String addedBy;
  private final UUID addedByUUID;

  private boolean ready;

  public boolean isCompleted() {
    return !rewards.isEmpty() || balance > 0;
  }

  public Player getTarget() {
    return Bukkit.getPlayer(uuid);
  }

  public static BountyPlayer get(UUID uuid) {
    return bounties.get(uuid);
  }

  public static BountyPlayer get(Player player) {
    return get(player.getUniqueId());
  }

  public static BountyPlayer getAddedBy(Player player) {
    return bounties.values().stream()
            .filter(bounty -> bounty.getAddedByUUID() != null)
        .filter(bountyPlayer -> bountyPlayer.getAddedByUUID().equals(player.getUniqueId()))
        .findFirst().orElse(null);
  }


}