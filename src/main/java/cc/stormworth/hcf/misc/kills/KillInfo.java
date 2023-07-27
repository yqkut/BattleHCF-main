package cc.stormworth.hcf.misc.kills;

import cc.stormworth.core.CorePlugin;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

@Getter
public class KillInfo {

  public final static DateFormat FORMAT = new SimpleDateFormat("M dd yyyy h:mm a");

  private final UUID killer;
  private final UUID victim;
  private final ItemStack item;
  private final long date;

  public KillInfo(UUID killer, UUID victim, ItemStack item, long date) {
    this.killer = killer;
    this.victim = victim;
    this.item = item;
    this.date = date;
  }

  public KillInfo(Document document) {
    this.killer = UUID.fromString(document.getString("killer"));
    this.victim = UUID.fromString(document.getString("victim"));
    this.item = CorePlugin.GSON.fromJson(document.getString("item"), ItemStack.class);
    this.date = document.getLong("date");
  }

  public String getFormattedDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(date);
    return FORMAT.format(calendar.getTime());
  }

}