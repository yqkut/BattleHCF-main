package cc.stormworth.hcf.team.menu.base;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class SelectBaseColorMenu extends Menu {

    private final CreateBaseMenu parent;


    @Override
    public String getTitle(Player player) {
        return "&eSelect Base Color";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
                .name("&cGo Back")
                .build(), e -> parent.openMenu(player)));

        AtomicInteger startSlot = new AtomicInteger(10);

        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).forEach(i -> {

            if (!isBorder(startSlot.get())){
                buttons.put(startSlot.get(), new Button() {

                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.STAINED_GLASS)
                                .setDurability(i)
                                .name("&e" + getColorName(i))
                                .addToLore(
                                        "",
                                        "&eClick to select this color."
                                ).build();
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        parent.setColor(i);
                        player.closeInventory();

                        parent.openMenu(player);
                    }
                });
            }

            startSlot.incrementAndGet();
        });

        for (int i = 0; i < 36; i++) {
            if (buttons.containsKey(i)) continue;

            buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .name(" ")
                    .build()));
        }


        return buttons;
    }

    public String getColorName(int color){
        switch(color){
            case 0:
                return ChatColor.WHITE + "White";
            case 1:
                return ChatColor.GOLD + "Orange";
            case 2:
                return ChatColor.DARK_PURPLE + "Magenta";
            case 3:
                return ChatColor.AQUA + "Light Blue";
            case 4:
                return ChatColor.YELLOW + "Yellow";
            case 5:
                return ChatColor.GREEN + "Lime";
            case 6:
                return ChatColor.LIGHT_PURPLE + "Pink";
            case 7:
                return ChatColor.DARK_GRAY + "Gray";
            case 8:
                return ChatColor.GRAY + "Light Gray";
            case 9:
                return ChatColor.BLUE + "Cyan";
            case 10:
                return ChatColor.DARK_PURPLE + "Purple";
            case 11:
                return ChatColor.DARK_BLUE + "Blue";
            case 12:
                return ChatColor.GOLD + "Brown";
            case 13:
                return ChatColor.DARK_GREEN + "Green";
            case 14:
                return ChatColor.RED + "Red";
            case 15:
                return ChatColor.BLACK + "Black";
            default:
                return null;
        }
    }

    private boolean isBorder(int slot) {
        return slot % 9 == 0 || slot % 9 == 8;
    }
}
