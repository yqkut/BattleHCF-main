package cc.stormworth.hcf.voteparty;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.countdown.Countdown;
import cc.stormworth.hcf.util.player.Players;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
public class VotePartyHandler {

  private final MongoCollection<Document> collection = Main.getInstance().getMongoPool()
      .getDatabase(Main.DATABASE_NAME).getCollection("vote_party");

  private final List<UUID> alreadyVotes = Lists.newArrayList();
  private int currentVotes = 0;
  private final int MAX_VOTES = 50;
  private long cooldown;

  public boolean hasVote(Player player) {
    return alreadyVotes.contains(player.getUniqueId());
  }

  public void vote(Player player) {
    alreadyVotes.add(player.getUniqueId());
    currentVotes++;

    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(CC.translate("&7(&6&lParty Vote&7) " + player.getDisplayName()
        + " &ehas voted the server "
        + "&7(&6" + currentVotes + "&7/&6" + MAX_VOTES + "&7) &7&o*Use /vote*"));
    Bukkit.broadcastMessage("");

    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);

    if (currentVotes == MAX_VOTES) {
      end();
    }
  }

  public VotePartyHandler() {
    load();
  }

  public void load() {
    Document document = collection.find(new Document("_id", "vote_party")).first();

    if (document == null) {
      return;
    }

    if (document.containsKey("currentVotes")) {
      currentVotes = document.getInteger("currentVotes");
    }

    List<String> votes = document.getList("votes", String.class);

    if (votes == null) {
      return;
    }

    alreadyVotes.addAll(votes.stream().map(UUID::fromString).collect(Collectors.toList()));
  }

  public void save() {
    Document document = new Document();
    List<String> votes = alreadyVotes.stream().map(UUID::toString).collect(Collectors.toList());

    document.append("_id", "vote_party");
    document.append("votes", votes);
    document.append("currentVotes", currentVotes);

    collection.replaceOne(new Document("_id", "vote_party"), document,
        new ReplaceOptions().upsert(true));
  }

  public boolean isOnCooldown() {
    return System.currentTimeMillis() < cooldown;
  }

  public void end() {
    currentVotes = 0;
    cooldown = System.currentTimeMillis() + TimeUtil.parseTime("1m");

    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(CC.translate(
        "&7(&6&lVote Party&7) &6&lBattle &ehas reached the &6&l50 votes &eneeded."));
    Bukkit.broadcastMessage("");

    Players.playSoundForAll(Sound.LEVEL_UP);

    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(CC.translate(
        "&7(&6&lVote Party&7) &d&lKeyall &ecountdown has been started. &7(&65 Minutes&7)"));
    Bukkit.broadcastMessage("");

    for (Player other : Bukkit.getOnlinePlayers()) {
      TitleBuilder titleBuilder = new TitleBuilder("&6&lVote Party&7", "&7has finished.", 10, 20, 10);
      titleBuilder.send(other);
    }

    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "createtimer 5m &4&lKeyAll");

    Countdown.of(5, TimeUnit.MINUTES).withMessage(null).onFinish(() -> {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "keyall Evil 40");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "Keyall Nightmare 32");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "keyall Horror 20");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "keyall Partner 10");
    }).start();
  }
}