package cc.stormworth.hcf.profile;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.brewingstand.BrewingStand;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.profile.deathban.DeathBan;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.profile.enderchest.EnderchestUpgrades;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.utils.FilterType;
import cc.stormworth.hcf.util.chat.ChatMode;
import cc.stormworth.hcf.util.countdown.Countdown;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.FastMath;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
public class HCFProfile {

  @Getter
  private static final Map<UUID, HCFProfile> profiles = new HashMap<>();

  public static final MongoCollection<Document> collection = Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("profiles");

  private UUID uuid;
  private String name;

  private int lives = 0;

  // ores
  private int emerald, diamond, gold, iron, lapis, redstone, coal = 0;
  private int currentOreMines = 0;

  // stats
  private int kills, deaths = 0;

  // settings
  private FilterType filterType = FilterType.HIGHEST_ONLINE;
  private boolean paymentsToggled = true;
  private boolean globalChat = true;
  private boolean deathMessages = true;
  private boolean teamNameTags = true;
  private boolean reclaimed = false;
  private boolean enderpearlCooldown = true;
  private boolean particlesBattle = true;
  private boolean onlyShowCaper = false;

  private boolean randomEffects = false;

  private Lang lang = Lang.UNDEFINED;

  @Setter
  private boolean particles = false;

  @Getter
  private Map<String, Long> kitCooldowns = new HashMap<>();
  @Getter
  private Map<String, Integer> kitUses = new HashMap<>();
  private Map<CEnchantments, List<EnchantmentEffect>> enchantments = Maps.newHashMap();

  private final List<KillBoosting> boostings = Lists.newArrayList();

  private Player lastDamager;
  private Player lastDamaged;

  private long lastDamagerTime;
  private long lastDamagedTime;

  private ItemStack helmet;
  private Hit hit;
  private PearlLocation lastPearlLocation;
  @Setter
  private ItemStack[] hotbar;
  private final List<String> selectedLffClasses = Lists.newArrayList();
  private List<ItemStack> noReclaimedItems = Lists.newArrayList();

  private boolean isTeleporting = false;
  private Countdown countdown;
  private Teleport teleport;

  private LinkedList<PrivateChest> privateChests = Lists.newLinkedList();

  private EnderchestUpgrades enderchestUpgrades;

  @Setter
  private int chestLevel;

  //eco
  @Setter
  private int gems;

  private long dailyCooldown;

  private boolean useMemeCommand = false;

  private boolean loaded = false;

  private Kit selectedKit;
  private boolean editing;
  private PlayerKit editingKit;
  private RestoreInv restoreInv;
  private ChatMode chatMode = ChatMode.PUBLIC;
  private List<ObjectId> spyTeam = Lists.newArrayList();

  private final Map<String, PlayerKit> kits = Maps.newHashMap();

  private long teamDelay = 0;

  private PvPTimer pvpTimer;

  private PlaySession playSession;
  @Setter private long playTime;
  @Setter private Team team;

  private EconomyData economyData = new EconomyData(0);;

  @Setter
  private DeathBan deathban;

  @Setter
  private BrewingStand openBrewingStand;

  public HCFProfile(String name, UUID uuid) {
    this.uuid = uuid;
    this.name = name;

    enderchestUpgrades = new EnderchestUpgrades();
    load();
    profiles.put(uuid, this);

    playSession = new PlaySession();
  }

  public HCFProfile(UUID uuid) {
    this.uuid = uuid;
  }

  public void setPvpTimer(PvPTimer pvpTimer) {
    this.pvpTimer = pvpTimer;

    CorePlugin.getInstance().getNametagEngine().reloadPlayer(getPlayer());
    CorePlugin.getInstance().getNametagEngine().reloadOthersFor(getPlayer());

    if (pvpTimer == null) {
      getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your protection has expired!");
    }
  }

  public boolean hasPvPTimer() {
    return pvpTimer != null && pvpTimer.isActive();
  }

  public boolean hasSotwTimer() {
    return pvpTimer != null && pvpTimer.isActive() && pvpTimer.isFirstTime();
  }


  public boolean isDeathBanned() {
    return deathban != null && !deathban.isExpired();
  }

  public PlayerKit getKit(Kit kit) {

    if(!this.kits.containsKey(kit.getName())){
      this.kits.put(kit.getName(), null);
    }

    return this.kits.get(kit.getName());
  }

  public void replaceKit(Kit ladder, PlayerKit kit) {
    this.kits.put(ladder.getName(), kit);
  }

  public void deleteKit(final Kit kit, PlayerKit playerKit) {
    if (playerKit == null) {
      return;
    }

    this.kits.put(kit.getName(), null);
  }

  public boolean hasTeamDelay() {
    return teamDelay > 0;
  }

  public String getSelectedClasses() {
    StringBuilder builder = new StringBuilder();
    for (String selectedClass : selectedLffClasses) {
      builder.append(selectedClass);
      if (selectedLffClasses.indexOf(selectedClass) != selectedLffClasses.size() - 1) {
        builder.append(", ");
      } else {
        builder.append(".");
      }
    }
    return builder.toString();
  }

  public KillBoosting getByTarget(UUID target) {
    return boostings.stream().filter(boosting -> boosting.getTarget().equals(target)).findFirst()
        .orElse(null);
  }

  public void claimItems(Player player) {
    int inventorySize = (int) (FastMath.ceil((noReclaimedItems.size() + 1) / 9.0) * 9.0);

    Inventory inventory = Bukkit.createInventory(null, inventorySize, CC.translate("&a&lClaimed Items"));

    for (ItemStack itemStack : noReclaimedItems) {
      inventory.addItem(itemStack);
    }

    player.openInventory(inventory);
  }

  public boolean isExpiredDamage() {
    return lastDamagerTime + TimeUnit.SECONDS.toMillis(10) < System.currentTimeMillis();
  }

  public boolean isExpiredDamager() {
    return lastDamagerTime + TimeUnit.SECONDS.toMillis(10) < System.currentTimeMillis();
  }

  public static void resetAllGKits() {
    profiles.forEach((uuid1, profile) -> {
      profile.kitCooldowns.clear();
    });
    profiles.forEach((uuid1, profile) -> {
      profile.kitUses.clear();
    });
  }

  public long getTotalPlayTime() {
    return (playTime + playSession.getCurrentSession());
  }

  public static HCFProfile getByUUIDIfAvailable(UUID uuid) {
    return profiles.get(uuid);
  }

  public static HCFProfile getByUUID(UUID uuid) {
    return profiles.get(uuid);
  }

  public static CompletableFuture<HCFProfile> load(UUID uuid) {
    return CompletableFuture.supplyAsync(() -> {

      if(collection.find(Filters.eq("uuid", uuid.toString())).first() == null){
        return null;
      }

      HCFProfile profile = new HCFProfile(uuid);
      profile.load();
      return profile;
    });
  }

  public static HCFProfile get(Player player) {

    if(!profiles.containsKey(player.getUniqueId())){
        TaskUtil.run(Main.getInstance(), () -> {
          player.kickPlayer("Profile Not found, please relog.");
        });
        return null;
    }

    return profiles.get(player.getUniqueId());
  }

  public void upgradeChest(int level) {
    chestLevel = level;
    privateChests.add(new PrivateChest(level));
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public void load() {
    Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

    if (document != null) {
      if (name == null) {
        name = document.getString("name");
      }

      if (document.containsKey("lives")) {
        this.lives = document.getInteger("lives");
      }
      if (document.containsKey("emerald")) {
        this.emerald = document.getInteger("emerald");
      }
      if (document.containsKey("diamond")) {
        this.diamond = document.getInteger("diamond");
      }
      if (document.containsKey("gold")) {
        this.gold = document.getInteger("gold");
      }
      if (document.containsKey("iron")) {
        this.iron = document.getInteger("iron");
      }
      if (document.containsKey("lapis")) {
        this.lapis = document.getInteger("lapis");
      }
      if (document.containsKey("redstone")) {
        this.redstone = document.getInteger("redstone");
      }
      if (document.containsKey("coal")) {
        this.coal = document.getInteger("coal");
      }
      if (document.containsKey("kills")) {
        this.kills = document.getInteger("kills");
      }
      if (document.containsKey("deaths")) {
        this.deaths = document.getInteger("deaths");
      }
      if (document.containsKey("dailycooldown")) {
        this.dailyCooldown = document.getLong("dailycooldown");
      }
      if (document.containsKey("enderpearlcooldown")) {
        this.enderpearlCooldown = document.getBoolean("enderpearlcooldown");
      }
      if (document.containsKey("particlesbattle")) {
        this.particlesBattle = document.getBoolean("particlesbattle");
      }
      if (document.containsKey("particles")) {
        this.particles = document.getBoolean("particles");
      }
      if (document.containsKey("gems")) {
        this.gems = document.getInteger("gems");
      }
      if (document.containsKey("filterType")) {
        this.filterType = FilterType.valueOf(document.getString("filterType"));
      }
      if (document.containsKey("lang")) {
        this.lang = Lang.valueOf(document.getString("lang"));
      }
      if (document.containsKey("paymentsToggled")) {
        this.paymentsToggled = document.getBoolean("paymentsToggled");
      }
      if (document.containsKey("useMemeCommand")) {
        this.useMemeCommand = document.getBoolean("useMemeCommand");
      }

      if(document.containsKey("enderchestUpgrades")){
        this.enderchestUpgrades = new EnderchestUpgrades(document.get("enderchestUpgrades", Document.class));
      }

      if(document.containsKey("chatMode")){
        this.chatMode = ChatMode.valueOf(document.getString("chatMode"));
      }

      if(document.containsKey("spyTeam")){
        this.spyTeam = document.getList("spyTeam", ObjectId.class);
      }

      if(document.containsKey("pvpTimer") && document.get("pvpTimer") instanceof Document){
        this.pvpTimer = new PvPTimer(document.get("pvpTimer", Document.class));
      }

      if(document.containsKey("team")){
        team = Main.getInstance().getTeamHandler().getTeam(document.get("team", ObjectId.class));
      }

      if(document.containsKey("economy")){
        economyData = new EconomyData(document.getDouble("economy"));
      }

      if(document.containsKey("playtime")){
        this.playTime = document.getLong("playtime");
      }

      if(document.containsKey("deathban")){
        this.deathban = new DeathBan(document.getLong("deathban"));
      }


      this.deathMessages =
          document.containsKey("deathMessages") ? document.getBoolean("deathMessages") : true;
      this.teamNameTags =
          document.containsKey("teamNameTags") ? document.getBoolean("teamNameTags") : true;
      this.reclaimed = document.getBoolean("reclaimed", false);
      //this.globalChat = document.containsKey("globalChat") ? document.getBoolean("globalChat") : true;
      this.globalChat = true;

      if (document.containsKey("cooldowns")) {
        final Document cooldowns = (Document) document.get("cooldowns");
        for (final Map.Entry<String, Object> entry : cooldowns.entrySet()) {
          if (!entry.getKey().equalsIgnoreCase("_id")) {
            if (entry.getKey().equalsIgnoreCase("uuid")) {
              continue;
            }
            final String key = entry.getKey();
            final long value = cooldowns.getLong(key);
            if (System.currentTimeMillis() >= value) {
              continue;
            }
            this.kitCooldowns.put(key, value);
          }
        }
      }

      if (document.containsKey("noreclaimeditems")) {
        List<String> noreclaimeditems = document.getList("noreclaimeditems", String.class);
        for (String item : noreclaimeditems) {
          ItemStack itemStack = CorePlugin.GSON.fromJson(item, ItemStack.class);
          this.noReclaimedItems.add(itemStack);
        }
      }

      if (document.containsKey("privatechests")) {
        List<Document> privatechests = document.getList("privatechests", Document.class);
        for (Document privateChest : privatechests) {
          this.privateChests.add(new PrivateChest(privateChest));
        }
      }

      chestLevel = privateChests.size();

      if (document.containsKey("uses")) {
        Document uses = (Document) document.get("uses");

        for (final Map.Entry<String, Object> entry : uses.entrySet()) {
          if (!entry.getKey().equalsIgnoreCase("_id")) {
            if (entry.getKey().equalsIgnoreCase("uuid")) {
              continue;
            }

            String key = entry.getKey();
            int value = uses.getInteger(key);
            this.kitUses.put(key, value);
          }
        }
      }

      if (document.containsKey("killboosting")) {
        List<Document> killboosting = document.getList("killboosting", Document.class);
        for (Document killboost : killboosting) {
          this.boostings.add(new KillBoosting(killboost));
        }
      }
    }

    for (Kit kit : Main.getInstance().getKitManager().getKits()) {
      if (!kitUses.containsKey(kit.getName())) {
        kitUses.put(kit.getName(), 0);
      }
    }

    loaded = true;
  }

  public void save() {
    Document document = new Document();

    document.put("name", name);
    document.put("uuid", uuid.toString());
    document.put("lives", lives);
    document.put("emerald", emerald);
    document.put("diamond", diamond);
    document.put("gold", gold);
    document.put("iron", iron);
    document.put("lapis", lapis);
    document.put("redstone", redstone);
    document.put("coal", coal);
    document.put("kills", kills);
    document.put("deaths", deaths);
    document.put("dailycooldown", dailyCooldown);
    document.put("filterType", filterType.name());
    document.put("paymentsToggled", paymentsToggled);
    document.put("globalChat", globalChat);
    document.put("deathMessages", deathMessages);
    document.put("teamNameTags", teamNameTags);
    document.put("reclaimed", reclaimed);
    document.put("gems", gems);
    document.put("enderpearlcooldown", enderpearlCooldown);
    document.put("particlesbattle", particlesBattle);
    document.put("particles", particles);
    document.put("lang", lang.name());
    document.put("useMemeCommand", useMemeCommand);
    document.put("chatMode", chatMode.name());
    document.put("playtime", playTime);
    document.put("spyTeam", spyTeam);

    if(isDeathBanned()){
      document.put("deathban", deathban.getExpireAt());
    }

    if (team != null){
      document.put("team", team.getUniqueId());
    }

    if (economyData != null){
      document.put("economy", economyData.getBalance());
    }

    document.put("enderchestUpgrades", enderchestUpgrades.serialize());

    List<String> noreclaimeditems = noReclaimedItems.stream().map(CorePlugin.GSON::toJson).collect(Collectors.toList());

    document.put("noreclaimeditems", noreclaimeditems);

    document.put("privatechests", privateChests.stream().map(PrivateChest::serialize).collect(Collectors.toList()));

    Document cooldowns = new Document();
    Document uses = new Document();

    document.put("uuid", this.uuid.toString());

    for (final Map.Entry<String, Long> entry : this.kitCooldowns.entrySet()) {
      if (System.currentTimeMillis() < entry.getValue()) {
        cooldowns.put(entry.getKey(), entry.getValue());
      }
    }

    if (hasPvPTimer()){
      document.put("pvpTimer", pvpTimer.toDocument());
    }

    document.put("cooldowns", cooldowns);

    uses.putAll(this.kitUses);

    document.put("uses", uses);

    document.put("killboosting", boostings.stream()
            .filter(killBoosting -> !killBoosting.isExpired())
            .map(KillBoosting::serialize)
            .collect(Collectors.toList()));

    collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
  }

  public void asyncSave() {
    TaskUtil.runAsync(Main.getInstance(), this::save);
  }

  public void addLives(int amount) {
    lives += amount;
  }

  public void removeLives(int amount) {
    lives -= amount;

    if (lives < 0) {
      lives = 0;
    }
  }

  public void addEmerald(int amount) {
    emerald += amount;
  }

  public void addDiamond(int amount) {
    diamond += amount;
  }

  public void addOreMine() {
    this.currentOreMines++;
  }

  public void resetOreMine() {
    this.currentOreMines = 0;
  }

  public void addGold(int amount) {
    gold += amount;
  }

  public void addIron(int amount) {
    iron += amount;
  }

  public void addLapis(int amount) {
    lapis += amount;
  }

  public void addRedstone(int amount) {
    redstone += amount;
  }

  public void addCoal(int amount) {
    coal += amount;
  }

  public void addKills(int amount) {
    kills += amount;
  }

  public void addDeaths(int amount) {
    deaths += amount;
  }

  public double getKDR() {
    if (kills == 0) {
      return 0.0;
    }
    if (deaths == 0) {
      return 1.0;
    }
    return kills / deaths;
  }

  public void addGems(int amount) {
    gems += amount;
  }

  public void removeGems(int amount) {
    gems -= amount;
  }

  public boolean canUseKit(final Kit kit) {
    if (kit.getMaxUses() > 0 && kitUses.containsKey(kit.getName()) && kitUses.get(kit.getName()) >= kit.getMaxUses()) {
      return false;
    }

    if (kit.getMinPlaytimeMillis() != 0) {
      int minPlaytime = (int) TimeUnit.MILLISECONDS.toSeconds(kit.getMinPlaytimeMillis());
      HCFProfile profile = HCFProfile.getByUUID(uuid);

      if(profile == null){
        return false;
      }

      int playtimeTime = (int) TimeUnit.MILLISECONDS.toSeconds(profile.getTotalPlayTime());

      if (playtimeTime < minPlaytime) {
        return false;
      }
    }
    return this.kitCooldowns.get(kit.getName()) == null
        || System.currentTimeMillis() >= this.kitCooldowns.get(kit.getName());
  }

  public long getRemainingKitCooldown(final Kit kit) {
    long cooldown;
    if (this.kitCooldowns.get(kit.getName()) == null) {
      cooldown = 0L;
    } else {
      cooldown = this.kitCooldowns.get(kit.getName());
    }
    return cooldown;
  }

  public int getKitUses(Kit kit) {
    if (!kitUses.containsKey(kit.getName())) {
      return 0;
    }
    return kitUses.get(kit.getName());
  }

  public void ResetKitCooldown(final Kit kit) {
    this.kitCooldowns.put(kit.getName(), 0L);
    asyncSave();
  }

  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
      if (obj == null) {
          return false;
      }
      if (getClass() != obj.getClass()) {
          return false;
      }
      final HCFProfile other = (HCFProfile) obj;
    return Objects.equals(this.uuid, other.uuid);
  }
}