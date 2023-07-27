package cc.stormworth.hcf.misc.partnerpackages;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.player.InventorySerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * @Author NulledCode
 * @Plugin BattleHCF
 * @Date 2022-04
 */
public class EditPackageListener implements Listener {

    public static ItemStack[] PPItems = new ItemStack[54];
    public static ItemStack[] PPRewards = new ItemStack[54];

    public EditPackageListener() {
        try {
            if (Main.getInstance().getUtilitiesFile().getConfig().contains("PPItems"))
                PPItems = InventorySerialization.itemStackArrayFromBase64(Main.getInstance().getUtilitiesFile().getConfig().getString("PPItems"));
            if (Main.getInstance().getUtilitiesFile().getConfig().contains("PPRewards"))
                PPRewards = InventorySerialization.itemStackArrayFromBase64(Main.getInstance().getUtilitiesFile().getConfig().getString("PPRewards"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getPlayer().isOp()) return;

        if (event.getInventory().getTitle().equalsIgnoreCase("Editing PPItems")) {
            ((Player) event.getPlayer()).sendMessage(CC.GREEN + "PPItems inventory saved.");
            PPItems = event.getInventory().getContents();
            Main.getInstance().getUtilitiesFile().getConfig().set("PPItems", InventorySerialization.itemStackArrayToBase64(event.getInventory().getContents()));
            Main.getInstance().getUtilitiesFile().save();
        } else if (event.getInventory().getTitle().equalsIgnoreCase("Editing PPRewards")) {
            ((Player) event.getPlayer()).sendMessage(CC.GREEN + "PPRewards inventory saved.");
            PPRewards = event.getInventory().getContents();
            Main.getInstance().getUtilitiesFile().getConfig().set("PPRewards", InventorySerialization.itemStackArrayToBase64(event.getInventory().getContents()));
            Main.getInstance().getUtilitiesFile().save();
        }
    }
}
