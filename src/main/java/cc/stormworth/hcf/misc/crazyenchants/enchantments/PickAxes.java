package cc.stormworth.hcf.misc.crazyenchants.enchantments;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PickAxes implements Listener {

  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  public PickAxes() {
    Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
      if (CustomTimerCreateCommand.sotwday) {
          return;
      }
      if (event.isCancelled()) {
          return;
      }

    Block block = event.getBlock();
    Player player = event.getPlayer();
    ItemStack item = player.getItemInHand();
    List<CEnchantment> enchantments = enchantmentsManager.getEnchantmentsOnItem(item);
    boolean isOre = isOre(block.getType());
    if (CEnchantments.EXPERIENCE.isActivated() && !hasSilkTouch(item) && isOre
        && (enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()))) {
      int power = enchantmentsManager.getLevel(item, CEnchantments.EXPERIENCE);
      if (CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
        event.setExpToDrop(event.getExpToDrop() + (power + 2));
      }
    }
  }

  private boolean hasSilkTouch(ItemStack item) {
    return item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
  }

  private boolean isOre(Material material) {
    if (material == Material.QUARTZ_ORE) {
      return true;
    }
    switch (material) {
      case COAL_ORE:
      case IRON_ORE:
      case GOLD_ORE:
      case DIAMOND_ORE:
      case EMERALD_ORE:
      case LAPIS_ORE:
      case REDSTONE_ORE:
        return true;
      default:
        return false;
    }
  }
}