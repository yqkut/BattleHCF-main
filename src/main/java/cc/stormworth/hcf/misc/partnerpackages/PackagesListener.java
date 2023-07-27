package cc.stormworth.hcf.misc.partnerpackages;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.util.player.FireworksUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @Author NulledCode
 * @Plugin BattleHCF
 * @Date 2022-04
 */
public class PackagesListener implements Listener {

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT")) {
            final ItemStack hand = event.getItem();
            if (hand == null) return;
            if (hand.isSimilar(packageItem(hand.getAmount()))) {

                final Player player = event.getPlayer();
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(CC.translate("&cInventory full."));
                    return;
                }
                if (player.getItemInHand().getAmount() > 1) {
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                } else {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                for (int i = 0; i < 2; ++i) {
                    final ItemStack[] abilityItem = EditPackageListener.PPRewards;

                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(abilityItem);
                    }

                    player.updateInventory();
                    event.setCancelled(true);
                }
                FireworksUtil.getRandomEffect();
            }
        }
    }

    public static ItemStack packageItem(final int amt) {
        return ItemBuilder.of(Material.GOLD_NUGGET).amount(amt).name(CC.translate("&6&lAbility Gift")).setLore(CC.translate(Arrays.asList("", "&&eFind &6common &eabilities and", "maybe exclusive ones.", "", "&6&nstore.battle.rip"))).build();
    }

    public static void giveAll(final Integer amount) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(packageItem(amount));
        }
    }


}
