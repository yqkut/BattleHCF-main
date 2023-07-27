package cc.stormworth.hcf.profile.enderchest.listeners;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.enderchest.EnderchestUpgrades;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderchestListener implements Listener {

    @EventHandler
    public void onOpenChest(InventoryOpenEvent event){
        Inventory inventory = event.getView().getTopInventory();

        Player player = (Player) event.getPlayer();

        if (inventory.getTitle().equals(player.getEnderChest().getTitle())){
            EnderchestUpgrades enderchestUpgrades = HCFProfile.get(player).getEnderchestUpgrades();

            if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().isBelow(Rank.HERO)){
                if (enderchestUpgrades.getRows() < 2){
                    for (int i = 9; i < 27; i++) {
                        inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14)
                                .name(" ")
                                        .addToLore(
                                                "&7Add more slots in: ",
                                                "&6/enderchestupgrade"
                                        )
                                .build());
                    }
                }else if (enderchestUpgrades.getRows() < 3){
                    for (int i = 18; i < 27; i++) {
                        inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14)
                                .name(" ")
                                .addToLore(
                                        "&7Add more slots in: ",
                                        "&6/enderchestupgrade"
                                )
                                .build());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Inventory inventory = event.getView().getTopInventory();

        Player player = (Player) event.getPlayer();

        if (inventory.getTitle().equals(player.getEnderChest().getTitle())){
            EnderchestUpgrades enderchestUpgrades = HCFProfile.get(player).getEnderchestUpgrades();
            if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().isBelow(Rank.HERO)) {
                if (enderchestUpgrades.getRows() < 2) {
                    for (int i = 9; i < 27; i++) {
                        inventory.setItem(i, new ItemStack(Material.AIR));
                    }
                } else if (enderchestUpgrades.getRows() < 3) {
                    for (int i = 18; i < 27; i++) {
                        inventory.setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();

        if(inventory == null){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (inventory.getTitle().equals(player.getEnderChest().getTitle())) {
            EnderchestUpgrades enderchestUpgrades = HCFProfile.get(player).getEnderchestUpgrades();

            if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().isBelow(Rank.HERO)) {
                if (enderchestUpgrades.getRows() < 2) {
                    if (event.getSlot() >= 9) {
                        event.setCancelled(true);
                    }
                } else if (enderchestUpgrades.getRows() < 3) {
                    if (event.getSlot() >= 18 ) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
