package cc.stormworth.hcf.team.menu.base;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class SelectHeightMenu extends Menu {

    private final CreateBaseMenu parent;

    @Override
    public String getTitle(Player player) {
        return "&eSelect Base Height";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(0, Button.fromItem(new ItemBuilder(Material.BED)
                .name("&cGo Back")
                .build(), e -> parent.openMenu(player)));

        buttons.put(4, Button.fromItem(new ItemBuilder(Material.SPONGE)
                .name("&eHeight &7(Settings)")
                .addToLore(
                        "",
                        "&eMax Height: " + 25,
                        "&eMin Height: " + 4
                ).build()));

        buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.PAINTING)
                .name("&6&lInfo")
                .addToLore(
                        "",
                        "&6➞ &eTotal&f: " + parent.getHeight() + " blocks",
                        "",
                        "&eClick to continue.")
                .build(), other -> parent.openMenu(player)));

        buttons.put(getSlot(2, 1), Button.fromItem(new ItemBuilder(Material.CARPET)
                        .setDurability(5)
                .name("&a&lMore &7(&a+&7)")
                .addToLore(
                        "",
                        "&6➞ &eCurrent&f: &c" + parent.getHeight() + " (Old) &7- &a" + (parent.getHeight() + 1) + " (New)",
                        "",
                        parent.getHeight() + 1 >= 25 ? "&c&lMax Height Reached" : "&eClick to increase height.")
                .build(),
                other -> {
                if(parent.getHeight() + 1 > 25) {
                    player.sendMessage(CC.translate("&c&lMax Height Reached"));
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return;
                }

                parent.setHeight(parent.getHeight() + 1);
        }));

        buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.CARPET)
                        .setDurability(14)
                .name("&c&lLess &7(&c-&7)")
                .addToLore(
                        "",
                        "&6➞ &eCurrent&f: &c" + parent.getHeight() + " (Old) &7- &a" + (parent.getHeight() - 1) + " (New)",
                        "",
                        parent.getHeight() - 1 <= 4 ? "&c&lMin Height Reached" : "&eClick to decrease height.")
                .build(),
                other -> {

                if((parent.getHeight() - 1) < 4){
                    other.sendMessage(CC.translate("&cMin Height Reached"));
                    player.playSound(player.getLocation(), Sound.DIG_GRASS, 1, 1);
                    return;
                }

                parent.setHeight(parent.getHeight() - 1);
        }));

        for (int i = 0; i < 27; i++) {
            if (buttons.containsKey(i)) continue;

            buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .name(" ")
                    .build()));
        }

        return buttons;
    }
}
