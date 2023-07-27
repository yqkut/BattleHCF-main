package cc.stormworth.hcf.listener;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class SellAllValuablesSignListener implements Listener {
	
	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		Player player = event.getPlayer();
		
		if (!player.isOp()) return;
		if (!event.getLine(0).equals("&a&lSell All")) return;
		
		event.setLine(0, "");
		event.setLine(1, "Sell All");
		event.setLine(2, "Valuables");
		event.setLine(3, "");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!(event.getClickedBlock().getState() instanceof Sign)) return;
		
		Player player = event.getPlayer();
		Sign sign = (Sign) event.getClickedBlock().getState();
		
		if (!sign.getLine(1).equals("Sell All") || !sign.getLine(2).equals("Valuables")) return;
		
		event.setCancelled(true);
		event.setUseItemInHand(Result.DENY);
		
		player.updateInventory();
		player.performCommand("sellinventory");
	}
}
