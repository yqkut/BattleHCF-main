package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.profile.event.PlayerVoteServerEvent;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.onedoteight.ActionBarUtils;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.mad.MadGame;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import cc.stormworth.hcf.server.RegionData;
import cc.stormworth.hcf.server.RegionType;
import cc.stormworth.hcf.server.ServerHandler;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.commands.team.TeamClaimCommand;
import cc.stormworth.hcf.team.commands.team.TeamResizeCommand;
import cc.stormworth.hcf.team.commands.team.TeamStuckCommand;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.team.event.PlayerInteractInOthersClaimEvent;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import cc.stormworth.hcf.voteparty.VotePartyHandler;
import com.google.common.collect.ImmutableSet;
import dev.nulledcode.spigot.events.BeaconEffectEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Material.*;

public class PlayerListener implements Listener {

  public static final Set<PotionEffectType> DEBUFFS;
  public static final Set<PotionEffectType> NORMALPOTION;
  public static final Set<Material> NO_INTERACT_WITH;
  public static final Set<Material> NO_INTERACT;

  static {
    DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW,
        PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    NORMALPOTION = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW,
        PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    NO_INTERACT_WITH = ImmutableSet.of(Material.LAVA_BUCKET, Material.WATER_BUCKET,
        Material.BUCKET);
    NO_INTERACT = ImmutableSet.of(Material.REDSTONE_COMPARATOR,
        Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.DIODE,
        DIODE_BLOCK_ON, DIODE_BLOCK_OFF,
        Material.FENCE_GATE, Material.FURNACE,
        Material.BURNING_FURNACE, Material.BREWING_STAND,
        Material.CHEST,
        Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR, Material.STONE_BUTTON,
        Material.WOOD_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER,
        Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL,
        Material.BEACON);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerTeleport(final PlayerTeleportEvent event) {
    this.processTerritoryInfo(event);
  }


  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Arrow)) {
      return;
    }

    if(event.getTarget() == null){
      return;
    }

    Arrow arrow = (Arrow) event.getEntity();

    if (arrow.getShooter() instanceof Player) {
      Player player = (Player) arrow.getShooter();

      player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
    }
  }

  @EventHandler
  public void onCraftItem(PrepareItemCraftEvent event) {
    ItemStack item = event.getInventory().getResult();

    if (item.getType() == Material.LEATHER_BOOTS
        || item.getType() == Material.LEATHER_CHESTPLATE
        || item.getType() == Material.LEATHER_HELMET
        || item.getType() == Material.LEATHER_LEGGINGS) {

      LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

      if (!meta.getColor().equals(Color.fromRGB(160, 101, 64))) {
        item.setType(Material.AIR);
      }
    }

  }

  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    HCFProfile profile = HCFProfile.get(player);

    if (player.hasMetadata("removeParticles")) {
      profile.setParticlesBattle(false);
      player.removeMetadata("removeParticles", Main.getInstance());
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerTeleport(final EntityDamageEvent event) {

    if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (player.getInventory().getBoots() == null) {
      return;
    }
    if (!player.getInventory().getBoots().containsEnchantment(Enchantment.PROTECTION_FALL)) {
      return;
    }
    event.setDamage(event.getDamage() * 1.52);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(final PlayerMoveEvent event) {
    if (event.getFrom().getBlockX() == event.getTo().getBlockX()
        && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
      return;
    }

    this.processTerritoryInfo(event);
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final Player player = (Player) event.getWhoClicked();
    final Inventory inventory = event.getInventory();
    final InventoryType.SlotType slotType = event.getSlotType();
    if (inventory.getType().equals(InventoryType.ANVIL) && slotType.equals(
        InventoryType.SlotType.RESULT)) {
      final ItemStack item = event.getCurrentItem();
      if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
        for (final String lore : item.getItemMeta().getLore()) {
          if (lore.equals(CC.translate("&cUnrepairable"))) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cThis item is unrepairable."));
            return;
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlace(final BlockPlaceEvent event) {
    if (event.isCancelled() || event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getBlock().getType() == Material.ENDER_CHEST
        && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
      event.setCancelled(true);
    }
    if (event.getBlock().getType() == Material.WEB) {
      if (Main.getInstance().getServerHandler().isEOTW() || Main.getInstance()
          .getServerHandler()
          .isPreEOTW()) {
        return;
      }

      if (EOTWCommand.isFfaEnabled()){
        event.setCancelled(true);
        return;
      }

      new BukkitRunnable() {
        public void run() {
          if (event.getBlock().getType() == Material.WEB) {
            event.getBlock().setType(Material.AIR);
          }
        }
      }.runTaskLater(Main.getInstance(), 20 * 10); //wtf xd
    }
  }

 /* @EventHandler
  public void onDamage(EntityDamageEvent event){
    if(event.getEntity() instanceof Player){
      Player player = (Player) event.getEntity();

      if (event.getCause() == EntityDamageEvent.DamageCause.WITHER){
        event.setCancelled(true);
      }

    }
  }*/

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    HCFProfile profile = HCFProfile.get(player);

    if(profile == null){
      return;
    }

    if (player.getGameMode() == GameMode.ADVENTURE) {
      player.setGameMode(GameMode.SURVIVAL);
    }

    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
      if (potionEffect.getDuration() > 1_000_000) {
        player.removePotionEffect(potionEffect.getType());
      }
    }

    //Main.getInstance().getPlaytimeMap().playerJoined(player.getUniqueId());

    if (!player.hasPlayedBefore() && !Main.getInstance().getServerHandler().isPreEOTW()) {

      player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());

      TitleBuilder titleBuilder = new TitleBuilder("&6&lWelcome to Battle Network",
              "&eUse &7/f create &eto create your team. &a* NEW * &e/gkit sotw",
              10, 60, 10);

      titleBuilder.send(player);

      if (Main.getInstance().getServerHandler().getFjiItems() != null) {
        player.getInventory().setContents(Main.getInstance().getServerHandler().getFjiItems());
      }

      if (profile.getEconomyData().getBalance() <= 0.00) {
        if(Main.getInstance().getMapHandler().isKitMap()){
          profile.getEconomyData().setBalance(3000);
        }else{
          profile.getEconomyData().setBalance(800);
        }
      }

      /*List<String> lines = Lists.newArrayList(
              "&7&m----------------------------------------------------",
              "&9&lUPDATES &7(Changelogs)",
              "",
              "&7➟&e ChatReaction &7(Fix)",
              "&7➟&e Build NPC &a(Added)",
              "&7➟&e Build Command > /buildapply &a(Added)",
              "&7➟&e SOTW Timer ended lunar Tittle &a(Added)",
              "&7➟&e First join Welcome tittle expand the time &a(Added)",
              "&7➟&e Battle Chat Reactions &7(Fixed)",
              "&7➟&e Faction Upgrades &7(Fixed)",
              "&7&m----------------------------------------------------"
      );

      lines.forEach(line -> player.sendMessage(CC.translate(line)));*/
      //BookUtils.openBook(book, player);

      if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
        profile.setPvpTimer(new PvPTimer(true));
      }
    }
    
    if (profile.hasPvPTimer() && (Main.getInstance().getServerHandler().isEOTW() || Main.getInstance().getServerHandler().isPreEOTW())) {
      profile.setPvpTimer(null);
    }

    if (profile.hasPvPTimer() && DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())){
      profile.getPvpTimer().pause();
    }

    if (profile.getEconomyData().getBalance() <= 0.00) { //Remove this in next map
      if(Main.getInstance().getMapHandler().isKitMap()){
        profile.getEconomyData().setBalance(3000);
      }
    }

    player.sendMessage("");
    player.sendMessage(CC.translate("&eHey! &6&l" + player.getName()));
    player.sendMessage("");
    player.sendMessage(
        CC.translate("&eLearn more about us and how the gameplay works by using &6&l/tips"));
    player.sendMessage("");

    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onPotionEffectAdd(final BeaconEffectEvent event) {
    Player player = event.getPlayer();
    PotionEffect effect = event.getEffect();

    if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
      event.setCancelled(true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 180, 0));
    }
    
    if (effect.getAmplifier() > 0 && effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
      event.setCancelled(true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 180, 0));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onStealthPickaxe(final BlockBreakEvent event) {
    final Block block = event.getBlock();
    final ItemStack inHand = event.getPlayer().getItemInHand();
    if (inHand.getType() == Material.GOLD_PICKAXE && inHand.hasItemMeta()
        && inHand.getItemMeta()
        .getDisplayName().startsWith(ChatColor.AQUA.toString())) {
      event.setCancelled(true);
      block.breakNaturally(inHand);
    }
  }

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (event.getItemDrop().getItemStack().equals(TeamClaimCommand.SELECTION_WAND)
        || event.getItemDrop().getItemStack().equals(TeamResizeCommand.SELECTION_WAND)) {
      event.getItemDrop().remove();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityDamage(final EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (Main.getInstance().getServerHandler().getSpawntasks()
          .containsKey(player.getName())) {
        Main.getInstance().getServer().getScheduler().cancelTask(
            Main.getInstance().getServerHandler().getSpawntasks().get(player.getName())
                .getTaskId());
        Main.getInstance().getServerHandler().getSpawntasks().remove(player.getName());
        player.sendMessage(
            ChatColor.YELLOW.toString() + ChatColor.BOLD + "TELEPORTING " + ChatColor.RED
                + ChatColor.BOLD + "CANCELLED!");
      }
      if (Main.getInstance().getServerHandler().getLogouttasks()
          .containsKey(player.getName())) {
        Main.getInstance().getServer().getScheduler().cancelTask(
            Main.getInstance().getServerHandler().getLogouttasks().get(player.getName())
                .getTaskId());
        Main.getInstance().getServerHandler().getLogouttasks().remove(player.getName());
        CooldownManager.removeCooldown(player.getUniqueId(), CooldownType.LOGOUT);
        player.sendMessage(
            ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED
                + ChatColor.BOLD + "CANCELLED!");
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    Block block = event.getClickedBlock();
    Player player = event.getPlayer();

    if (block == null) {
      return;
    }

    if (Main.getInstance().getServerHandler().isUnclaimedOrRaidable(block.getLocation()) || Main.getInstance().getServerHandler().isAdminOverride(player)) {
      return;
    }

    if (DTRBitmask.ROAD.appliesAt(block.getLocation())){
      return;
    }

    Team team = LandBoard.getInstance().getTeam(block.getLocation());
    if (team != null && !team.isMember(player.getUniqueId())) {
      if (team.hasDTRBitmask(DTRBitmask.RESTRICTED_EVENT)) {
        if (Main.getInstance().getEventHandler().getEvent("citadel") != null
            && !Main.getInstance().getEventHandler().getEvent("citadel").isActive()
            && Main.getInstance().getConquestHandler().getGame() == null) {
          return;
        }
      }

      if (NO_INTERACT.contains(block.getType()) && Main.getInstance().getEventHandler().getEclipseEvent().isActive() && Main.getInstance().getEventHandler().getEclipseEvent().isInRadius(block.getLocation())) {
        return;
      }

      if (NO_INTERACT.contains(block.getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {

          PlayerInteractInOthersClaimEvent interactEvent = new PlayerInteractInOthersClaimEvent(player, block, team);

          Bukkit.getPluginManager().callEvent(interactEvent);

          if (interactEvent.isWillIgnore()) {
            return;
          }
        }

        if (event.getItem() != null &&
            (event.getItem().getType() == Material.POTION
                || event.getItem().getType() == GOLDEN_APPLE
                || event.getItem().getType() == SNOW_BALL
                || event.getItem().getType() == EGG
                || event.getItem().getType() == BOW) && player.isSneaking()) {
          return;
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.YELLOW + "You cannot interact here: " + team.getName(player) + ChatColor.YELLOW + ".");

        if (player.hasMetadata("switcher")) {
          player.removeMetadata("switcher", Main.getInstance());
        }
      } else if (event.getAction() == Action.PHYSICAL) {
        event.setCancelled(true);
      }
    } else if (event.getMaterial() == Material.LAVA_BUCKET && (team == null || !team.isMember(player.getUniqueId()))) {
      event.setCancelled(true);
      player.sendMessage(ChatColor.RED + "You can only do this in your own claims!");
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onSignPlace(final BlockPlaceEvent event) {
    Block block = event.getBlock();
    ItemStack hand = event.getItemInHand();
    if (hand.getType() == SIGN) {
      if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
        List<String> lore = hand.getItemMeta().getLore();

        if (block.getType() == WALL_SIGN
            || block.getType() == SIGN_POST) {
          Sign s = (Sign) block.getState();

          for (int i = 0; i < 4; i++) {
            s.setLine(i, lore.get(i));
          }

          event.getPlayer().closeInventory();
          s.setMetadata("deathSign", new FixedMetadataValue(Main.getInstance(), true));
          s.update();
        }
      }
    }
  }

  @EventHandler
  public void onSignChange(final SignChangeEvent e) {
    if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock()
        .getState()).getLine(1).contains("§e")) {
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onSignBreak(BlockBreakEvent e) {
    if (e.getBlock().getType() == Material.WALL_SIGN
        || e.getBlock().getType() == Material.SIGN_POST) {
      if (e.getBlock().getState().hasMetadata("deathSign") || ((
          e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(
                  1)
              .contains("§e")))) {
        e.setCancelled(true);

        Sign sign = (Sign) e.getBlock().getState();

        ItemStack deathsign = new ItemStack(Material.SIGN);
        ItemMeta meta = deathsign.getItemMeta();

        if (sign.getLine(1).contains("Captured")) {
          meta.setDisplayName("§6KOTH Capture Sign");
        } else {
          meta.setDisplayName("§6Death Sign");
        }

        meta.setLore(Arrays.asList(sign.getLines()));
        deathsign.setItemMeta(meta);
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

        e.getBlock().setType(Material.AIR);
        e.getBlock().getState().removeMetadata("deathSign", Main.getInstance());
      }
    }
  }

  @EventHandler
  public void onRefund(final PotionEffectAddEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
        CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
      }
    }
  }

  @EventHandler
  public void onExpire(final PotionEffectExpireEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
        CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
      }
    }
  }

  @EventHandler
  public void onRemove(final PotionEffectRemoveEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
        CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDrop(final PlayerDropItemEvent event) {
    final Team team = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
    if (event.getPlayer().getGameMode() != GameMode.CREATIVE && team != null
        && team.hasDTRBitmask(
        DTRBitmask.SAFE_ZONE)) {
      if (Main.getInstance().getMapHandler().isKitMap()
          || !CustomTimerCreateCommand.itemsInSpawn) {
        event.getItemDrop().remove();
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPortal(EntityPortalEvent event) {
    if (event.getEntityType() == EntityType.PLAYER) {
      return;
    }

    event.getEntity().remove();
  }

  /*@EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event) {

    if(Main.getInstance().getTeamHandler().getTeam(event.getEntity()) != null && Main.getInstance().getFactionDuelManager().isInMatch(Main.getInstance().getTeamHandler().getTeam(event.getEntity()))){
      return;
    }

    if (Main.getInstance().getMapHandler().isKitMap()) {
      KitManager.getLastClicked()
          .remove(event.getEntity().getUniqueId());
    }
    Utils.removeThrownPearls(event.getEntity());

    if (event.getEntity().getKiller() != null
        && Main.getInstance().getDeathbannedMap().isDeathbanned(event.getEntity().getKiller().getUniqueId())
        && !Main.getInstance().getDeathbannedMap().isDeathbanned(event.getEntity().getUniqueId())) {
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
          // Add kills to sword lore if the victim does not equal the killer
          if (!event.getEntity().equals(killer)) {
            ItemStack hand = killer.getItemInHand();

            if (hand.getType().name().contains("SWORD") || hand.getType() == BOW) {
              InventoryUtils.addKill(hand, ChatColor.YELLOW + killer.getDisplayName() + ChatColor.YELLOW + " " + (hand.getType() == BOW ? "shot" : "killed") + " "
                      + event.getEntity().getDisplayName());
            }
          }
        }

        if (!Utils.isDeathbanned(event.getEntity().getUniqueId())) {
          Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getEntity());

          if (playerTeam != null) {
            playerTeam.playerDeath(event.getEntity().getName(),
                event.getEntity().getUniqueId(),
                event.getEntity().getKiller() != null ? event.getEntity().getKiller() : null,
                Main.getInstance().getServerHandler().getDTRLoss(event.getEntity()));
          }

          if (!EOTWCommand.isFfaEnabled()) {
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
          }

          CooldownAPI.removeCooldown(event.getEntity(), "enderpearl");

          boolean shouldBypass = event.getEntity().hasPermission("core.staff");

          if (Main.getInstance().getMapHandler().isKitMap() || shouldBypass || (
              event.getEntity().hasPermission("deathban.bypass") &&
                      !Main.getInstance().getServerHandler().isEOTW() && !Main.getInstance().getServerHandler().isPreEOTW())) {

            Main.getInstance().getDeathbanMap().revive(event.getEntity().getUniqueId(), false);
            return;
          }

          int seconds = (int) Main.getInstance().getServerHandler().getDeathban(event.getEntity());

          HCFProfile profile = HCFProfile.get(event.getEntity());

          profile.setDeathban(new DeathBan(seconds * 1000L));
          event.getEntity().setMetadata("deathban", new FixedMetadataValue(Main.getInstance(), true));

          String time = TimeUtils.formatIntoDetailedString(seconds);

          if (!event.getEntity().isOnline()) {
            return;
          }

          if (Main.getInstance().getServerHandler().isPreEOTW() || Main.getInstance().getServerHandler().isEOTW()) {
            event.getEntity().kickPlayer(ChatColor.RED + "Thank you for playing! Come back next map!");
            return;
          }

          if (profile.hasPvPTimer()) {
            profile.setPvpTimer(null);
            //Main.getInstance().getPvPTimerMap().removeTimer(event.getEntity().getUniqueId());
          }

          Players.teleportWithChunkLoad(event.getEntity(), SetListener.getDeathban());

          event.getEntity().sendMessage(CC.translate("&cYou have been sent to &lHospital."));
          event.getEntity().sendMessage(CC.translate(""));
          event.getEntity().sendMessage(CC.translate("&cClick the sign to revive yourself, if you have lives"));
          event.getEntity().sendMessage(CC.translate("&cHere you can pvp against other deathbaned players!"));
          event.getEntity().sendMessage(CC.translate(""));
          event.getEntity().sendMessage(CC.translate("&cYou are still deathbanned for &l" + time));
          event.getEntity().sendMessage(CC.translate("&cYou have &l" + profile.getLives() + " lives&c."));

          if (profile.getLives() == 0) {
            event.getEntity().sendMessage(CC.translate("&cPurchase lives at &lstore.battle.rip."));
          }

        } else {
          if (!event.getEntity().isOnline()) {
            return;
          }

          Players.teleportWithChunkLoad(event.getEntity(), SetListener.getDeathban());
        }

        if (killer == null || !event.getEntity().equals(killer)) {
          String deathMsg = ChatColor.YELLOW + event.getEntity().getDisplayName() + ChatColor.YELLOW
                  + " " + (
                  (event.getEntity().getKiller() != null) ? ("killed by "
                      + event.getEntity().getKiller().getDisplayName()) : "died") + " " + ChatColor.GOLD
                  + InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

          for (final ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
              InventoryUtils.addDeath(armor, deathMsg);
            }
          }
        }

        HCFProfile profile = HCFProfile.get(event.getEntity());

        double bal = profile.getEconomyData().getBalance();

        profile.getEconomyData().setBalance(0);

        if (event.getEntity().getKiller() != null && bal > 0) {

          HCFProfile profileKiller = HCFProfile.get(event.getEntity().getKiller());

          profileKiller.getEconomyData().addBalance(bal);
          event.getEntity().getKiller().sendMessage(ChatColor.GOLD + "You earned " + ChatColor.BOLD + "$" + bal + ChatColor.GOLD
                  + " for killing " + event.getEntity().getDisplayName() + ChatColor.GOLD
                  + "!");
        }
      }
    }.runTaskAsynchronously(Main.getInstance());
  }*/

  @EventHandler
  public void onChangeWorld(PlayerChangedWorldEvent event) {
    if(event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
      event.getPlayer().removeMetadata("no_npc", Main.getInstance());
    }
  }

  private void processTerritoryInfo(final PlayerMoveEvent event) {

    Player player = event.getPlayer();


    Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

    HCFProfile profile = HCFProfile.get(player);

    if (profile.hasPvPTimer() && !DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {

      if (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo())) {
        profile.setPvpTimer(null);

        player.sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");

      } else if (ownerTo != null && ownerTo.getOwner() != null && !ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
        event.setCancelled(true);
        for (final Claim claim : ownerTo.getClaims()) {
          if (claim.contains(event.getFrom()) && !ownerTo.isMember(
              event.getPlayer().getUniqueId())) {
            Location nearest = TeamStuckCommand.nearestSafeLocation(
                event.getPlayer().getLocation());
            boolean spawn = false;
            if (nearest == null) {
              nearest = Main.getInstance().getServerHandler().getSpawnLocation();
              spawn = true;
            }
            event.getPlayer().teleport(nearest);
            event.getPlayer().sendMessage(
                ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed.");
            return;
          }
        }
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another team's territory with PvP Protection.");
        event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
        return;
      }
    }

    Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());

    if (ownerFrom != ownerTo) {
      ServerHandler sm = Main.getInstance().getServerHandler();
      RegionData from = sm.getRegion(ownerFrom, event.getFrom());
      RegionData to2 = sm.getRegion(ownerTo, event.getTo());

      if (from.equals(to2)) {
        return;
      }

      if (!to2.getRegionType().getMoveHandler().handleMove(event)) {
        return;
      }

      if (event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {
        return;
      }

      Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

      if (!Main.getInstance().getMapHandler().isKitMap()) {

        if (DTRBitmask.CITADEL.appliesAt(event.getTo()) && !DTRBitmask.CITADEL.appliesAt(
            event.getFrom())) {

          boolean citadelrequirement = player.getGameMode() == GameMode.SURVIVAL && !Main.getInstance().getMapHandler().isKitMap()
                  && Main.getInstance().getEventHandler().getEvent("Citadel") != null
                  && Main.getInstance().getEventHandler().getEvent("Citadel").isActive()
                  && !DTRBitmask.CITADEL.appliesAt(player.getLocation())
                  && ((playerTeam == null));

         if(citadelrequirement) {
           event.setTo(event.getFrom());
           event.getPlayer()
                   .sendMessage(
                           ChatColor.RED + "You need to be in a team to enter this event.");
         }
        } else if (DTRBitmask.CONQUEST.appliesAt(event.getTo()) && !DTRBitmask.CONQUEST.appliesAt(
            event.getFrom())) {

          boolean conquestrequirements = player.getGameMode() == GameMode.SURVIVAL && !Main.getInstance().getMapHandler().isKitMap()
                  && Main.getInstance().getConquestHandler().getGame() != null
                  && !DTRBitmask.CONQUEST.appliesAt(player.getLocation())
                  && ((playerTeam == null));

          if(conquestrequirements){
            event.setTo(event.getFrom());
            event.getPlayer().sendMessage(ChatColor.RED + "You need to be in a team to enter this event.");
          }
          return;
        }

        if ((DTRBitmask.CONQUEST.appliesAt(event.getTo()) ||
            DTRBitmask.CITADEL.appliesAt(event.getTo())) ) {

          boolean shouldDenyEntry =
                  playerTeam != null && !playerTeam.isBypassEvent() &&
                          event.getPlayer().getGameMode() != GameMode.CREATIVE &&
                          denyEnter(event.getPlayer(), playerTeam, event.getTo());

          if (shouldDenyEntry) {
            event.setTo(event.getFrom());
          }
          return;
        }
      }

      if (SpawnTagHandler.isTagged(event.getPlayer())) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {
          player.setVelocity(player.getWorld().getSpawnLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(-0.1));
          return;
        }
      }

      if(!player.getWorld().getName().equalsIgnoreCase("void")) {
        if ((to2.getRegionType() == RegionType.WARZONE || to2.getRegionType() == RegionType.ROAD) && (from.getRegionType() != RegionType.WARZONE || from.getRegionType() != RegionType.ROAD)){
          player.setMetadata("no_npc", new FixedMetadataValue(Main.getInstance(), true));
        }else if ((from.getRegionType() == RegionType.WARZONE || from.getRegionType() == RegionType.ROAD) && (to2.getRegionType() != RegionType.WARZONE || to2.getRegionType() != RegionType.ROAD)){
          player.removeMetadata("no_npc", Main.getInstance());
        }else if (to2.getRegionType() == RegionType.SPAWN){
          player.removeMetadata("no_npc", Main.getInstance());
        }
      }

      //event.getPlayer().sendMessage(CC.translate("&6» &fLeaving from: " + from.getName(player) + ", &6» &fEntering into: " + to2.getName(player)));
      event.getPlayer().sendMessage(CC.translate("&eEntering " + to2.getName(player, true) + "&e, Leaving " + from.getName(player, true)));

      ActionBarUtils.sendActionBarMessage(player,
              CC.translate("&eEntering " + to2.getName(player, true) + "&e, Leaving " + from.getName(player, true)));
    }
  }

  public boolean denyEnter(Player player, Team team, Location to) {

    Event activeEvent = Main.getInstance().getEventHandler().getActiveEvent();

    if (activeEvent == null && Main.getInstance().getConquestHandler().getGame() == null) {
      return false;
    }

    if (activeEvent instanceof KOTH) {
      KOTH koth = (KOTH) activeEvent;

      if (team.getEventDisqualified().contains(koth.getName())) {
        player.sendMessage(CC.translate("&cYour team is disqualified from this event."));
        return true;
      }
    } else {
      if (team.getEventDisqualified().contains("Conquest")) {
        player.sendMessage(CC.translate("&cYour team is disqualified from this event."));
        return true;
      }
    }

    if (team.isRecently()) {
      long time = System.currentTimeMillis() - team.getCreateAt();
      long requiredTime = TimeUnit.MINUTES.toMillis(20);

      long remaining = requiredTime - time;

      player.sendMessage(CC.translate(
          "&CYour faction is too new. You must wait &c&l" + TimeUtil.millisToRoundedTime(remaining)
              + " &cbefore joining the event."));

      return true;
    }

    if (team.getHQ() == null) {
      player.sendMessage(CC.translate("&cYou must set a hq before entering the event."));
      return true;
    }

    if (team.isDisqualified() || Main.getInstance().getEventHandler()
        .getBannedTeams().contains(team)) {
      player.sendMessage(CC.translate("&cYour team is disqualified from the event."));
      return true;
    }

    if ((Main.getInstance().getEventHandler().getEvent("Citadel") != null &&
        Main.getInstance().getEventHandler().getEvent("Citadel").isActive()) ||
        Main.getInstance().getConquestHandler().getGame() != null || MadGame.isStarted()) {

      if (team.getOnlineMembers().size() < 3) {

        if (DTRBitmask.CITADEL.appliesAt(to)) {
          player.sendMessage(CC.translate("&cYou cannot enter &5&lCitadel &cclaim."));
        } else if(MadGame.isStarted()){
          player.sendMessage(CC.translate("&cYou cannot enter &4&lMad Event &cclaim."));
        }else {
          player.sendMessage(CC.translate("&cYou cannot enter &6&lConquest &cclaim."));
        }

        player.sendMessage(
            CC.translate("&cYou must have +3 Online Members to enter this claim."));
        return true;
      }
    }

    return false;
  }

  @EventHandler
  public void onPlayerInteractWithSkull(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (event.getAction() != Action.RIGHT_CLICK_AIR &&
        event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (event.getItem() == null) {
      return;
    }

    if (CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
      return;
    }

    if (event.getItem().getType() != Material.SKULL_ITEM) {
      return;
    }

    if (CooldownAPI.hasCooldown(player, "SkullStrength")) {
      player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtil.millisToRoundedTime(
          CooldownAPI.getCooldown(player, "SkullStrength")) +
          " to use it again");
      return;
    }

    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 4, 0));

    if (player.getItemInHand().getAmount() == 1) {
      player.setItemInHand(null);
    } else {
      player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
    }
    player.updateInventory();

    CooldownAPI.setCooldown(player, "SkullStrength", TimeUtil.parseTimeLong("30s"));
  }

  @EventHandler
  public void onItemSpawn(ItemSpawnEvent event){
    Item item = event.getEntity();

    if (CustomTimerCreateCommand.clearItems){
      if(DTRBitmask.SAFE_ZONE.appliesAt(item.getLocation()) && CustomTimerCreateCommand.sotwday){
        item.remove();
      }
    }
  }

  private final VotePartyHandler votePartyHandler = Main.getInstance().getVotePartyHandler();

  @EventHandler
  public void onPlayerVote(PlayerVoteServerEvent event){
    Player player = event.getPlayer();

    if (!votePartyHandler.hasVote(player)){
      votePartyHandler.vote(player);
    }

  }

}