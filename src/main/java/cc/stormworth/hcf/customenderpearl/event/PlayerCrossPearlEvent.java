package cc.stormworth.hcf.customenderpearl.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerCrossPearlEvent extends PlayerEvent implements Cancellable {

   private Location to;

   private boolean cancelled;

   private Location from;

   private static final HandlerList handlers = new HandlerList();

   public void setTo(Location location) {
      this.to = location;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public Location getTo() {
      return this.to;
   }

   public Location getFrom() {
      return this.from;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   public void setFrom(Location location) {
      this.to = location;
   }

   public PlayerCrossPearlEvent(Player player, Location from, Location to) {
      super(player);
      this.from = from;
      this.to = to;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
