package cc.stormworth.hcf.misc.kits.defaults;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.kits.Kit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class DistanceArcherKit extends Kit {

    public DistanceArcherKit() {
        super("DistanceArcherKit", ItemBuilder.of(Material.INK_SACK).data((short) 12).build(),
                new ItemStack[]{
                        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ALL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.ENDER_PEARL, 16).build(),
                        ItemBuilder.of(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 4).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.SUGAR, 64).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),

                        ItemBuilder.of(Material.ARROW).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.FEATHER, 64).build(),
                },
                new ItemStack[]{
                        ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.AQUA).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).build(),
                        ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.BLUE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.BLUE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.LEATHER_HELMET).color(Color.AQUA).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                });
    }
}