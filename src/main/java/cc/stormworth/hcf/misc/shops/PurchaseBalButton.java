package cc.stormworth.hcf.misc.shops;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.deathmessage.util.MobUtil;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PurchaseBalButton extends Button {

    ItemStack itemStack;
    int price;
    String matname;

    public PurchaseBalButton(ItemStack itemStack, int price) {
        this.itemStack = itemStack;
        this.price = price;
        this.matname = MobUtil.getItemName(itemStack);
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.of(itemStack.getType()).amount(itemStack.getAmount()).data(itemStack.getDurability()).name(ChatColor.YELLOW + matname).setLore(Arrays.asList(CC.YELLOW + "Price" + CC.GRAY + ": " + CC.GOLD + "$" + price)).build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

        HCFProfile profile = HCFProfile.get(player);

        double balance = profile.getEconomyData().getBalance();

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(CC.translate("&cInventory full."));
            playFail(player);
            return;
        }
        if (price > balance) {
            player.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            playFail(player);
            return;
        }

        playSuccess(player);
        player.getInventory().addItem(ItemBuilder.of(itemStack.getType()).amount(itemStack.getAmount()).data(itemStack.getDurability()).build());
        player.sendMessage(CC.translate("&eSuccessfully purchased &6x" + itemStack.getAmount() + " " + matname + " &efor &6$" + price + "&e."));
        profile.getEconomyData().subtractBalance(price);
    }
}