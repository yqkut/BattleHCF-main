package cc.stormworth.hcf.profile.enderchest;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class EnderchestUpgradeMenu extends Menu {

    private final HCFProfile profile;

    @Override
    public String getTitle(Player player) {
        return "Enderchest Upgrades";
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

        buttons.put(getSlot(2, 1), new CapacityButton());

        buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.REDSTONE_TORCH_ON)
                        .name("&6Command Access &7(/ec)")
                        .addToLore(
                                "&7Get access to the enderchest command",
                                "&8&m----------------------------",
                                "&6&l▏ &7Permission&8: " + (profile.getEnderchestUpgrades().isCanUse() ? "&aAccess" : "&cNo permission"),
                                "&6&l▏ &7Cost&8: &a◊ 600",
                                "&8&m----------------------------",
                                "&6Click to purchase!"
                        ).build(), (other) -> {

            if (profile.getEnderchestUpgrades().isCanUse()){
                other.sendMessage(ChatColor.RED + "You already have this upgrade.");
                other.playSound(other.getLocation(), Sound.ANVIL_LAND, 1, 1);
                return;
            }

            if(profile.getGems() < 600) {
                other.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this upgrade.");
                other.playSound(other.getLocation(), Sound.ANVIL_LAND, 1, 1);
                return;
            }

            profile.setGems(profile.getGems() - 600);
            profile.getEnderchestUpgrades().setCanUse(true);
            other.sendMessage(ChatColor.GREEN + "You have successfully purchased an upgrade to your enderchest.");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }));
        buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&6Combat-Tag Bypass &7(Enderchest)")
                        .addToLore(
                                "&7Be able to use your enderchest in combat",
                                "&8&m----------------------------",
                                "&6&l▏ &7Permission&8: " + (profile.getEnderchestUpgrades().isCanUseInCombat() ? "&aAccess" : "&cNo permission"),
                                "&6&l▏ &7Cost&8: &a◊ 800",
                                "&8&m----------------------------",
                                "&6Click to purchase!"
                        ).build(), (other) -> {

            if (profile.getEnderchestUpgrades().isCanUseInCombat()){
                other.sendMessage(ChatColor.RED + "You already have this upgrade.");
                other.playSound(other.getLocation(), Sound.ANVIL_LAND, 1, 1);
                return;
            }

            if(profile.getGems() < 800) {
                other.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this upgrade.");
                other.playSound(other.getLocation(), Sound.ANVIL_LAND, 1, 1);
                return;
            }

            profile.setGems(profile.getGems() - 800);
            profile.getEnderchestUpgrades().setCanUseInCombat(true);
            other.sendMessage(ChatColor.GREEN + "You have successfully purchased an upgrade to your enderchest.");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }));

        return buttons;
    }

    public class CapacityButton extends Button{

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.ENDER_CHEST)
                    .name("&6EnderChest &7(Capacity)")
                    .addToLore(
                            "&7Add more slots to your enderchest",
                            "&8&m----------------------------",
                            "&6&l▏&7Capacity: &f" + profile.getEnderchestUpgrades().getRows() * 9 + " slots",
                            "&8&m----------------------------",
                            "&eClick to open! &7(Left Click)",
                            "&6Click to edit! &7(Right Click)"
                    )
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            if(clickType == ClickType.LEFT) {
                player.closeInventory();
                player.performCommand("ec");
            }else{
                new EnderchestUpgradeCapacityMenu(profile).open(player);
            }
        }
    }
}
