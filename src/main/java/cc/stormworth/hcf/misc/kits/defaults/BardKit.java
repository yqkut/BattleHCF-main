package cc.stormworth.hcf.misc.kits.defaults;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BardKit extends Kit {

    public BardKit() {
        super("Bard", ItemBuilder.of(Material.BLAZE_POWDER).build(),
                new ItemStack[]{
                        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ALL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.ENDER_PEARL, 16).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.BLAZE_POWDER, 64).build(),
                        ItemBuilder.of(Material.SUGAR, 64).build(),
                        ItemBuilder.of(Material.IRON_INGOT, 64).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.FEATHER, 64).build(),
                        ItemBuilder.of(Material.GHAST_TEAR, 64).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.MAGMA_CREAM, 64).build(),
                        ItemBuilder.of(Material.WHEAT, 64).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.SPIDER_EYE, 64).build(),
                        ItemBuilder.of(Material.FERMENTED_SPIDER_EYE, 64).build(),
                },
                new ItemStack[]{
                        ItemBuilder.of(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).build(),
                        ItemBuilder.of(Material.GOLD_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.GOLD_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.GOLD_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                });
    }
}