package cc.stormworth.hcf.misc.kits.command;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.npc.NPCEntity;
import cc.stormworth.hcf.listener.HologramManager;
import cc.stormworth.hcf.misc.kits.KitsNPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KitNPCCommand {

  @Command(names = {"setkitnpc donor"}, permission = "op")
  public static void donor(Player sender) {
    final Location previous = KitsNPC.getDonorloc();
    KitsNPC.setDonorloc(sender.getLocation());
    final Location current = KitsNPC.getDonorloc();
    if (previous != null) {
      sender.sendMessage(
              ChatColor.GREEN + "Donor (" + ChatColor.WHITE + previous.getBlockX() + ":"
                      + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
                      + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
                      + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
              ChatColor.GREEN + "Donor (" + ChatColor.WHITE + current.getBlockX() + ":"
                      + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveDonor();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
            .addLines("&6&lDonor Kit",
                    "&e&lCLICK ME",
                    "",
                    "&ePurchaseable at",
                    "&c♡ &7&ostore.battle.rip &c♡").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("donor");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc battle"}, permission = "op")
  public static void battle(Player sender) {
    final Location previous = KitsNPC.getBattleloc();
    KitsNPC.setBattleloc(sender.getLocation());
    final Location current = KitsNPC.getBattleloc();
    if (previous != null) {
      sender.sendMessage(
              ChatColor.GREEN + "Battle (" + ChatColor.WHITE + previous.getBlockX() + ":"
                      + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
                      + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
                      + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
              ChatColor.GREEN + "Battle (" + ChatColor.WHITE + current.getBlockX() + ":"
                      + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveBattle();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
            .addLines("&6&lBattle Kit",
                    "&e&lCLICK ME",
                    "",
                    "&ePurchaseable at",
                    "&c♡ &7&ostore.battle.rip &c♡").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("battle");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc seasonal"}, permission = "op")
  public static void release(Player sender) {
    final Location previous = KitsNPC.getReleaseloc();
    KitsNPC.setReleaseloc(sender.getLocation());
    final Location current = KitsNPC.getReleaseloc();
    if (previous != null) {
      sender.sendMessage(
              ChatColor.GREEN + "Seasonal (" + ChatColor.WHITE + previous.getBlockX() + ":"
                      + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
                      + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
                      + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
              ChatColor.GREEN + "Seasonal (" + ChatColor.WHITE + current.getBlockX() + ":"
                      + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveRelease();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
            .addLines("&6&lSeasonal Kit",
                    "&e&lCLICK ME",
                    "",
                    "&ePurchaseable at",
                    "&c♡ &7&ostore.battle.rip &c♡").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("seasonal");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc archerpremium"}, permission = "op")
  public static void archerpremium(Player sender) {

    final Location previous = KitsNPC.getArcherPremiumloc();
    KitsNPC.setArcherPremiumloc(sender.getLocation());
    final Location current = KitsNPC.getArcherPremiumloc();

    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Archer Premium (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Archer Premium (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveArcherPremium();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lArcher Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡")
        .build();

    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("archerpremium");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc bardpremium"}, permission = "op")
  public static void bardpremium(Player sender) {

    final Location previous = KitsNPC.getBardPremiumloc();
    KitsNPC.setBardPremiumloc(sender.getLocation());
    final Location current = KitsNPC.getBardPremiumloc();

    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Bard Premium (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Bard Premium (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveBardPremium();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lBard Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡")
        .build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("bardpremium");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc roguepremium"}, permission = "op")
  public static void rougepremium(Player sender) {

    final Location previous = KitsNPC.getRougePremiumloc();
    KitsNPC.setRougePremiumloc(sender.getLocation());
    final Location current = KitsNPC.getRougePremiumloc();

    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Rogue Premium (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Rogue Premium (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveRougePremium();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lRogue Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡")
        .build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("rougepremium");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc duelistpremium"}, permission = "op")
  public static void duelistpremium(Player sender) {

    final Location previous = KitsNPC.getDuelistPremiumloc();
    KitsNPC.setDuelistPremiumloc(sender.getLocation());
    final Location current = KitsNPC.getDuelistPremiumloc();

    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Duelist Premium (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
          ChatColor.GREEN + "Duelist Premium (" + ChatColor.WHITE + current.getBlockX() + ":"
              + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveDuelistPremium();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lDuelist Premium Kit",
            "&e&lCLICK ME",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡")
        .build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("duelistpremium");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc duelist"}, permission = "op")
  public static void duelist(Player sender) {

    final Location previous = KitsNPC.getDuelistloc();
    KitsNPC.setDuelistloc(sender.getLocation());
    final Location current = KitsNPC.getDuelistloc();

    if (previous != null) {
      sender.sendMessage(
              ChatColor.GREEN + "Duelist (" + ChatColor.WHITE + previous.getBlockX() + ":"
                      + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
                      + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
                      + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(
              ChatColor.GREEN + "Duelist (" + ChatColor.WHITE + current.getBlockX() + ":"
                      + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saveDuelist();

    if (HologramManager.holograms.containsKey(previous)) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }

    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
            .addLines("&6&lDuelist Kit",
                    "&e&lCLICK ME")
            .build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("duelist");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc pvp"}, permission = "op")
  public static void pvp(final Player sender) {
    final Location previous = KitsNPC.getPvploc();
    KitsNPC.setPvploc(sender.getLocation());
    final Location current = KitsNPC.getPvploc();
    if (previous != null) {
      sender.sendMessage(ChatColor.GREEN + "PvP (" + ChatColor.WHITE + previous.getBlockX() + ":"
          + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
          + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
          + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(ChatColor.GREEN + "PvP (" + ChatColor.WHITE + current.getBlockX() + ":"
          + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.savepvp();

    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lPvP Kit", "&e&lCLICK ME").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("pvp");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc bard"}, permission = "op")
  public static void bard(final Player sender) {
    final Location previous = KitsNPC.getBardloc();
    KitsNPC.setBardloc(sender.getLocation());
    final Location current = KitsNPC.getBardloc();
    if (previous != null) {
      sender.sendMessage(ChatColor.GREEN + "Bard (" + ChatColor.WHITE + previous.getBlockX() + ":"
          + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
          + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
          + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(ChatColor.GREEN + "Bard (" + ChatColor.WHITE + current.getBlockX() + ":"
          + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.savebard();

    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lBard Kit", "&e&lCLICK ME").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("bard");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc archer"}, permission = "op")
  public static void archer(final Player sender) {
    final Location previous = KitsNPC.getArcherloc();
    KitsNPC.setArcherloc(sender.getLocation());
    final Location current = KitsNPC.getArcherloc();
    if (previous != null) {
      sender.sendMessage(ChatColor.GREEN + "Archer (" + ChatColor.WHITE + previous.getBlockX() + ":"
          + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
          + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
          + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(ChatColor.GREEN + "Archer (" + ChatColor.WHITE + current.getBlockX() + ":"
          + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.savearcher();

    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lArcher Kit", "&e&lCLICK ME").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("archer");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc rogue"}, permission = "op")
  public static void rogue(final Player sender) {
    final Location previous = KitsNPC.getRogueloc();
    KitsNPC.setRogueloc(sender.getLocation());
    final Location current = KitsNPC.getRogueloc();
    if (previous != null) {
      sender.sendMessage(ChatColor.GREEN + "Rogue (" + ChatColor.WHITE + previous.getBlockX() + ":"
          + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
          + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
          + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(ChatColor.GREEN + "Rogue (" + ChatColor.WHITE + current.getBlockX() + ":"
          + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.saverogue();

    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lRogue Kit", "&e&lCLICK ME").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("rogue");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }

  @Command(names = {"setkitnpc builder"}, permission = "op")
  public static void builder(final Player sender) {
    final Location previous = KitsNPC.getBuilderloc();
    KitsNPC.setBuilderloc(sender.getLocation());
    final Location current = KitsNPC.getBuilderloc();
    if (previous != null) {
      sender.sendMessage(
          ChatColor.GREEN + "Builder (" + ChatColor.WHITE + previous.getBlockX() + ":"
              + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> "
              + ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":"
              + current.getBlockZ() + ChatColor.GREEN + ")");
    } else {
      sender.sendMessage(ChatColor.GREEN + "Builder (" + ChatColor.WHITE + current.getBlockX() + ":"
          + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")");
    }
    KitsNPC.savebuilder();

    if (HologramManager.holograms.get(previous) != null) {
      HologramManager.holograms.get(previous).destroy();
      HologramManager.holograms.remove(previous);
    }
    Hologram holo = Holograms.newHologram().at(current.clone().add(0, 1.7, 0))
        .addLines("&6&lBuilder Kit", "&e&lCLICK ME").build();
    holo.send();
    HologramManager.holograms.put(current.clone().add(0, 1.7, 0), holo);

    NPCEntity npc = NPCEntity.getByName("builder");
    npc.setLocation(current);
    Bukkit.getOnlinePlayers().forEach(npc::destroy);
    Bukkit.getOnlinePlayers().forEach(npc::spawn);
  }


}