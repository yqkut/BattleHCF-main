package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.game.CampCommand;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.stream.IntStream;

public class KitMapListener implements Listener {

  private static final String[] refillLines = new String[]{CC.translate("&7&m----------"),
      CC.translate("&6&lRefill"), CC.translate("Click Here"), CC.translate("&7&m----------")};
  private static final String[] error = {CC.translate("&7&m----------"), CC.translate("&4ERROR"),
      CC.translate(":("), CC.translate("&7&m----------")};

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();

      if (CampCommand.warping.containsKey(player.getName())) {
        CampCommand.damaged.add(player.getName());
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onPlatesInteract(final PlayerInteractEvent event) {
    if (event.getAction() != Action.PHYSICAL) {
      return;
    }
    Block block = event.getClickedBlock();
    if (block == null || !block.getType().name().contains("_PLATE")) {
      return;
    }
    if (!DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
      return;
    }

    final Player player = event.getPlayer();
    player.setVelocity(player.getLocation().getDirection().multiply(4));
    player.setVelocity(new Vector(
        Main.getInstance().getMapHandler().getLaunchX() == 0.0 ? player.getVelocity().getX()
            : Main.getInstance().getMapHandler().getLaunchX(),
        Main.getInstance().getMapHandler().getLaunchY(),
        Main.getInstance().getMapHandler().getLaunchZ() == 0.0 ? player.getVelocity().getZ()
            : Main.getInstance().getMapHandler().getLaunchZ()));

    player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.0F, 1.0F);
  }

 /* @EventHandler
  public void onSignPlace(final SignChangeEvent event) {
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      return;
    }
    if (event.getLine(0).equals("-refill")) {
      final Player player = event.getPlayer();
      if (player.isOp()) {
        for (int i = 0; i < refillLines.length; ++i) {
          event.setLine(i, refillLines[i]);
        }
      } else {
        for (int i = 0; i < error.length; ++i) {
          event.setLine(i, error[i]);
        }
      }
    }
  }*/

  /*@EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      return;
    }
    final Player player = event.getPlayer();
    final Block block = event.getClickedBlock();
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK
        || event.getAction() == Action.LEFT_CLICK_BLOCK) && block.getState() instanceof Sign) {
      final Sign sign = (Sign) block.getState();

      boolean refill = true;
      for (int i = 0; i < refillLines.length; ++i) {
        if (!sign.getLine(i).equals(refillLines[i])) {
          refill = false;
          break;
        }
      }
      if (refill) {
        if (Main.getInstance().getRefillMap().isonCooldown(player.getUniqueId())) {
          player.sendMessage(CC.translate(
              "&cYou cannot use this for another " + Main.getInstance().getRefillMap()
                  .getDetailedRemaining(player)));
          return;
        }
        player.openInventory(this.getRefillInventory());
        Main.getInstance().getRefillMap().addCooldown(player.getUniqueId(), 120L);
      }
    }
  }*/

  private Inventory getRefillInventory() {
    final Inventory inventory = Bukkit.createInventory(null, 54, CC.translate("Refill"));
    inventory.setItem(0, ItemBuilder.of(Material.ENDER_PEARL, 16).build());
    inventory.setItem(1, ItemBuilder.of(Material.ENDER_PEARL, 16).build());
    inventory.setItem(2, ItemBuilder.of(Material.ENDER_PEARL, 16).build());
    inventory.setItem(3, ItemBuilder.of(Material.POTION, 1).data((short) 8226).build());
    inventory.setItem(4, ItemBuilder.of(Material.POTION, 1).data((short) 8226).build());
    inventory.setItem(5, ItemBuilder.of(Material.POTION, 1).data((short) 8226).build());
    inventory.setItem(6, ItemBuilder.of(Material.POTION, 1).data((short) 8259).build());
    inventory.setItem(7, ItemBuilder.of(Material.POTION, 1).data((short) 8259).build());
    inventory.setItem(8, ItemBuilder.of(Material.POTION, 1).data((short) 8259).build());
    inventory.setItem(45, ItemBuilder.of(Material.SNOW_BALL, 64).name("&cDebuff").build());
    inventory.setItem(46, ItemBuilder.of(Material.INK_SACK, 64).build());
    inventory.setItem(47, ItemBuilder.of(Material.QUARTZ, 64).build());
    inventory.setItem(48, ItemBuilder.of(Material.GOLD_SWORD, 1).build());
    inventory.setItem(49, ItemBuilder.of(Material.GOLD_SWORD, 1).build());
    inventory.setItem(50, ItemBuilder.of(Material.GOLD_SWORD, 1).build());
    inventory.setItem(51, ItemBuilder.of(Material.GOLD_SWORD, 1).build());
    inventory.setItem(52, ItemBuilder.of(Material.GOLD_SWORD, 1).build());
    inventory.setItem(53, ItemBuilder.of(Material.GOLD_SWORD, 1).build());
    IntStream.range(0, inventory.getSize()).forEach(i -> {
      if (inventory.getItem(i) == null) {
        inventory.setItem(i, ItemBuilder.of(Material.POTION).data((short) 16421).build());
      }
      return;
    });
    return inventory;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onProjectileLaunch(final ProjectileLaunchEvent event) {
    final Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
    if (team != null && event.getEntity() instanceof Arrow && team.hasDTRBitmask(
        DTRBitmask.SAFE_ZONE)) {
      event.setCancelled(true);
    }
  }
}