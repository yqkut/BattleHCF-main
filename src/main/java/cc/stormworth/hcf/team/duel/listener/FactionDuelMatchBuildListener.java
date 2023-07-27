package cc.stormworth.hcf.team.duel.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public final class FactionDuelMatchBuildListener implements Listener {

	private final Set<Material> NON_CLICKABLE_BLOCKS = ImmutableSet.of(
			Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.HOPPER, Material.ANVIL, Material.ENDER_CHEST, 
			Material.IRON_DOOR, Material.WOOD_DOOR, Material.TRAP_DOOR, Material.FENCE_GATE, Material.WOOD_BUTTON, Material.STONE_BUTTON
			);
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.isInWar(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (this.isInWar(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!this.isInWar(player)) {
			return;
		}
		
		if (!this.NON_CLICKABLE_BLOCKS.contains(event.getClickedBlock().getType())) {
			return;
		}
		
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		
		player.updateInventory();
	}
	
	private boolean isInWar(Player player) {
		FactionDuelMatch match = Main.getInstance().getFactionDuelManager().getMatch(player);
		
		if (match == null || !match.isAlive(player)) {
			return false;
		}
		
		return true;
	}
}
