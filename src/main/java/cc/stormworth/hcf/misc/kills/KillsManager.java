package cc.stormworth.hcf.misc.kills;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

public class KillsManager {

  private static final MongoCollection<Document> collection = Main.getInstance().getMongoPool()
      .getDatabase(Main.DATABASE_NAME).getCollection("kills");

  public static void saveKill(UUID killer, UUID victim, ItemStack itemStack) {
    Document document = new Document();

    document.append("killer", killer.toString());
    document.append("victim", victim.toString());
    document.append("itemStack", CorePlugin.GSON.toJson(itemStack));
    document.append("date", System.currentTimeMillis());

    collection.insertOne(document);
  }

  public static List<KillInfo> getAllKills(UUID killer) {
    List<KillInfo> kills = Lists.newArrayList();

    for (Document document : collection.find(Filters.eq("killer", killer.toString()))
        .sort(new Document("date", -1))) {
      kills.add(new KillInfo(document));
    }

    return kills;
  }

}