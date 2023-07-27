package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.core.util.general.PlayerUtils;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.npc.NPCEntity;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.op.EndEventCommand;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class SetListener implements Listener {

  public static final List<String> revivelines = Arrays.asList(
      "&7&m-----------------------",
      "&6&lUse one live",
      "",
      "&e&lCLICK ME",
      "&7&m-----------------------");
  public static final List<String> deathbankitlines = Arrays.asList(
      "&7&m-----------------------",
      "&c&lKit Deathban",
      "",
      "&e&lCLICK ME",
      "&7&m-----------------------");

  @Getter
  @Setter
  public static Location endExit;
  @Getter
  @Setter
  public static Location endreturn;
  @Getter
  @Setter
  public static Location deathban;
  @Getter
  @Setter
  public static Location revive;
  @Getter
  @Setter
  public static Location deathbankit;
  @Getter
  @Setter
  public static Location eotwffa;
  @Getter
  @Setter
  public static Location infoholo;
  @Getter
  @Setter
  public static Location shop;
  @Getter
  @Setter
  public static Location daily;
  @Getter
  @Setter
  public static Location store;

  public Map<String, Long> msgCooldown;

  public SetListener() {
    this.msgCooldown = new HashMap<>();

    loadrevive();
    loadDeathban();
    loaddeathbankit();
    loadEndreturn();

    NPCEntity revivenpc = new NPCEntity("revive");
    revivenpc.setLocation(revive);
    revivenpc.setSkinowner(UUID.fromString("cbc6e6a4-eb17-4afe-a84d-a2e9634b4165"));
    revivenpc.setCmdcooldown(false);
    revivenpc.setCommand("uselive");
    revivenpc.setHand(new ItemStack(Material.PAPER));

    Hologram reviveholo = Holograms.newHologram().at(revive.clone().add(0, 1.7, 0))
        .addLines(revivelines).build();
    reviveholo.send();
    HologramManager.holograms.put(revive.clone().add(0, 1.7, 0), reviveholo);

    NPCEntity kitnpc = new NPCEntity("deathbankit");
    kitnpc.setLocation(deathbankit);
    kitnpc.setSkinowner(UUID.fromString("d9ca9c25-a0d4-410e-ad09-fd8011c8d356"));
    kitnpc.setCmdcooldown(false);
    kitnpc.setCommand("kit deathban");
    kitnpc.setHand(
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build());
    kitnpc.setHand(
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build());
    kitnpc.setHelmet(
        ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 1).build());
    kitnpc.setChest(
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build());
    kitnpc.setLegs(
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build());
    kitnpc.setBoots(
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build());

    Hologram kitholo = Holograms.newHologram().at(deathbankit.clone().add(0, 1.7, 0))
        .addLines(deathbankitlines).build();
    kitholo.send();
    HologramManager.holograms.put(deathbankit.clone().add(0, 1.7, 0), kitholo);

    loadEndExit();
  }

  public static void loadEndreturn() {
    if (endreturn != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("endreturn")) {
        endreturn = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("endreturn"),
            (Class) Location.class);
      } else {
        endreturn = new Location(Bukkit.getWorlds().get(0), 0.6, 64.0, 346.5);
      }
      return null;
    });
  }

  public static void saveEndreturn() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("endreturn", CorePlugin.PLAIN_GSON.toJson(endreturn));
          return null;
        }));
  }

  public static void saveEndExit() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("endExit", CorePlugin.PLAIN_GSON.toJson(endExit));
          return null;
        }));
  }

  public static void loadEndExit() {
    if (endExit != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("endExit")) {
        endExit = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("endExit"),
            (Class) Location.class);
      } else {
        endExit = new Location(Bukkit.getWorlds().get(0), 0.6, 64.0, 346.5);
      }
      return null;
    });
  }

  public static void saveDeathban() {
    Main.getInstance().getUtilitiesFile().getConfig()
        .set("deathban", LocationUtil.parseLocation(deathban));
    Main.getInstance().getUtilitiesFile().save();
  }

  public static void loadDeathban() {
    if (deathban != null) {
      return;
    }
    if (Main.getInstance().getUtilitiesFile().getConfig().contains("deathban")) {
      deathban = LocationUtil.convertLocation(
          Main.getInstance().getUtilitiesFile().getConfig().getString("deathban"));
    } else {
      deathban = new Location(Bukkit.getWorld("void"), 0.6, 80.0, 0.5);
    }
  }

  public static void saverevive() {
    Main.getInstance().getUtilitiesFile().getConfig()
        .set("revive", LocationUtil.parseLocation(revive));
    Main.getInstance().getUtilitiesFile().save();
  }

  public static void loadrevive() {
    if (revive != null) {
      return;
    }
    if (Main.getInstance().getUtilitiesFile().getConfig().contains("revive")) {
      revive = LocationUtil.convertLocation(
          Main.getInstance().getUtilitiesFile().getConfig().getString("revive"));
    } else {
      revive = new Location(Bukkit.getWorld("void"), 0.6, 80.0, 0.5);
    }
  }

  public static void savedeathbankit() {
    Main.getInstance().getUtilitiesFile().getConfig()
        .set("deathbankit", LocationUtil.parseLocation(deathbankit));
    Main.getInstance().getUtilitiesFile().save();
  }

  public static void loaddeathbankit() {
    if (deathbankit != null) {
      return;
    }
    if (Main.getInstance().getUtilitiesFile().getConfig().contains("deathbankit")) {
      deathbankit = LocationUtil.convertLocation(
          Main.getInstance().getUtilitiesFile().getConfig().getString("deathbankit"));
    } else {
      deathbankit = new Location(Bukkit.getWorld("void"), 0.6, 80.0, 0.5);
    }
  }

  public static void saveEotwFFA() {
    Main.getInstance().getUtilitiesFile().getConfig()
        .set("eotwffa", LocationUtil.parseLocation(eotwffa));
    Main.getInstance().getUtilitiesFile().save();
  }

  public static void loadEotwFFA() {
    if (eotwffa != null) {
      return;
    }
    if (Main.getInstance().getUtilitiesFile().getConfig().contains("eotwffa")) {
      eotwffa = LocationUtil.convertLocation(
          Main.getInstance().getUtilitiesFile().getConfig().getString("eotwffa"));
    } else {
      eotwffa = new Location(Bukkit.getWorld("void"), 0.6, 80.0, 0.5);
    }
  }

  public static void saveInfoHolo() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("infoholo", CorePlugin.PLAIN_GSON.toJson(infoholo));
          return null;
        }));
  }

  public static void loadInfoHolo() {
    if (infoholo != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("infoholo")) {
        infoholo = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("infoholo"),
            (Class) Location.class);
      } else {
        infoholo = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveStore() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("store", CorePlugin.PLAIN_GSON.toJson(store));
          return null;
        }));
  }

  public static void loadStore() {
    if (store != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("store")) {
        store = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("store"),
            (Class) Location.class);
      } else {
        store = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveDaily() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("daily", CorePlugin.PLAIN_GSON.toJson(daily));
          return null;
        }));
  }

  public static void loadDaily() {
    if (daily != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("daily")) {
        daily = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("daily"),
            (Class) Location.class);
      } else {
        daily = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveBlockShop() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("shop", CorePlugin.PLAIN_GSON.toJson(shop));
          return null;
        }));
  }

  public static void loadBlockShop() {
    if (shop != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("shop")) {
        shop = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("shop"),
            (Class) Location.class);
      } else {
        shop = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  @EventHandler
  public void onEntityDeath(final EntityDeathEvent event) {
    if (event.getEntity() instanceof EnderDragon) {
      final Team team = Main.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller());
      String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

      if (team != null) {
        teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
        team.addPoints(50);
      }

      EndEventCommand.started = false;
      for (int i = 0; i < 6; ++i) {
        Bukkit.broadcastMessage("");
      }

      String top1 = "";
      String top2 = "";
      String top3 = "";
      int displayed = 0;
      for (final Map.Entry<UUID, Integer> entry : EndEventCommand.getHits().entrySet()) {
        if (displayed == 0) {
          top1 = UUIDUtils.name(entry.getKey());
        } else if (displayed == 1) {
          top2 = UUIDUtils.name(entry.getKey());
        } else if (displayed == 2) {
          top3 = UUIDUtils.name(entry.getKey());
        }
        if (++displayed == 3) {
          break;
        }
      }
      Main.getInstance().getServer()
          .broadcastMessage(ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      Main.getInstance().getServer()
          .broadcastMessage(ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      Bukkit.broadcastMessage(
          ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD
              + " [Enderdragon]");
      Bukkit.broadcastMessage(
          ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.YELLOW
              + " killed by");
      Bukkit.broadcastMessage(
          ChatColor.LIGHT_PURPLE + "\u2588" + ChatColor.DARK_PURPLE + "\u2588"
              + ChatColor.LIGHT_PURPLE + "\u2588" + ChatColor.BLACK + "\u2588\u2588"
              + ChatColor.LIGHT_PURPLE + "\u2588" + ChatColor.DARK_PURPLE + "\u2588"
              + ChatColor.LIGHT_PURPLE + "\u2588 " + teamName);
      Bukkit.broadcastMessage(
          ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588 " + event.getEntity()
              .getKiller().getDisplayName());
      Bukkit.broadcastMessage(
          ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + CC.translate(
              "&aTop #1&7: " + top1 + "&7, " + "&eTop #2&7: " + top2 + "&7, " + "&cTop #3&7: "
                  + top3));
      Main.getInstance().getServer()
          .broadcastMessage(ChatColor.BLACK + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");

    }
  }

  @EventHandler
  public void onEntityCreatePortal(final EntityCreatePortalEvent event) {
    if (event.getEntity() instanceof Item && event.getPortalType() == PortalType.ENDER) {
      event.getBlocks().clear();
    }
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof EnderDragon
        && event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
      ((EnderDragon) event.getEntity()).setCustomName(
          "Ender Dragon " + ChatColor.YELLOW + ChatColor.BOLD + FastMath.round(
              ((EnderDragon) event.getEntity()).getHealth()
                  / ((EnderDragon) event.getEntity()).getMaxHealth() * 100.0) + "% Health");

      Player damager = PlayerUtils.getDamageSource(event.getDamager());
      if (damager != null) {
        if (EndEventCommand.hits.containsKey(damager.getUniqueId())) {
          EndEventCommand.hits.put(damager.getUniqueId(),
              EndEventCommand.hits.get(damager.getUniqueId()) + 1);
        } else {
          EndEventCommand.hits.put(damager.getUniqueId(), 1);
        }

        EndEventCommand.hits = EndEventCommand.sortByValues(EndEventCommand.hits);
      }
    }
  }

  @EventHandler
  public void onBlockPlace(final BlockPlaceEvent event) {
    if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null
        && LandBoard.getInstance().getTeam(event.getBlock().getLocation()).getOwner() != null) {
      return;
    }
    if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
      if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
        return;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBreak(final BlockBreakEvent event) {
    if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null
        && LandBoard.getInstance().getTeam(event.getBlock().getLocation()).getOwner() != null) {
      return;
    }
    if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
      if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
        return;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerBukkitEmpty(final PlayerBucketEmptyEvent event) {
    if (LandBoard.getInstance().getTeam(event.getBlockClicked().getLocation()) != null
        && LandBoard.getInstance().getTeam(event.getBlockClicked().getLocation()).getOwner()
        != null) {
      return;
    }
    if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
      if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
        return;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
    if (LandBoard.getInstance().getTeam(event.getBlockClicked().getLocation()) != null
        && LandBoard.getInstance().getTeam(event.getBlockClicked().getLocation()).getOwner()
        != null) {
      return;
    }
    if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
      if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
        return;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCreatePortal(final EntityCreatePortalEvent event) {
    if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPortal(final PlayerPortalEvent event) {
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
      return;
    }
    final Player player = event.getPlayer();
    if ((event.getTo() == null || event.getTo().getWorld() == null) && event.getFrom().getWorld()
        .getName().equalsIgnoreCase("world_miner")) {
      event.setTo(Main.getInstance().getServerHandler().getSpawnLocation());
    }
    if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
      if (EndEventCommand.started) {
        event.setCancelled(true);
        if (!this.msgCooldown.containsKey(player.getName())
            || this.msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
          event.getPlayer()
              .sendMessage(ChatColor.RED + "You cannot leave the end before the dragon is killed.");
          this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
        }
        return;
      }
      event.setTo(endExit);
    } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
      event.setTo(event.getTo().getWorld().getSpawnLocation());
      if (HCFProfile.get(player).hasPvPTimer()) {
        event.setCancelled(true);
        if (!this.msgCooldown.containsKey(player.getName())
            || this.msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
          event.getPlayer().sendMessage(
              ChatColor.RED + "You cannot enter the end while you have PvP Protection.");
          this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
        }
      }
      if (SpawnTagHandler.isTagged(event.getPlayer())) {
        event.setCancelled(true);
        if (!this.msgCooldown.containsKey(player.getName())
            || this.msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
          event.getPlayer()
              .sendMessage(ChatColor.RED + "You cannot enter the end while you are spawn tagged.");
          this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
        }
      }
      for (final PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
        if (potionEffect.getDuration() < 180) {
          event.getPlayer().removePotionEffect(potionEffect.getType());
        }
      }
    }
  }

  @EventHandler
  public void onEntityExplode(final EntityExplodeEvent event) {
    if (event.getEntity() instanceof EnderDragon) {
      event.blockList().clear();
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onMove(final PlayerMoveEvent event) {
    final Location from = event.getFrom();
    final Location to = event.getTo();
    final Player player = event.getPlayer();
    if (from.getWorld().getEnvironment() != World.Environment.THE_END
        || to.getWorld().getEnvironment() != World.Environment.THE_END) {
      return;
    }
    if ((LandBoard.getInstance().getTeam(event.getPlayer().getLocation()) != null
        && LandBoard.getInstance().getTeam(event.getPlayer().getLocation()).getOwner() != null)) {
      return;
    }

    if (event.getPlayer().getLocation().getBlock().getType() == Material.WATER
        || event.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
      final float pitch = endExit.getPitch();
      final float yaw = endExit.getYaw();
      player.teleport(
          new Location(Bukkit.getWorlds().get(0), endExit.getX(), endExit.getY(), endExit.getZ()));
      player.getLocation().setPitch(pitch);
      player.getLocation().setPitch(yaw);
    }
    if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
      return;
    }
    if (CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
      return;
    }
    if (to.getBlockY() < -128) {
      event.getPlayer().teleport(Main.getInstance().getServerHandler().getSpawnLocation());
    }
  }

  @EventHandler
  public void onEntityPortal(final EntityPortalEvent event) {
    if (event.getEntity() instanceof EnderDragon) {
      event.setCancelled(true);
    }
  }
}