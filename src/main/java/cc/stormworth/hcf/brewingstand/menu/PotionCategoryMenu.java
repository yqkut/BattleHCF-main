package cc.stormworth.hcf.brewingstand.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.brewingstand.BrewingStand;
import cc.stormworth.hcf.brewingstand.PotionCategory;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class PotionCategoryMenu extends Menu {

    private final BrewingStand brewingStand;

    {
        setAutoUpdate(true);
    }

    @Override
    public void onOpen(Player player) {
        HCFProfile profile = HCFProfile.get(player);

        profile.setOpenBrewingStand(brewingStand);
    }

    @Override
    public String getTitle(Player player) {
        return "&eSelect a category";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 3 * 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(8, Button.fromItem(new ItemBuilder(Material.BED).name("&cGo Back!").build(), other -> {
            new BrewingStandMenu(brewingStand).openMenu(player);
        }));

        int startSlot = 10;

        for (PotionCategory potionCategory : PotionCategory.values()) {
            buttons.put(startSlot,
                    Button.fromItem(new ItemBuilder(potionCategory.getItemStack().clone()).addToLore("", "&eClick to choose!").build(),
                    other -> {
                        brewingStand.setActiveCategory(potionCategory);
                        new BrewingStandMenu(brewingStand).openMenu(player);
                    }));
            startSlot++;
        }

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
