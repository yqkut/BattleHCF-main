package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class LastInvCommand {

    @Command(names = {"lastinv"}, permission = "MODPLUS")
    public static void lastInv(Player sender, @Param(name = "player") UUID player) {
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            CorePlugin.getInstance().runRedisCommand((redis) -> {
                if (!redis.exists("lastInv:contents:" + player.toString())) {
                    sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + UUIDUtils.name(player));
                    return null;
                }

                ItemStack[] contents = CorePlugin.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + player), ItemStack[].class);
                ItemStack[] armor = CorePlugin.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + player), ItemStack[].class);

                cleanLoot(contents);
                cleanLoot(armor);

                Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
                    sender.getInventory().setContents(contents);
                    sender.getInventory().setArmorContents(armor);
                    sender.sendMessage(ChatColor.GREEN + "Loaded " + UUIDUtils.name(player) + "'s last inventory.");
                });

                return null;
            });
        });
    }

    public static void cleanLoot(ItemStack[] stack) {
        for (ItemStack item : stack) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = item.getItemMeta().getLore();
                lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                meta.setLore(lore);

                item.setItemMeta(meta);
            }
        }
    }

    public static void recordInventory(Player player) {
        recordInventory(player.getUniqueId(), player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public static void recordInventory(UUID player, ItemStack[] contents, ItemStack[] armor) {
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            CorePlugin.getInstance().runRedisCommand((redis) -> {
                redis.set("lastInv:contents:" + player.toString(), CorePlugin.PLAIN_GSON.toJson(contents));
                redis.set("lastInv:armorContents:" + player, CorePlugin.PLAIN_GSON.toJson(armor));
                return null;
            });
        });
    }
}