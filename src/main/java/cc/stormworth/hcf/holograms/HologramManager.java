package cc.stormworth.hcf.holograms;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.npc.NPCEntity;
import cc.stormworth.hcf.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class HologramManager {

  private final HashMap<String, HoloNPC> hologramLocations = new HashMap<>();

  public HologramManager() {
    if(Main.getInstance().getMapHandler().isKitMap()){
        createNpcAndHologram("welcome",
                new String[]{
                        "&7Welcome to &6&lBattle &7┃ &fKits",
                        "&7&oWe are currently on Map 6",
                        "",
                        "&e&l✦ &6&lHalloween Sale &7has started on &fstore.battle.rip &e&l✦",
                        "",
                        "&7➛ &eMap Kit &7- &fProtection 1, Sharpness 1",
                        "&7➛ &eFaction Size &7- &f12 man, 0 Allies",
                        "&7➛ &eBorder &7- &f2000 x 2000",
                        "",
                        "&7store.battle.rip &a&l(40% SALE)",
                        "&7battle.rip/discord",
                },
                "cc15af68-0cd7-4050-9756-d7df87e64ec3", //Technoblade never dies
                "f help");
    }else{
        createNpcAndHologram("welcome",
                new String[]{
                        "&7Welcome to &6&lBattle &7┃ &fHCF",
                        "&7&oWe are currently on Map 28",
                        "",
                        "&e&l✦ &6&lHalloween Sale &7has started on &fstore.battle.rip &e&l✦",
                        "",
                        "&7➛ &eMap Kit &7- &fProtection 1, Sharpness 1",
                        "&7➛ &eFaction Size &7- &f5 man, 0 Allies",
                        "&7➛ &eBorder &7- &f2000 x 2000",
                        "",
                        "&e♚ &e#1 F-TOP &7● &f$50 &9Pay&bPal",
                        "&e♚ &f#2 F-TOP &7● &f$30 &9Pay&bPal",
                        "&e♚ &6#3 F-TOP &7● &f$10 &9Pay&bPal",
                        "",
                        "&7store.battle.rip &a&l(40% SALE)",
                        "&7battle.rip/discord",
                },
                "cc15af68-0cd7-4050-9756-d7df87e64ec3", //Technoblade never dies
                "f help");
    }

    createNpcAndHologram("ability",
        new String[]{
            "&a&l* NEW *",
            "&eAbilities:",
            "&7Last updated: 28/10/2022",
            "",
            "&e&lCLICK",
        },
        "89416d3c-1970-4d72-accb-134fa79bd904",
        "abilities");

    createNpcAndHologram("shop",
        new String[]{
            "&6&lShop",
            "",
            "&7Compra & vende items a para construir, decorar",
            "&7o renovar tu base a travez de este NPC.",
            "",
            "&e&lCLICK",
        },
        "c52d2ccf-94e4-440f-848b-14940fd9bf90",
        "shop");

    createNpcAndHologram("vote",
        new String[]{
            "&9Like us on &6&l&nNameMC",
            "",
            "&7Help the server grow up by liking us",
            "&7and obtain in-game rewards!",
            "",
            "&7Rewards",
            "&2•&a•&f• &aVerified Rank &7(&a✓&7)",
            "&6•&e•&f• &6Coins &7(&e500&7)",
            "&1•&9•&f• &bVerified Discord Role &7(&bDiscord&7)",
            "&6•&e•&f• &7[&f#&6Battle&7] &7(&6Tag&7)",
            "",
            "&e&lClick to like the server!",
        },
        "2577334a-c30e-4c2a-85f6-83b7ea84c86f",
        "vote");

    createNpcAndHologram("leaderboard",
        new String[]{
            "&6&lTop Players",
            "",
            "&a&lHmm... &7parece que hay gente mejor que tu!",
            "&7Revisalo usando &f/leaderboards",
            "",
            "&7Por cierto... recuerda utilizar &e/rewards &7para ver los premios de este mapa.",
            "",
            "&e&lCLICK"
        },
        "0175212e-c38a-42c6-8e49-ef69592f54a1",
        "leaderboard");

    if(Main.getInstance().getMapHandler().isKitMap()){
      createNpcAndHologram("trading",
              new String[]{
                      "&6&lItem Trading",
                      "&7Trade your items safely with the &6trade system",
                      "&7within &f10 blocks &7of the &fplayer&7.",
                      "",
                      "&7To use this system, use &f/trade <player>",
                      "&7and wait for the recipient to accept",
              },
              "ddf5b743-7947-4bdb-97b0-b43d5745203d",
              "trade");
    }

    /*createNpcAndHologram("battlepass",
        new String[]{
            "&6&lBattle Pass",
            "&7You will find a totally innovative pass",
            "&7You won''t have to suffer anymore for doing boring challenges",
            "&7We challenge you to do every &6PvP &7mission",
            "",
            "&7You will find &f&n2 &7categories: \"&a&lFree&7\" & \"&5&lPremium&7",
            "&7We recommend you to take advantage of each &c&lOP &7rewards",
            "",
            "&7Buy the &a&lBattle Pass &7at &fstore.battle.rip",
        },
        "ddf5b743-7947-4bdb-97b0-b43d5745203d",
        "");*/

/*    createNpcAndHologram("crypto",
        new String[]{
            "&6&lCrypto",
            "&7Invest your &eCoins &7via &6&lCrypto &7to be able of",
            "&7purchasing &c&lOP &7stuff on all our custom services",
            "",
            "&7Exchange your &eCoins &7and make investments &eright now&7!",
            "&7Learn more about &6&lCrypto &7by typing &e/tips",
        },
        "CryptoNPC",
        "cbc6e6a4-eb17-4afe-a84d-a2e9634b4165",
        "",
        new Location(Bukkit.getWorld("world"), 6.877, 95.260, 10.999)
    );*/
    createNpcAndHologram("deepweb",
        new String[]{
            "&d&lMerchant &7| &a◊ Gems Shop ◊",
            "&7Tradea tus gemas por diferentes items,",
            "&7kits o abilitys por un precio bajo.",
            "",
            "&7Te ofrecemos: ",
            "&fOne-Time Kits",
            "&fAbilitys",
            "&fPrivates Chests",
            "",
            "&e&lClick to view the market!",
            "",
        },
        "3c904d71-45b3-40c8-9b79-7be69c1144a3",
        "merchant");

    createNpcAndHologram("partners",
        new String[]{
            "&d&lPartner &8| &7Support",
                "",
            "&7Apoya a tu creador de contenido favorito",
            "&7para obtener recompensas.",
            "",
            "&7Utiliza &6/redeem &7para apoyar",
            "",
            "&e&lCLICK"
        },
        "d9ca9c25-a0d4-410e-ad09-fd8011c8d356",
        "redeem");

    if (Main.getInstance().getMapHandler().isKitMap()) {
      createNpcAndHologram("archerupgrades",
          new String[]{
              "&5&lArcher Upgrades",
              "&7Improvements for your &fArmor",
              "&7Purchase these effects with &a&lGems",
              "&d&lEffects &7- &cWither&7, &fSlowness&7, &2Poison&7, &9Weakness&7.",
              "",
              "&e&lCLICK",
          },
          "0175212e-c38a-42c6-8e49-ef69592f54a1",
          "archerupgrades");

      createNpcAndHologram("dimentional",
          new String[]{
              "&6&lDimentional Teleport &8| &7TP",
              "&7Explora de manera unica nuestro Teletransportes.",
              "&7Encontraras lugares como &4&lKoTHs&7, &4&lFarms&7.",
              "",
              "&eViaja alrededor &5&lEnd &eor &4&lNether&e.",
              "",
              "&e&lCLICK",
          },
          "afe8af6d-2db9-467a-bba0-d82b1c7fef47",
          "dimentionalteleport");
    }

    createNpcAndHologram("factionduel",
        new String[]{
            "&c&l17.06.2022",
            "",
            "&e&lFaction Duel",
            "&7Using &e/f duel &7command, you will be able to",
            "&efight &7against an enemy faction in a custom terrain",
        },
        "148f1abc-6352-41fa-9c91-f666c3b04082",
        "factionduel"
    );

    createNpcAndHologram("factionupgrades",
        new String[]{
            "&6&lFaction Upgrades",
            "&7Mejora las propiedades de tu claim",
            "&7añadiendo efectos o reduciendo el DTR freeze.",
            "",
            "&7Utiliza &f/f upgrades &7para mejorar",
            "",
            "&e&lCLICK"
        },
        "127db98f-ffd9-4543-80fd-3fd9a0d3df2d",
        "f upgrades"
    );

    createNpcAndHologram("polls",
        new String[]{
            "&6&lPolls",
            "&7Déjanos un feedback del servidor",
            "&7así podremos cambiar las  cosas",
            "&7que no le gusta a la comunidad.",
            "",
            "&e&lCLICK"
        },
        "5ff85b64-5a12-49ef-b8ee-7b450c4e5165",
        "poll"
    );

    createNpcAndHologram("daily",
        new String[]{
            "&6&lDailystreak &8| &aRewards",
            "",
            "&7Mientras mas juegues mejores recompensas",
            "&7podras ganar, !Mantente activo!",
            "",
            "&7Utiliza &f/daily &7para reclamar",
            "",
            "&e&lCLICK"
        },
        "5ff85b64-5a12-49ef-b8ee-7b450c4e5165",
        "daily"
    );

    createNpcAndHologram("gkitsotw",
        new String[]{
                "&a&l* NEW *",
                "&eSOTW Kit",
                "&7Use &e/gkit sotw &7to ease your map&e! &c&l❤",
                "",
                "&e&lCLICK",
        },
        "bff40781-2302-400f-b2b9-2f66e1b15ec5",
        "gkit sotw",
            new ItemStack[]{
                    ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 1).build(),
                    ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).build(),
                    ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 1).build(),
                    ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 1).build()
            }
    );

    createNpcAndHologram(
            "buildert",
            new String[]{
                    "&a&l* NEW *",
                    "&6&lBuilding Tournament",
                    "",
                    "&eTheme&f: &nHalloween",
                    "",
                    "&7[&e♚&7] &e#1&7: &6&l$20 Giftcard + PREFIX",
                    "",
                    "&7&oEnds on EOTW Timer.",
                    "",
                    "&e&lCLICK"
            },
            "d07fae5c-0a06-4116-8e9d-f8d0f835c67c",
            "buildapply"
            );

  }

  public Location loadLocation(String name) {
    return CorePlugin.getInstance().runRedisCommand(redis -> {
      if (redis.exists(CorePlugin.getInstance().getServerId() + "_" + name)) {
        return CorePlugin.PLAIN_GSON.fromJson(
            redis.get(CorePlugin.getInstance().getServerId() + "_" + name), Location.class);
      } else if (redis.exists(name)) {
        return CorePlugin.PLAIN_GSON.fromJson(redis.get(name), Location.class);
      } else {
        return new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
      }
    });
  }

  public void saveLocation(String name) {
    CorePlugin.getInstance().runRedisCommand(redis -> {
      redis.set(CorePlugin.getInstance().getServerId() + "_" + name,
          CorePlugin.PLAIN_GSON.toJson(hologramLocations.get(name).getLocation()));
      return null;
    });
  }

  private void createNpcAndHologram(String name,
      String[] hologramLines,
      String uuid,
      String command) {
    createNpcAndHologram(name, hologramLines, uuid, command, null);
  }

  private void createNpcAndHologram(String name,
                                    String[] hologramLines,
                                    String uuid,
                                    String command, ItemStack[] armor) {

    Location location = loadLocation(name);

    if (location == null) {
      location = new Location(Bukkit.getWorlds().get(0), 0.6, 80.0, 0.5);
    }

    Hologram hologram = Holograms.newHologram()
        .at(location.clone().add(0, 1.7, 0))
        .addLines(hologramLines)
        .build();

    hologram.send();

    hologramLocations.put(name, new HoloNPC(location, hologram));

    NPCEntity npc = new NPCEntity(name);

    npc.setLocation(location);
    npc.setSkinowner(UUID.fromString(uuid));
    npc.setCmdcooldown(false);

    if (armor != null) {
        npc.setHelmet(armor[0]);
        npc.setChest(armor[1]);
        npc.setLegs(armor[2]);
        npc.setBoots(armor[3]);
    }

    npc.setCommand(command);
  }

}