package cc.stormworth.hcf.bounty;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.bounty.menu.BountyMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BountyListener implements Listener {

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();
    Player player = (Player) event.getWhoClicked();

    if (event.getClickedInventory() == player.getInventory()) {
      return;
    }

    if (inventory.getName().equalsIgnoreCase(CC.translate("&eAdd Bounty items"))) {

      if (event.getSlot() == 4) {
        event.setCancelled(true);

        player.closeInventory();
        Button.playNeutral(player);
      }

      if (event.getSlot() == 0) {
        event.setCancelled(true);

        player.closeInventory();
        Button.playNeutral(player);
      }

      ItemStack item = event.getCurrentItem();

      if (item != null && item.getType() != Material.AIR) {
        if (item.getType() == Material.STAINED_GLASS_PANE) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();

    Player player = (Player) event.getPlayer();

    if (inventory.getName().equalsIgnoreCase(CC.translate("&eAdd Bounty items"))) {
      BountyPlayer bountyPlayer = BountyPlayer.getAddedBy(player);

      if(bountyPlayer == null){
        return;
      }

      bountyPlayer.getRewards().clear();

      for (ItemStack item : inventory.getContents()) {
        if (item != null && item.getType() != Material.AIR) {

          if (item.getType() == Material.STAINED_GLASS_PANE
              || item.getType() == Material.BED || item.getType() == Material.STAINED_GLASS) {
            continue;
          }

          bountyPlayer.getRewards().add(item);
        }
      }

      player.sendMessage(CC.translate("&aYou have successfully added the bounty items!"));

      TaskUtil.runLater(Main.getInstance(), () -> new BountyMenu(bountyPlayer).openMenu(player),
          3L);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    BountyPlayer bountyPlayer = BountyPlayer.getAddedBy(player);

    if (bountyPlayer == null) {
      return;
    }

    Player addedBy = Bukkit.getPlayer(bountyPlayer.getAddedBy());

    if (addedBy != null) {

      HCFProfile profile = HCFProfile.get(addedBy);

      profile.getEconomyData().addBalance(bountyPlayer.getBalance());

      for (ItemStack itemStack : bountyPlayer.getRewards()) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
          continue;
        }

        if (player.getInventory().firstEmpty() == -1) {
          player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
          continue;
        }

        player.getInventory().addItem(itemStack);
      }

      addedBy.sendMessage(CC.translate("&c" + player.getName() + " has left the server."));
      addedBy.sendMessage(CC.translate(
          "&aYou have been refunded $&e" + bountyPlayer.getBalance() + " &a and bounty items."));
    }

    BountyPlayer.getBounties().remove(player.getUniqueId());
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Player killer = player.getKiller();

    if (killer == null) {
      return;
    }

    BountyPlayer bountyPlayer = BountyPlayer.get(player.getUniqueId());

    if (bountyPlayer == null) {
      return;
    }

    HCFProfile profile = HCFProfile.get(killer);
    profile.getEconomyData().addBalance(bountyPlayer.getBalance());

    for (ItemStack itemStack : bountyPlayer.getRewards()) {
      if (itemStack == null || itemStack.getType() == Material.AIR) {
        continue;
      }

      if (killer.getInventory().firstEmpty() == -1) {
        killer.getWorld().dropItemNaturally(killer.getLocation(), itemStack);
        continue;
      }

      killer.getInventory().addItem(itemStack);
    }

    CorePlugin.getInstance().getNametagEngine().reloadPlayer(bountyPlayer.getTarget());
    CorePlugin.getInstance().getNametagEngine().reloadOthersFor(bountyPlayer.getTarget());

    BountyPlayer.getBounties().remove(player.getUniqueId());

    player.sendMessage(CC.translate("&aYou received $&e" + bountyPlayer.getBalance() + "&a."));
    player.sendMessage(CC.translate("&aAnd following items:"));

    for (ItemStack itemStack : bountyPlayer.getRewards()) {
      String displayName = (
          itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null
              ? itemStack.getItemMeta().getDisplayName()
              : WordUtils.capitalize(itemStack.getType().name().toLowerCase().replace("_", " ")));

      player.sendMessage(CC.translate("&7- &f" + displayName));
    }

    Bukkit.broadcastMessage(CC.translate(
        "&7[&6Bounty&7] &6" + killer.getName() + " &ehas claimed &6" + player.getName()
            + " &ebounty by killing him."));
  }

}