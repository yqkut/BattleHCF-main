package cc.stormworth.hcf.deathmessage.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;

public class MobUtil {

    public static String getItemName(final ItemStack itemStack) {
        if (itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        }
        return WordUtils.capitalizeFully(itemStack.getType().name().replace('_', ' '));
    }
}