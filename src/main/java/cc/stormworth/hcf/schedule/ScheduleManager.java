package cc.stormworth.hcf.schedule;

import cc.stormworth.hcf.Main;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;

import java.util.Calendar;
import java.util.List;

@Getter
public class ScheduleManager {

    private final List<Schedule> schedules = Lists.newArrayList();
    protected final MongoCollection<Document> collection;

    public ScheduleManager(Main plugin){
        this.collection = plugin.getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("schedules");

        loadDefaults();
        //loadSchedules();
    }

    public void loadSchedules() {
        /*for (Document document : collection.find()) {
            Schedule schedule = new Schedule(document);
            schedules.put(schedule.getName(), schedule);
        }*/
    }

    public void loadDefaults(){
        Schedule monday = new Schedule("Monday", Calendar.MONDAY);
        monday.getEvents().add(new EventSchedule("&4&lEclipse", "18:00"));

        Schedule tuesday = new Schedule("Tuesday", Calendar.TUESDAY);
        tuesday.getEvents().add(new EventSchedule("&6&lPumpkin", "18:00"));
        tuesday.getEvents().add(new EventSchedule("&4&lMad", "18:30", "&7Rewards: &f" + (Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250)));

        Schedule wednesday = new Schedule("Wednesday", Calendar.WEDNESDAY);
        wednesday.getEvents().add(new EventSchedule("&4&lOpKeyAll", "18:00"));

        Schedule thursday = new Schedule("Thursday", Calendar.THURSDAY);
        thursday.getEvents().add(new EventSchedule("&4&lConquest", "18:00", "&7Rewards: &f" + (Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250)));

        Schedule friday = new Schedule("Friday", Calendar.FRIDAY);
        friday.getEvents().add(new EventSchedule("&4&lEOTW", "17:00"));
        friday.getEvents().add(new EventSchedule("&a&lSOTW", "18:00"));

        Schedule saturday = new Schedule("Saturday", Calendar.SATURDAY);

        Schedule sunday = new Schedule("Sunday", Calendar.SUNDAY);
        sunday.getEvents().add(new EventSchedule("&b&lKeyAll", "18:00"));

        schedules.add(monday);
        schedules.add(tuesday);
        schedules.add(wednesday);
        schedules.add(thursday);
        schedules.add(friday);
        schedules.add(saturday);
        schedules.add(sunday);
    }

    public void saveSchedule(Schedule schedule) {
        collection.replaceOne(Filters.eq("name", schedule.getName()), schedule.toDocument(), new ReplaceOptions().upsert(true));
    }
}
