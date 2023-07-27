package cc.stormworth.hcf.misc.payout.menu;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.payout.Payout;
import lombok.AllArgsConstructor;

public final class PayoutsMenu extends PaginatedMenu {

	{
		this.setAutoUpdate(true);
	}
	
	@Override
	public String getPrePaginatedTitle(Player player) {
		return "&6&lPayouts";
	}
	
	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = Maps.newHashMap();
		List<Payout> payouts = Main.getInstance().getPayoutManager().getPayouts();
		
		for (int i = 0; i < payouts.size(); i++) {
			buttons.put(buttons.size(), new PayoutButton(payouts.get(i), i + 1));
		}
		
		return buttons;
	}
	
	@AllArgsConstructor
	final class PayoutButton extends Button {
		
		private final Payout payout;
		private final int position;
		
		@Override
		public Material getMaterial(Player player) {
			return Material.WOOL;
		}
		
		@Override
		public byte getDamageValue(Player player) {
			return (byte) (this.payout.isPayed() ? 5 : 14);
		}
		
		@Override
		public String getName(Player player) {
			return "&6&l#" + this.position + " &7- &e" + this.payout.getCreatedOn();
		}
		
		@Override
		public List<String> getDescription(Player player) {
			List<String> lore = Lists.newArrayList();
			
			lore.add("&7&m----------------------");
			lore.add("&6&l► &fType: &6" + this.payout.getType());
			lore.add("&6&l► &fFaction: &6" + this.payout.getFaction());
			lore.add("&6&l► &fFaction Leader: &6" + this.payout.getFactionLeader());
			lore.add("&6&l► &fPayed: " + (this.payout.isPayed() ? "&aYes" : "&cNo"));
			
			if (this.payout.isPayed()) {
				lore.add("&6&l► &fPayed On: &6" + this.payout.getPayedOn());
			} else {
				lore.add("");
				lore.add("&aClick to mark as payed.");
			}
			
			lore.add("&7&m----------------------");
			
			return lore;
		}
		
		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (this.payout.isPayed()) return;
			
			new ConfirmationMenu(result -> {
				if (!result) return;
				
				this.payout.setPayedOn(TimeUtil.dateToString(new Date()));
				
				TaskUtil.runAsync(Main.getInstance(), () -> Main.getInstance().getPayoutManager().savePayout(this.payout));
				
				player.sendMessage(CC.translate("&aPayout successfully marked as payed!"));
			}).open(player);
		}
	}
}
