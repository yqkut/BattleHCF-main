package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.shop.ShopUtils;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class SellInventoryCommand {

	private static final Material[] SOLDABLE_ITEMS = new Material[] {
			Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK,
            Material.IRON_BLOCK,
            Material.COAL_BLOCK,
            Material.LAPIS_BLOCK,
            Material.EMERALD_BLOCK,
            Material.REDSTONE_BLOCK,
            Material.COBBLESTONE
	};
	
	@Command(names = { "sellinventory", "sellinv" }, permission = "")
	public static void sellInventory(Player player) {
		if (SpawnTagHandler.isTagged(player)) {
			player.sendMessage(CC.translate("&cYou can't perform this action while in combat-tag!"));
			return;
		}
		
        int price = 0;
        int itemsSold = 0;

        List<ItemStack> toSellList = Lists.newArrayList();

        for (Material item : SOLDABLE_ITEMS) {
        	Map<Integer, ? extends ItemStack> map = player.getInventory().all(item);
        	
        	if (map.isEmpty()) continue;
        	
        	for (ItemStack stack : map.values()) {
        		price += ShopUtils.getPrice(stack, Main.getInstance().getMapHandler().isKitMap() ? 64 : 16);
        		itemsSold += stack.getAmount();
        		toSellList.add(stack);
        	}
        }
        
        if (price == 0) {
        	player.sendMessage(CC.translate("&cYou don't have anything to sell!"));
        	return;
        }

		HCFProfile profile = HCFProfile.get(player);

		profile.getEconomyData().addBalance(price);

        for (ItemStack item : toSellList) {
        	player.getInventory().removeItem(item);
        }

        player.sendMessage(CC.translate("&aYou sold &e" + itemsSold + " &aitems for &e$" + price + "&a."));
	}
}
