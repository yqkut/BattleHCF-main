package cc.stormworth.hcf.misc.daily;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.daily.data.DailyPlayer;
import com.google.common.collect.Maps;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DailyManager implements Listener {

  @Getter private Map<UUID, DailyPlayer> dailyPlayers;

  public DailyManager() {
    this.load();

    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }

  public void disable() {
    Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME)
            .getCollection("dailies")
            .drop();

    for (DailyPlayer daily : this.dailyPlayers.values()) {
      Document document = new Document();

      document.put("owner", daily.getOwner().toString());
      document.put("daysJoined", daily.getDaysJoined());
      document.put("claimedRewards", daily.getClaimedRewards());
      document.put("streak", daily.getStreak());
      document.put("nextRewardLevel", daily.getNextRewardLevel());
      document.put("joined", daily.getJoined());
      document.put("nextReward", daily.getNextReward());

      Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("dailies")
          .replaceOne(Filters.eq("uuid", daily.getOwner().toString()), document,
              new ReplaceOptions().upsert(true));
    }
  }

  public void load() {
    this.dailyPlayers = Maps.newConcurrentMap();

    for (Document document : Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("dailies").find()) {
      DailyPlayer daily = new DailyPlayer();

      daily.setOwner(UUID.fromString(document.getString("owner")));
      daily.setDaysJoined(document.getInteger("daysJoined"));
      daily.setClaimedRewards(document.getInteger("claimedRewards"));
      daily.setStreak(document.getInteger("streak"));
      daily.setNextRewardLevel(document.getInteger("nextRewardLevel"));
      daily.setJoined(document.getLong("joined"));
      daily.setNextReward(document.getLong("nextReward"));

      dailyPlayers.put(daily.getOwner(), daily);
    }
  }

  @EventHandler
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    DailyPlayer dailyPlayer = this.findDailyPlayerByUUID(player.getUniqueId());

    if (dailyPlayer == null) {
      dailyPlayer = new DailyPlayer();

      dailyPlayer.setOwner(player.getUniqueId());
      dailyPlayer.setClaimedRewards(0);
      dailyPlayer.setJoined(System.currentTimeMillis());
      dailyPlayer.setDaysJoined(0);
      dailyPlayer.setStreak(0);
      dailyPlayer.setNextReward(System.currentTimeMillis());
      dailyPlayer.setNextRewardLevel(1);

      if (!dailyPlayers.containsKey(player.getUniqueId())) {
        dailyPlayers.put(player.getUniqueId(), dailyPlayer);
      }
    }else{
      if(this.unlockNextReward(dailyPlayer.getNextRewardLevel(), dailyPlayer.getNextReward())){
        TextComponent textComponent = new TextComponent(CC.translate("&eSeems you haven't claimed your &apending &6&lDaily Reward! &eDon't &eworry, just &a&l[Click Here]&e to claim it!"));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/daily"));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(CC.translate("&a&l[Click Here] to claim it!"))}));

        player.spigot().sendMessage(textComponent);
      }
    }

    if (!this.unlockNextReward(dailyPlayer.getNextRewardLevel(), dailyPlayer.getNextReward())) {
      return;
    }

    if (dailyPlayer.getNextRewardLevel() > 1){
      if (dailyPlayer.getStreak() + 1 < TimeUnit.MILLISECONDS.toDays(dailyPlayer.getJoined() - dailyPlayer.getNextReward())) {
        dailyPlayer.setClaimedRewards(0);
        dailyPlayer.setJoined(System.currentTimeMillis());
        dailyPlayer.setDaysJoined(0);
        dailyPlayer.setStreak(0);
        dailyPlayer.setNextReward(System.currentTimeMillis());
        dailyPlayer.setNextRewardLevel(1);

        player.sendMessage(CC.translate("&cYou lost your Daily Streak."));
      } else {
        dailyPlayer.setNextReward(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1L));
      }
    }
  }

  public long getRemaining(int days, long joined) {
    return (joined + TimeUnit.DAYS.toMillis(days)) - System.currentTimeMillis();
  }

  public long getRemaining(long nextReward) {
    return nextReward - System.currentTimeMillis();
  }

  public boolean unlockNextReward(long nextReward) {
    long finalLong = nextReward - System.currentTimeMillis();

    return finalLong <= 0L;
  }

  public boolean unlockNextReward(int days, long nextReward) {
    long finalLong = (nextReward + TimeUnit.DAYS.toMillis(days)) - System.currentTimeMillis();

    return finalLong <= 0L;
  }

  public void reset(Player player) {
    DailyPlayer dailyPlayer = this.findDailyPlayerByUUID(player.getUniqueId());

    if (dailyPlayer == null) {
      return;
    }

    dailyPlayer.setClaimedRewards(0);
    dailyPlayer.setJoined(System.currentTimeMillis());
    dailyPlayer.setDaysJoined(0);
    dailyPlayer.setStreak(0);
    dailyPlayer.setNextReward(System.currentTimeMillis());
    dailyPlayer.setNextRewardLevel(1);
  }

  public DailyPlayer findDailyPlayerByUUID(UUID uuid) {
    return dailyPlayers.get(uuid);
  }
}
