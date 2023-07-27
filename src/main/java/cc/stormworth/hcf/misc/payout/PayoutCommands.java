package cc.stormworth.hcf.misc.payout;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.payout.menu.PayoutsMenu;
import cc.stormworth.hcf.team.Team;
import org.bukkit.entity.Player;

public final class PayoutCommands {

	@Command(names = { "payout create" }, permission = "PLATFORMADMINISTRATOR", hidden = true)
	public static void payoutCreate(Player sender, @Param(name = "faction") String faction, @Param(name = "type", wildcard = true) String type) {
		PayoutManager payoutManager = Main.getInstance().getPayoutManager();

		Team team = Main.getInstance().getTeamHandler().getTeam(faction);

		if (team == null) {
			sender.sendMessage(CC.translate("&cThat team doesn't exist!"));
			return;
		}

		Payout payout = new Payout(type, faction, UUIDUtils.name(team.getOwner()));
		
		payoutManager.getPayouts().add(payout);
		
		TaskUtil.runAsync(Main.getInstance(), () -> payoutManager.savePayout(payout));
		
		sender.sendMessage(CC.translate("&aPayout successfully created!"));
	}
	
	@Command(names = { "payout delete" }, permission = "PLATFORMADMINISTRATOR", hidden = true)
	public static void payoutDelete(Player sender, @Param(name = "index") int index) {
		PayoutManager payoutManager = Main.getInstance().getPayoutManager();
		
		if (index <= 0 || index > payoutManager.getPayouts().size()) {
			sender.sendMessage(CC.translate("&cPayout #" + index + " not found!"));
			return;
		}
		
		TaskUtil.runAsync(Main.getInstance(), () -> payoutManager.deletePayout(payoutManager.getPayouts().remove(index - 1)));
		
		sender.sendMessage(CC.translate("&cPayout successfully deleted!"));
	}
	
	@Command(names = { "payouts" }, permission = "PLATFORMADMINISTRATOR", hidden = true)
	public static void payouts(Player sender) {
		if (Main.getInstance().getPayoutManager().getPayouts().isEmpty()) {
			sender.sendMessage(CC.translate("&cThere are no payouts created yet!"));
			return;
		}
		
		new PayoutsMenu().open(sender);
	}
}
