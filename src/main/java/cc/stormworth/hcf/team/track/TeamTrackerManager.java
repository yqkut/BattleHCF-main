package cc.stormworth.hcf.team.track;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class TeamTrackerManager {
    private final static MongoCollection<Document> collection = Main.getInstance().getMongoPool()
            .getDatabase(Main.DATABASE_NAME).getCollection("team_trackers");

    public static void logAsync(Team team, TeamActionType type, Map<String, Object> data){

        if(team == null){
            return;
        }

        TaskUtil.runAsync(Main.getInstance(), () -> {
            Document document = new Document();

            document.put("team_id", team.getUniqueId().toString());
            document.put("team_name", team.getName());
            document.put("type", type.getInternalName());

            Document dataDocument = new Document();

            if(data != null && !data.isEmpty()){
                dataDocument.putAll(data);
            }

            document.put("data", dataDocument);

            collection.insertOne(document);
        });
    }


    public static List<TrackEntry> loadEntry(Team team, TeamActionType type){
        return loadEntry(team, type, Lists.newArrayList());
    }

    public static List<TrackEntry> loadEntry(Team team, TeamActionType type, List<TrackEntry> entries){
        List<Document> documents = collection.find(
                Filters.and(
                        Filters.eq("team_id", team.getUniqueId().toString()),
                        Filters.eq("type", type.getInternalName())))
                .sort(new Document("date", -1))
                .into(Lists.newArrayList());

        for(Document document : documents){
            entries.add(new TrackEntry(document));
        }

        return entries;
    }



}
