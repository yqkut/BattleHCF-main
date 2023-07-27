package cc.stormworth.hcf.misc.payout.menu;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.Maps;

import cc.stormworth.core.kt.util.Callback;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class ConfirmationMenu extends Menu {

	private final Callback<Boolean> callback;
	
	@Override
	public String getTitle(Player player) {
		return "Are you sure?";
	}
	
	@Override
	public int size(Map<Integer, Button> buttons) {
		return 3 * 9;
	}
	
	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = Maps.newHashMap();
		
		buttons.put(11, new OptionButton(true));
		buttons.put(15, new OptionButton(false));
		
		return buttons;
	}
	
	@AllArgsConstructor
	final class OptionButton extends Button {
		
		private final boolean result;
		
		@Override
		public Material getMaterial(Player player) {
			return Material.WOOL;
		}
		
		@Override
		public byte getDamageValue(Player player) {
			return (byte) (this.result ? 5 : 14);
		}
		
		@Override
		public String getName(Player player) {
			return this.result ? "&aYes" : "&cNo";
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			callback.callback(this.result);
			
			new PayoutsMenu().open(player);
		}
	}
}
