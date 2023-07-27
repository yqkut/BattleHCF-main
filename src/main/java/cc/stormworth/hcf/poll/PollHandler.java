package cc.stormworth.hcf.poll;


import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
public class PollHandler {

  private final MongoCollection<Document> collection = Main.getInstance().getMongoPool()
      .getDatabase(Main.DATABASE_NAME).getCollection("polls");

  private final Map<String, GlobalPoll> globalPolls = Maps.newLinkedHashMap();

  private Poll currentPoll;

  public PollHandler() {
    rCommandHandler.registerPackage(Main.getInstance(), "cc.stormworth.hcf.poll.commands");
    load();
  }

  public void load() {
    globalPolls.clear();

    collection.find().forEach((Consumer<? super Document>) (document) -> {
      String type = document.getString("type");
      GlobalPoll poll = new GlobalPoll(document.get("poll", Document.class));

      globalPolls.put(type, poll);
    });

    if (globalPolls.isEmpty()) {
      globalPolls.put("Knockback",
          new GlobalPoll("Are you comfortable with the", "KB that we currently handle?"));

      globalPolls.put("Cross Pearl",
          new GlobalPoll("Give us your opinion", "about ender pearls"));
    }

    for (Ability ability : Ability.getAbilities()) {
      if (!globalPolls.containsKey(ability.getName())) {
        globalPolls.put(ability.getName(),
            new GlobalPoll("Do you like the " + ability.getName() + " ability?", ""));
      }
    }
  }

  public void save() {
    globalPolls.forEach((key, poll) -> {
      Document document = new Document();
      document.put("type", key);
      document.put("poll", poll.serialize());

      collection.replaceOne(Filters.eq("type", key), document, new ReplaceOptions().upsert(true));
    });
  }

  public GlobalPoll getGlobalPoll(String name) {
    return globalPolls.get(name);
  }

  public GlobalPoll getPoll(String pollName) {
    return globalPolls.get(pollName);
  }
}