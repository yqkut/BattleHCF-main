package cc.stormworth.hcf.brewingstand;

import cc.stormworth.core.kt.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
@Getter
public enum PotionCategory {

    POSION("Poison I", new ItemBuilder(Material.POTION).setDurability(16388).name("&3&lPosion I").build(),
            new ItemBuilder(Material.POTION).setDurability(16388).build(),
            Material.NETHER_STALK, Material.SPIDER_EYE, Material.SULPHUR),
    SPEED("Speed II", new ItemBuilder(Material.POTION).setDurability(8226).name("&b&lSpeed II").build(),
            new ItemBuilder(Material.POTION).setDurability(8226).build(),
            Material.NETHER_STALK, Material.SUGAR, Material.REDSTONE, Material.GLOWSTONE_DUST),
    FIRE_RESISTANCE("Fire Resistance I", new ItemBuilder(Material.POTION).setDurability(8259).name("&6&lFire Resistance I").build(),
            new ItemBuilder(Material.POTION).setDurability(8259).build(),
            Material.NETHER_STALK, Material.MAGMA_CREAM, Material.REDSTONE),
    SLOWNESS("Slowness I", new ItemBuilder(Material.POTION).setDurability(16426).name("&9&lSlowness I").build(),
            new ItemBuilder(Material.POTION).setDurability(16426).build(),
            Material.NETHER_STALK, Material.SUGAR, Material.FERMENTED_SPIDER_EYE, Material.SULPHUR),
    HEALING("Healing II", new ItemBuilder(Material.POTION).setDurability(16421).name("&c&lHealing II").build(),
            new ItemBuilder(Material.POTION).setDurability(16421).build(),
            Material.NETHER_STALK, Material.SPECKLED_MELON, Material.GLOWSTONE_DUST, Material.SULPHUR),
    INVINCIBILITY("Invincibility I", new ItemBuilder(Material.POTION).setDurability(8270).name("&8&lInvincibility I").build(),
            new ItemBuilder(Material.POTION).setDurability(8270).build(),
            Material.NETHER_STALK, Material.GOLDEN_CARROT, Material.FERMENTED_SPIDER_EYE, Material.REDSTONE);

    private final String name;
    private final ItemStack itemStack;
    private final ItemStack result;
    private final Material[] resources;

    PotionCategory(String name, ItemStack itemStack, ItemStack result,  Material... resources) {
        this.name = name;
        this.itemStack = itemStack;
        this.result = result;
        this.resources = resources;
    }
}
