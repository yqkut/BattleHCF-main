package cc.stormworth.hcf.brewingstand.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class BuyMachineMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&e&lBuy Machine";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 3 * 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.BREWING_STAND_ITEM)
                .name("&6Pots Machine")
                .addToLore(
                        "",
                        "&7Purchase this item to ease your potions brewing",
                        "",
                        "",
                        "&ePrice: &a15 Gems",
                        "",
                        "&eClick to purchase!"
                )
                .build(), (other) -> {
            HCFProfile profile = HCFProfile.get(other);

            if (profile.getGems() >= 15){
                profile.setGems(profile.getGems() - 15);
                other.getInventory().addItem(new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .name("&6Pots Machine")
                        .addToLore(
                                "",
                                "&ePlace it on a &6Hopper &eor &6Chest &eto interact!"
                        )
                        .build());
            } else {
                other.sendMessage(CC.translate("&cYou do not have enough gems to purchase this item!"));
            }

        }));

        return buttons;
    }
}
