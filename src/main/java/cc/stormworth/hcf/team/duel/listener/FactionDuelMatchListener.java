package cc.stormworth.hcf.team.duel.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.util.Task;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatch.FactionDuelMatchState;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class FactionDuelMatchListener implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isAlive(player)) {
			return;
		}

		event.setDeathMessage(null);
		
		Location location = player.getLocation().clone();
		
		Task.runLater(() -> {
//			player.spigot().respawn();
			player.teleport(location.add(0.0D, 0.25D, 0.0D));
			
			match.setDead(player);
			match.tryFinish();
		}, 1L);
		
		Task.runLater(() -> event.getDrops().clear(), 200L);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isAlive(player)) {
			return;
		}
		
		match.setDead(player);
		match.tryFinish();
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isAlive(player)) {
			return;
		}
		
		if (match.getState() == FactionDuelMatchState.STARTING) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		this.onEntityDamage(event);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isAlive(player)) {
			return;
		}
		
		match.getDroppedItemsCache().add(event.getItemDrop());
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isAlive(player)) {
			return;
		}
		
		Item item = event.getItem();
		
		if (match.getDroppedItemsCache().contains(item)) {
			match.getDroppedItemsCache().remove(item);
		}
	}
}
