package cc.stormworth.hcf.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import dev.nulledcode.spigot.events.AnvilPreRepairEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

public class MapListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (!EOTWCommand.isFfaEnabled()) return;
        if (event.getPlayer().hasPermission("core.staff")) return;
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot run commands during FFA.");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownExpBottle)) {
            return;
        }
        final ThrownExpBottle thrown = (ThrownExpBottle) event.getEntity();
        final ProjectileSource shooter = thrown.getShooter();
        if (!(shooter instanceof Player)) {
            return;
        }
        final ItemStack inHand = ((Player) shooter).getItemInHand();
        if (inHand != null && inHand.getType() == Material.EXP_BOTTLE && inHand.hasItemMeta() && inHand.getItemMeta().hasLore() && inHand.getItemMeta().getLore().size() == 1) {
            final String number = ChatColor.stripColor(inHand.getItemMeta().getLore().get(0)).replace("XP: ", "").replaceAll(",", "");
            final Integer xp = Integer.valueOf(number);
            thrown.setMetadata("XP", new FixedMetadataValue(Main.getInstance(), xp));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSplash(final ExpBottleEvent event) {
        final ThrownExpBottle bottle = event.getEntity();
        if (bottle.hasMetadata("XP")) {
            event.setExperience(bottle.getMetadata("XP").get(0).asInt());
            bottle.removeMetadata("XP", Main.getInstance());
        }
    }

    @EventHandler
    public void onPreAnvilEvent(AnvilPreRepairEvent event){
        ItemStack itemStack = event.getItem();

        if(itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasEnchants()){
            return;
        }

        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if(enchantment != null && itemStack.getItemMeta().getEnchantLevel(enchantment) > enchantment.getMaxLevel()){
                itemStack.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();

        if (event.getInventory() != event.getWhoClicked().getInventory()){
            return;
        }

        if(itemStack == null){

            itemStack = event.getCursor();

            if(itemStack == null){
                return;
            }

            return;
        }

        if(!itemStack.hasItemMeta()){
            return;
        }

        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if(enchantment == Enchantment.PROTECTION_ENVIRONMENTAL && itemStack.getItemMeta().getEnchantLevel(enchantment) > 3){
                itemStack.addUnsafeEnchantment(enchantment, 3);
            }
        }

    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
    	if (Main.getInstance().getMapHandler().isKitMap() && event.getFoodLevel() < 20) {
    		event.setFoodLevel(20);
    	}
    }
}