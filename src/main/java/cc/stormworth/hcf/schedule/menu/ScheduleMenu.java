package cc.stormworth.hcf.schedule.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.schedule.EventSchedule;
import cc.stormworth.hcf.schedule.Schedule;
import cc.stormworth.hcf.schedule.ScheduleManager;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ScheduleMenu extends Menu {

    private final ScheduleManager scheduleManager;

    @Override
    public String getTitle(Player player) {
        return "&b&lSchedules";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 9 * 3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        int startSlotX = 1;

        for (Schedule schedule : scheduleManager.getSchedules()) {
            buttons.put(getSlot(startSlotX++, 1), new ScheduleButton(schedule));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    public class ScheduleButton extends Button {

        private final Schedule schedule;

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.BOOK);

            builder.name("&6" + schedule.getName());

            List<EventSchedule> events = schedule.getEvents();

            if (events.isEmpty()){
                builder.addToLore("","&cNo events scheduled!");
            }else{
                for (EventSchedule event : events) {

                    String timeLeft = event.getTimeLeft(schedule.getDay());

                    builder.addToLore(
                            "",
                            "&6" + event.getName(),
                            "&7At:&f " + event.getTime(),
                            "&7Time Left: &f" + timeLeft);

                }
            }

            return builder.build();
        }
    }

}
