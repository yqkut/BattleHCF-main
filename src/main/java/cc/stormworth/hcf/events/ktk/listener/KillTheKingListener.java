package cc.stormworth.hcf.events.ktk.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.ktk.KillTheKing;
import cc.stormworth.hcf.events.ktk.commands.KTKCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class KillTheKingListener implements Listener {

    public KillTheKingListener() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player death = event.getEntity();
        final Player killer = event.getEntity().getKiller();
        if (Main.getInstance().getKillTheKing() != null) {
            final KillTheKing killTheKing = Main.getInstance().getKillTheKing();
            if (killTheKing.getUuid().toString().equalsIgnoreCase(death.getUniqueId().toString())) {
                event.setDroppedExp(0);
                event.getDrops().clear();
                if (killer == null) {
                    Main.getInstance().setKillTheKing(null);
                    if (KTKCommand.killTheKingListener != null) {
                        KTKCommand.killTheKingListener.unload();
                        KTKCommand.killTheKingListener = null;
                    }
                } else {
                    killTheKing.win(killer);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (Main.getInstance().getKillTheKing() != null) {
            final KillTheKing killTheKing = Main.getInstance().getKillTheKing();
            if (killTheKing.getUuid().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (Main.getInstance().getKillTheKing() != null) {
            final KillTheKing killTheKing = Main.getInstance().getKillTheKing();
            if (killTheKing.getUuid().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                player.getOpenInventory().getTopInventory().clear();
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                Main.getInstance().setKillTheKing(null);
                Bukkit.broadcastMessage(ChatColor.GOLD + "[KillTheKing] " + player.getDisplayName() + ChatColor.RED + " has disconnected!");
                if (KTKCommand.killTheKingListener != null) {
                    KTKCommand.killTheKingListener.unload();
                    KTKCommand.killTheKingListener = null;
                }
            }
        }
    }
}