package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.holograms.BaseHologram;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.npc.NPCEntity;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.listener.HologramManager;
import cc.stormworth.hcf.listener.SpectatorListener;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.pvpclasses.pvpclasses.MinerClass;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.threads.PacketBorderThread;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EOTWCommand {

  private static boolean ffaEnabled;
  private static long ffaActiveAt;

  static {
    EOTWCommand.ffaEnabled = false;
    EOTWCommand.ffaActiveAt = -1L;
  }

  @Command(names = {"EOTW start"}, permission = "op", async = true)
  public static void eotw(final CommandSender sender) {

    if((sender instanceof Player)){
      Bukkit.getConsoleSender().sendMessage(sender.getName() + " tried to start EOTW");
        sender.sendMessage(ChatColor.RED + "This command is only available in console.");
        return;
    }

    Main.getInstance().getServerHandler().setEOTW(!Main.getInstance().getServerHandler().isEOTW());

    WaypointManager.getGlobalWaypoints().forEach((type, waypoint) ->
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> WaypointManager.removeWaypoint(onlinePlayer, waypoint)));

    Main.getInstance().getHologramManager().getHologramLocations().forEach((name, hologram) -> {
      NPCEntity npc = NPCEntity.getByName(name);

      Bukkit.getOnlinePlayers().forEach(npc::destroy);

      hologram.getHologram().destroy();
    });

    if (Main.getInstance().getServerHandler().isEOTW()) {
      TaskUtil.run(Main.getInstance(), () -> {
        HandlerList.unregisterAll(Main.getInstance().getSignSubclaimListener());
        HandlerList.unregisterAll(Main.getInstance().getPickAxes());
        HandlerList.unregisterAll(Main.getInstance().getSwords());
        HandlerList.unregisterAll(Main.getInstance().getInvisibilityListener());
        HandlerList.unregisterAll(Main.getInstance().getEventHandler().getDtcListener());
      });
      Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
          () -> Main.getInstance().getTeamHandler().getTeams().stream()
              .filter(team -> team.getOwner() != null).forEach(team -> {
                team.setDTR(-0.99);
              }));
      new KOTH("EOTW", Main.getInstance().getServerHandler().getSpawnLocation());
      new EOTWTeam();

      for (final Player player : Bukkit.getOnlinePlayers()) {
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
      }
      ((KOTH) Main.getInstance().getEventHandler().getEvent("EOTW")).setCapTime((int) (8 * 60F));
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588"
              + ChatColor.RED + "\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.DARK_RED + "[EOTW]");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588" + ChatColor.RED
              + "\u2588\u2588 " + ChatColor.RED + ChatColor.BOLD + "EOTW has commenced.");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.RED
              + "All SafeZones are now Deathban.");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588"
              + ChatColor.RED + "\u2588");
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      for (Hologram holo : HologramManager.holograms.values()) {
        BaseHologram hologram2 = (BaseHologram) holo;
        hologram2.destroy();
      }

      HologramManager.holograms.clear();
      //HologramsListener.factionHologram.destroy();
      //HologramsListener.personalHologram.destroy();
      if (Main.getInstance().getMapHandler().isKitMap()) {
        NPCEntity pvp = NPCEntity.getByName("pvp");
        if (pvp != null) {
          Bukkit.getOnlinePlayers().forEach(pvp::destroy);
          NPCEntity.getNpcs().remove("pvp");
        }

        NPCEntity bard = NPCEntity.getByName("bard");
        if (bard != null) {
          Bukkit.getOnlinePlayers().forEach(bard::destroy);
          NPCEntity.getNpcs().remove("bard");
        }

        NPCEntity archer = NPCEntity.getByName("archer");
        if (archer != null) {
          Bukkit.getOnlinePlayers().forEach(archer::destroy);
          NPCEntity.getNpcs().remove("archer");
        }

        NPCEntity rogue = NPCEntity.getByName("rogue");
        if (rogue != null) {
          Bukkit.getOnlinePlayers().forEach(rogue::destroy);
          NPCEntity.getNpcs().remove("rogue");
        }

        NPCEntity builder = NPCEntity.getByName("builder");
        if (builder != null) {
          Bukkit.getOnlinePlayers().forEach(builder::destroy);
          NPCEntity.getNpcs().remove("builder");
        }

      }
      NPCEntity shop = NPCEntity.getByName("shop");
      if (shop != null) {
        Bukkit.getOnlinePlayers().forEach(shop::destroy);
        NPCEntity.getNpcs().remove("shop");
      }
    } else {
      sender.sendMessage(ChatColor.RED + "The server is no longer in EOTW mode.");
    }
  }

  @Command(names = {"EOTW startffa"}, permission = "op", hidden = true)
  public static void startffa(final Player sender) {

    if (sender.getGameMode() != GameMode.CREATIVE) {
      sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
      return;
    }

    startFFA();

    sender.sendMessage(ChatColor.GREEN + "EOTW FFA Started.");
  }

  @Command(names = {"giveffaeffects", "ffaeffects"}, permission = "op", hidden = true)
  public static void giveffaeffects(Player sender) {

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer.getGameMode() != GameMode.CREATIVE) {
        onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
      }
    }

    sender.sendMessage(CC.translate("&aYou have given all players FFA effects."));
  }

  @Command(names = {"EOTW teleport"}, permission = "op", hidden = true)
  public static void eotwTpAll(final Player sender) {

    if (sender.getGameMode() != GameMode.CREATIVE) {
      sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
      return;
    }
    if (!Main.getInstance().getServerHandler().isEOTW()) {
      sender.sendMessage(ChatColor.RED + "This command must be ran during EOTW. (/eotw)");
      return;
    }

    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      onlinePlayer.teleport(sender.getLocation());
    }

    sender.sendMessage(ChatColor.RED + "Players teleported.");
  }

  @Command(names = {"EOTW pre"}, permission = "op", hidden = true)
  public static void preeotw(final Player sender) {

    if (sender.getGameMode() != GameMode.CREATIVE) {
      sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
      return;
    }

    Main.getInstance().spectatorListener = new SpectatorListener();

    Main.getInstance().getServerHandler().setPreEOTW(!Main.getInstance().getServerHandler().isPreEOTW());

    TaskUtil.runAsync(Main.getInstance(), () -> {
      /*for (UUID uuid : Main.getInstance().getDeathbanMap().getDeathbannedPlayers()) {
        Main.getInstance().getDeathbanMap().revive(uuid, true);
      }*/
    });

    if (Main.getInstance().dtrTask != null) {
      Main.getInstance().getDtrTask().cancel();
      Main.getInstance().dtrTask = null;
    }

    if (MinerClass.minertask != null) {
      MinerClass.minertask.cancel();
      MinerClass.minertask = null;
    }

    //Main.getInstance().getDeathbanMap().wipeDeathbans();

    if (Main.getInstance().getServerHandler().isPreEOTW()) {
      for (final Player player : Bukkit.getOnlinePlayers()) {
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
      }
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588"
              + ChatColor.RED + "\u2588 " + ChatColor.DARK_RED + "[Pre-EOTW]");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.RED + ChatColor.BOLD
              + "EOTW is about to commence.");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588" + ChatColor.RED
              + "\u2588\u2588 " + ChatColor.RED + "PvP Protection is disabled.");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.RED
              + "All players have been un-deathbanned.");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588"
              + ChatColor.RED + "\u2588 " + ChatColor.RED + "All deathbans are now permanent.");
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      new PacketBorderThread().stop();
    } else {
      new PacketBorderThread().start();
      sender.sendMessage(ChatColor.RED + "The server is no longer in Pre-EOTW mode.");
    }
  }

  public static void startFFA() {
    EOTWCommand.ffaEnabled = true;
    EOTWCommand.ffaActiveAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2L);
    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588"
              + ChatColor.RED + "\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.DARK_RED + "[EOTW]");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.RED
              + "\u2588\u2588 " + ChatColor.RED + ChatColor.BOLD + "EOTW " + ChatColor.GOLD
              + ChatColor.BOLD + "FFA" + ChatColor.RED + ChatColor.BOLD
              + " will commence in: 2:00.");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.RED);
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588");
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      return;
    });
    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588"
              + ChatColor.RED + "\u2588");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.DARK_RED + "[EOTW]");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.RED
              + "\u2588\u2588 " + ChatColor.RED + ChatColor.BOLD + "EOTW " + ChatColor.GOLD
              + ChatColor.BOLD + "FFA" + ChatColor.RED + ChatColor.BOLD + " has now commenced!");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588 " + ChatColor.RED + "Good luck and have fun!");
      Bukkit.broadcastMessage(
          ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED
              + "\u2588\u2588\u2588\u2588\u2588");
      Bukkit
          .broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
    }, 2400L);
  }

  public static boolean isFfaEnabled() {
    return EOTWCommand.ffaEnabled;
  }

  public static long getFfaActiveAt() {
    return EOTWCommand.ffaActiveAt;
  }

  public static class EOTWTeam extends Team {

    public EOTWTeam() {
      super("EOTW");
      int dtrInt = (int) this.getDTR();
      dtrInt += DTRBitmask.KOTH.getBitmask();
      this.setUniqueId(new ObjectId());
      this.setName("EOTW");
      this.setDTR(dtrInt);
      this.setHQ(Main.getInstance().getServerHandler().getSpawnLocation());
      Team spawn = Main.getInstance().getTeamHandler().getTeam("Spawn");
      if (spawn != null) {
        List<Claim> toAdd = new ArrayList<>();
        for (Claim claim : spawn.getClaims()) {
          if (!claim.getWorld().equalsIgnoreCase("world")) {
            continue;
          }
          Location loc1 = new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(),
              claim.getY1(), claim.getZ1());
          Location loc2 = new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(),
              claim.getY2(), claim.getZ2());
          toAdd.add(new Claim(loc1, loc2));
        }
        spawn.disband();

        for (Claim claim : toAdd) {
          claim.setName(this.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
          LandBoard.getInstance().setTeamAt(claim, this);
          this.getClaims().add(claim);
        }
        this.flagForSave();
        if (!Main.getInstance().getMapHandler().isKitMap()) {
          for (Claim claim : toAdd) {
            int buffer = 15;

            Location corner1 = claim.getCornerLocations()[0];
            Location corner2 = claim.getCornerLocations()[1];
            Location corner3 = claim.getCornerLocations()[2];
            Location corner4 = claim.getCornerLocations()[3];

            Main.getInstance().getTeamHandler().claimBufferKoth(corner1.clone().add(-buffer, 0, -1),
                corner4.clone().add(buffer, 0, -buffer));
            Main.getInstance().getTeamHandler()
                .claimBufferKoth(corner4.clone().add(buffer, 0, -buffer),
                    corner2.clone().add(1, 0, buffer));
            Main.getInstance().getTeamHandler().claimBufferKoth(corner2.clone().add(1, 0, buffer),
                corner3.clone().add(-buffer, 0, 1));
            Main.getInstance().getTeamHandler().claimBufferKoth(corner3.clone().add(-buffer, 0, 1),
                corner1.clone().add(-1, 0, -buffer));
          }
        }
      }

      Main.getInstance().getTeamHandler().setupTeam(this);
    }
  }
}