package cc.stormworth.hcf.schedule;

import cc.stormworth.core.util.time.TimeUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter @Setter
public class EventSchedule {

    private String name;
    private int hour;
    private int minute;
    private List<String> description = Lists.newArrayList();

    public EventSchedule(String name, String time, String... description){
        this.name = name;
        String[] split = time.split(":");
        this.hour = Integer.parseInt(split[0]);
        this.minute = Integer.parseInt(split[1]);
        this.description.addAll(Arrays.asList(description));
    }


    public EventSchedule(Document document){
        this.name = document.getString("name");
        this.hour = document.getInteger("hour");
        this.minute = document.getInteger("minute");
    }

    public String getTimeLeft(int day){
        Calendar calendar = new GregorianCalendar();

        calendar.setTimeZone(TimeZone.getTimeZone("EST"));

        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        long timeLeft = calendar.getTimeInMillis() - System.currentTimeMillis();

        int currentDay = new GregorianCalendar(TimeZone.getTimeZone("EST")).get(Calendar.DAY_OF_WEEK);

        if(day == 1 && currentDay != 6 && currentDay != 7){
            return "&eEvent has already passed!";
        }else if (currentDay != 1 && currentDay > day)  {
            return "&eEvent has already passed!";
        } else if(timeLeft < 0){
            return "&6Event has already started!";
        }

        return TimeUtil.getMoreDetailedTime(timeLeft);
    }

    public String getTime(){
        boolean isPM = hour > 12;

        return (isPM ? hour - 12 : hour) + ":" + (minute < 10 ? "0" + minute : minute) + (isPM ? "PM" : "AM");
    }

    public String getDayOfWeek(int day){
        if(day == 1){
            return "Sunday";
        } else if(day == 2){
            return "Monday";
        } else if(day == 3){
            return "Tuesday";
        } else if(day == 4){
            return "Wednesday";
        } else if(day == 5){
            return "Thursday";
        } else if(day == 6){
            return "Friday";
        } else if(day == 7){
            return "Saturday";
        } else {
            return "Unknown";
        }
    }

    public Document toDocument(){
        return new Document("name", name)
                .append("hour", hour)
                .append("minute", minute);
    }
}
