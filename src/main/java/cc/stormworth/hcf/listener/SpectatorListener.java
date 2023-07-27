package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import lombok.Getter;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class SpectatorListener implements Listener {

    @Getter
    public static Set<UUID> spectators = new HashSet<>();
    @Getter
    public static Set<UUID> viewing = new HashSet<>();
    private final Map<UUID, Long> toggleVisiblityUsable = new HashMap<>();

    public SpectatorListener() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public static int spectatorsSize() {
        Set<UUID> list = spectators;
        list.removeIf(spectator -> Bukkit.getPlayer(spectator) == null);

        return list.size();
    }

    public static void toggleSpectators(Player player, boolean setitem) {
        if (!viewing.contains(player.getUniqueId())) {
            viewing.add(player.getUniqueId());
            for (UUID uuid : SpectatorListener.spectators) {
                Player spectator = Bukkit.getPlayer(uuid);
                if (spectator != null) {
                    player.showPlayer(spectator);
                }
            }
            CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
            if (setitem)
                player.getInventory().setItem(0, ItemBuilder.of(Material.INK_SACK).name("&cHide Spectators").data((short) 8).build());
            player.sendMessage(CC.YELLOW + "You are now viewing the spectators.");
            return;
        }
        viewing.remove(player.getUniqueId());
        for (UUID uuid : SpectatorListener.spectators) {
            Player spectator = Bukkit.getPlayer(uuid);
            if (spectator != null) {
                player.hidePlayer(spectator);
            }
        }
        CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
        if (setitem)
            player.getInventory().setItem(0, ItemBuilder.of(Material.INK_SACK).name("&aShow Spectators").data((short) 10).build());
        player.sendMessage(CC.YELLOW + "You are now hiding the spectators.");
    }

    public static void enableSpectator(Player player) {
        spectators.add(player.getUniqueId());
        player.getInventory().clear();

        final ItemStack helmet = ItemBuilder.of(Material.STAINED_GLASS).enchant(Enchantment.DURABILITY, 10).build();
        final ItemStack chestplate = ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.WHITE).enchant(Enchantment.DURABILITY, 10).build();
        final ItemStack leggins = ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.WHITE).enchant(Enchantment.DURABILITY, 10).build();
        final ItemStack boots = ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.WHITE).enchant(Enchantment.DURABILITY, 10).build();
        final ItemStack[] pvp = {boots, leggins, chestplate, helmet};
        player.getInventory().setArmorContents(pvp);

        player.sendMessage(new String[]{
                "",
                CC.translate("&cYou are now in &lSpectator Mode"),
                "",
                CC.translate("&cYou can spectate a 150x150 from the spawn."),
                CC.translate("&cand also you will respawn after the"),
                CC.translate("&cEOTW get captured, for give you the opportunity to play the ffa."),
                "",});
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
        player.setMetadata("loggedout", new FixedMetadataValue(Main.getInstance(), true));

        viewing.remove(player.getUniqueId());
        viewing.add(player.getUniqueId());
        player.getInventory().setItem(0, ItemBuilder.of(Material.INK_SACK).name("&aShow Spectators").data((short) 10).build());
        player.getInventory().setItem(4, ItemBuilder.of(Material.RED_ROSE).name("&cYou are spectator").addToLore("&7You will respawn when", "&7the EOTW get captured.").build());
        player.getInventory().setItem(8, ItemBuilder.of(Material.REDSTONE).name("&cLeave to hub").build());
        player.getInventory().setHeldItemSlot(4);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (viewing.contains(online.getUniqueId())) {
                CorePlugin.getInstance().getNametagEngine().reloadOthersFor(online);
                continue;
            }
            online.hidePlayer(player);
        }
        for (UUID uuid : spectators) {
            Player spectator = Bukkit.getPlayer(uuid);
            player.hidePlayer(spectator);
        }
        CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
        CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
    }

    public static void disableSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
        player.removeMetadata("loggedout", Main.getInstance());
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }

        HCFProfile profile = HCFProfile.get(player);

        if (profile.isDeathBanned()) profile.getDeathban().revive(player.getUniqueId());

        CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
        CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
        player.sendMessage(CC.YELLOW + "You have been respawned from the spectator mode.");
    }

    @EventHandler
    public static void onPickup(PlayerPickupItemEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public static void onJoin(PlayerQuitEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        viewing.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public static void onJoin(PlayerJoinEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;

        ItemStack helmet = event.getPlayer().getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.STAINED_GLASS) {
            enableSpectator(event.getPlayer());
        }

        if (!spectators.contains(event.getPlayer().getUniqueId())) {
            for (UUID uuid : SpectatorListener.spectators) {
                Player spectator = Bukkit.getPlayer(uuid);
                if (spectator != null) {
                    event.getPlayer().hidePlayer(spectator);
                }
            }
            return;
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (!spectators.contains(event.getPlayer().getUniqueId())) return;
        int bordersize = 150;
        if (event.getTo().getWorld().equals(event.getFrom().getWorld())) {
            if (!(event.getTo().distance(event.getFrom()) < 0.0D) && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
                if (FastMath.abs(event.getTo().getBlockX()) > bordersize || FastMath.abs(event.getTo().getBlockZ()) > bordersize) {
                    Location newLocation = event.getTo().clone();

                    while (FastMath.abs(newLocation.getX()) > (double) bordersize) {
                        newLocation.setX(newLocation.getX() - (double) (newLocation.getX() > 0.0D ? 1 : -1));
                    }

                    while (FastMath.abs(newLocation.getZ()) > (double) bordersize) {
                        newLocation.setZ(newLocation.getZ() - (double) (newLocation.getZ() > 0.0D ? 1 : -1));
                    }

                    while (newLocation.getBlock().getType() != Material.AIR) {
                        newLocation.setY(newLocation.getBlockY() + 1);
                    }

                    event.setTo(newLocation);
                    event.getPlayer().sendMessage(ChatColor.RED + "That location is past the border.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (!spectators.contains(event.getPlayer().getUniqueId())) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        int bordersize = 150;
        if ((from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) && (FastMath.abs(event.getTo().getBlockX()) > bordersize || FastMath.abs(event.getTo().getBlockZ()) > bordersize)) {
            if (event.getPlayer().getVehicle() != null) {
                event.getPlayer().getVehicle().eject();
            }

            Location newLocation = event.getTo().clone();
            int tries = 0;

            while (FastMath.abs(newLocation.getX()) > (double) bordersize && tries++ < 100) {
                newLocation.setX(newLocation.getX() - (double) (newLocation.getX() > 0.0D ? 1 : -1));
            }

            if (tries >= 99) {
                return;
            }

            tries = 0;

            while (FastMath.abs(newLocation.getZ()) > (double) bordersize && tries++ < 100) {
                newLocation.setZ(newLocation.getZ() - (double) (newLocation.getZ() > 0.0D ? 1 : -1));
            }

            if (tries >= 99) {
                return;
            }

            while (newLocation.getBlock().getType() != Material.AIR) {
                newLocation.setY(newLocation.getBlockY() + 1);
            }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "You have hit the border!");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("core.staff")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot run commands with spectator mode.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getDamager().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getWhoClicked().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getWhoClicked().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (spectators.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().teleport(Main.getInstance().getServerHandler().getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Main.getInstance().getServerHandler().isPreEOTW()) return;
        if (!event.getAction().name().contains("RIGHT")) return;
        if (event.getItem() == null) return;
        if (!spectators.contains(event.getPlayer().getUniqueId())) return;

        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.REDSTONE) {
            player.kickPlayer(CC.RED + "You have been kicked to hub.");
        } else if (event.getItem().getType() == Material.INK_SACK) {
            boolean togglePermitted = toggleVisiblityUsable.getOrDefault(player.getUniqueId(), 0L) < System.currentTimeMillis();

            if (!togglePermitted) {
                player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
                return;
            }

            toggleSpectators(player, true);
            toggleVisiblityUsable.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
        }
        event.setCancelled(true);
    }
}