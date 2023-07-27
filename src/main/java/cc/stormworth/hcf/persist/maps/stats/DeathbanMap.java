package cc.stormworth.hcf.persist.maps.stats;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.persist.PersistMap;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.util.Utils;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class DeathbanMap extends PersistMap<Long> {

  public DeathbanMap() {
    super("Deathbans");
    /*new BukkitRunnable() {
      public void run() {
        for (final UUID uuid : getDeathbannedPlayers()) {
          if (isDeathbanned(uuid)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
              final long unbannedOn = Main.getInstance().getDeathbanMap().getDeathban(uuid);
              final long left = unbannedOn - System.currentTimeMillis();
              final int time = (int) left / 1000;
              final int newValue = time - 1;
              if (newValue % 60 != 0) {
                continue;
              }
              final int minutes = newValue / 60;
              if (minutes <= 0) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.translate("&aYour deathban has expired!"));
                player.setExp(0.0F);
                player.setLevel(0);
                Main.getInstance().getDeathbanMap().revive(uuid, true);
              } else {
            	  player.setExp(left / TimeUnit.MINUTES.toMillis(Deathban.getDeathbanSeconds(player)));
            	  player.setLevel(minutes);
            	  
            	  *//*player.sendMessage(
                    ChatColor.GREEN + "You have " + ChatColor.BOLD + minutes + ChatColor.GREEN
                        + " minute" + ((minutes == 1) ? "" : "s") + " of deathban remaining.");*//*
              }
            } else {
              final long unbannedOn = Main.getInstance().getDeathbanMap().getDeathban(uuid);
              final long left = unbannedOn - System.currentTimeMillis();
              final int time = (int) left / 1000;
              final int newValue = time - 1;
              if (newValue % 60 != 0) {
                continue;
              }
              final int minutes = newValue / 60;
              if (minutes > 0) {
                continue;
              }
                if (!Main.getInstance().getRevivedMap().isRevived(uuid)) {
                    CorePlugin.getInstance().getRedisManager().writePlayerMessage(uuid,
                        CC.translate("&eYou deathban at &6HCF &ehas expired."));
                }
              Main.getInstance().getDeathbanMap().revive(uuid, true);
            }
          }
        }
      }
    }.runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);*/
  }

  @Override
  public String getRedisValue(final Long time) {
    return String.valueOf(time);
  }

  @Override
  public Long getJavaObject(final String str) {
    return Long.parseLong(str);
  }

  public boolean isDeathbanned(final UUID check) {
      if (Main.getInstance().getMapHandler().isKitMap()) {
          return false;
      }
    return this.getValue(check) != null && this.getValue(check) > System.currentTimeMillis();
  }

  public void deathban(final UUID update, final long seconds) {
	this.updateValueAsync(update, System.currentTimeMillis() + seconds * 1000L);
  }

  public void removeTime(final UUID update, final long seconds) {
    /*final long unbannedOn = Main.getInstance().getDeathbanMap().getDeathban(update);
    final long left = unbannedOn - System.currentTimeMillis();
    long finaltime = ((int) left / 1000) - seconds;
    if (finaltime <= 1) {
      Player player = Bukkit.getPlayer(update);
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
      player.sendMessage(CC.translate("&aYour deathban has expired!"));
      this.revive(update, true);
      this.updateValueAsync(update, System.currentTimeMillis() + 1 * 1000L);
    } else {
      this.updateValueAsync(update, System.currentTimeMillis() + finaltime * 1000L);
    }*/
  }

  public void revive(final UUID update, boolean doevents) {
    if (doevents) {
      Player player = Bukkit.getPlayer(update);
      if (player != null) {
        TaskUtil.runLater(Main.getInstance(), () -> {
          if (SpawnTagHandler.isTagged(player)) {
              SpawnTagHandler.removeTag(player);
          }
          player.getInventory().clear();
          player.getOpenInventory().getTopInventory().clear();
          player.getInventory().setArmorContents(null);

          HCFProfile profile = HCFProfile.get(player);

          //Main.getInstance().getPvPTimerMap().createTimer(update, 1800);
          profile.setPvpTimer(new PvPTimer(false));

          player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
          if (player.hasMetadata("deathban")) {
              player.removeMetadata("deathban", Main.getInstance());
          }

          Utils.removeThrownPearls(player);
        }, 10L);

        //TaskUtil.runLater(Main.getInstance(), () -> Main.getInstance().getRevivedMap().setRevived(update, false), 30L);
      } else {
       // Main.getInstance().getRevivedMap().setRevived(update, true);
      }
    }
    //Main.getInstance().getDeathbannedMap().setDeathbanned(update, false);
    this.updateValueAsync(update, 0L);
  }

  public long getDeathban(final UUID check) {
    return this.contains(check) ? this.getValue(check) : 0L;
  }

  public void wipeDeathbans() {
    this.wipeValues();
  }

  public void wipeVals() {
    this.wipeValues();
  }

  public ConcurrentSet<UUID> getDeathbannedPlayers() {
    final ConcurrentSet<UUID> deathbannedPlayers = new ConcurrentSet<>();
    for (final Map.Entry<UUID, Long> entry : this.wrappedMap.entrySet()) {
      if (this.isDeathbanned(entry.getKey())) {
        deathbannedPlayers.add(entry.getKey());
      }
    }
    return deathbannedPlayers;
  }
}