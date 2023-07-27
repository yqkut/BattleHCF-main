package cc.stormworth.hcf.misc.kits.defaults;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ArcherKit extends Kit {

  public ArcherKit() {
    super("Archer", ItemBuilder.of(Material.BOW).build(),
        new ItemStack[]{
            ItemBuilder.of(Material.DIAMOND_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ALL.getStartLevel() + 1)
                .enchant(Enchantment.DURABILITY, 3).build(),
            ItemBuilder.of(Material.ENDER_PEARL, 16).build(),
            ItemBuilder.of(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 4)
                .enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1)
                .enchant(Enchantment.DURABILITY, 3).build(),
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
            ItemBuilder.of(Material.LEATHER_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL,
                    Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1)
                .enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).build(),
            ItemBuilder.of(Material.LEATHER_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL,
                    Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel())
                .enchant(Enchantment.DURABILITY, 3).build(),
            ItemBuilder.of(Material.LEATHER_CHESTPLATE)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL,
                    Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel())
                .enchant(Enchantment.DURABILITY, 3).build(),
            ItemBuilder.of(Material.LEATHER_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL,
                    Enchantment.PROTECTION_ENVIRONMENTAL.getStartLevel() + 1)
                .enchant(Enchantment.DURABILITY, 3).build(),
        });
  }
}