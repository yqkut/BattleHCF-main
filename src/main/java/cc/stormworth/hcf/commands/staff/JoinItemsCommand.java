package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.player.InventorySerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JoinItemsCommand {

    @Command(names = {"joinitems set"}, permission = "DEVELOPER", hidden = true, requiresPlayer = true)
    public static void set(final Player sender) {
        final ItemStack[] items = sender.getInventory().getContents();
        Main.getInstance().getServerHandler().setFjiItems(items);
        Main.getInstance().getUtilitiesFile().getConfig().set("first-join-items", InventorySerialization.itemStackArrayToBase64(items));
        final ItemStack[] armorContents = sender.getInventory().getArmorContents();
        Main.getInstance().getUtilitiesFile().save();
        sender.sendMessage(CC.translate("&eYou have successfully set &fFirst Join &eitems."));
    }

    @Command(names = {"joinitems load"}, permission = "DEVELOPER", hidden = true, requiresPlayer = true)
    public static void get(final Player sender) {
        if (Main.getInstance().getServerHandler().getFjiItems() == null) {
            sender.sendMessage(CC.RED + "The FJI are not set.");
            return;
        }
        sender.getInventory().clear();
        sender.getInventory().setContents(Main.getInstance().getServerHandler().getFjiItems());
        sender.sendMessage(CC.translate("&eYou have received &6First Join &eitems."));
    }
}