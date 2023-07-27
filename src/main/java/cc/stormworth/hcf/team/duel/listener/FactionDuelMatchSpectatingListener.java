package cc.stormworth.hcf.team.duel.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;
import cc.stormworth.hcf.util.player.Players;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class FactionDuelMatchSpectatingListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isSpectating(player)) {
			return;
		}
		
		match.getSpectators().remove(player);
		
		if (!match.getParticipantsCache().containsKey(player.getUniqueId())) {
			Players.reset(player, player.getGameMode(), false);
			
			match.getPlayersStuffCache().remove(player.getUniqueId()).restore(player);
			
			player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.isSpectating(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (this.isSpectating(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (this.isSpectating(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (this.isSpectating(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (this.isSpectating((Player) event.getWhoClicked())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (this.isSpectating(player)) {
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
			player.updateInventory();
		}
	}
	
	private boolean isSpectating(Player player) {
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isSpectating(player)) {
			return false;
		}
		
		return true;
	}
}
