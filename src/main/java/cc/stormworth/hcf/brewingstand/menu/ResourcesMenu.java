package cc.stormworth.hcf.brewingstand.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.brewingstand.BrewingStand;
import cc.stormworth.hcf.brewingstand.PotionCategory;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.InventoryUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ResourcesMenu extends Menu {

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
        return "&eResources";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 3 * 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(8, Button.fromItem(new ItemBuilder(Material.BED).name("&cGo Back!").build(), other ->
                new BrewingStandMenu(brewingStand).openMenu(player)));

        buttons.put(2,
                Button.fromItem(new ItemBuilder(Material.NETHER_STALK).addToLore("", "&eClick to add!").build(),
                        other -> brewingStand.openInventoryOf(player, Material.NETHER_STALK)));

        if (brewingStand.getActiveCategory() == null){
            return buttons;
        }

        PotionCategory category = brewingStand.getActiveCategory();

        List<String> lore = Lists.newArrayList();

        int startSlot = 3;

        for (Material resource : category.getResources()){

            if (resource != Material.NETHER_STALK){
                buttons.put(startSlot,
                        Button.fromItem(new ItemBuilder(resource).addToLore("", "&eClick to add!").build(),
                                other -> brewingStand.openInventoryOf(player, resource)));
            }

            if (brewingStand.getAmountOf(resource) <= 0){
                lore.add("&e&l➞ &c" + StringUtils.capitalize(resource.name().toLowerCase().replace("_", " ")) + " &7(&c✘&7) &8/ &e" + brewingStand.getAmountOf(resource) + " Total.");
            } else {
                lore.add("&e&l➞ &a" + StringUtils.capitalize(resource.name().toLowerCase().replace("_", " ")) + " &7(&a✔&7) &8/ &e" + brewingStand.getAmountOf(resource) + " Total.");
            }

            startSlot++;
        }

        buttons.put(InventoryUtil.getSlot(4, 2), Button.fromItem(new ItemBuilder(category.getItemStack().clone())
                .setLore(lore)
                .build()));

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
