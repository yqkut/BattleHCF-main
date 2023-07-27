package cc.stormworth.hcf.misc.kits.defaults;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class PvPKit extends Kit {

    public PvPKit() {
        super("PvP", ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                new ItemStack[]{
                        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ALL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.ENDER_PEARL, 16).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 8226).build(),
                        ItemBuilder.of(Material.POTION).data((short) 8259).build(),
                        ItemBuilder.of(Material.BAKED_POTATO, 64).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 8226).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 8226).build(),

                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 16421).build(),
                        ItemBuilder.of(Material.POTION).data((short) 8226).build(),
                },
                new ItemStack[]{
                        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).build(),
                        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                });
    }
}