package cc.stormworth.hcf.misc.vouchers.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.misc.vouchers.Voucher;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class VouchersListener implements Listener {

  @EventHandler
  public void onClickEvent(PlayerInteractEvent event) {
      if (event.useInteractedBlock() == Event.Result.DENY
          && event.useItemInHand() == Event.Result.DENY || !event.getAction().name().contains("RIGHT")) {
          return;
      }
      if (!event.hasItem() || !event.getItem().hasItemMeta()) {
          return;
      }

    ItemMeta itemMeta = event.getItem().getItemMeta();
      if (!itemMeta.hasDisplayName() || !itemMeta.hasLore()) {
          return;
      }

    int hash = Voucher.calculateItemHash(itemMeta);
    Voucher voucher = Voucher.getVouchers().get(hash);

      if (voucher == null) {
          return;
      }

    if (SpawnTagHandler.isTagged(event.getPlayer())) {
      event.getPlayer().sendMessage(CC.translate("&cYou cannot use this while tagged."));
      return;
    }

    if (DTRBitmask.CONQUEST.appliesAt(event.getPlayer().getLocation())
        || DTRBitmask.KOTH.appliesAt(event.getPlayer().getLocation())) {
      event.getPlayer().sendMessage(CC.RED + "You cannot use this inside events.");
      return;
    }

    if (voucher.getCommands() != null && !voucher.getCommands().isEmpty()) {
      for (String command : voucher.getCommands()) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            command.replace("%player%", event.getPlayer().getName()));
      }
      Utils.removeOneItem(event.getPlayer());
    }
  }
}