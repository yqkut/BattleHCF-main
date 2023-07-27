package cc.stormworth.hcf.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class PlayerKit {
    private String name;

    private ItemStack[] contents;
    private String displayName;

    public void applyToPlayer(Player player) {
        for (ItemStack itemStack : contents) {
            if (itemStack != null) {
                if (itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }
        }
        player.getInventory().setContents(contents);
        player.updateInventory();
        //player.sendMessage(ChatColor.GREEN + "Giving you " + ChatColor.YELLOW + displayName + ChatColor.GREEN + ".");
    }

}
