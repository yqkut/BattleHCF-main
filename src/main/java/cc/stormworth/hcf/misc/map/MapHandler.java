package cc.stormworth.hcf.misc.map;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.listener.BorderListener;
import cc.stormworth.hcf.listener.HologramManager;
import cc.stormworth.hcf.listener.SetListener;
import cc.stormworth.hcf.misc.kits.KitManager;
import cc.stormworth.hcf.misc.map.killstreaks.KillstreakHandler;
import cc.stormworth.hcf.misc.map.stats.StatsHandler;
import cc.stormworth.hcf.providers.nametag.HCFNametagProvider;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class MapHandler {

  public KitManager kitManager;
  private transient File mapInfo;
  private boolean kitMap;
  private int allyLimit;
  private int teamSize;
  private long regenTimeDeath;
  private long regenTimeRaidable;
  private String mapStartedString;
  private String mapEndsString;
  private int warzone;
  private double baseLootingMultiplier;
  private double level1LootingMultiplier;
  private double level2LootingMultiplier;
  private double level3LootingMultiplier;
  private boolean craftingGopple;
  private boolean craftingReducedMelon;
  private int goppleCooldown;
  private String endPortalLocation;
  @Setter
  private int netherBuffer;
  @Setter
  private int worldBuffer;
  private float dtrIncrementMultiplier;
  private int conquestWinPoints;
  private final String archerTagColor = CC.DARK_RED;
  private final String stunTagColor = CC.BLUE;
  private final String defaultRelationColor = CC.YELLOW;
  private final String teamRelationColor = CC.DARK_GREEN;
  private final String allyRelationColor = CC.LIGHT_PURPLE;
  // Kit-Map only stuff:
  private StatsHandler statsHandler;
  private KillstreakHandler killstreakHandler;
  private double launchX;
  private double launchY;
  private double launchZ;
  @Getter
  public static Map<Rank, List<String>> reclaims = new HashMap<>();

  public void load() {
    reloadConfig();

    if (Bukkit.getWorld("void") == null) {
      WorldCreator wc = new WorldCreator("void");
      wc.environment(World.Environment.NORMAL);
      wc.type(WorldType.FLAT);
      wc.generateStructures(false);
      wc.createWorld();
      BorderListener.setBorder("void", 1500);
    }

    CorePlugin.getInstance().getNametagEngine().registerProvider(new HCFNametagProvider());
    //CorePlugin.getInstance().getTabEngine().setLayoutProvider(new TabProvider());

    Iterator<Recipe> recipeIterator = Main.getInstance().getServer().recipeIterator();

    while (recipeIterator.hasNext()) {
      Recipe recipe = recipeIterator.next();

      // Disallow the crafting of gopples.
      if (!craftingGopple && recipe.getResult().getDurability() == (short) 1
          && recipe.getResult().getType() == org.bukkit.Material.GOLDEN_APPLE) {
        recipeIterator.remove();
      }

      if (recipe.getResult().getType() == Material.EXPLOSIVE_MINECART) {
        recipeIterator.remove();
      }
    }

    // add our glistering melon recipe
    if (craftingReducedMelon) {
      Main.getInstance().getServer().addRecipe(
          new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON)).addIngredient(Material.MELON)
              .addIngredient(Material.GOLD_NUGGET));
    }

    statsHandler = new StatsHandler();
    if (isKitMap()) {
      killstreakHandler = new KillstreakHandler();
      kitManager = new KitManager();
    }
    /*
      // start a KOTH after 5 minutes of uptime
      Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
        EventHandler kothHandler = Main.getInstance().getEventHandler();
        List<KOTH> koths = kothHandler.getEvents().stream()
            .filter(e -> e instanceof KOTH)
            .filter(e -> !e.isHidden())
            .filter(e -> !e.getName().equalsIgnoreCase("citadel"))
            .map(e -> (KOTH) e).collect(Collectors.toList());

        if (koths.isEmpty()) {
          return;
        }

        KOTH selected = koths.get(CorePlugin.RANDOM.nextInt(koths.size()));
        selected.activate();
      }, 5 * 60 * 20);
    }*/
  }

  public void reloadConfig() {

    for (Rank rank : Rank.values()) {
      if (Main.getInstance().getConfig().getStringList("reclaim." + rank.getName()) == null || !Main.getInstance().getConfig().contains("reclaim." + rank.getName())) {
        continue;
      }

      reclaims.put(rank, Main.getInstance().getConfig().getStringList("reclaim." + rank.getName()));
    }

    try {
      mapInfo = new File(Main.getInstance().getDataFolder(), "mapInfo.json");

      if (!mapInfo.exists()) {
        mapInfo.createNewFile();

        BasicDBObject dbObject = getDefaults();

        FileUtils.write(mapInfo, CorePlugin.GSON.toJson(new JsonParser().parse(dbObject.toString())));
      } else {
        // basically check for any new keys in the defaults which aren't contained in the actual file
        // if there are any, add them to the file.
        BasicDBObject file = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

        BasicDBObject defaults = getDefaults();

        defaults.keySet().stream().filter(key -> !file.containsKey(key))
            .forEach(key -> file.put(key, defaults.get(key)));

        FileUtils.write(mapInfo, CorePlugin.GSON.toJson(new JsonParser().parse(file.toString())));
      }

      BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

      if (dbObject != null) {
        this.kitMap = dbObject.getBoolean("kitMap", false);
        this.allyLimit = dbObject.getInt("allyLimit", 0);
        this.teamSize = dbObject.getInt("teamSize", 30);
        this.regenTimeDeath = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeDeath", 60));
        this.regenTimeRaidable = TimeUnit.MINUTES.toMillis(
            dbObject.getInt("regenTimeRaidable", 60));
        this.mapStartedString = dbObject.getString("mapStartedString");
        this.mapEndsString = dbObject.getString("mapEndsString");
        this.warzone = dbObject.getInt("warzone", 1000);
        this.goppleCooldown = dbObject.getInt("goppleCooldown");
        this.netherBuffer = dbObject.getInt("netherBuffer");
        this.worldBuffer = dbObject.getInt("worldBuffer");
        this.endPortalLocation = dbObject.getString("endPortalLocation");

        BasicDBObject looting = (BasicDBObject) dbObject.get("looting");

        this.baseLootingMultiplier = looting.getDouble("base");
        this.level1LootingMultiplier = looting.getDouble("level1");
        this.level2LootingMultiplier = looting.getDouble("level2");
        this.level3LootingMultiplier = looting.getDouble("level3");

        BasicDBObject crafting = (BasicDBObject) dbObject.get("crafting");

        this.craftingGopple = crafting.getBoolean("gopple");
        this.craftingReducedMelon = crafting.getBoolean("reducedMelon");

        this.conquestWinPoints = dbObject.getInt("conquestWinPoints", 250);

        this.dtrIncrementMultiplier = (float) dbObject.getDouble("dtrIncrementMultiplier", 4.5F);
        this.launchX = dbObject.getDouble("launchX", 0.0F);
        this.launchY = dbObject.getDouble("launchY", 1.1F);
        this.launchZ = dbObject.getDouble("launchZ", 0.0F);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    SetListener.loadInfoHolo();
    if (HologramManager.holograms.containsKey(SetListener.getInfoholo())) {
      HologramManager.holograms.remove(SetListener.getInfoholo()).destroy();
      final Location infoholo = SetListener.getInfoholo();
      Hologram infoholohologram = Holograms.newHologram().at(infoholo.clone().add(0, 1.5, 0))
          .addLines(
              HologramManager.infolines).build();
      infoholohologram.send();
      HologramManager.holograms.put(infoholo, infoholohologram);
    }
  }

  private BasicDBObject getDefaults() {
    BasicDBObject dbObject = new BasicDBObject();

    BasicDBObject looting = new BasicDBObject();
    BasicDBObject crafting = new BasicDBObject();
    //BasicDBObject deathban = new BasicDBObject();

    dbObject.put("kitMap", false);
    dbObject.put("allyLimit", 0);
    dbObject.put("warzone", 1000);
    dbObject.put("teamSize", 30);
    dbObject.put("regenTimeDeath", 60);
    dbObject.put("regenTimeRaidable", 60);
    dbObject.put("scoreboardTitle", "&6&lbattle &c[Map 1]");
    dbObject.put("mapStartedString", "Map 150 - Started January 31, 1475");
    dbObject.put("mapEndsString", "Map 150 - Ends January 31, 1970");
    dbObject.put("netherBuffer", 150);
    dbObject.put("worldBuffer", 300);
    dbObject.put("endPortalLocation", "2500, 2500");
    dbObject.put("goppleCooldown", TimeUnit.HOURS.toMinutes(4));

    looting.put("base", 1D);
    looting.put("level1", 1.2D);
    looting.put("level2", 1.4D);
    looting.put("level3", 2D);
    dbObject.put("looting", looting);

    crafting.put("gopple", true);
    crafting.put("reducedMelon", true);
    dbObject.put("crafting", crafting);
    dbObject.put("conquestWinPoints", 250);

    dbObject.put("dtrIncrementMultiplier", 4.5F);
    dbObject.put("launchX", 0.0F);
    dbObject.put("launchY", 1.1F);
    dbObject.put("launchZ", 0.0F);
    return dbObject;
  }

  public void saveNetherBuffer() {
    try {
      BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

      if (dbObject != null) {
        dbObject.put("netherBuffer",
            Main.getInstance().getMapHandler().getNetherBuffer()); // update the nether buffer

        FileUtils.write(mapInfo, CorePlugin.GSON.toJson(new JsonParser().parse(
            dbObject.toString()))); // save it exactly like it was except for the nether that was changed.
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void saveWorldBuffer() {
    try {
      BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

      if (dbObject != null) {
        dbObject.put("worldBuffer",
            Main.getInstance().getMapHandler().getWorldBuffer()); // update the world buffer

        FileUtils.write(mapInfo, CorePlugin.GSON.toJson(new JsonParser().parse(
            dbObject.toString()))); // save it exactly like it was except for the nether that was changed.
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}