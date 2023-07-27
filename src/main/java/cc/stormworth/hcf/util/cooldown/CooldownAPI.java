package cc.stormworth.hcf.util.cooldown;

import cc.stormworth.core.util.chat.CC;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class CooldownAPI {

  private final Map<UUID, List<Cooldown>> cooldowns = Maps.newHashMap();

  public void setCooldown(Player player, String name, long time) {
    if (!cooldowns.containsKey(player.getUniqueId())) {
      cooldowns.put(player.getUniqueId(), new CopyOnWriteArrayList<>());
    }
    cooldowns.get(player.getUniqueId()).add(new Cooldown(name, System.currentTimeMillis() + time));
  }

  public void setCooldown(Player player, String name, long time, String expireMessage) {
    if (!cooldowns.containsKey(player.getUniqueId())) {
      cooldowns.put(player.getUniqueId(), new CopyOnWriteArrayList<>());
    }
    cooldowns.get(player.getUniqueId()).add(new Cooldown(name, System.currentTimeMillis() + time, expireMessage));
  }

  public boolean hasCooldown(Player player, String name) {
    if (!cooldowns.containsKey(player.getUniqueId())) {
      return false;
    }
    for (Cooldown cooldown : cooldowns.get(player.getUniqueId())) {
      if (hasExpired(cooldown)) {

        if(cooldown.getExpireMessage() != null && !cooldown.isAnnounced()){
          player.sendMessage(CC.translate(cooldown.getExpireMessage()));
          player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.1f, 0.1f);
          cooldown.setAnnounced(true);
        }

        removeCooldown(player, cooldown.getName());
        continue;
      }
      if (cooldown.getName().equals(name) && !hasExpired(cooldown)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasExpired(Cooldown cooldown) {
    return cooldown.getTime() <= 0;
  }

  public long getCooldown(Player player, String name) {
    if (!cooldowns.containsKey(player.getUniqueId())) {
      return 0;
    }
    for (Cooldown cooldown : cooldowns.get(player.getUniqueId())) {
      if (cooldown.getName().equals(name)) {
        return cooldown.getTime();
      }
    }
    return 0;
  }

  public void removeCooldown(Player player, String name) {
    if (!cooldowns.containsKey(player.getUniqueId())) {
      return;
    }
    cooldowns.get(player.getUniqueId()).removeIf(cooldown -> cooldown.getName().equals(name));
  }

}