package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.general.PlayerUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PvPTimerListener implements Listener {

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        double multiplier = Main.getInstance().getMapHandler().getBaseLootingMultiplier();
        if (event.getEntity().getKiller() != null) {
            final Player player = event.getEntity().getKiller();
            if (player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                switch (player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)) {
                    case 1: {
                        multiplier = Main.getInstance().getMapHandler().getLevel1LootingMultiplier();
                        break;
                    }
                    case 2: {
                        multiplier = Main.getInstance().getMapHandler().getLevel2LootingMultiplier();
                        break;
                    }
                    case 3: {
                        multiplier = Main.getInstance().getMapHandler().getLevel3LootingMultiplier();
                        break;
                    }
                }
            }
        }
        event.setDroppedExp((int) FastMath.ceil(event.getDroppedExp() * multiplier));
    }

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent event) {
        final ItemStack itemStack = event.getEntity().getItemStack();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().contains("§8PVP Loot")) {
            final ItemMeta meta = itemStack.getItemMeta();
            final List<String> lore = meta.getLore();
            lore.remove("§8PVP Loot");
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            event.getEntity().setItemStack(itemStack);
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        for (final ItemStack itemStack : event.getDrops()) {
            final ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            lore.add("§8PVP Loot");
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onEntityShootBow(final EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            if (HCFProfile.get(player).hasPvPTimer()) {
                player.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                player.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player damager = PlayerUtils.getDamageSource(event.getDamager());
        if (damager == null) return;
        if (HCFProfile.get(damager).hasPvPTimer()) {
            damager.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            damager.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setCancelled(true);
            return;
        }
        HCFProfile profile = HCFProfile.get((Player) event.getEntity());
        if(profile == null) return;
        if (profile.hasPvPTimer()) {
            damager.sendMessage(ChatColor.RED + "That player currently has their PVP Timer!");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (HCFProfile.get(player).hasPvPTimer()) {
                event.setCancelled(true);
            }
        }
    }
}