package cc.stormworth.hcf.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

@AllArgsConstructor
@Getter
public class EnchantmentEffect {

    private ItemStack item;
    private PotionEffect effect;
    private int index;

}
