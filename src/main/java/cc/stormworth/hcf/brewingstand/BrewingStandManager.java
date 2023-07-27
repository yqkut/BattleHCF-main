package cc.stormworth.hcf.brewingstand;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;

import java.util.Map;
import java.util.function.Consumer;

@Getter
public class BrewingStandManager {

    private final Map<Location, BrewingStand> brewingStands = Maps.newHashMap();
    private final MongoCollection<Document> collection = Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("brewing_stands");

    public BrewingStandManager() {
        loadBrewingStands();
    }

    public void saveBrewingStands(){
        brewingStands.values().forEach(brewingStand -> {
            Document document = brewingStand.toDocument();

            collection.replaceOne(Filters.eq("location", CorePlugin.GSON.toJson(brewingStand.getLocation())), document, new ReplaceOptions().upsert(true));
        });
    }

    public void loadBrewingStands(){
        collection.find().forEach((Consumer<? super Document>) document -> {
            BrewingStand brewingStand = new BrewingStand(document);

            if (brewingStand.getBrewingStand() == null) return;

            brewingStands.put(brewingStand.getLocation(), brewingStand);
        });
    }

    public void addBrewingStand(BrewingStand brewingStand){
        brewingStands.put(brewingStand.getLocation(), brewingStand);
    }

    public void removeBrewingStand(BrewingStand brewingStand){
        brewingStands.remove(brewingStand.getLocation());
    }

    public BrewingStand getBrewingStand(Location location){
        return brewingStands.get(location);
    }
}
