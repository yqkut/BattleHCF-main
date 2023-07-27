package cc.stormworth.hcf.server;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.player.InventorySerialization;
import cc.stormworth.hcf.util.player.Logout;
import cc.stormworth.hcf.util.player.Spawn;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerHandler {

  private static final double MAX_DISTANCE = 1;
  public static int WARZONE_RADIUS = Main.getInstance().getMapHandler().getWarzone();
  public static int WARZONE_BORDER = 3000;
  private final Map<String, Logout> logouttasks;
  private final Map<String, Spawn> spawntasks;
  private final Map<String, Long> homeTimer;
  private boolean EOTW = false;
  private boolean PreEOTW = false;
  @Getter
  @Setter
  private ItemStack[] fjiItems;
  @Getter
  @Setter
  private ItemStack[] dailyItems;

  public ServerHandler() {
    this.logouttasks = new HashMap<>();
    this.spawntasks = new HashMap<>();
    this.homeTimer = new HashMap<>();
    this.EOTW = false;
    this.PreEOTW = false;
    try {
      if (Main.getInstance().getUtilitiesFile().getConfig().getString("first-join-items").equals("")) {
        this.fjiItems = new ItemStack[36];
      } else {
        this.fjiItems = InventorySerialization.itemStackArrayFromBase64(
            Main.getInstance().getUtilitiesFile().getConfig().getString("first-join-items"));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getEnchants() {
    if (Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0
        && Enchantment.DAMAGE_ALL.getMaxLevel() == 0) {
      return "No Enchants";
    }
    return "Prot " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + ", Sharp "
        + Enchantment.DAMAGE_ALL.getMaxLevel();
  }

  public boolean isWarzone(final Location loc) {
    return loc.getWorld().getEnvironment() == World.Environment.NORMAL && (
        (FastMath.abs(loc.getBlockX()) <= ServerHandler.WARZONE_RADIUS
            && FastMath.abs(loc.getBlockZ()) <= ServerHandler.WARZONE_RADIUS)
            || FastMath.abs(loc.getBlockX()) > ServerHandler.WARZONE_BORDER
            || FastMath.abs(loc.getBlockZ()) > ServerHandler.WARZONE_BORDER);
  }

  public void startLogoutSequence(final Player player) {
    player.sendMessage(
        ChatColor.YELLOW.toString() + ChatColor.BOLD + "Logging out... " + ChatColor.YELLOW
            + "Please wait" + ChatColor.RED + " 30" + ChatColor.YELLOW + " seconds.");
    CooldownManager.addCooldown(player.getUniqueId(), CooldownType.LOGOUT, 30);

    final BukkitTask taskid = new BukkitRunnable() {
      private final Location loc = player.getLocation();
      private final int xStart = (int) loc.getX();
      private final int yStart = (int) loc.getY();
      private final int zStart = (int) loc.getZ();

      int seconds = 30;

      public void run() {
        Location loc = player.getLocation();
        if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (
            loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (
            loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
          player.sendMessage(
              ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED
                  + ChatColor.BOLD + "CANCELLED!");
          ServerHandler.this.logouttasks.remove(player.getName());
          CooldownManager.removeCooldown(player.getUniqueId(), CooldownType.LOGOUT);
          cancel();
          return;
        }
        if (this.seconds <= 0 && ServerHandler.this.logouttasks.containsKey(player.getName())) {
          ServerHandler.this.logouttasks.remove(player.getName());
          player.setMetadata("loggedout", new FixedMetadataValue(Main.getInstance(), true));
          player.kickPlayer("§cYou have been safely logged out of the server!");
          this.cancel();
        }
        --this.seconds;
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);

    this.logouttasks.put(player.getName(), new Logout(taskid.getTaskId(),
        System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L)));
  }

  public void startSpawnSequence(final Player player) {
    player.sendMessage(
        ChatColor.YELLOW.toString() + ChatColor.BOLD + "Teleporting... " + ChatColor.YELLOW
            + "Please wait" + ChatColor.RED + " 30" + ChatColor.YELLOW + " seconds.");

    final BukkitTask taskid = new BukkitRunnable() {
      private final Location loc = player.getLocation();
      private final int xStart = (int) loc.getX();
      private final int yStart = (int) loc.getY();
      private final int zStart = (int) loc.getZ();

      int seconds = 15;

      public void run() {
        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
          player.sendMessage(CC.RED + "You cannot use spawn inside a safe-zone.");
          ServerHandler.this.spawntasks.remove(player.getName());
          this.cancel();
          return;
        }
        Location loc = player.getLocation();
        if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (
            loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (
            loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
          player.sendMessage(
              ChatColor.YELLOW.toString() + ChatColor.BOLD + "TELEPORTING " + ChatColor.RED
                  + ChatColor.BOLD + "CANCELLED!");
          ServerHandler.this.spawntasks.remove(player.getName());
          cancel();
          return;
        }
        if (this.seconds <= 0 && ServerHandler.this.spawntasks.containsKey(player.getName())) {
          ServerHandler.this.spawntasks.remove(player.getName());
          player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
          this.cancel();
        }
        --this.seconds;
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);

    this.spawntasks.put(player.getName(),
        new Spawn(taskid.getTaskId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L)));
  }

  public RegionData getRegion(final Team ownerTo, final Location location) {
    if (ownerTo != null && ownerTo.getOwner() == null) {
      if (ownerTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
        return new RegionData(RegionType.SPAWN, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.KOTH)) {
        return new RegionData(RegionType.KOTH, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.CITADEL)) {
        return new RegionData(RegionType.CITADEL, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.ROAD)) {
        return new RegionData(RegionType.ROAD, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.END_PORTAL)) {
        return new RegionData(RegionType.END_PORTAL, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.RESTRICTED_ZONE)) {
        return new RegionData(RegionType.BUFFER_ZONE, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.RESTRICTED_ZONE)) {
        return new RegionData(RegionType.RESTRICTED_ZONE, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
        return new RegionData(RegionType.CONQUEST, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.NETHER)) {
        return new RegionData(RegionType.NETHER, ownerTo);
      }
      if (ownerTo.hasDTRBitmask(DTRBitmask.NETHER_ZONE)) {
        return new RegionData(RegionType.NETHER_ZONE, ownerTo);
      }
            /*if (ownerTo.hasDTRBitmask(DTRBitmask.FOREST)) {
                return new RegionData(RegionType.FOREST, ownerTo);
            }*/
    }
    if (ownerTo != null) {
      return new RegionData(RegionType.CLAIMED_LAND, ownerTo);
    }
    if (this.isWarzone(location)) {
      return new RegionData(RegionType.WARZONE, null);
    }
    return new RegionData(RegionType.WILDNERNESS, null);
  }

  public boolean isUnclaimed(final Location loc) {
    return LandBoard.getInstance().getClaim(loc) == null && !this.isWarzone(loc);
  }

  public boolean isAdminOverride(final Player player) {
    return player.getGameMode() == GameMode.CREATIVE;
  }

  public Location getSpawnLocation() {
    return (Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 0, 0.5));
  }

  public boolean isUnclaimedOrRaidable(Location loc) {
    Team owner = LandBoard.getInstance().getTeam(loc);
    return owner == null || owner.isRaidable();
  }

  public boolean isRaidable(final Location loc) {
    final Team owner = LandBoard.getInstance().getTeam(loc);
    return owner.isRaidable();
  }

  public double getDTRLoss(final Player player) {
    return this.getDTRLoss(player.getLocation());
  }

  public double getDTRLoss(final Location location) {
    double dtrLoss = 1.00D;
    if (Main.getInstance().getMapHandler().isKitMap()) {
      if (DTRBitmask.KOTH.appliesAt(location) || DTRBitmask.CONQUEST.appliesAt(location)
          || DTRBitmask.CITADEL.appliesAt(location)) {
        dtrLoss = FastMath.min(dtrLoss, 0.80D);
      } else {
        dtrLoss = FastMath.min(dtrLoss, 0.40D);
      }
    }
    final Team ownerTo = LandBoard.getInstance().getTeam(location);
    if (Main.getInstance().getConquestHandler().getGame() != null
        && location.getWorld().getEnvironment() == World.Environment.THE_END && ownerTo != null
        && ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
      dtrLoss = FastMath.min(dtrLoss, 0.50D);
    }
    if (ownerTo != null) {
      if (ownerTo.hasDTRBitmask(DTRBitmask.QUARTER_DTR_LOSS)) {
        dtrLoss = FastMath.min(dtrLoss, 0.25D);
      } else if (ownerTo.hasDTRBitmask(DTRBitmask.REDUCED_DTR_LOSS)) {
        dtrLoss = FastMath.min(dtrLoss, 0.75D);
      }
    }
    return dtrLoss;
  }

  public long getDeathban(final Player player) {
    return this.getDeathban(player.getUniqueId(), player.getLocation());
  }

  public int getDeathban(final UUID playerUUID, final Location location) {
    final Player player = Main.getInstance().getServer().getPlayer(playerUUID);
    if (this.isPreEOTW() || this.isEOTW()) {
      return (int) TimeUnit.DAYS.toSeconds(7L);
    }
    if (Main.getInstance().getMapHandler().isKitMap()) {
      return (int) TimeUnit.SECONDS.toSeconds(5L);
    }
    return Deathban.getDeathbanSeconds(player);
  }

  public void beginHQWarp(Player player, Team team, int warmup, boolean charge) {
    Team inClaim = LandBoard.getInstance().getTeam(player.getLocation());

    if (inClaim != null) {
      if (inClaim.getOwner() == null && (inClaim.hasDTRBitmask(DTRBitmask.KOTH)
          || inClaim.hasDTRBitmask(DTRBitmask.CITADEL))) {
        player.sendMessage(
            ChatColor.RED + "You may not go to your team headquarters from inside of events!");
        return;
      }
      if (inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
          player.teleport(team.getHQ());
        } else {
          player.sendMessage(ChatColor.RED
              + "You cannot teleport to your end headquarters while you're in end spawn!");
        }
        return;
      }
    }
    if (SpawnTagHandler.isTagged(player)) {
      player.sendMessage(
          ChatColor.RED + "You may not go to your team headquarters while spawn tagged!");
      return;
    }
    player.sendMessage(
        ChatColor.YELLOW + "Teleporting to your team's HQ in " + ChatColor.GOLD + warmup
            + " seconds" + ChatColor.YELLOW + "... Stay still and don't take damage.");
    if (HCFProfile.get(player).hasPvPTimer()) {
      player.sendMessage(
          ChatColor.RED + "Your PvP Timer will be removed if the teleport is not cancelled.");
    }

    CooldownManager.addCooldown(player.getUniqueId(), CooldownType.HOME, warmup);

    this.homeTimer.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmup));
    new BukkitRunnable() {
      int time = warmup;
      final Location startLocation = player.getLocation();
      double startHealth = player.getHealth();

      public void run() {
        --this.time;

        if (!player.isOnline()){
          ServerHandler.this.homeTimer.remove(player.getName());
          this.cancel();
          return;
        }

        if (!player.getLocation().getWorld().equals(this.startLocation.getWorld()) || player.getLocation().distanceSquared(this.startLocation) >= 0.1 || player.getHealth() < this.startHealth) {
          player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
          ServerHandler.this.homeTimer.remove(player.getName());
          CooldownManager.removeCooldown(player.getUniqueId(), CooldownType.HOME);
          this.cancel();
          return;
        }
        this.startHealth = player.getHealth();
        if (!ServerHandler.this.homeTimer.containsKey(player.getName()) || ServerHandler.this.homeTimer.get(player.getName()) > System.currentTimeMillis()) {

          if (this.time == 0) {

            HCFProfile profile = HCFProfile.get(player);

           if(profile == null){
             ServerHandler.this.homeTimer.remove(player.getName());
             this.cancel();
             return;
           }

            if (profile.hasPvPTimer()) {
              profile.setPvpTimer(null);
            }

            TaskUtil.runAsync(Main.getInstance(), () -> {
              Utils.removeThrownPearls(player);
            });
            player.teleport(team.getHQ());
            ServerHandler.this.homeTimer.remove(player.getName());
            this.cancel();
          }
          return;
        }

        HCFProfile profile = HCFProfile.get(player);

        if(profile == null) {
          this.cancel();
          return;
        }

        if (profile.hasPvPTimer()) {
          profile.setPvpTimer(null);
        }

        TaskUtil.runAsync(Main.getInstance(), () -> {
          Utils.removeThrownPearls(player);
        });
        if (team.getHQ() == null) {
          ServerHandler.this.homeTimer.remove(player.getName());
          CooldownManager.removeCooldown(player.getUniqueId(), CooldownType.HOME);
          this.cancel();
          return;
        }
        player.teleport(team.getHQ());
        ServerHandler.this.homeTimer.remove(player.getName());
        this.cancel();
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);
  }


  public boolean isSpawnBufferZone(final Location loc) {
    if (loc.getWorld().getEnvironment() != World.Environment.NORMAL) {
      return false;
    }
    final int radius = Main.getInstance().getMapHandler().getWorldBuffer();
    final int x = loc.getBlockX();
    final int z = loc.getBlockZ();
    return x < radius && x > -radius && z < radius && z > -radius;
  }

  public boolean isNetherBufferZone(final Location loc) {
    if (loc.getWorld().getEnvironment() != World.Environment.NETHER) {
      return false;
    }
    final int radius = Main.getInstance().getMapHandler().getNetherBuffer();
    final int x = loc.getBlockX();
    final int z = loc.getBlockZ();
    return x < radius && x > -radius && z < radius && z > -radius;
  }

  public ItemStack generateDeathSign(final String killed, final String killer) {
    final ItemStack deathsign = new ItemStack(Material.SIGN);
    final ItemMeta meta = deathsign.getItemMeta();
    final List<String> lore = new ArrayList<>();
    lore.add("§c" + killed);
    lore.add("§eSlain By:");
    lore.add("§a" + killer);
    final DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
    lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));
    meta.setLore(lore);
    meta.setDisplayName("§6Death Sign");
    deathsign.setItemMeta(meta);
    return deathsign;
  }

  public ItemStack generateKOTHSign(String koth, String capper, EventType eventType) {
    ItemStack kothsign = new ItemStack(Material.SIGN);
    ItemMeta meta = kothsign.getItemMeta();

    List<String> lore = new ArrayList<>();

    lore.add("§6" + koth);
    lore.add("§eCaptured By:");
    lore.add("§a" + capper);

    DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

    lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

    meta.setLore(lore);
    meta.setDisplayName("§6" + eventType.name() + "Capture Sign");
    kothsign.setItemMeta(meta);

    return (kothsign);
  }

  public Map<String, Logout> getLogouttasks() {
    return this.logouttasks;
  }

  public Map<String, Spawn> getSpawntasks() {
    return this.spawntasks;
  }

  public Map<String, Long> getHomeTimer() {
    return this.homeTimer;
  }

  public boolean isEOTW() {
    return this.EOTW;
  }

  public void setEOTW(final boolean EOTW) {
    this.EOTW = EOTW;
  }

  public boolean isPreEOTW() {
    return this.PreEOTW;
  }

  public void setPreEOTW(final boolean PreEOTW) {
    this.PreEOTW = PreEOTW;
  }
}