package cc.stormworth.hcf.refill;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.refill.menu.RefillTypesMenu;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RefillListener implements Listener {

  @EventHandler
  public void onSignChange(SignChangeEvent event) {
    Player player = event.getPlayer();
    Sign sign = (Sign) event.getBlock().getState();

    if (!player.isOp()) {
      return;
    }

    if (!event.getLine(0).equalsIgnoreCase("[refill]")) {
      return;
    }

    event.setLine(0, "");
    event.setLine(1, CC.translate("&6&lRefill"));
    event.setLine(2, CC.translate("&8(Right click)"));
    event.setLine(3, "");
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    if (!block.getType().name().contains("SIGN")) {
      return;
    }

    if (!this.isRefillSign((Sign) block.getState())) {
      return;
    }

    if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
      player.sendMessage(CC.translate("&cYou can only use this sign in the Safe Zone."));
      return;
    }

    new RefillTypesMenu().openMenu(player);
  }

  private boolean isRefillSign(Sign sign) {
    return CC.strip(sign.getLine(1)).equalsIgnoreCase("Refill");
  }

}
