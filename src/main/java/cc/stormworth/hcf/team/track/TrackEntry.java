package cc.stormworth.hcf.team.track;

import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;


@Getter @Setter
public class TrackEntry {

    public final static DateFormat FORMAT = new SimpleDateFormat("M dd yyyy h:mm a");

    private Team team;
    private TeamActionType type;
    private Map<String, Object> data = Maps.newHashMap();

    public TrackEntry(Document document){
        this.team = Main.getInstance().getTeamHandler().getTeam(document.getString("team_name"));
        this.type = TeamActionType.fromInternalName(document.getString("type"));
        Document data = document.get("data", Document.class);

        this.data.putAll(data);
    }

    public Map<String, Object> getFormattedData(){
        Map<String, Object> formattedData = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : data.entrySet()){
            if (entry.getValue() instanceof Long){

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis((Long) entry.getValue());

                formattedData.put(entry.getKey(), FORMAT.format(calendar.getTime()));
            }else if (entry.getValue() instanceof String){

                if(entry.getKey().contains("Id")){
                    try{
                        UUID uuid = UUID.fromString((String) entry.getValue());

                        formattedData.put(entry.getKey().replace("Id", "Name"), UUIDUtils.name(uuid));
                    }catch (IllegalArgumentException e){
                        formattedData.put(entry.getKey(), entry.getValue());
                    }

                    continue;
                }

                formattedData.put(entry.getKey(), entry.getValue());
            }else{
                formattedData.put(entry.getKey(), entry.getValue());
            }
        }

        return formattedData;
    }
}
