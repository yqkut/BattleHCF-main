package cc.stormworth.hcf.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.player.MaterialUtils;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class AntiGlitchListener implements Listener {

    @EventHandler
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;
        final Block block = event.getRetractLocation().getBlock();
        if (block.getType().name().contains("ORE") || block.getType().name().contains("RAIL")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks()) {
            if (block.getType().name().contains("ORE") || block.getType().name().contains("RAIL")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /*@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleExit(final VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getExited();
        Location location = player.getLocation();
        while (location.getBlock().getType().isSolid()) {
            location.add(0.0, 1.0, 0.0);
            if (location.getBlockY() == 255) {
                break;
            }
        }
        while (location.getBlock().getType().isSolid()) {
            location.subtract(0.0, 1.0, 0.0);
            if (location.getBlockY() == 1) {
                break;
            }
        }
        player.teleport(location);
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Horse || event.getVehicle() instanceof Minecart) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }
        if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You aren't allowed to place mob spawners in the nether.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountClipping(final VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getExited();
        Location pLoc = player.getLocation();
        Location vLoc = event.getVehicle().getLocation();
        if (player.getLocation().getY() > 250.0) {
            pLoc.add(0.0, 10.0, 0.0);
        } else if (!MaterialUtils.isFullBlock(vLoc.add(0.0, 1.0, 0.0).getBlock().getType())) {
            if (!MaterialUtils.isFullBlock(vLoc.getBlock().getType())) {
                pLoc = new Location(vLoc.getWorld(), vLoc.getBlockX() + 0.5, vLoc.getBlockY(), vLoc.getBlockZ() + 0.5, pLoc.getYaw(), pLoc.getPitch());
            } else {
                pLoc.subtract(0.0, 1.0, 0.0);
            }
        }
        Location finalLocation = pLoc;
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.teleport(finalLocation));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnHorse(final CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.HORSE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDeathEvent(final PlayerDeathEvent event) {
        if (event.getEntity().isInsideVehicle()) {
            event.getEntity().getVehicle().remove();
        }
    }

    @EventHandler
    public void onExplosion(final ExplosionPrimeEvent event) {
        if (event.getEntityType() == EntityType.MINECART_TNT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMinecartSpawn(final EntitySpawnEvent event) {
        if (event.getEntityType().name().contains("MINECART")) {
            event.setCancelled(true);
        }
    }


    @Getter private static final Map<UUID, Long> blockGlitch = Maps.newHashMap();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        Team team = LandBoard.getInstance().getTeam(player.getLocation());

        if(team != null && team.getMembers().contains(player.getUniqueId())){
            return;
        }

        if(team == null || team.isRaidable()){
            return;
        }

        blockGlitch.put(player.getUniqueId(), System.currentTimeMillis() + 1000 + ((CraftPlayer) player).getHandle().ping);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){

        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();

            if(player.getGameMode() == GameMode.CREATIVE){
                return;
            }

            if(blockGlitch.containsKey(player.getUniqueId()) && blockGlitch.get(player.getUniqueId()) >= System.currentTimeMillis()){
                event.setCancelled(true);
                blockGlitch.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if(block == null){
            return;
        }

        if(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
            if(blockGlitch.containsKey(player.getUniqueId()) && blockGlitch.get(player.getUniqueId()) >= System.currentTimeMillis()){
                event.setCancelled(true);
                blockGlitch.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        Inventory inventory = player.getOpenInventory().getTopInventory();

        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) {
            return;
        }

        if (inventory.getType() == InventoryType.ANVIL){
            System.out.println("Anvil");
            Ability ability = Ability.getByItem(itemStack);

            if(ability != null){
                event.setCancelled(true);
            }
        }
    }
}