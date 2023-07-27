package cc.stormworth.hcf.util.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

  public String formatDate(long time, char firstColor, char secondColor) {
    SimpleDateFormat day = new SimpleDateFormat("dd");
    SimpleDateFormat month = new SimpleDateFormat("MM");
    SimpleDateFormat year = new SimpleDateFormat("yyyy");

    Date date = new Date(time);

    return "&" + firstColor + day.format(date) + "&" + secondColor + "/" +
        "&" + firstColor + month.format(date) + "&" + secondColor + "/" +
        "&" + firstColor + year.format(date);
  }
}