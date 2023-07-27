package cc.stormworth.hcf.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class InventoryUtil {

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

}
