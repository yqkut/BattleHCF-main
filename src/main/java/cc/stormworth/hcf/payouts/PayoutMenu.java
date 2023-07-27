package cc.stormworth.hcf.payouts;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PayoutMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW + (Main.getInstance().getMapHandler().isKitMap()  ? "Kits " : "HCF ") + "Payouts";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();
        

        return buttons;
    }
}
