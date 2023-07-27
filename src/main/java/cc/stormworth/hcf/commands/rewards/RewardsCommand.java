package cc.stormworth.hcf.commands.rewards;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.rewards.RewardsMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RewardsCommand {

  @Command(names = {"rewards"}, permission = "")
  public static void rewards(Player player) {
    new RewardsMenu().openMenu(player);
  }

  @Command(names = {"rewards edit"}, permission = "")
  public static void edit(Player player) {
    Inventory inventory = Bukkit.createInventory(null, 9 * 3, CC.translate("&6Rewards Edit"));

    for (ItemStack reward : Main.getInstance().getRewardsManager().getRewards()) {
      if (reward != null) {
        inventory.addItem(reward);
      }
    }

    player.openInventory(inventory);
  }

}