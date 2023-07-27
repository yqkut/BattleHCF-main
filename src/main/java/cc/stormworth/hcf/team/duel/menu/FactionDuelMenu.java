package cc.stormworth.hcf.team.duel.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class FactionDuelMenu extends PaginatedMenu {

	private final Team team;
	private final Team targetTeam;

	private boolean isDueling;

	{
		setAutoUpdate(true);
		setUpdateAfterClick(true);
	}
	
	@Override
	public String getPrePaginatedTitle(Player player) {
		return "&6&lSet members classes";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = Maps.newHashMap();
		
		for (Player member : this.team.getOnlineMembers()) {
			buttons.put(buttons.size(), new MemberButton(member));
			if(!team.getPvpClassesMap().containsKey(member.getUniqueId())){
				team.getPvpClassesMap().put(member.getUniqueId(), "Diamond");
			}
		}
		
		return buttons;
	}
	
	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		return ImmutableMap.of(4, new ContinueButton());
	}
	
	@Override
	public int getMaxItemsPerPage(Player player) {
		return 54;
	}

	private String getPosition(Player player){

		if(team.getOwner().equals(player.getUniqueId())){
			return "&4Leader";
		} else if (team.getCoLeaders().contains(player.getUniqueId())) {
			return "&cCo-Leader";
		} else if (team.getCaptains().contains(player.getUniqueId())) {
			return "&bCaptain";
		} else {
			return "&aMember";
		}
	}

	private String getSelectedClass(Player player) {

		String pvpClass = team.getPvpClassesMap().get(player.getUniqueId());

		if (pvpClass.equalsIgnoreCase("Bard")) {
			return "&6Bard";
		} else if (pvpClass.equalsIgnoreCase("Archer")) {
			return "&5Archer";
		} else if (pvpClass.equalsIgnoreCase("Rouge")) {
			return "&7Rouge";
		} else {
			return "&bDiamond";
		}
	}

	@AllArgsConstructor
	final class MemberButton extends Button {
		
		private final Player member;

		@Override
		public ItemStack getButtonItem(Player player) {
			String memberName = this.member.getName();
			ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM)
					.data((short) 3)
					.name("&6" + memberName)
					.setSkullOwner(memberName);
			
			List<String> lore = Lists.newArrayList();
			
			lore.add("");
			lore.add("&fPosition: " + getPosition(this.member));
			lore.add("&7- (Currently " + getSelectedClass(this.member) + " Class &7selected)");
			lore.add("");
			lore.add("&7Click to edit class!");
			
			return item.setLore(lore).build();
		}
		
		@Override
		public void clicked(Player player, int Slot, ClickType clickType) {
			new SelectClassMenu(team, targetTeam, member, isDueling).openMenu(player);
		}
	}
	
	final class ContinueButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.WOOL)
					.data((short) 5)
					.name("&a&lContinue")
					.addToLore(
							"",
							"&7Once you have set all your faction members",
							"&7pvp classes, click this button to sent duel",
							"&7to &f" + targetTeam.getName(),
							"",
							"&eClick to sent duel to &f" + targetTeam.getName()
					).build();
		}
		
		@Override
		public void clicked(Player player, int Slot, ClickType clickType) {
			playSuccess(player);

			CooldownAPI.setCooldown(player, "FDUEL", TimeUtil.parseTimeLong("1m"));

			if (isDueling){
				Main.getInstance().getFactionDuelManager().sentInvite(team, targetTeam);
				player.closeInventory();
			}else{
				Main.getInstance().getFactionDuelManager().accept(team, targetTeam);
				player.closeInventory();
			}
		}
	}
}
