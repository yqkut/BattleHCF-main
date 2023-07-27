package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.PlayerUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class BasicPreventionListener implements Listener {

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    if (event.getClickedInventory() == null || event.getCurrentItem() == null || event.getSlotType() == null || event.getCurrentItem().getType() == Material.AIR) {
      return;
    }
    Player player = (Player) event.getWhoClicked();
    Inventory top = player.getOpenInventory().getTopInventory();
    if(!top.getName().equals(CC.translate("&a&lClaimed Items"))) return;
    Inventory inventory = event.getClickedInventory();
    if(inventory.equals(top)) {
      if(event.getAction().name().contains("PLACE")) {
        event.setCancelled(true);
        return;
      }
    }
    if(inventory.equals(player.getInventory())) {
      if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    if (event.getEntity() instanceof Wither) {
      event.setCancelled(true);
    }
    if (event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase("void")) {
      event.setCancelled(true);
      return;
    }
    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
    ItemStack item = event.getItem();
    Player player = event.getPlayer();

    if (item.getType() != Material.MILK_BUCKET) {
      return;
    }

    HCFProfile profile = HCFProfile.getByUUID(event.getPlayer().getUniqueId());
    profile.getEnchantments().clear();

    event.setCancelled(true);
    player.setItemInHand(new ItemStack(Material.BUCKET));
    player.removePotionEffect(PotionEffectType.BLINDNESS);
    player.removePotionEffect(PotionEffectType.CONFUSION);
    player.removePotionEffect(PotionEffectType.HARM);
    player.removePotionEffect(PotionEffectType.HUNGER);
    player.removePotionEffect(PotionEffectType.POISON);
    player.removePotionEffect(PotionEffectType.SLOW);
    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    player.removePotionEffect(PotionEffectType.WEAKNESS);
    player.setFoodLevel(20);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onClick(InventoryClickEvent event) {
    if (event.getCurrentItem() == null
        || event.getCurrentItem().getType() != Material.SKULL_ITEM) {
      return;
    }

    if (event.getInventory().getTitle().startsWith("Chest #") || event.getInventory().getTitle()
        .startsWith("Ender Chest")) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCommandPreprocess(final PlayerCommandPreprocessEvent event) {
    if ((event.getMessage().toLowerCase().startsWith("/fix") || event.getMessage().toLowerCase()
        .startsWith("/repair"))
        && SpawnTagHandler.isTagged(event.getPlayer())
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.getPlayer()
          .sendMessage(ChatColor.RED + "You cannot use this command while spawn tagged.");
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onVehicleEnter(final VehicleEnterEvent event) {
    if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
      final Horse horse = (Horse) event.getVehicle();
      final Player player = (Player) event.getEntered();
      if (horse.getOwner() != null && !horse.getOwner().getName().equals(player.getName())) {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "This is not your horse!");
      }
    }
  }

  @EventHandler
  public void onFoodLevelChange(final FoodLevelChangeEvent event) {
    Player player = (Player) event.getEntity();
    HCFProfile profile = HCFProfile.get(player);

    if (event.getFoodLevel() < player.getFoodLevel()) {
      if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") || EOTWCommand.isFfaEnabled() || profile.hasSotwTimer()) {
        event.setCancelled(true);
        return;
      }
      if (player.getWorld().getName().equalsIgnoreCase("void")) {
        event.setCancelled(true);
        return;
      }

      if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
        event.setCancelled(true);
        return;
      }

      if (Main.getInstance().getMapHandler().isKitMap()) {
        event.setCancelled(player.hasPermission("hcf.hunger.bypass"));
      }


      // Make food drop 1/2 as fast if you have PvP protection
      if (CorePlugin.RANDOM.nextInt(100) > (profile.hasPvPTimer() ? 10 : 30)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityPressurePlate(EntityInteractEvent event) {
    if (event.getBlock().getType().name().contains("_PLATE")
        && !(event.getEntity() instanceof Player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getEntity().getType() == EntityType.VILLAGER
        || event.getEntity().getType() == EntityType.ITEM_FRAME
        || event.getEntity().getType() == EntityType.DROPPED_ITEM
        || event.getEntity().getType() == EntityType.PIG) {
      return;
    }
    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
        && event.getEntity().getType() == EntityType.SKELETON
        && ((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
      event.setCancelled(true);
    }
        /*if (event.getEntity().getLocation().getChunk().getEntities().length > 8) {
            event.setCancelled(true);
        }*/
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER && (
        event.getBlock().getType() == Material.BED
            || event.getBlock().getType() == Material.BED_BLOCK)) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "You cannot place beds in the Nether.");
    }

    Player player = event.getPlayer();
    if (player.getWorld().getName().equalsIgnoreCase("void")) {
      return;
    }

    Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());

    if (teamAt != null && !teamAt.getMembers().contains(player.getUniqueId())) {

      if (player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight()) {
        return;
      }

      if(teamAt.isRaidable()){
        return;
      }


      Block block = event.getBlockPlaced();

      //Check if block is below of player
      if (block.getLocation().getBlockX() == player.getLocation().getBlockX()
              && block.getLocation().getBlockY() != player.getLocation().getBlockY() - 1
              && block.getLocation().getBlockZ() == player.getLocation().getBlockZ()) {
        return;
      }

      if (block.getType().isSolid() && !(block.getState() instanceof Sign)
          && block.getType() != Material.WEB && !event.getPlayer().hasMetadata("ImmuneFromGlitchCheck")) {
        //player.teleport(player.getLocation());
        //player.setNoDamageTicks(0);
        player.setVelocity(player.getVelocity().setY(-1));
        //System.out.println("Glitch detected");
      }
    }
  }

  @EventHandler
  public void onBlockPlace(BlockBreakEvent event) {
    Player player = event.getPlayer();
    if (player.getWorld().getName().equalsIgnoreCase("void")) {
      return;
    }

    Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());

    if (teamAt != null && !teamAt.getMembers().contains(player.getUniqueId())) {
      if (player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight()) {
        return;
      }

      if(teamAt.isRaidable()){
        return;
      }

      Block block = event.getBlock();

      //Check if block is below of player
      if (block.getLocation().getBlockX() == player.getLocation().getBlockX()
          && block.getLocation().getBlockY() != player.getLocation().getBlockY() - 1
          && block.getLocation().getBlockZ() == player.getLocation().getBlockZ()) {
        return;
      }

      if(block.getType() == Material.DOUBLE_PLANT || block.getType() == Material.LONG_GRASS ||
              block.getType() == Material.YELLOW_FLOWER || block.getType() == Material.RED_ROSE || block.getType() == Material.SAPLING ||
              block.getType() == Material.DEAD_BUSH || block.getType() == Material.VINE || block.getType() == Material.WATER_LILY ||
              block.getType() == Material.RED_MUSHROOM || block.getType() == Material.BROWN_MUSHROOM || block.getType() == Material.CACTUS ||
              block.getType() == Material.SUGAR_CANE || block.getType() == Material.STRING || block.getType() == Material.REDSTONE ||
              block.getType() == Material.REDSTONE_WIRE || block.getType() == Material.REDSTONE_TORCH_ON || block.getType() == Material.REDSTONE_TORCH_OFF ||
              block.getType() == Material.REDSTONE_COMPARATOR_ON || block.getType() == Material.REDSTONE_COMPARATOR_OFF ||
              block.getType() == Material.DIODE_BLOCK_ON || block.getType() == Material.DIODE_BLOCK_OFF){
        return;
      }

      if (!(block.getState() instanceof Sign) && block.getType() != Material.WEB && !event.getPlayer().hasMetadata("ImmuneFromGlitchCheck")) {
        player.teleport(player.getLocation());
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockIgnite(BlockIgniteEvent event) {
    if (event.getPlayer() == null) {
      return;
    }

    Block targetBlock = event.getPlayer().getTargetBlock(null, 4);
    if (targetBlock.getType() != Material.OBSIDIAN) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onFireBurn(BlockBurnEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    event.blockList().clear();
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onDamageByEntity(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {

      Player player = (Player) event.getEntity();

      HCFProfile profile = HCFProfile.get(player);

      if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
        return;
      }

      if (profile.hasSotwTimer()) {
        event.setCancelled(true);
      }

      Player damager = PlayerUtils.getDamageSource(event.getDamager());
      if (damager != null && !CustomTimerCreateCommand.hasSOTWEnabled(
              damager.getUniqueId())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onDamage(final EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") &&
            !CustomTimerCreateCommand.hasSOTWEnabled(event.getEntity().getUniqueId())) {
      event.setCancelled(true);
    }

    if (event.getEntity() instanceof Player) {
      HCFProfile profile = HCFProfile.get((Player) event.getEntity());

      if (profile.hasSotwTimer()) {
        event.setCancelled(true);
      }
    }

    if (event.getEntity() instanceof ItemFrame && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
      event.setCancelled(true);
    }
  }
}