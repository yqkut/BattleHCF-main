package cc.stormworth.hcf.misc.kits.defaults;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.hcf.misc.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class DonorPremiumKit extends Kit {

  public DonorPremiumKit() {
    super("DonorPremium", new ItemBuilder(Material.DIAMOND).build(),
        new ItemStack[]{
            new ItemBuilder(Material.DIAMOND_SPADE)
                .enchant(Enchantment.DAMAGE_ALL, 3)
                .enchant(Enchantment.DURABILITY, 3)
                .name("&d&lBlood &f| Sword")
                .build(),

            new ItemBuilder(Material.ENDER_PEARL).amount(16).build(),
            new ItemBuilder(Material.GOLDEN_CARROT).amount(64).build(),

            //x33
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),
            new ItemBuilder(Material.POTION).data((short) 16421).build(),


        },
        new ItemStack[]{
            new ItemBuilder(Material.DIAMOND_BOOTS)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .enchant(Enchantment.DURABILITY, 3)
                .name("&d&lBlood &f| Helmet").build(),
            new ItemBuilder(Material.DIAMOND_LEGGINGS)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .enchant(Enchantment.DURABILITY, 3)
                .name("&d&lBlood &f| Leggings").build(),
            new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .enchant(Enchantment.DURABILITY, 3)
                .name("&d&lBlood &f| Chestplate").build(),
            new ItemBuilder(Material.DIAMOND_HELMET)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .enchant(Enchantment.DURABILITY, 3)
                .name("&d&lBlood &f| Helmet").build(),
        });
  }
}