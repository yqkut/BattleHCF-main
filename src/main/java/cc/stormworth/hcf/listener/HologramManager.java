package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class HologramManager {

  public static Map<Location, Hologram> holograms = new HashMap();
  public static List<String> infolines = CC.translate(Arrays.asList("&fWelcome to &6&lBattle &7(" + CorePlugin.getInstance().getServerId() + ")", "", "&7Started on the 21nd of March", "", "&6Map Kit&f: S1, P1", "&6Team Size&f: &e6 &fMan, &c0 &fAllies", "&6Map Border&f: 1500 &ex &f1500", "", CC.RED + CC.UNICODE_HEART + " &7store.battle.rip " + CC.RED + CC.UNICODE_HEART));
  public static List<String> shoplines = CC.translate(Arrays.asList("&6&lShop", "", "&7This &eNPC &7can be clicked", "&7to &6navigate &7through the store", "", "&6&lCategories:", "", "&aBlocks &6Brewer &cItems", "&5Specials &8Spawner &4Sell", "", "&e&lCLICK ME"));
  public static List<String> dailylines = CC.translate(Arrays.asList("&6&lDaily", "", "&7Join the server every day", "&7to keep claiming your daily &ereward", "", "&c&lWarning! &cYour streak will be reset", "", "&e&lCLICK ME"));
  public static List<String> storelines = CC.translate(Arrays.asList("&a&lStore", "", "&7Explore all perks you can purchase!", "", "&7Purchase &6&lGold &7at our &eStore", "", "&c&lComing Soon..."));

  public HologramManager() {

    final Location infoholo = SetListener.getInfoholo();
    Hologram infoholohologram = Holograms.newHologram().at(infoholo.clone().add(0, 1.5, 0))
        .addLines(infolines).build();
    infoholohologram.send();
    holograms.put(infoholo.clone().add(0, 1.5, 0), infoholohologram);

    SetListener.loadBlockShop();
    final Location shop = SetListener.getShop();
    Hologram shophologram = Holograms.newHologram().at(shop.clone().add(0, 1.7, 0))
        .addLines(shoplines).build();
    shophologram.send();
    holograms.put(shop.clone().add(0, 1.7, 0), shophologram);

    SetListener.loadDaily();
    final Location daily = SetListener.getDaily();
    Hologram dailyHologram = Holograms.newHologram().at(daily.clone().add(0, 1.7, 0))
            .addLines(dailylines).build();
    dailyHologram.send();
    holograms.put(daily.clone().add(0, 1.7, 0), dailyHologram);

    SetListener.loadStore();
    final Location store = SetListener.getStore();
    Hologram storeHologram = Holograms.newHologram().at(store.clone().add(0, 1.7, 0))
            .addLines(storelines).build();
    dailyHologram.send();
    holograms.put(store.clone().add(0, 1.7, 0), storeHologram);

    // npc shop
    NPCEntity bsnpc = new NPCEntity("shop");
    bsnpc.setLocation(shop);
    bsnpc.setSkinowner(UUID.fromString("d664f4b4-d688-4d28-9fbd-3d58ef2844b4"));
    bsnpc.setCmdcooldown(false);
    bsnpc.setCommand("shop");
    bsnpc.setHand(ItemBuilder.of(Material.QUARTZ_BLOCK).enchant(Enchantment.DURABILITY, 1).build());

    //npc daily
    NPCEntity dlnpc = new NPCEntity("daily");
    dlnpc.setLocation(daily);
    dlnpc.setSkinowner(UUID.fromString("cbc6e6a4-eb17-4afe-a84d-a2e9634b4165"));
    dlnpc.setCmdcooldown(false);
    dlnpc.setCommand("daily");
    dlnpc.setHand(ItemBuilder.of(Material.BOOK).enchant(Enchantment.DURABILITY, 1).build());

    //npc store
    NPCEntity storenpc = new NPCEntity("store");
    storenpc.setLocation(store);
    storenpc.setSkinowner(UUID.fromString("cbc6e6a4-eb17-4afe-a84d-a2e9634b4165"));
    storenpc.setCmdcooldown(false);
    storenpc.setHand(ItemBuilder.of(Material.BOOK).enchant(Enchantment.DURABILITY, 1).build());
  }
}