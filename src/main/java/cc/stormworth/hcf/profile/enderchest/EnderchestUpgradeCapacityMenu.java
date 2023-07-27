package cc.stormworth.hcf.profile.enderchest;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.menu.buttons.BackButton;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class EnderchestUpgradeCapacityMenu extends Menu {
    private final HCFProfile profile;

    @Override
    public String getTitle(Player player) {
        return "&7Upgrade Capacity";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ");

        for (int i = 0; i < 9; i++) {
            buttons.put(getSlot(i, 0), Button.fromItem(glass.data((short) 7).build()));
        }

        for (int i = 0; i < 9; i++) {
            buttons.put(getSlot(i, 2), Button.fromItem(glass.data((short) 7).build()));
        }

        for (int i = 0; i < 2; i++) {
            buttons.put(getSlot(0, i), Button.fromItem(glass.data((short) 15).build()));
        }

        for (int i = 0; i < 2; i++) {
            buttons.put(getSlot(8, i), Button.fromItem(glass.data((short) 15).build()));
        }

        buttons.put(getSlot(1, 1), new BackButton(new EnderchestUpgradeMenu(profile)));

        buttons.put(getSlot(3, 1), Button.fromItem(new ItemBuilder(Material.ENDER_CHEST)
                .name("&6Enderchest &7(Current)")
                .addToLore(
                        "&7Add more slots to your enderchest",
                        "&8&m----------------------------",
                        "&6&l▏ &7Capacity: &f" + profile.getEnderchestUpgrades().getRows() * 9 + " slots",
                        "&8&m----------------------------"
                )
                .build()
        ));

        buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.EXP_BOTTLE)
                .name("&6Upgrade &7(Capacity)")
                .addToLore(
                        "&7Improvide your enderchest with gems",
                        "&8&m----------------------------",
                        "&6&l▏ &7Cost: &a◊ 20",
                        "&6&l▏ &7Capacity: &c" + (profile.getEnderchestUpgrades().getRows() == 3 ? "&aMax" : profile.getEnderchestUpgrades().getRows() * 9 + " &8⟶ &a" + (profile.getEnderchestUpgrades().getRows() * 9 + 9)),
                        "&8&m----------------------------",
                        "&6Click to purchase!"
                )
                .build(),(other) -> {

            if (profile.getEnderchestUpgrades().getRows() == 3){
                other.sendMessage(ChatColor.RED + "You have reached the maximum capacity of your enderchest!");
                other.playSound(other.getLocation(), Sound.ANVIL_LAND, 1, 1);
                return;
            }

            if(profile.getGems() < 20) {
                other.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this upgrade.");
                other.playSound(other.getLocation(), Sound.ANVIL_LAND, 1, 1);
                return;
            }

            profile.setGems(profile.getGems() - 20);
            profile.getEnderchestUpgrades().setRows(profile.getEnderchestUpgrades().getRows() + 1);
            other.sendMessage(ChatColor.GREEN + "You have successfully purchased an upgrade to your enderchest.");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }
        ));

        return buttons;
    }
}
