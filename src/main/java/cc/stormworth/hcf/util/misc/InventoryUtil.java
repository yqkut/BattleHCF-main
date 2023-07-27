package cc.stormworth.hcf.util.misc;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

  public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
    ItemStack[] fixed = new ItemStack[36];

    System.arraycopy(source, 0, fixed, 27, 9);
    System.arraycopy(source, 9, fixed, 0, 27);

    return fixed;
  }


  public static String getItemName(ItemStack item) {

    if (item.hasItemMeta()){
        if (item.getItemMeta().hasDisplayName()){
            return item.getItemMeta().getDisplayName();
        }
    }


    return StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " "));
  }
}
