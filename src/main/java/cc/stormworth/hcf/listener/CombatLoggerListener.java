package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.gson.serialization.PlayerInventorySerializer;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.deathban.DeathBan;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CombatLoggerListener implements Listener {

  public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
  @Getter
  private static final Set<LivingEntity> combatLoggers = new HashSet<>();

    /*public static boolean hasCombatLogger(String name) {
        Iterator<Entity> combatLoggerIterator = combatLoggers.iterator();

        while (combatLoggerIterator.hasNext()) {
            Villager villager = (Villager) combatLoggerIterator.next();

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(name)) {
                return true;
            }
        }
        return false;
    }*/

  public CombatLoggerListener() {
    for (World world : Bukkit.getWorlds()) {
      Iterator<Entity> combatLoggerIterator = world.getEntitiesByClasses(Villager.class).iterator();

      while (combatLoggerIterator.hasNext()) {
        Villager villager = (Villager) combatLoggerIterator.next();

        if (villager.isCustomNameVisible() && villager.hasMetadata(COMBAT_LOGGER_METADATA)) {
          villager.remove();
          combatLoggerIterator.remove();
        }
      }
    }
  }

  public static double calculateCombatLoggerHealth(Player player) {
    int potions = 1;
    boolean gapple = false;

    for (ItemStack itemStack : player.getInventory().getContents()) {
      if (itemStack == null) {
        continue;
      }

      if (itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16421) {
        potions++;
      } else if (!gapple && itemStack.getType() == Material.GOLDEN_APPLE
          && itemStack.getDurability() == (short) 1) {
        // Only let the player have one gapple count.
        potions += 15;
        gapple = true;
      }
    }

    return ((potions * 3.5D) + player.getHealth());
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
      boolean isKitMap = Main.getInstance().getMapHandler().isKitMap();

      if (!combatLoggers.contains(event.getEntity())) {
        return;
      }

      combatLoggers.remove(event.getEntity());
      CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

      // Drop the player's items.
      for (ItemStack item : metadata.contents) {
        event.getDrops().add(item);
      }
      for (ItemStack item : metadata.armor) {
        event.getDrops().add(item);
      }

      String deathMessage;
      if (!metadata.playerName.equals(event.getEntity().getCustomName().substring(2))) {
        Main.getInstance().getLogger().warning(
            "Combat logger name doesn't match metadata for " + metadata.playerName + " ("
                + event.getEntity().getCustomName().substring(2) + ")");
      }

      Team team = Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID);
      if (team != null) {
        team.playerDeath(metadata.playerName, metadata.playerUUID,
            event.getEntity().getKiller() != null ? event.getEntity().getKiller() : null,
            Main.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()));

        team.setDeaths(team.getDeaths() + 1);
      }

      // Deathban the player
      if (!Main.getInstance().getMapHandler().isKitMap()) {

        CompletableFuture<HCFProfile> future = HCFProfile.load(metadata.playerUUID);

        future.thenAccept(profile -> {
          if (profile == null) {
            return;
          }

          if (profile.isDeathBanned()){
            return;
          }

          profile.setDeathban(new DeathBan(metadata.deathBanTime * 1000L));
          profile.asyncSave();
        });

        /*Main.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);
        Main.getInstance().getDeathbannedMap().setDeathbanned(metadata.playerUUID, true);*/
      }

      if (isKitMap) {
        if (event.getEntity().getKiller() != null) {
          Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).addKill();
        }

        Main.getInstance().getMapHandler().getStatsHandler().getStats(metadata.playerUUID).addDeath();
      } else {
        //targetProfile.addDeaths(1);

        if (event.getEntity().getKiller() != null) {
          HCFProfile profile = HCFProfile.getByUUID(event.getEntity().getKiller().getUniqueId());
          profile.addKills(1);
          Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).addKill();
        }

        Main.getInstance().getMapHandler().getStatsHandler().getStats(metadata.playerUUID).addDeath();
      }

      int victimKills = Main.getInstance().getMapHandler().getStatsHandler().getStats(metadata.playerUUID).getKills();

      if (isKitMap) {
        victimKills = Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getUniqueId()).getKills();
      }

      if (event.getEntity().getKiller() != null) {
        // give them a kill
        HCFProfile profile = HCFProfile.getByUUID(event.getEntity().getKiller().getUniqueId());
        profile.addKills(1);

        // store the kill amount -- we'll use this later on.
        int killerKills = profile.getKills();

        if (isKitMap) {
          Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).addKill();

          killerKills = Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).getKills();
        }

        deathMessage =
            ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]"
                + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by "
                + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.DARK_RED + "["
                + killerKills + "]" + ChatColor.YELLOW + ".";

        for (final Player player : Bukkit.getOnlinePlayers()) {
          HCFProfile hcfProfile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());
          boolean sendMessage = hcfProfile == null || hcfProfile.isDeathMessages();

          if (sendMessage) {
            player.sendMessage(deathMessage);
          } else {
            if (Main.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
              continue;
            }

            if (Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null &&
                    Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID)
                    .equals(Main.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
              player.sendMessage(deathMessage);
            }

            if (Main.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()) != null
                && Main.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId())
                .equals(Main.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
              player.sendMessage(deathMessage);
            }
          }
        }
      } else {
        deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]"
                    + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

        for (Player player : Bukkit.getOnlinePlayers()) {
          HCFProfile hcfProfile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());
          boolean sendMessage = hcfProfile == null || hcfProfile.isDeathMessages();

          if (sendMessage) {
            player.sendMessage(deathMessage);
          } else {
            if (Main.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
              continue;
            }

            if (Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                && Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID)
                .equals(Main.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
              player.sendMessage(deathMessage);
            }
          }
        }
      }

      Player target = Main.getInstance().getServer().getPlayer(metadata.playerUUID);

      if (target == null) {
        // Create an entity to load the player data
        MinecraftServer server = ((CraftServer) Main.getInstance().getServer()).getServer();
        EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0),
            new GameProfile(metadata.playerUUID, metadata.playerName),
            new PlayerInteractManager(server.getWorldServer(0)), false);

        target = entity.getBukkitEntity();

        if (target != null) {
          target.loadData();
        }
      }

      if (target != null) {
        EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

        WebsiteListener.saveDeath(target.getUniqueId(),
            event.getEntity().getKiller() != null ? event.getEntity().getKiller().getUniqueId()
                : null,
            PlayerInventorySerializer.getInsertableObject(target),
            deathMessage,
            (event.getEntity().getKiller() != null
                    && event.getEntity().getKiller().getItemInHand() != null ? event.getEntity()
                .getKiller().getItemInHand() : null),
            event.getEntity().getLocation());

        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
        humanTarget.setHealth(0);

        target.saveData();
      }

      event.getEntity().remove();
            /*new BukkitRunnable() {
                @Override
                public void run() {

                }
            }.runTaskAsynchronously(Main.getInstance());*/
    }
  }

  // Prevent trading with the logger.
  @EventHandler
  public void onEntityInteract(PlayerInteractEntityEvent event) {
    if (event.getRightClicked().hasMetadata(COMBAT_LOGGER_METADATA)) {
      event.setCancelled(true);
    }
  }

  // Kill loggers when their chunk unloads
  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    for (Entity entity : event.getChunk().getEntities()) {
      if (entity.hasMetadata(COMBAT_LOGGER_METADATA) && !entity.isDead()) {
        entity.remove();
      }
    }
  }

  // Don't let the NPC go through portals
  @EventHandler
  public void onEntityPortal(EntityPortalEvent event) {
    if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
      event.setCancelled(true);
    }
  }

  // Despawn the NPC when its owner joins.
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")
        && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
      return;
    }

    Player player = event.getPlayer();
    HCFProfile profile = HCFProfile.get(player);

    if (profile.hasSotwTimer()) {
      return;
    }


    Iterator<LivingEntity> combatLoggerIterator = combatLoggers.iterator();

    while (combatLoggerIterator.hasNext()) {
      Villager villager = (Villager) combatLoggerIterator.next();

      if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {

        try {
          Field underlyingEntityField = CraftEntity.class.getDeclaredField("entity");
          underlyingEntityField.setAccessible(true);
          Object underlyingPlayerObj = underlyingEntityField.get(event.getPlayer());
          if (underlyingPlayerObj instanceof EntityPlayer) {
            EntityPlayer underlyingPlayer = (EntityPlayer) underlyingPlayerObj;
            underlyingPlayer.invulnerableTicks = 1;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        /*player.teleport(villager.getLocation());

        player.setHealth(villager.getHealth() / 2);
*/
        villager.remove();
        combatLoggerIterator.remove();
      }
    }
  }

  public static LivingEntity getCombatLogger(UUID uuid) {
    for (LivingEntity villager : combatLoggers) {

      CombatLoggerMetadata metadata = (CombatLoggerMetadata) villager.getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

        if (metadata.playerUUID.equals(uuid)) {
            return villager;
        }
    }

    return null;
  }

  // Prevent combat logger friendly fire.
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
      return;
    }

    Player damager = null;

    if (event.getDamager() instanceof Player) {
      damager = (Player) event.getDamager();
    } else if (event.getDamager() instanceof Projectile) {
      Projectile projectile = (Projectile) event.getDamager();

      if (projectile.getShooter() instanceof Player) {
        damager = (Player) projectile.getShooter();
      }
    }

    if (damager != null) {
      CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

      if (DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(
          event.getEntity().getLocation())) {
        event.setCancelled(true);
        return;
      }

      if (Main.getInstance().getServerHandler()
          .isSpawnBufferZone(event.getEntity().getLocation())) {
        ((EntityLiving) ((CraftEntity) event.getEntity()).getHandle()).knockbackReduction = 1D;
      }

      HCFProfile profile = HCFProfile.get(damager);

      if (profile.hasPvPTimer()) {
        event.setCancelled(true);
        return;
      }

      Team team = Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

      if (team != null && team.isMember(damager.getUniqueId())) {
        event.setCancelled(true);
        return;
      }

      SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
    }
  }

  @EventHandler
  public void onEntityPressurePlate(EntityInteractEvent event) {
    if (event.getBlock().getType() == Material.STONE_PLATE && event.getEntity() instanceof Villager
        && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
      event.setCancelled(true); // block is stone, entity is a combat tagged villager
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerQuit(PlayerQuitEvent event) {

    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")
        && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
      return;
    }


    HCFProfile profile = HCFProfile.get(event.getPlayer());

    if (profile == null){
      return;
    }

    if (profile.hasSotwTimer()) {
      return;
    }

    if (event.getPlayer().getWorld().getName().equalsIgnoreCase("void")
        || DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())
        || CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(event.getPlayer())
        || profile.isDeathBanned()
        || profile.hasPvPTimer()) {
      return;
    }

    if (event.getPlayer().hasMetadata("loggedout")) {
      event.getPlayer().removeMetadata("loggedout", Main.getInstance());
      return;
    }

    if (event.getPlayer().isDead()) {
      return;
    }

    if (!event.getPlayer().hasMetadata("invisible") && !event.getPlayer().hasMetadata("deathban")) {
      ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
      ItemStack[] inv = event.getPlayer().getInventory().getContents();
      Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

      villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));

      if (event.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
        for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
          if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
            villager.addPotionEffect(potionEffect);
            break;
          }
        }
      }

      CombatLoggerMetadata metadata = new CombatLoggerMetadata();

      metadata.playerName = event.getPlayer().getName();
      metadata.playerUUID = event.getPlayer().getUniqueId();
      metadata.deathBanTime = Main.getInstance().getServerHandler().getDeathban(metadata.playerUUID, event.getPlayer().getLocation());
      metadata.contents = inv;
      metadata.armor = armor;

      villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(Main.getInstance(), metadata));

      villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
      villager.setHealth(villager.getMaxHealth());

      if (!villager.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
        villager.setCustomName(ChatColor.YELLOW + event.getPlayer().getName());
        villager.setCustomNameVisible(true);
      }

      villager.setFallDistance(event.getPlayer().getFallDistance());
      villager.setRemoveWhenFarAway(false);
      villager.setVelocity(new Vector());

      combatLoggers.add(villager);

      new BukkitRunnable() {
        public void run() {
          if (!villager.isDead() && villager.isValid()) {
            combatLoggers.remove(villager);
            villager.remove();
          }
        }
      }.runTaskLater(Main.getInstance(), 30 * 20L);

      if (villager.getWorld().getEnvironment() == World.Environment.THE_END) {
        new BukkitRunnable() {

          int tries = 0;

          @Override
          public void run() {
            if (villager.getLocation().getBlockY() > 0) {
              tries++;

              if (tries >= 60) {
                cancel();
              }
              return;
            }

            // Deathban the player
            Team team = Main.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            if (team != null) {
              team.playerDeath(metadata.playerName, metadata.playerUUID, null, Main.getInstance().getServerHandler().getDTRLoss(villager.getLocation()));
            }

            // Deathban the player
            if (!Main.getInstance().getMapHandler().isKitMap()) {
              CompletableFuture<HCFProfile> future = HCFProfile.load(metadata.playerUUID);

              future.thenAccept(profile -> {
                if (profile == null) {
                  return;
                }

                if (profile.isDeathBanned()){
                  return;
                }

                profile.setDeathban(new DeathBan(metadata.deathBanTime * 1000L));
                profile.asyncSave();
              });
            }

            // store the death amount -- we'll use this later on.
            int victimKills = Main.getInstance().getMapHandler().getStatsHandler()
                    .getStats(metadata.playerUUID).getKills();

            String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]"
                    + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

            for (Player player : Bukkit.getOnlinePlayers()) {
              HCFProfile profile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());
              boolean sendMessage = profile != null && profile.isDeathMessages();
              if (sendMessage) {
                player.sendMessage(deathMessage);
              } else {
                if (team != null && team == Main.getInstance().getTeamHandler().getTeam(player.getUniqueId())) {
                  player.sendMessage(deathMessage);
                }
              }
            }

            Player target = Main.getInstance().getServer().getPlayer(metadata.playerUUID);

            if (target == null) {
              // Create an entity to load the player data
              MinecraftServer server = ((CraftServer) Main.getInstance().getServer()).getServer();
              EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0),
                  new GameProfile(metadata.playerUUID, metadata.playerName),
                  new PlayerInteractManager(server.getWorldServer(0)), false);
              target = entity.getBukkitEntity();

              if (target != null) {
                target.loadData();
              }
            }

            if (target != null) {
              EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

              WebsiteListener.saveDeath(target.getUniqueId(),
                  null,
                  PlayerInventorySerializer.getInsertableObject(target),
                  deathMessage,
                  null,
                  event.getPlayer().getLocation());

              target.getInventory().clear();
              target.getInventory().setArmorContents(null);
              humanTarget.setHealth(0);

              target.saveData();
            }

            villager.remove();
            cancel();
          }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);
      }
    }
  }

  public static class CombatLoggerMetadata {

    private ItemStack[] contents;
    private ItemStack[] armor;
    private String playerName;
    private UUID playerUUID;
    private long deathBanTime;
  }
}