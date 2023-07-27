package cc.stormworth.hcf.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.player.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrowbarListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (event.getItem() == null || !InventoryUtils.isSimilar(event.getItem(),
        InventoryUtils.CROWBAR_NAME) || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    if (!Main.getInstance().getServerHandler()
        .isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) && !Main.getInstance()
        .getServerHandler().isAdminOverride(event.getPlayer())) {
      final Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());
      if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
        event.getPlayer().sendMessage(
            ChatColor.YELLOW + "You cannot crowbar in " + ChatColor.RED + team.getName(
                event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
        return;
      }
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.getPlayer()
          .sendMessage(ChatColor.YELLOW + "You cannot use a crowbar inside safe-zone.");
      return;
    }
    if (event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
      int portals = InventoryUtils.getCrowbarUsesPortal(event.getItem());
      if (portals == 0) {
        event.getPlayer()
            .sendMessage(ChatColor.RED + "This crowbar has no more uses on end portals!");
        return;
      }
      event.getClickedBlock().getWorld()
          .playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND,
              event.getClickedBlock().getTypeId());
      event.getClickedBlock().setType(Material.AIR);
      event.getClickedBlock().getState().update();
      event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(),
          new ItemStack(Material.ENDER_PORTAL_FRAME));
      event.getClickedBlock().getWorld()
          .playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0f, 1.0f);
      for (int x = -3; x < 3; ++x) {
        for (int z = -3; z < 3; ++z) {
          final Block block = event.getClickedBlock().getLocation().add(x, 0.0, z).getBlock();
          if (block.getType() == Material.ENDER_PORTAL) {
            block.setType(Material.AIR);
            block.getWorld()
                .playEffect(block.getLocation(), Effect.STEP_SOUND, Material.ENDER_PORTAL.getId());
          }
        }
      }
      if (--portals == 0) {
        event.getPlayer().setItemInHand(null);
        event.getClickedBlock().getLocation().getWorld()
            .playSound(event.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
        return;
      }
      final ItemMeta meta = event.getItem().getItemMeta();
      meta.setLore(InventoryUtils.getCrowbarLore(portals, 0));
      event.getItem().setItemMeta(meta);
      final double max = Material.DIAMOND_HOE.getMaxDurability();
      final double dura = max / 6.0 * portals;
      event.getItem().setDurability((short) (max - dura));
      event.getPlayer().setItemInHand(event.getItem());
    } else if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
      final CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();
      int spawners = InventoryUtils.getCrowbarUsesSpawner(event.getItem());
      if (spawners == 0) {
        event.getPlayer()
            .sendMessage(ChatColor.RED + "This crowbar has no more uses on mob spawners!");
        return;
      }
      if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.NETHER
          && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the nether!");
        event.setCancelled(true);
        return;
      }
      if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.THE_END
          && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the end!");
        event.setCancelled(true);
        return;
      }
      event.getClickedBlock().getLocation().getWorld()
          .playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND,
              event.getClickedBlock().getTypeId());
      event.getClickedBlock().setType(Material.AIR);
      event.getClickedBlock().getState().update();
      final ItemStack drop = new ItemStack(Material.MOB_SPAWNER);
      ItemMeta meta2 = drop.getItemMeta();
      EntityType type = EntityType.valueOf(spawner.getSpawnedType().name());
      event.getClickedBlock().getLocation().getWorld()
          .dropItemNaturally(event.getClickedBlock().getLocation(), Utils.getSpawnerItem(1, type));

      event.getClickedBlock().getLocation().getWorld()
          .playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0f, 1.0f);
      if (--spawners == 0) {
        event.getPlayer().setItemInHand(null);
        event.getClickedBlock().getLocation().getWorld()
            .playSound(event.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
        return;
      }
      meta2 = event.getItem().getItemMeta();
      meta2.setLore(InventoryUtils.getCrowbarLore(0, spawners));
      event.getItem().setItemMeta(meta2);
      final double max2 = Material.DIAMOND_HOE.getMaxDurability();
      final double dura2 = max2 / 1.0 * spawners;
      event.getItem().setDurability((short) (max2 - dura2));
      event.getPlayer().setItemInHand(event.getItem());
    } else {
      event.getPlayer()
          .sendMessage(ChatColor.RED + "Crowbars can only break end portals and mob spawners!");
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBreak(final BlockBreakEvent event) {
    if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER
        && event.getBlock().getType() == Material.MOB_SPAWNER
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the nether!");
      event.setCancelled(true);
      return;
    }
    if (event.getBlock().getType() == Material.MOB_SPAWNER
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.getPlayer().sendMessage(
          ChatColor.RED + "This is too strong for you to break! Try using a crowbar instead.");
      event.setCancelled(true);
    }
  }
}