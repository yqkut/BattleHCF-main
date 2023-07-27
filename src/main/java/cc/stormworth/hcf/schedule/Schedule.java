package cc.stormworth.hcf.schedule;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class Schedule {

    private String name;
    private int day;
    private List<EventSchedule> events = Lists.newArrayList();

    public Schedule(String name, int day){
        this.name = name;
        this.day = day;
    }

    public Schedule(Document document){
        this.name = document.getString("name");
        this.events = document.getList("events", Document.class).stream().map(EventSchedule::new).collect(Collectors.toList());
        this.day = document.getInteger("day");
    }

    public Document toDocument(){
        return new Document("name", name)
                .append("events", events.stream().map(EventSchedule::toDocument).collect(Collectors.toList()))
                .append("day", day);
    }
}
