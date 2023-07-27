package cc.stormworth.hcf.misc.crazyenchants.processors;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Methods {

    private static Class<?> getClass(final String prefix, final String nmsClassString) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        final String name = prefix + version + nmsClassString;
        final Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }

    public static void setItemInHand(Player player, ItemStack item) {
        player.setItemInHand(item);
    }

    public static String getPrefix() {
        return getPrefix("");
    }

    public static String getPrefix(String string) {
        return CC.translate(CC.PRIMARY + "&lBattle &7┃&r " + string);
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static Player getPlayer(String name) {
        return Bukkit.getServer().getPlayer(name);
    }

    public static void removeItem(ItemStack item, Player player) {
        removeItem(item, player, 1);
    }

    public static void removeItem(ItemStack item, Player player, int amount) {
        if (player.getInventory().contains(item)) {
            if (item.getAmount() <= amount) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - amount);
            }
        }
    }

    public static ItemStack removeItem(ItemStack item) {
        return removeItem(item, 1);
    }

    public static ItemStack removeItem(ItemStack item, int amount) {
        ItemStack itemStack = item.clone();
        if (item.getAmount() <= amount) {
            itemStack = new ItemStack(Material.AIR);
        } else {
            itemStack.setAmount(item.getAmount() - amount);
        }
        return itemStack;
    }

    public static ItemStack addLore(ItemStack item, String i) {
        List<String> lore = new ArrayList<>();
        ItemMeta m = item.getItemMeta();
        if (item.getItemMeta().hasLore()) {
            lore.addAll(item.getItemMeta().getLore());
        }
        lore.add(CC.translate(i));
        if (lore.contains(CC.translate(FileManager.Files.CUSTOMENCHANTS.getFile().getString("Settings.WhiteScroll.ProtectedName")))) {
            lore.remove(CC.translate(FileManager.Files.CUSTOMENCHANTS.getFile().getString("Settings.WhiteScroll.ProtectedName")));
            lore.add(CC.translate(FileManager.Files.CUSTOMENCHANTS.getFile().getString("Settings.WhiteScroll.ProtectedName")));
        }
        if (lore.contains(CC.translate(FileManager.Files.CUSTOMENCHANTS.getFile().getString("Settings.ProtectionCrystal.Protected")))) {
            lore.remove(CC.translate(FileManager.Files.CUSTOMENCHANTS.getFile().getString("Settings.ProtectionCrystal.Protected")));
            lore.add(CC.translate(FileManager.Files.CUSTOMENCHANTS.getFile().getString("Settings.ProtectionCrystal.Protected")));
        }
        m.setLore(lore);
        item.setItemMeta(m);
        return item;
    }

    public static int getPercent(String argument, ItemStack item, List<String> originalLore, int defaultValue) {
        String arg = defaultValue + "";
        for (String originalLine : originalLore) {
            originalLine = CC.translate(originalLine).toLowerCase();
            if (originalLine.contains(argument.toLowerCase())) {
                String[] b = originalLine.split(argument.toLowerCase());
                for (String itemLine : item.getItemMeta().getLore()) {
                    boolean toggle = false;// Checks to make sure the lore is the same.
                    if (b.length >= 1) {
                        if (itemLine.toLowerCase().startsWith(b[0])) {
                            arg = itemLine.toLowerCase().replace(b[0], "");
                            toggle = true;
                        }
                    }
                    if (b.length >= 2) {
                        if (itemLine.toLowerCase().endsWith(b[1])) {
                            arg = arg.toLowerCase().replace(b[1], "");
                        } else {
                            toggle = false;
                        }
                    }
                    if (toggle) {
                        break;
                    }
                }
                if (isInt(arg)) {
                    break;
                }
            }
        }
        int percent = defaultValue;
        if (isInt(arg)) {
            percent = Integer.parseInt(arg);
        }
        return percent;
    }

    public static boolean hasArgument(String arg, List<String> message) {
        for (String line : message) {
            line = CC.translate(line).toLowerCase();
            if (line.contains(arg.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean randomPicker(int max) {
        if (max <= 0) {
            return true;
        }
        int chance = 1 + CorePlugin.RANDOM.nextInt(max);
        return chance == 1;
    }

    public static boolean randomPicker(int min, int max) {
        if (max <= min || max <= 0) {
            return true;
        }
        int chance = 1 + CorePlugin.RANDOM.nextInt(max);
        return chance >= 1 && chance <= min;
    }

    public static Integer percentPick(int max, int min) {
        if (max == min) {
            return max;
        } else {
            return min + CorePlugin.RANDOM.nextInt(max - min);
        }
    }

    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static Color getColor(String color) {
        switch (color.toUpperCase()) {
            case "AQUA":
                return Color.AQUA;
            case "BLACK":
                return Color.BLACK;
            case "BLUE":
                return Color.BLUE;
            case "FUCHSIA":
                return Color.FUCHSIA;
            case "GRAY":
                return Color.GRAY;
            case "GREEN":
                return Color.GREEN;
            case "LIME":
                return Color.LIME;
            case "MAROON":
                return Color.MAROON;
            case "NAVY":
                return Color.NAVY;
            case "OLIVE":
                return Color.OLIVE;
            case "ORANGE":
                return Color.ORANGE;
            case "PURPLE":
                return Color.PURPLE;
            case "RED":
                return Color.RED;
            case "SILVER":
                return Color.SILVER;
            case "TEAL":
                return Color.TEAL;
            case "YELLOW":
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }

    public static String stripString(String string) {
        return string != null ? string.replace("-", "").replace("_", "").replace(" ", "") : "";
    }

    public static Enchantment getEnchantment(String enchantmentName) {
        try {
            HashMap<String, String> enchantments = getEnchantments();
            enchantmentName = stripString(enchantmentName);
            for (Enchantment enchantment : Enchantment.values()) {
                if (stripString(enchantment.getName()).equalsIgnoreCase(enchantmentName) ||
                        (enchantments.get(enchantment.getName()) != null &&
                                stripString(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) {
                    return enchantment;
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public static boolean verifyItemLore(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().hasLore();
    }

    public static HashMap<String, String> getEnchantments() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        return enchantments;
    }

    public static boolean isSimilar(ItemStack one, ItemStack two) {
        if (one.getType() == two.getType()) {
            if (one.hasItemMeta() && two.hasItemMeta()) {
                if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()) {
                    if (one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
                        if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
                            int i = 0;
                            for (String lore : one.getItemMeta().getLore()) {
                                if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                    return false;
                                }
                                i++;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack getRandomPaneColor() {
        List<Integer> colors = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15);
        return ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data(colors.get(CorePlugin.RANDOM.nextInt(colors.size())).shortValue()).build();
    }
}