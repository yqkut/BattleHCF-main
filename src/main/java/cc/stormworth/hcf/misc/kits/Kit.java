package cc.stormworth.hcf.misc.kits;

import cc.stormworth.core.util.command.param.ParameterType;
import cc.stormworth.hcf.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

@Getter
@Setter
public class Kit {

    public String name;
    public ItemStack icon;
    public ItemStack[] inventoryContents;
    public ItemStack[] armorContents;

    public Kit(final String name) {
        this.name = name;
    }

    public Kit(final String name, ItemStack icon, ItemStack[] inventoryContents, ItemStack[] armorContents) {
        this.name = name;
        this.icon = icon;
        this.inventoryContents = inventoryContents;
        this.armorContents = armorContents;
    }

    public void apply(Player player) {
        if (inventoryContents != null) player.getInventory().setContents(inventoryContents);
        if (armorContents != null) player.getInventory().setArmorContents(armorContents);
        player.updateInventory();
    }

    public void update(PlayerInventory inventory) {
        inventoryContents = inventory.getContents();
        armorContents = inventory.getArmorContents();
    }

    public Kit clone() {
        Kit kit = new Kit(this.getName());
        kit.setIcon(this.icon);
        kit.setArmorContents(Arrays.copyOf(this.armorContents, this.armorContents.length));
        kit.setInventoryContents(Arrays.copyOf(this.inventoryContents, this.inventoryContents.length));
        return kit;
    }

    public static class Type implements ParameterType<Kit> {

        @Override
        public Kit transform(CommandSender sender, String source) {
            Kit kit = Main.getInstance().getMapHandler().getKitManager().get(source);

            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "Kit '" + source + "' not found.");
                return null;
            }

            return kit;
        }

    }
}