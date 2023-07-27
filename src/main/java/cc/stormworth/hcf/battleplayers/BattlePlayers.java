package cc.stormworth.hcf.battleplayers;

import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.battleplayers.listeners.BattlePlayersListener;
import cc.stormworth.hcf.battleplayers.map.BattlePlayersMap;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Map;

@Getter
@Setter
public class BattlePlayers {
 
  private final MongoCollection<Document> collection = Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("maps");

  public static final int CURRENT_MAP = 2;

  private final Map<Integer, BattlePlayersMap> maps = Maps.newHashMap();

  public BattlePlayers() {
    load();
    rCommandHandler.registerPackage(Main.getInstance(), "cc.stormworth.hcf.battleplayers.commands");
    Bukkit.getPluginManager()
        .registerEvents(new BattlePlayersListener(Main.getInstance()), Main.getInstance());
  }

  public void startMap() {
    BattlePlayersMap battlePlayersMap = new BattlePlayersMap();
    battlePlayersMap.setStarted(true);
    battlePlayersMap.setStartedAt(System.currentTimeMillis());
    maps.put(CURRENT_MAP, battlePlayersMap);
  }

  public void stopMap() {
    BattlePlayersMap battlePlayersMap = maps.get(CURRENT_MAP);
    battlePlayersMap.setStarted(false);
    battlePlayersMap.setEndedAt(System.currentTimeMillis());
  }

  public void save() {
    for (Map.Entry<Integer, BattlePlayersMap> entry : maps.entrySet()) {
      Document document = new Document();
      document.put("number", entry.getKey());

      BattlePlayersMap battlePlayersMap = entry.getValue();

      document.put("map", battlePlayersMap.serialize());

      collection.replaceOne(
          Filters.eq("number", entry.getKey()),
          document,
          new ReplaceOptions().upsert(true));
    }
  }

  public void load() {
    for (Document document : collection.find()) {
      int number = document.getInteger("number");
      BattlePlayersMap battlePlayersMap = new BattlePlayersMap(document.get("map", Document.class));
      maps.put(number, battlePlayersMap);
    }
  }

  public BattlePlayersMap getMap() {
    return maps.get(CURRENT_MAP);
  }
}