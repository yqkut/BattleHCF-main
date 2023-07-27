package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;

public class BorderListener implements Listener {

    public static HashMap<String, Integer> borders = new HashMap<>();

    public BorderListener() {
        updateBorders();
    }

    public static void updateBorders() {
        borders.clear();
        for (World world : Bukkit.getWorlds()) {
            if (borders.containsKey(world.getName())) continue;
            borders.put(world.getName(), CorePlugin.getInstance().getConfigFile().getConfig().getInt("borders." + world.getName()));
        }
    }

    public static void setBorder(String world, Integer border) {
        borders.put(world, border);
        CorePlugin.getInstance().getConfigFile().getConfig().set("borders." + world, border);
        CorePlugin.getInstance().getConfigFile().save();
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!borders.containsKey(event.getPlayer().getWorld().getName())) return;
        int bordersize = borders.get(event.getPlayer().getWorld().getName());
        if (FastMath.abs(event.getBlock().getX()) > bordersize || FastMath.abs(event.getBlock().getZ()) > bordersize) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!borders.containsKey(event.getPlayer().getWorld().getName())) return;
        int bordersize = borders.get(event.getPlayer().getWorld().getName());
        if (FastMath.abs(event.getBlock().getX()) > bordersize || FastMath.abs(event.getBlock().getZ()) > bordersize) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.isCancelled() || !borders.containsKey(event.getPlayer().getWorld().getName()) || event.getTo() == null)
            return;
        int bordersize = borders.get(event.getPlayer().getWorld().getName());
        if (FastMath.abs(event.getTo().getBlockX()) > bordersize || FastMath.abs(event.getTo().getBlockZ()) > bordersize) {
            Location newLocation = event.getTo().clone();

            while (FastMath.abs(newLocation.getX()) > bordersize) {
                newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
            }

            while (FastMath.abs(newLocation.getZ()) > bordersize) {
                newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
            }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "That portal's location is past the border. It has been moved inwards.");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!borders.containsKey(event.getPlayer().getWorld().getName())) return;
        int bordersize = borders.get(event.getPlayer().getWorld().getName());
        if (!event.getTo().getWorld().equals(event.getFrom().getWorld())) {
            return;
        }

        if (event.getTo().distance(event.getFrom()) < 0 || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            return;
        }

        if (FastMath.abs(event.getTo().getBlockX()) > bordersize || FastMath.abs(event.getTo().getBlockZ()) > bordersize) {
            Location newLocation = event.getTo().clone();

            while (FastMath.abs(newLocation.getX()) > bordersize) {
                newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
            }

            while (FastMath.abs(newLocation.getZ()) > bordersize) {
                newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
            }

            while (newLocation.getBlock().getType() != Material.AIR) {
                newLocation.setY(newLocation.getBlockY() + 1);
            }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "That location is past the border.");
        }
    }

    private boolean isInsideBorder(Location location) {
        if (!borders.containsKey(location.getWorld().getName())) return false;
        int bordersize = borders.get(location.getWorld().getName());
        return FastMath.abs(location.getBlockX()) <= bordersize && FastMath.abs(location.getBlockZ()) <= bordersize;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (this.isInsideBorder(event.getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBorderBlockPlace(BlockPlaceEvent event) {
        if (this.isInsideBorder(event.getBlock().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBorderBlockBreak(BlockBreakEvent event) {
        if (this.isInsideBorder(event.getBlock().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketFillEvent event) {
        if (this.isInsideBorder(event.getBlockClicked().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (this.isInsideBorder(event.getBlockClicked().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (!borders.containsKey(event.getPlayer().getWorld().getName())) return;
        int bordersize = borders.get(event.getPlayer().getWorld().getName());

        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) { // || from.getBlockY() != to.getBlockY()
            if (FastMath.abs(event.getTo().getBlockX()) > bordersize || FastMath.abs(event.getTo().getBlockZ()) > bordersize) {
                if (event.getPlayer().getVehicle() != null) {
                    event.getPlayer().getVehicle().eject();
                }

                Location newLocation = event.getTo().clone();
                int tries = 0;

                while (FastMath.abs(newLocation.getX()) > bordersize && tries++ < 50) {
                    newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
                }

                if (tries >= 49) {
                    //CorePlugin.getInstance().getLogger().severe("The server would have crashed while doing border checks! New X: " + newLocation.getX() + ", Old X: " + event.getTo().getBlockX());
                    return;
                }

                tries = 0;

                while (FastMath.abs(newLocation.getZ()) > bordersize && tries++ < 50) {
                    newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
                }

                if (tries >= 49) {
                    //CorePlugin.getInstance().getLogger().severe("The server would have crashed while doing border checks! New Z: " + newLocation.getZ() + ", Old Z: " + event.getTo().getBlockZ());
                    return;
                }

                while (newLocation.getBlock().getType() != Material.AIR) {
                    newLocation.setY(newLocation.getBlockY() + 1);
                }

                event.setTo(newLocation);
                event.getPlayer().sendMessage(ChatColor.RED + "You have hit the border!");
            }
        }
    }
}
