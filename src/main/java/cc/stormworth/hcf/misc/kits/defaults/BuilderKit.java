package cc.stormworth.hcf.misc.kits.defaults;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BuilderKit extends Kit {

    public BuilderKit() {
        super("Builder", ItemBuilder.of(Material.DIAMOND_AXE).build(),
                new ItemStack[]{
                        ItemBuilder.of(Material.DIAMOND_AXE).enchant(Enchantment.DIG_SPEED, 6).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.ENDER_PEARL, 16).build(),
                        ItemBuilder.of(Material.DIAMOND_PICKAXE).enchant(Enchantment.SILK_TOUCH, 1).enchant(Enchantment.DIG_SPEED, 6).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).enchant(Enchantment.DIG_SPEED, 6).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.DIAMOND_SPADE).enchant(Enchantment.DIG_SPEED, 6).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.GRASS, 64).build(),
                        ItemBuilder.of(Material.GRASS, 64).build(),
                        ItemBuilder.of(Material.CHEST, 64).build(),
                        ItemBuilder.of(Material.TRAPPED_CHEST, 64).build(),

                        ItemBuilder.of(Material.GLASS, 64).build(),
                        ItemBuilder.of(Material.STONE, 64).build(),
                        ItemBuilder.of(Material.ANVIL, 64).build(),
                        ItemBuilder.of(Material.LEVER, 64).build(),
                        ItemBuilder.of(Material.FENCE_GATE, 64).build(),
                        ItemBuilder.of(Material.GLASS, 64).build(),
                        ItemBuilder.of(Material.SAND, 64).build(),
                        ItemBuilder.of(Material.STONE, 64).build(),
                        ItemBuilder.of(Material.STONE, 64).build(),

                        ItemBuilder.of(Material.FURNACE, 64).build(),
                        ItemBuilder.of(Material.FURNACE, 64).build(),
                        ItemBuilder.of(Material.LAVA_BUCKET).build(),
                        ItemBuilder.of(Material.WATER_BUCKET).build(),
                        ItemBuilder.of(Material.SAND, 64).build(),
                        ItemBuilder.of(Material.REDSTONE, 64).build(),
                        ItemBuilder.of(Material.HOPPER, 64).build(),
                        ItemBuilder.of(Material.WOOD_BUTTON, 64).build(),
                        ItemBuilder.of(Material.REDSTONE_TORCH_ON, 64).build(),

                        ItemBuilder.of(Material.PISTON_BASE, 64).build(),
                        ItemBuilder.of(Material.DIODE, 64).build(),
                        ItemBuilder.of(Material.LOG, 64).build(),
                        ItemBuilder.of(Material.LOG, 64).build(),
                        ItemBuilder.of(Material.LOG, 64).build(),
                        ItemBuilder.of(Material.WORKBENCH, 64).build(),
                        ItemBuilder.of(Material.REDSTONE_COMPARATOR, 64).build(),
                        ItemBuilder.of(Material.PISTON_STICKY_BASE, 64).build(),
                        ItemBuilder.of(Material.WATER_BUCKET).build(),
                },
                new ItemStack[]{
                        ItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).build(),
                        ItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel()).enchant(Enchantment.DURABILITY, 3).build(),
                        ItemBuilder.of(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1).enchant(Enchantment.DURABILITY, 3).build(),
                });
    }
}