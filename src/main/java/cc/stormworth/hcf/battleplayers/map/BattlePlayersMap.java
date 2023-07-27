package cc.stormworth.hcf.battleplayers.map;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.battleplayers.BattlePlayers;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class BattlePlayersMap {

  private static final MongoCollection<Document> collection =
      Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("profiles");

  private long startedAt;
  private boolean started = false;
  private long endedAt;
  private Set<BattlePlayer> totalPlayers = Sets.newHashSet();
  private int map;

  public BattlePlayersMap() {
    map = BattlePlayers.CURRENT_MAP;
  }

  public BattlePlayersMap(Document document) {
    totalPlayers = document.getList("uuid", Document.class)
        .stream()
        .map(BattlePlayer::new)
        .collect(Collectors.toSet());

    System.out.println(totalPlayers.size());
    
    map = document.getInteger("map");
    started = document.getBoolean("started");
    startedAt = document.getLong("startedAt");
    endedAt = document.getLong("endedAt");
  }

  public void addPlayer(Player player) {
    if (totalPlayers.stream()
        .noneMatch(battlePlayer -> battlePlayer.getUuid().equals(player.getUniqueId()))) {
      totalPlayers.add(new BattlePlayer(player.getUniqueId(), player.getName()));
    }
  }

  public void removePlayer(Player player) {
    totalPlayers.removeIf(battlePlayer -> battlePlayer.getUuid().equals(player.getUniqueId()));
  }

  public Document serialize() {
    Document document = new Document();
    document.append("map", map);

    List<Document> documents = Lists.newArrayList();

    for (BattlePlayer battlePlayer : totalPlayers) {
      documents.add(battlePlayer.serialize());
    }

    document.append("uuid", documents);
    document.append("startedAt", startedAt);
    document.append("started", started);
    document.append("endedAt", endedAt);

    return document;
  }

  public void reset() {
    totalPlayers.clear();
    started = false;
    startedAt = 0;
    endedAt = 0;
  }
}