package cc.stormworth.hcf.misc.kits;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.npc.NPCEntity;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.listener.HologramManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

//TitleBuilder title = new TitleBuilder("&6&lStaff Mode", "&cHas been disabled", 20, 60, 20);

public class KitsNPC {

  @Getter
  @Setter
  public static Location pvploc;
  @Getter
  @Setter
  public static Location bardloc;
  @Getter
  @Setter
  public static Location rogueloc;
  @Getter
  @Setter
  public static Location archerloc;
  @Getter
  @Setter
  public static Location builderloc;
  @Getter
  @Setter
  public static Location donorloc;
  @Getter
  @Setter
  public static Location duelistloc;
  @Getter
  @Setter
  public static Location archerPremiumloc;
  @Getter
  @Setter
  public static Location bardPremiumloc;
  @Getter
  @Setter
  public static Location rougePremiumloc;
  @Getter
  @Setter
  public static Location duelistPremiumloc;
  @Getter
  @Setter
  public static Location battleloc;
  @Getter
  @Setter
  public static Location releaseloc;

  public KitsNPC() {
    loadpvp();
    loadbard();
    loadrogue();
    loadarcher();
    loadbuilder();
    loadDuelist();
    loadDonor();
    loadBattle();
    loadRelease();
    loadArcherPremium();
    loadBardPremium();
    loadRougePremium();
    loadDuelistPremium();

    // pvp
    Hologram pvpholo = Holograms.newHologram().at(pvploc.clone().add(0, 1.7, 0))
        .addLines("&6&lPvP Kit", "&e&lCLICK ME").build();
    pvpholo.send();
    HologramManager.holograms.put(pvploc.clone().add(0, 1.7, 0), pvpholo);

    NPCEntity pvpnpc = new NPCEntity("pvp");
    pvpnpc.setLocation(pvploc);
    pvpnpc.setSkinowner(UUID.fromString("0175212e-c38a-42c6-8e49-ef69592f54a1"));
    pvpnpc.setCmdcooldown(false);
    pvpnpc.setCommand("_managekit applynpc pvp");
    pvpnpc.setHand(
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build());
    pvpnpc.setHelmet(
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build());
    pvpnpc.setChest(
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build());
    pvpnpc.setLegs(
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build());
    pvpnpc.setBoots(
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build());

    // bard
    Hologram bardholo = Holograms.newHologram().at(bardloc.clone().add(0, 1.7, 0))
        .addLines("&6&lBard Kit", "&e&lCLICK ME").build();
    bardholo.send();
    HologramManager.holograms.put(bardloc.clone().add(0, 1.7, 0), bardholo);
    NPCEntity bardnpc = new NPCEntity("bard");
    bardnpc.setLocation(bardloc);
    bardnpc.setSkinowner(UUID.fromString("bf0ed950-cf39-431a-9463-6a9ac40dfd39"));
    bardnpc.setCmdcooldown(false);
    bardnpc.setCommand("_managekit applynpc bard");
    bardnpc.setHand(
        ItemBuilder.of(Material.BLAZE_POWDER).enchant(Enchantment.DURABILITY, 1).build());
    bardnpc.setHelmet(
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build());
    bardnpc.setChest(
        ItemBuilder.of(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build());
    bardnpc.setLegs(
        ItemBuilder.of(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build());
    bardnpc.setBoots(
        ItemBuilder.of(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 1).build());

    // archer
    Hologram archerholo = Holograms.newHologram().at(archerloc.clone().add(0, 1.7, 0))
        .addLines("&6&lArcher Kit", "&e&lCLICK ME").build();
    archerholo.send();
    HologramManager.holograms.put(archerloc.clone().add(0, 1.7, 0), archerholo);
    NPCEntity archernpc = new NPCEntity("archer");
    archernpc.setLocation(archerloc);
    archernpc.setSkinowner(UUID.fromString("5dd7513c-f66e-483f-9735-5a1c22e61ce2"));
    archernpc.setCmdcooldown(false);
    archernpc.setCommand("_managekit applynpc archer");
    archernpc.setHand(ItemBuilder.of(Material.BOW).enchant(Enchantment.DURABILITY, 1).build());
    archernpc.setHelmet(
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build());
    archernpc.setChest(
        ItemBuilder.of(Material.LEATHER_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build());
    archernpc.setLegs(
        ItemBuilder.of(Material.LEATHER_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build());
    archernpc.setBoots(
        ItemBuilder.of(Material.LEATHER_BOOTS).enchant(Enchantment.DURABILITY, 1).build());

    // rogue
    Hologram rogueholo = Holograms.newHologram().at(rogueloc.clone().add(0, 1.7, 0))
        .addLines("&6&lRogue Kit", "&e&lCLICK ME").build();
    rogueholo.send();
    HologramManager.holograms.put(rogueloc.clone().add(0, 1.7, 0), rogueholo);
    NPCEntity roguenpc = new NPCEntity("rogue");
    roguenpc.setLocation(rogueloc);
    roguenpc.setSkinowner(UUID.fromString("e1198a82-0056-453f-a803-1c43fc7734e7"));
    roguenpc.setCmdcooldown(false);
    roguenpc.setCommand("_managekit applynpc rogue");
    roguenpc.setHand(
        ItemBuilder.of(Material.GOLD_SWORD).enchant(Enchantment.DURABILITY, 1).build());
    roguenpc.setHelmet(
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build());
    roguenpc.setChest(
        ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build());
    roguenpc.setLegs(
        ItemBuilder.of(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build());
    roguenpc.setBoots(
        ItemBuilder.of(Material.CHAINMAIL_BOOTS).enchant(Enchantment.DURABILITY, 1).build());

    // builder
    Hologram builderholo = Holograms.newHologram().at(builderloc.clone().add(0, 1.7, 0))
        .addLines("&6&lBuilder Kit", "&e&lCLICK ME").build();
    builderholo.send();
    HologramManager.holograms.put(builderloc.clone().add(0, 1.7, 0), builderholo);
    NPCEntity buildernpc = new NPCEntity("builder");
    buildernpc.setLocation(builderloc);
    buildernpc.setSkinowner(UUID.fromString("0b4a36c3-bb8d-4153-b355-8843b99a5c95"));
    buildernpc.setCmdcooldown(false);
    buildernpc.setCommand("_managekit applynpc builder");
    buildernpc.setHand(
        ItemBuilder.of(Material.DIAMOND_PICKAXE).enchant(Enchantment.DURABILITY, 1).build());
    buildernpc.setHelmet(
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build());
    buildernpc.setChest(
        ItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build());
    buildernpc.setLegs(
        ItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build());
    buildernpc.setBoots(
        ItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.DURABILITY, 1).build());

    //duelist
    createNpcAndHologram(new String[]{
            "&6&lDuelist Kit",
            "&e&lCLICK ME"},
        "duelist",
        "afe8af6d-2db9-467a-bba0-d82b1c7fef47",
        "_managekit applynpc duelist",
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        duelistloc);

    //Donor
    createNpcAndHologram(new String[]{
            "&6&lDonor Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "donor",
        "d9ca9c25-a0d4-410e-ad09-fd8011c8d356",
        "_managekit applynpc donor",
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        donorloc);

    //Archer premium
    createNpcAndHologram(new String[]{
            "&6&lArcher Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "archerpremium",
        "6f5b6d19-5b3d-41a0-9f8e-333e18050ac4",
        "_managekit applynpc archerpremium",
        ItemBuilder.of(Material.BOW).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.LEATHER_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.LEATHER_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.LEATHER_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        archerPremiumloc);

    //Bard Premium
    createNpcAndHologram(new String[]{
            "&6&lBard Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "bardpremium",
        "6a0a664f-8895-474a-b2a0-9563bee0a7ba",
        "_managekit applynpc bardpremium",
        ItemBuilder.of(Material.BLAZE_POWDER).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        bardPremiumloc);

    //Rogue Premium
    createNpcAndHologram(new String[]{
            "&6&lRogue Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "rougepremium",
        "53bef45e-e52c-41df-9584-94ff31a30ce6",
        "_managekit applynpc roguepremium",
        ItemBuilder.of(Material.GOLD_SWORD).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.CHAINMAIL_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        rougePremiumloc);

    //Duelist Premium
    createNpcAndHologram(new String[]{
            "&6&lDuelist Premium Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "duelistpremium",
        "ddf5b743-7947-4bdb-97b0-b43d5745203d",
        "_managekit applynpc duelistpremium",
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        duelistPremiumloc);

    createNpcAndHologram(new String[]{
            "&6&lBattle Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "battle",
        "5dd7513c-f66e-483f-9735-5a1c22e61ce2",
        "_managekit applynpc battle",
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        battleloc);

    createNpcAndHologram(new String[]{
            "&6&lSeasonal Kit",
            "&e&lCLICK ME",
            "",
            "&ePurchaseable at",
            "&c♡ &7&ostore.battle.rip &c♡"},
        "seasonal",
        "65ec4657-2a60-4ad8-ab50-5c46306e5d39",
        "_managekit applynpc seasonal",
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.JACK_O_LANTERN).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build(),
        releaseloc);
  }

  private void createNpcAndHologram(String[] hologramLines, String npcName, String uuid,
      String command, ItemStack hand, ItemStack helmet, ItemStack chest, ItemStack legs,
      ItemStack boots, Location location) {

    Hologram hologram = Holograms.newHologram().at(location.clone().add(0, 1.7, 0))
        .addLines(hologramLines).build();

    hologram.send();
    HologramManager.holograms.put(location, hologram);

    NPCEntity donornpc = new NPCEntity(npcName);

    donornpc.setLocation(location);
    donornpc.setSkinowner(UUID.fromString(uuid));
    donornpc.setCmdcooldown(false);

    donornpc.setCommand(command);
    donornpc.setHand(hand);
    donornpc.setHelmet(helmet);
    donornpc.setChest(chest);
    donornpc.setLegs(legs);
    donornpc.setBoots(boots);
  }

  public static void savepvp() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("pvploc", CorePlugin.PLAIN_GSON.toJson(pvploc));
          return null;
        }));
  }

  public static void loadpvp() {
    if (pvploc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("pvploc")) {
        pvploc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("pvploc"),
            (Class) Location.class);
      } else {
        pvploc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void savebard() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("bardloc", CorePlugin.PLAIN_GSON.toJson(bardloc));
          return null;
        }));
  }

  public static void loadbard() {
    if (bardloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("bardloc")) {
        bardloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("bardloc"),
            (Class) Location.class);
      } else {
        bardloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveBattle() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("battleloc", CorePlugin.PLAIN_GSON.toJson(battleloc));
          return null;
        }));
  }

  public static void loadBattle() {
    if (battleloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("battleloc")) {
        battleloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("battleloc"),
            (Class) Location.class);
      } else {
        battleloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveRelease() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("releaseloc", CorePlugin.PLAIN_GSON.toJson(releaseloc));
          return null;
        }));
  }

  public static void loadRelease() {
    if (releaseloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("releaseloc")) {
        releaseloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("releaseloc"),
            (Class) Location.class);
      } else {
        releaseloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saverogue() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("rogueloc", CorePlugin.PLAIN_GSON.toJson(rogueloc));
          return null;
        }));
  }

  public static void loadrogue() {
    if (rogueloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("rogueloc")) {
        rogueloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("rogueloc"),
            (Class) Location.class);
      } else {
        rogueloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void savearcher() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("archerloc", CorePlugin.PLAIN_GSON.toJson(archerloc));
          return null;
        }));
  }

  public static void loadarcher() {
    if (archerloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("archerloc")) {
        archerloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("archerloc"),
            (Class) Location.class);
      } else {
        archerloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void savebuilder() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("builderloc", CorePlugin.PLAIN_GSON.toJson(builderloc));
          return null;
        }));
  }

  public static void loadbuilder() {
    if (builderloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("builderloc")) {
        builderloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("builderloc"),
            (Class) Location.class);
      } else {
        builderloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void loadDonor() {
    if (donorloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("donorloc")) {
        donorloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("donorloc"),
            (Class) Location.class);
      } else {
        donorloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveDonor() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("donorloc", CorePlugin.PLAIN_GSON.toJson(donorloc));
          return null;
        }));
  }

  public static void loadDuelist() {
    if (duelistloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("duelistloc")) {
        duelistloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("duelistloc"),
            (Class) Location.class);
      } else {
        duelistloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveDuelist() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("duelistloc", CorePlugin.PLAIN_GSON.toJson(duelistloc));
          return null;
        }));
  }

  public static void loadArcherPremium() {
    if (archerPremiumloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("archerpremiumloc")) {
        archerPremiumloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("archerpremiumloc"),
            (Class) Location.class);
      } else {
        archerPremiumloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveArcherPremium() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("archerpremiumloc", CorePlugin.PLAIN_GSON.toJson(archerPremiumloc));
          return null;
        }));
  }

  public static void loadBardPremium() {
    if (bardPremiumloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("bardpremiumloc")) {
        bardPremiumloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("bardpremiumloc"),
            (Class) Location.class);
      } else {
        bardPremiumloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveBardPremium() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("bardpremiumloc", CorePlugin.PLAIN_GSON.toJson(bardPremiumloc));
          return null;
        }));
  }

  public static void loadRougePremium() {
    if (rougePremiumloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("rougepremiumloc")) {
        rougePremiumloc = (Location) CorePlugin.PLAIN_GSON.fromJson(redis.get("rougepremiumloc"),
            (Class) Location.class);
      } else {
        rougePremiumloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveRougePremium() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("rougepremiumloc", CorePlugin.PLAIN_GSON.toJson(rougePremiumloc));
          return null;
        }));
  }

  public static void loadDuelistPremium() {
    if (duelistPremiumloc != null) {
      return;
    }
    CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists("duelistpremiumloc")) {
        duelistPremiumloc = (Location) CorePlugin.PLAIN_GSON.fromJson(
            redis.get("duelistpremiumloc"),
            (Class) Location.class);
      } else {
        duelistPremiumloc = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
      return null;
    });
  }

  public static void saveDuelistPremium() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
        () -> CorePlugin.getInstance().runRedisCommand(redis -> {
          redis.set("duelistpremiumloc", CorePlugin.PLAIN_GSON.toJson(duelistPremiumloc));
          return null;
        }));
  }
}