package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.npc.NPCEntity;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.holograms.HoloNPC;
import cc.stormworth.hcf.listener.HologramManager;
import cc.stormworth.hcf.listener.SetListener;
import cc.stormworth.hcf.misc.lunarclient.waypoint.PlayerWaypointType;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetCommand {


  @Command(names = {"setlocation endexit"}, permission = "op", description = "overworld")
  public static void setendexit(final Player sender) {
    final Location previous = SetListener.getEndExit();
    SetListener.setEndExit(sender.getLocation());
    final Location current = SetListener.getEndExit();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "End exit (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "End exit (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveEndExit();
  }

  @Command(names = {"setlocation eotwffa"}, permission = "op")
  public static void eotwffa(final Player sender) {
    final Location previous = SetListener.getEotwffa();
    SetListener.setEotwffa(sender.getLocation());
    final Location current = SetListener.getEotwffa();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "EOTW FFA (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "EOTW FFA (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveEotwFFA();
  }

  @Command(names = {"setlocation endreturn"}, permission = "op", description = "End")
  public static void setendreturn(final Player sender) {
    final Location previous = SetListener.getEndreturn();
    SetListener.setEndreturn(sender.getLocation());
    final Location current = SetListener.getEndreturn();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "End exit (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "End exit (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveEndreturn();
    WaypointManager.updateGlobalWaypoints(PlayerWaypointType.END_RETURN, true);
  }

  @Command(names = {"setlocation infoholo"}, permission = "op")
  public static void infoholo(final Player sender) {
    final Location previous = SetListener.getInfoholo();
    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    SetListener.setInfoholo(sender.getLocation());
    final Location current = SetListener.getInfoholo();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Info Holo (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Info Holo (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveInfoHolo();
    Hologram hologram = Holograms.newHologram().at(current.clone().add(0, 1.5, 0)).addLines(
        HologramManager.infolines).build();
    hologram.send();
  }

  @Command(names = {"setlocation deathban"}, permission = "op")
  public static void setdeathban(final Player sender) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis is a HCF only command."));
      return;
    }
    final Location previous = SetListener.getDeathban();
    SetListener.setDeathban(sender.getLocation());
    final Location current = SetListener.getDeathban();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Deathban (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Deathban (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveDeathban();
  }


  @Command(names = {"setlocation shop"}, permission = "op")
  public static void shop(final Player sender) {
    final Location previous = SetListener.getShop();
    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Location loc = sender.getLocation();
    SetListener.setShop(loc);
    final Location current = SetListener.getShop();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Shop (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Shop (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveBlockShop();

    // hologram
    Hologram hologram = Holograms.newHologram().at(loc.clone().add(0, 1.7, 0)).addLines(
        HologramManager.shoplines).build();
    hologram.send();
    HologramManager.holograms.put(loc.clone().add(0, 1.7, 0), hologram);

    // npc
    NPCEntity npc = NPCEntity.getByName("shop");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setlocation daily"}, permission = "op")
  public static void daily(final Player sender) {
    final Location previous = SetListener.getDaily();
    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Location loc = sender.getLocation();
    SetListener.setDaily(loc);
    final Location current = SetListener.getDaily();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Daily (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Daily (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveDaily();

    // hologram
    Hologram hologram = Holograms.newHologram().at(loc.clone().add(0, 1.7, 0)).addLines(
        HologramManager.dailylines).build();
    hologram.send();
    HologramManager.holograms.put(loc.clone().add(0, 1.7, 0), hologram);

    // npc
    NPCEntity npc = NPCEntity.getByName("daily");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setlocation store"}, permission = "op")
  public static void store(final Player sender) {
    final Location previous = SetListener.getStore();
    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Location loc = sender.getLocation();
    SetListener.setStore(loc);
    final Location current = SetListener.getStore();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Store (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Store (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saveStore();

    // hologram
    Hologram hologram = Holograms.newHologram().at(loc.clone().add(0, 1.7, 0)).addLines(
        HologramManager.storelines).build();
    hologram.send();
    HologramManager.holograms.put(loc.clone().add(0, 1.7, 0), hologram);

    // npc
    NPCEntity npc = NPCEntity.getByName("store");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setlocation deathbanrevive"}, permission = "op")
  public static void revive(final Player sender) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis is a HCF only command."));
      return;
    }
    final Location previous = SetListener.getRevive();
    SetListener.setRevive(sender.getLocation());
    final Location current = SetListener.getRevive();

    NPCEntity npc = NPCEntity.getByName("revive");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines(SetListener.revivelines).build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    if (previous != null) {
      sender.sendMessage(ChatColor.GREEN + "Revive (" + ChatColor.WHITE + previous.getBlockX() + ":"
          + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
          + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
          + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(ChatColor.GREEN + "Revive (" + ChatColor.WHITE + current.getBlockX() + ":"
          + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.saverevive();
  }

  @Command(names = {"setnpc deathbankit"}, permission = "op")
  public static void deathbankit(final Player sender) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis is a HCF only command."));
      return;
    }
    final Location previous = SetListener.getDeathbankit();
    SetListener.setDeathbankit(sender.getLocation());
    final Location current = SetListener.getDeathbankit();

    NPCEntity npc = NPCEntity.getByName("deathbankit");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines(SetListener.deathbankitlines).build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "DeathbanKit (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "DeathbanKit (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    SetListener.savedeathbankit();
  }

  private static final cc.stormworth.hcf.holograms.HologramManager manager = Main.getInstance()
      .getHologramManager();

  @Command(names = {"setnpclist"}, permission = "op")
  public static void setnpclist(Player player) {
    player.sendMessage(CC.translate("&cNPCs: "));
    manager.getHologramLocations().keySet()
        .forEach(name -> player.sendMessage(CC.translate("&e" + name)));
  }

  @Command(names = {"setnpc"}, permission = "op")
  public static void setnpc(Player player, @Param(name = "name") String name) {

    if(!manager.getHologramLocations().containsKey(name)){
        player.sendMessage(CC.translate("&cNPC does not exist."));
        player.sendMessage(CC.translate("&cNPCs: "));

        manager.getHologramLocations().keySet().forEach(n -> player.sendMessage(CC.translate("&e" + n)));
        return;
    }

    HoloNPC holoNPC = manager.getHologramLocations().get(name);

    NPCEntity npc = NPCEntity.getByName(name);
    npc.setLocation(player.getLocation());

    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);

    if (holoNPC.getHologram() != null) {
      holoNPC.getHologram().destroy();
    }

    Hologram holo = Holograms.newHologram().at(player.getLocation().clone().add(0, 1.7, 0))
        .addLines(holoNPC.getHologram().getLines()).build();
    holo.send();

    manager.getHologramLocations().put(name,
        new HoloNPC(player.getLocation(), holo));
    manager.saveLocation(name);

    player.sendMessage(CC.translate("&aYou have set the NPC &e" + name + " &ato your location."));
  }

}