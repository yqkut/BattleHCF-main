package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class RepairSignListener implements Listener {

  private static final String[] repairlines = {CC.translate("&7&m----------"),
      CC.translate("&6&lRepair"), CC.translate("$500"), CC.translate("&7&m----------")};
  private static final String[] error = {CC.translate("&7&m----------"), CC.translate("&4ERROR"),
      CC.translate(":("), CC.translate("&7&m----------")};
  //public static Location repairloc;

  @EventHandler
  public void onSignPlace(SignChangeEvent event) {
    if (event.getLine(0).equalsIgnoreCase("-repair")) {
      Player player = event.getPlayer();
      if (player.isOp()) {
        for (int i = 0; i < repairlines.length; i++) {
          event.setLine(i, repairlines[i]);
        }
      } else {
        for (int i = 0; i < error.length; i++) {
          event.setLine(i, error[i]);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void repairAnvil(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Block block = event.getClickedBlock();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ((block.getState() instanceof Sign))) {
      Sign sign = (Sign) block.getState();
      for (int i = 0; i < repairlines.length; i++) {
        if (!sign.getLine(i).equals(repairlines[i])) {
          return;
        }
      }
      event.setUseItemInHand(Event.Result.DENY);
      new BukkitRunnable() {
        @Override
        public void run() {
          ItemStack item = player.getItemInHand();
          if (item == null
              || item.getType() == Material.AIR
              || item.getType() == Material.POTION
              || item.getType() == Material.GOLDEN_APPLE
              || item.getType().isBlock()
              || item.getType().getMaxDurability() < 1) {
            player.sendMessage(CC.RED + "This item cannot be repaired.");
            return;
          }
          if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String lore : item.getItemMeta().getLore()) {
              if (lore.equals(CC.translate("&cUnrepairable"))) {
                player.sendMessage(CC.translate("&cThis item is unrepairable"));
                return;
              }
            }
          }
          if (item.getDurability() == 0) {
            player.sendMessage(CC.RED + "This item is already repaired.");
            return;
          }

          HCFProfile profile = HCFProfile.get(player);

          double balance = profile.getEconomyData().getBalance();

          int amount = 500;

          if (amount > balance) {
            player.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
          }
          item.setDurability((short) 0);
          player.sendMessage(CC.translate("&aItem in your hand has been repaired."));
          profile.getEconomyData().subtractBalance(amount);
        }
      }.runTaskAsynchronously(Main.getInstance());
    }
  }
}