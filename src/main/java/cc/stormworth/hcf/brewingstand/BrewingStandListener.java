package cc.stormworth.hcf.brewingstand;

import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.brewingstand.menu.BrewingStandMenu;
import cc.stormworth.hcf.brewingstand.menu.ResourcesMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.InventoryUtil;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class BrewingStandListener implements Listener {

    private final BrewingStandManager brewingStandManager;

    private final ImmutableList<Integer> blockedSlots = ImmutableList.copyOf(Arrays.asList(0, 1, 9, 10, 18, 19));

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        for (BlockState block : event.getChunk().getTileEntities()){
            if (block instanceof org.bukkit.block.BrewingStand){
                BrewingStand brewingStand = brewingStandManager.getBrewingStand(block.getLocation());

                if (brewingStand != null){
                    brewingStand.setActive(true);
                }

            }
        }
    }

    @EventHandler
    public void onChunkUnLoad(ChunkUnloadEvent event){
        for (BlockState block : event.getChunk().getTileEntities()){
            if (block instanceof org.bukkit.block.BrewingStand){
                BrewingStand brewingStand = brewingStandManager.getBrewingStand(block.getLocation());

                if (brewingStand != null){
                    brewingStand.setActive(false);
                }

            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory openInventory = player.getOpenInventory().getTopInventory();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory playerInventory = player.getInventory();

        if (openInventory == null) return;

        if (!openInventory.getTitle().equalsIgnoreCase("Resources")) {
            return;
        }

        int slot = event.getSlot();

        HCFProfile profile = HCFProfile.get(player);
        BrewingStand brewingStand = profile.getOpenBrewingStand();

        ItemStack resource = openInventory.getItem(0);

        if (clickedInventory == openInventory) {
            if (blockedSlots.contains(slot)) {

                if (slot == InventoryUtil.getSlot(0, 2)) {
                    new ResourcesMenu(brewingStand).open(player);
                }

                event.setCancelled(true);
            }
        } else {
            if (clickedInventory == playerInventory) {

                if (event.getCurrentItem() == null) {
                    event.setCancelled(true);
                    return;
                }

                if (resource.getType() != event.getCurrentItem().getType()) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        Inventory inventory = event.getInventory();

        if (inventory == null || inventory == player.getInventory()) return;

        HCFProfile profile = HCFProfile.get(player);

        BrewingStand brewingStand = profile.getOpenBrewingStand();

        if (brewingStand == null) return;

        if (inventory.getTitle().equalsIgnoreCase("Resources")) {

            ItemStack itemStack = inventory.getItem(0);

            if (!brewingStand.getResources().containsKey(itemStack.getType())) {
                brewingStand.getResources().put(itemStack.getType(), new CopyOnWriteArrayList<>());
            }

            List<ItemStack> resources = brewingStand.getResources().get(itemStack.getType());

            resources.clear();

            for (int i = 0; i < 27; i++) {

                if (blockedSlots.contains(i)) continue;

                ItemStack item = inventory.getItem(i);

                if (item != null) {
                    resources.add(item);
                }
            }
        }
        if (inventory.getTitle().equalsIgnoreCase("Glass Bottles")) {

            brewingStand.getResources().put(Material.POTION, new CopyOnWriteArrayList<>());

            for (ItemStack item : inventory.getContents()) {
                if (item == null) {
                    continue;
                }

                if (item.getType() == Material.POTION) {
                    brewingStand.getResources().get(Material.POTION).add(item);
                }
            }
        }

        if (!Menu.currentlyOpenedMenus.containsKey(player.getName())) {
            profile.setOpenBrewingStand(null);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (block.getType() != Material.BREWING_STAND) {
            return;
        }

        ItemStack item = event.getItem();

        if (item != null && player.isSneaking()){

            if (!item.getType().isBlock()){
                return;
            }

            //Check if can place block...
            if (block.getRelative(event.getBlockFace()).getType() == Material.AIR){
                return;
            }
        }

        HCFProfile profile = HCFProfile.get(player);
        BrewingStand brewingStand = brewingStandManager.getBrewingStand(block.getLocation());

        if (brewingStand == null) {
            return;
        }

        event.setCancelled(true);

        profile.setOpenBrewingStand(brewingStand);

        new BrewingStandMenu(brewingStand).open(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBuild(BlockPlaceEvent event){

        if (event.isCancelled()) return;

        Block block = event.getBlock();

        ItemStack item = event.getItemInHand();

        if (item == null) return;

        if (item.getType() != Material.BREWING_STAND_ITEM) return;

        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        if (!meta.hasDisplayName()) return;

        if (!meta.getDisplayName().equalsIgnoreCase("§6Pots Machine")) return;

        List<String> lore = meta.getLore();

        if (lore == null) return;

        if (lore.size() != 2) return;

        if (!lore.get(0).equalsIgnoreCase("") &&
                !lore.get(1).equalsIgnoreCase("§ePlace it on a §6Hopper &eor §6Chest §eto interact!")) return;

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE &&
                (team == null || !team.isMember(event.getPlayer().getUniqueId()))) {
            return;
        }

        BrewingStand brewingStand = brewingStandManager.getBrewingStand(block.getLocation());

        if (brewingStand == null) {
            brewingStand = new BrewingStand(block.getLocation());

            brewingStand.setBrewingStand((org.bukkit.block.BrewingStand) block.getState());

            brewingStandManager.addBrewingStand(brewingStand);
        }
    }
}
