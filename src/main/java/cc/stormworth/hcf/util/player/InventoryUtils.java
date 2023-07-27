package cc.stormworth.hcf.util.player;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryUtils {

    public static final SimpleDateFormat DEATH_TIME_FORMAT;
    public static final String KILLS_LORE_IDENTIFIER;
    public static final ItemStack CROWBAR;
    public static final ItemStack ANTIDOTE;
    public static final String CROWBAR_NAME;
    public static final int CROWBAR_PORTALS = 6;
    public static final int CROWBAR_SPAWNERS = 1;

    static {
        DEATH_TIME_FORMAT = new SimpleDateFormat("MM.dd.yy HH:mm");
        KILLS_LORE_IDENTIFIER = ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills: " + ChatColor.WHITE + ChatColor.BOLD;
        ANTIDOTE = ItemBuilder.of(Material.POTION).data((short) 8196).name(ChatColor.GREEN + "Antidote").setLore(new ArrayList()).addToLore(new String[]{ChatColor.GRAY + "Drink to relieve yourself of potion debuffs."}).build();
        CROWBAR_NAME = ChatColor.RED + "Crowbar";
        CROWBAR = new ItemStack(Material.DIAMOND_HOE);
        final ItemMeta meta = InventoryUtils.CROWBAR.getItemMeta();
        meta.setDisplayName(InventoryUtils.CROWBAR_NAME);
        meta.setLore(getCrowbarLore(6, 1));
        InventoryUtils.CROWBAR.setItemMeta(meta);
    }

    public static int findItem(final Player player, final ItemStack finding) {
        for (int i = 0; i < player.getInventory().getSize(); ++i) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getItemMeta().getLore() != null && item.getItemMeta().getLore().get(0).contains(CC.translate("&7This is an tier")) && item == finding) {
                return i;
            }
        }
        return -1;
    }

    public static boolean conformEnchants(final ItemStack item) {
        if (Boolean.TRUE) {
            return false;
        }
        if (item == null) {
            return false;
        }
        if (item.hasItemMeta()) {
            final ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().contains(ChatColor.AQUA.toString())) {
                return false;
            }
        }
        boolean fixed = false;
        final Map<Enchantment, Integer> enchants = item.getEnchantments();
        for (final Map.Entry<Enchantment, Integer> enchantmentSet : enchants.entrySet()) {
            if (enchantmentSet.getValue() > enchantmentSet.getKey().getMaxLevel()) {
                item.addUnsafeEnchantment(enchantmentSet.getKey(), enchantmentSet.getKey().getMaxLevel());
                fixed = true;
            }
        }
        return fixed;
    }

    public static ItemStack addToPart(final ItemStack item, final String title, final String key, final int max) {
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasLore() && meta.getLore().size() != 0) {
            final List<String> lore = meta.getLore();
            if (lore.contains(title)) {
                final int titleIndex = lore.indexOf(title);
                int keys = 0;
                for (int i = titleIndex; i < lore.size() && !lore.get(i).equals(""); ++i) {
                    ++keys;
                }
                lore.add(titleIndex + 1, key);
                if (keys > max) {
                    lore.remove(titleIndex + keys);
                }
            } else {
                lore.add("");
                lore.add(title);
                lore.add(key);
            }
            meta.setLore(lore);
        } else {
            final List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(title);
            lore.add(key);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addDeath(final ItemStack item, final String key) {
        return addToPart(item, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Deaths:", key, 10);
    }

    public static ItemStack addKill(ItemStack item, String key) {
        int killsIndex = 1;
        int[] lastKills = {3, 4, 5};
        int currentKills = 1;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (meta.hasLore()) {
            lore = meta.getLore();

            if (meta.getLore() != null && meta.getLore().size() > killsIndex) {
                String killStr = lore.get(killsIndex);
                if (!killStr.contains(":")) return null;
                currentKills += Integer.parseInt(ChatColor.stripColor(killStr.split(":")[1]).trim());
            }

            for (int j : lastKills) {
                if (j == lastKills[lastKills.length - 1]) {
                    continue;
                }
                if (lore.size() > j) {
                    String atJ = meta.getLore().get(j);

                    if (lore.size() <= j + 1) {
                        lore.add(null);
                    }

                    lore.set(j + 1, atJ);
                }

            }
        }

        if (lore.size() <= killsIndex) {
            for (int i = lore.size(); i <= killsIndex + 1; i++) {
                lore.add("");
            }
        }
        lore.set(killsIndex, "§6§lKills:§f " + currentKills);

        int firsKill = lastKills[0];

        if (lore.size() <= firsKill) {
            for (int i = lore.size(); i <= firsKill + 1; i++) {
                lore.add("");
            }
        }

        lore.set(firsKill, key);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return (item);
    }

    public static List<String> getCrowbarLore(final int portals, final int spawners) {
        final List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "Can Break:");
        lore.add(ChatColor.GOLD + " - " + ChatColor.AQUA + "End Portals: " + ChatColor.WHITE + "{" + ChatColor.GOLD + portals + ChatColor.WHITE + "}");
        lore.add(ChatColor.GOLD + " - " + ChatColor.AQUA + "Spawners: " + ChatColor.WHITE + "{" + ChatColor.GOLD + spawners + ChatColor.WHITE + "}");
        return lore;
    }

    public static int getCrowbarUsesPortal(final ItemStack item) {
        return Integer.parseInt(getLoreData(item, 2));
    }

    public static int getCrowbarUsesSpawner(final ItemStack item) {
        return Integer.parseInt(getLoreData(item, 3));
    }

    public static boolean isArmor(final ItemStack item) {
        return item.getTypeId() > 297 && item.getTypeId() < 318;
    }

    public static boolean isBoots(final ItemStack item) {
        return item.getTypeId() == 301 || item.getTypeId() == 305 || item.getTypeId() == 309 || item.getTypeId() == 313 || item.getTypeId() == 317;
    }

    public static String getLoreData(final ItemStack item, final int index) {
        final List<String> lore = item.getItemMeta().getLore();
        if (lore != null && index < lore.size()) {
            final String str = ChatColor.stripColor(lore.get(index));
            return str.split("\\{")[1].replace("}", "").replace(" ", "");
        }
        return "";
    }

    public static boolean isSimilar(ItemStack item, ItemStack compare) {
        if (item == null) return false;

        if (item.getType() != compare.getType()) return false;

        if (item.hasItemMeta()) return false;

        ItemMeta compareMeta = compare.getItemMeta();
        ItemMeta itemMeta = item.getItemMeta();

        return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equalsIgnoreCase(compareMeta.getDisplayName()) &&
                (itemMeta.hasLore() == compareMeta.hasLore()) && itemMeta.getLore().equals(compareMeta.getLore());
    }

    public static boolean isSimilar(final ItemStack item, final String name) {
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name);
    }
}
