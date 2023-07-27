package cc.stormworth.hcf;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.core.util.scoreboard.ScoreboardHandler;
import cc.stormworth.core.util.scoreboard.ScoreboardStyle;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.battleplayers.BattlePlayers;
import cc.stormworth.hcf.bounty.BountyListener;
import cc.stormworth.hcf.bounty.BountyRunnable;
import cc.stormworth.hcf.brewingstand.BrewingStandListener;
import cc.stormworth.hcf.brewingstand.BrewingStandManager;
import cc.stormworth.hcf.brewingstand.BrewingStandRunnable;
import cc.stormworth.hcf.chatreaction.ReactionChatRunnable;
import cc.stormworth.hcf.comunitychest.ComunityChest;
import cc.stormworth.hcf.customenderpearl.CustomPearlListener;
import cc.stormworth.hcf.customenderpearl.EnderPearlSettings;
import cc.stormworth.hcf.customenderpearl.VItemEnderPearl17;
import cc.stormworth.hcf.deathmessage.DeathMessageHandler;
import cc.stormworth.hcf.deathrefound.DeathRefoundListener;
import cc.stormworth.hcf.events.EventHandler;
import cc.stormworth.hcf.events.citadel.CitadelHandler;
import cc.stormworth.hcf.events.conquest.ConquestHandler;
import cc.stormworth.hcf.events.ktk.KillTheKing;
import cc.stormworth.hcf.events.region.glowmtn.GlowHandler;
import cc.stormworth.hcf.events.region.nether.NetherHandler;
import cc.stormworth.hcf.events.region.oremountain.OreMountainHandler;
import cc.stormworth.hcf.giveaway.GiveAwayHandler;
import cc.stormworth.hcf.holograms.HologramManager;
import cc.stormworth.hcf.listener.*;
import cc.stormworth.hcf.listener.enderpearls.EnderPearlRunnable;
import cc.stormworth.hcf.listener.enderpearls.EnderPearlStuckListener;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.enchantments.Armor;
import cc.stormworth.hcf.misc.crazyenchants.enchantments.Axes;
import cc.stormworth.hcf.misc.crazyenchants.enchantments.PickAxes;
import cc.stormworth.hcf.misc.crazyenchants.enchantments.Swords;
import cc.stormworth.hcf.misc.crazyenchants.processors.CEnchantmentType;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import cc.stormworth.hcf.misc.daily.DailyManager;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.misc.gkits.KitListener;
import cc.stormworth.hcf.misc.gkits.KitParameterType;
import cc.stormworth.hcf.misc.gkits.data.FlatFileKitManager;
import cc.stormworth.hcf.misc.kills.KillListener;
import cc.stormworth.hcf.misc.lunarclient.LunarClientManager;
import cc.stormworth.hcf.misc.map.MapHandler;
import cc.stormworth.hcf.misc.map.leaderboards.LeaderboardRunnable;
import cc.stormworth.hcf.misc.partnerpackages.EditPackageListener;
import cc.stormworth.hcf.misc.payout.PayoutManager;
import cc.stormworth.hcf.misc.rewards.RewardsManager;
import cc.stormworth.hcf.misc.tournaments.handler.TournamentHandler;
import cc.stormworth.hcf.misc.trade.TradeListener;
import cc.stormworth.hcf.misc.vouchers.Voucher;
import cc.stormworth.hcf.misc.vouchers.listeners.VouchersListener;
import cc.stormworth.hcf.misc.war.FactionWarManager;
import cc.stormworth.hcf.persist.RedisSaveTask;
import cc.stormworth.hcf.persist.maps.misc.CreatorsCountMap;
import cc.stormworth.hcf.persist.maps.misc.SupportedMap;
import cc.stormworth.hcf.persist.maps.stats.OppleMap;
import cc.stormworth.hcf.poll.PollHandler;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.deathban.DeathBanRunnable;
import cc.stormworth.hcf.profile.enderchest.listeners.EnderchestListener;
import cc.stormworth.hcf.profile.listeners.DeathBanListener;
import cc.stormworth.hcf.profile.listeners.ProfileListener;
import cc.stormworth.hcf.providers.scoreboard.HCFBoardAdapter;
import cc.stormworth.hcf.providers.tab.TabProvider;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.pvpclasses.event.EffectRestorer;
import cc.stormworth.hcf.refill.RefillListener;
import cc.stormworth.hcf.runnable.ActionBarRunnable;
import cc.stormworth.hcf.schedule.ScheduleManager;
import cc.stormworth.hcf.server.EnderpearlCooldownHandler;
import cc.stormworth.hcf.server.ServerHandler;
import cc.stormworth.hcf.supplydrop.SupplyDropManager;
import cc.stormworth.hcf.team.TeamHandler;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import cc.stormworth.hcf.team.duel.FactionDuelManager;
import cc.stormworth.hcf.team.listener.TeamListener;
import cc.stormworth.hcf.team.system.*;
import cc.stormworth.hcf.team.upgrades.UpgradeListener;
import cc.stormworth.hcf.team.utils.LunarTeammatesHandler;
import cc.stormworth.hcf.tip.TipManager;
import cc.stormworth.hcf.util.cooldowntimer.TimerManager;
import cc.stormworth.hcf.util.glass.GlassManager;
import cc.stormworth.hcf.util.misc.RegenUtils;
import cc.stormworth.hcf.util.reflect.PacketsUtils;
import cc.stormworth.hcf.util.support.PartnerFaces;
import cc.stormworth.hcf.util.tasks.OnlineDonorsTask;
import cc.stormworth.hcf.util.threads.PacketBorderThread;
import cc.stormworth.hcf.util.workload.WorKLoadQueue;
import cc.stormworth.hcf.voteparty.VotePartyHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static String DATABASE_NAME;

    public final static Random RANDOM = ThreadLocalRandom.current();
    @Getter
    private static Main instance;
    private MongoClient mongoPool;
    public MapHandler mapHandler;
    public FlatFileKitManager kitManager;
    public ThreadLocalRandom random;
    public NonKitMapListener nonKitMapListener;
    public ProfileListener playtimeListener;
    public BukkitTask dtrTask;
    public InvisibilityListener invisibilityListener;
    public PickAxes pickAxes;
    public SignSubclaimListener signSubclaimListener;
    public Swords swords;
    private PvPClassHandler pvpClassHandler;
    private TeamHandler teamHandler;
    private ServerHandler serverHandler;
    private CitadelHandler citadelHandler;
    private EventHandler eventHandler;
    private ConquestHandler conquestHandler;
    private GlowHandler glowHandler;
    private NetherHandler netherHandler;
    private OreMountainHandler oreHandler;
    private EffectRestorer effectRestorer;
    private OppleMap oppleMap;
    private DailyManager dailyManager;
    private SupportedMap supportedMap;
    private CreatorsCountMap creatorsCountMap;
    @Setter private ConfigFile limitersfile, utilitiesFile, arenasConfig, arenasDuelConfig, enderPearlConfig;
    private FactionWarManager factionWarManager;
    @Setter
    private KillTheKing killTheKing;
    public SpectatorListener spectatorListener;
    private OnlineDonorsTask onlineDonorsTask;
    private GlassManager glassManager;
    private LunarClientManager lunarClientManager;
    private FactionDuelManager factionDuelManager;
    private TournamentHandler tournamentHandler;

    private WorKLoadQueue worKLoadQueue;

    private TipManager tipManager;

    private BattlePlayers battlePlayers;
    private ComunityChest comunityChest;
    private GiveAwayHandler giveAwayHandler;
    private PollHandler pollHandler;
    private VotePartyHandler votePartyHandler;

    private EnchantmentsManager enchantmentsManager;

    private HologramManager hologramManager;
    private RewardsManager rewardsManager;
    private PayoutManager payoutManager;

    private EnderPearlRunnable enderPearlRunnable;
    private DeathBanRunnable deathbanRunnable;
    private UpgradeListener upgradeListener;
    private SupplyDropManager supplyDropManager;
    private EnderPearlSettings enderPearlSettings;

    private ScheduleManager scheduleManager;

    private BrewingStandManager brewingStandManager;

    private void loadConfigurations() {
        limitersfile = new ConfigFile(this, "limiters.yml");
        utilitiesFile = new ConfigFile(this, "utilities.yml");
        arenasConfig = new ConfigFile(this, "arenaswar.yml");
        arenasDuelConfig = new ConfigFile(this, "arenasduel.yml");
        enderPearlConfig = new ConfigFile(this, "enderpearls.yml");
        kitManager = new FlatFileKitManager(this);

        enderPearlSettings = new EnderPearlSettings(enderPearlConfig);
    }

    @Override
    public void onEnable() {
        instance = this;

        new PacketsUtils();

        this.saveDefaultConfig();
        loadConfigurations();
        loadDatabases();

        setupHandlers();
        setupPersistence();
        setupListeners();
        registerTasks();
        registerCommands();

        //CorePlugin.setChatFormat(Boolean.TRUE);

        SpigotConfig.tpsValue = 15;
        SpigotConfig.crosspearls = true;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                HCFProfile profile = HCFProfile.get(player);

                if (profile == null) {
                    continue;
                }

                profile.getPlaySession().setEndTime(System.currentTimeMillis());

                profile.setPlayTime(profile.getPlayTime() + profile.getPlaySession().getTime());

                profile.save();

                player.setMetadata("loggedout", new FixedMetadataValue(this, true));

                player.kickPlayer("Server is restarting. Please try again in a few minutes.");
            }
        }

        getEventHandler().saveEvents();
        saveData();

        worKLoadQueue.shutdown();

        RedisSaveTask.save(null, true);
        onlineDonorsTask.disable();
        for (World world : Bukkit.getWorlds()) {
            Iterator<Entity> combatLoggerIterator = world.getEntitiesByClasses(Villager.class).iterator();

            while (combatLoggerIterator.hasNext()) {
                Villager villager = (Villager) combatLoggerIterator.next();

                if (villager.isCustomNameVisible() && villager.hasMetadata(CombatLoggerListener.COMBAT_LOGGER_METADATA)) {
                    villager.remove();
                    combatLoggerIterator.remove();
                }
            }
        }

        votePartyHandler.save();
        glassManager.disable();
        dailyManager.disable();

        comunityChest.save();
        battlePlayers.save();
        pollHandler.save();
        rewardsManager.save();
        getMapHandler().getStatsHandler().save();
        //factionWarManager.save();
        factionDuelManager.save();
        RegenUtils.resetAll();

        CorePlugin.getInstance().runRedisCommand(jedis -> {
            jedis.save();
            return null;
        });

        if (!mapHandler.isKitMap()) {
            if (glowHandler != null && glowHandler.getGlowMountain() != null) {
                glowHandler.getGlowMountain().reset();
            }
            if (netherHandler != null && netherHandler.getArea() != null) {
                netherHandler.getArea().reset();
            }
            if (oreHandler != null && oreHandler.getOreMountain() != null) {
                oreHandler.getOreMountain().reset();
            }
        }

        brewingStandManager.saveBrewingStands();
    }

    private void loadDatabases() {
        try {
            Main.DATABASE_NAME = getConfig().getString("Mongo.DBName");
            String host = getConfig().getString("Mongo.Host", "127.0.0.1");
            String authDB = getConfig().getString("Mongo.AuthDB", "admin");
            String username = getConfig().getString("Mongo.Username", "MineHQ");
            String password = getConfig().getString("Mongo.Password", "");

            boolean authRequired = password.length() > 0;
            ServerAddress address = new ServerAddress(host, 27017);

            if (!authRequired) {
                mongoPool = new MongoClient(address);
            } else {
                mongoPool = new MongoClient(address, MongoCredential.createCredential(
                        username,
                        authDB,
                        password.toCharArray()
                ), MongoClientOptions.builder()
                        .retryWrites(true)
                        .build());
            }

            System.out.println("[HCF] Connected to MongoDB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadVouchers() {
        if (getConfig().getConfigurationSection("vouchers") == null) {
            return;
        }
        for (String voucher : getConfig().getConfigurationSection("vouchers").getKeys(false)) {
            new Voucher(voucher.toLowerCase(),
                    getConfig().getStringList("vouchers." + voucher + ".commands"), ItemBuilder.of(
                            Material.getMaterial(getConfig().getString("vouchers." + voucher + ".material.type")))
                    .name(getConfig().getString("vouchers." + voucher + ".material.name"))
                    .setLore(getConfig().getStringList("vouchers." + voucher + ".material.lore")).build());
        }
        Bukkit.getServer().getPluginManager().registerEvents(new VouchersListener(), this);
    }

    public void saveData() {
        getKitManager().saveKitData();
        getMapHandler().getStatsHandler().save();
        if (mapHandler.isKitMap()) {
            getMapHandler().getKitManager().save();
        }
    }

    private void setupHandlers() {
        new WebsiteListener();

        mapHandler = new MapHandler();
        mapHandler.load();

        if (!getMapHandler().isKitMap()) {
            invisibilityListener = new InvisibilityListener();
        }
        new PacketBorderThread().start();

        enchantmentsManager = new EnchantmentsManager(this);
        enchantmentsManager.setupCrazyEnchants();

        hologramManager = new HologramManager();
        rewardsManager = new RewardsManager();
        payoutManager = new PayoutManager();

        glassManager = new GlassManager();

        serverHandler = new ServerHandler();
        teamHandler = new TeamHandler();
        LandBoard.getInstance().loadFromTeams();
        citadelHandler = new CitadelHandler();
        effectRestorer = new EffectRestorer(this);
        pvpClassHandler = new PvPClassHandler();
        if (!mapHandler.isKitMap()) {
            nonKitMapListener = new NonKitMapListener();
        }
        eventHandler = new EventHandler();
        conquestHandler = new ConquestHandler();
        if (getConfig().getBoolean("glowstoneMountain", false)) {
            glowHandler = new GlowHandler();
        }
        if (!Main.getInstance().getMapHandler().isKitMap()) {
            netherHandler = new NetherHandler();
        }
        if (getConfig().getBoolean("oreMountain", false)) {
            oreHandler = new OreMountainHandler();
        }

        //abilityManager = new AbilityManager();

        //factionWarManager = new FactionWarManager(this);

        factionDuelManager = new FactionDuelManager(this);

        tournamentHandler = new TournamentHandler();

        dailyManager = new DailyManager();

        ScoreboardHandler scoreboard = new ScoreboardHandler(this, new HCFBoardAdapter());
        scoreboard.setTicks(2);
        scoreboard.setStyle(ScoreboardStyle.MODERN);

        CorePlugin.getInstance().getTabEngine().setLayoutProvider(new TabProvider());

        //new TabHandler(new v1_7_R4TabAdapter(), new TabProvider(), this, 20L);

        for (PartnerFaces value : PartnerFaces.values()) {
            ItemStack item;
            UUID uuid = UUID.fromString(value.getUuid());
            try {
                item = PartnerFaces.createSkull(UUIDUtils.name(uuid), 1, uuid, null);
            } catch (Exception exception) {
                item = ItemBuilder.of(Material.SKULL_ITEM).data((short) 3).name(UUIDUtils.name(uuid))
                        .build();
            }
            value.setItem(item);
        }
        if (getTeamHandler().getTeam("spawn") == null) {
            new SpawnTeam();
            if (!getMapHandler().isKitMap()) {
                new EventRestrictedTeam();
                new RestrictedZoneTeam();
            }
            new RoadTeam.NorthRoadTeam();
            new RoadTeam.SouthRoadTeam();
            new RoadTeam.WestRoadTeam();
            new RoadTeam.EastRoadTeam();
            new ConquestTeam();
            if (!getMapHandler().isKitMap()) {
                new EndPortalTeam();
                new OreMountainTeam();
                new GlowstoneTeam();
                new NetherTeam();
            }
        }

        if (getTeamHandler().getTeam("Glowstone") == null && !getMapHandler().isKitMap()){
            new GlowstoneTeam();
        }

        if (Main.getInstance().getMapHandler().isKitMap()) {
            removeRecipe(Material.FURNACE);
        }

        removeRecipe(Material.TNT);
        removeRecipe(Material.ENDER_CHEST);
        removeRecipe(Material.DROPPER);
        removeRecipe(Material.DISPENSER);
        removeRecipe(Material.BED);
        removeRecipe(Material.BEACON);
        removeRecipe(Material.GOLDEN_APPLE);
        removeRecipe(Material.HOPPER_MINECART);
        removeRecipe(Material.JUKEBOX);
        removeRecipe(Material.NOTE_BLOCK);

        battlePlayers = new BattlePlayers();
        comunityChest = new ComunityChest();
        giveAwayHandler = new GiveAwayHandler();
        votePartyHandler = new VotePartyHandler();

        tipManager = new TipManager(this);

        Ability.init();
        new TimerManager();
        pollHandler = new PollHandler();
        //new HologramManager();
        loadVouchers();

        //SpigotConfig.tpsValue = 15;
        SpigotConfig.notrackbypass = true;

        scheduleManager = new ScheduleManager(this);
        brewingStandManager = new BrewingStandManager();
    }

    private void registerCommands() {
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.events");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.misc");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.team");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.shop.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.gemsshop");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.tip.commands");
        //rCommandHandler.registerPackage(this, "cc.stormworth.hcf.airdrop.command");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.bounty.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.misc.lunarclient.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.profile.editor.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.profile.ec.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.supplydrop");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.profile.enderchest.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.util.worldload.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.schedule.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.util.workload.commands");
        rCommandHandler.registerPackage(this, "cc.stormworth.hcf.events.eclipse.commands");

        rCommandHandler.registerParameterType(Kit.class, new KitParameterType());
        rCommandHandler.registerParameterType(CEnchantment.class, new CEnchantmentType());
        DeathMessageHandler.init();
    }

    private void removeRecipe(final Material material) {
        final Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getResult().getType() == material) {
                iterator.remove();
            }
        }
    }

    private void registerTasks() {
        dtrTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DTRHandler(), 20L, 120L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new LunarTeammatesHandler(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RedisSaveTask(), 1200L, 1200L);

        //Bukkit.getScheduler().runTaskTimer(this, new BlinderRunnable(), 0L, 10L);

        //Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ParticleRunnable(), 0L, 5L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new ReactionChatRunnable(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new LeaderboardRunnable(), 20L * 60L * 60L,
                        20L * 60L * 60L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new ActionBarRunnable(), 0L, 0L);

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), enderPearlRunnable = new EnderPearlRunnable(), 0L, 10L);

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), deathbanRunnable = new DeathBanRunnable(), 0L,  20L);

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new BrewingStandRunnable(brewingStandManager), 0L,  20L);

        onlineDonorsTask = new OnlineDonorsTask();

        if(mapHandler.isKitMap()){
            supplyDropManager = new SupplyDropManager();
            Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new BountyRunnable(), 0L, 20L);
        }

        lunarClientManager = new LunarClientManager();

        worKLoadQueue = new WorKLoadQueue();
    }

    private void setupListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();


        //BattleSpigot.getInstance().addMovementHandler(new UpgradeMovementAdapter());
        //BattleSpigot.getInstance().addMovementHandler(new CustomMovementHandler());
        Method method;
        Item item = null;

        try {
            method = Item.class.getDeclaredMethod("f", String.class);
            method.setAccessible(true);

            try {
                item = (Item)method.invoke((new VItemEnderPearl17()).c("enderPearl"), "ender_pearl");
                this.getLogger().info("Enderpearl 1.7.10 successfully invoked.");
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
            }

            method.setAccessible(false);
        } catch (SecurityException | NoSuchMethodException var8) {
        }


        Item.REGISTRY.a(368, "ender_pearl", item);
        Arrays.asList(
                new ProfileListener(this),
                new RepairSignListener(),
                new PotionLimiterListener(),
                new MapListener(),
                new ChatListener(),
                new AntiGlitchListener(),
                new BasicPreventionListener(),
                new CombatLoggerListener(),
                new EnderpearlCooldownHandler(),
                new SetListener(),
                new PlayerListener(),
                new GoldenAppleListener(),
                new NetherPortalTrapHandler(),
                new SpawnListener(),
                new SpawnTagListener(),
                new TeamListener(),
                new KitListener(),
                new SignElevatorListener(),
                //new BlockHitFixListener(),
                new MobChangesListener(),
                //new PackagesListener(),
                new EditPackageListener(),
                new SignSubclaimListener(),
                new Armor(),
                new TradeListener(),
                new RefillListener(),
                new BountyListener(),
                upgradeListener = new UpgradeListener(),
                new KillListener(),
                new EnderPearlStuckListener(),
                new EnderchestListener(),
                new CustomPearlListener(),
                new SellAllValuablesSignListener(),
                new DeathBanListener(this),
                //new BorderListener(),
                new DeathRefoundListener(),
                new BrewingStandListener(brewingStandManager)
        ).forEach(listener -> pluginManager.registerEvents(listener, this));

        if (mapHandler.isKitMap()) {
            pluginManager.registerEvents(new RepairSignListener(), this);
            pluginManager.registerEvents(new KitMapListener(), this);
            pluginManager.registerEvents(new StrafeAbilityListener(), this);
        } else {
            pluginManager.registerEvents(new BaseRegenListener(), this);
            pluginManager.registerEvents(new CrowbarListener(), this);
            pluginManager.registerEvents(new PvPTimerListener(), this);
        }

        pluginManager.registerEvents(new Axes(), this);
        pickAxes = new PickAxes();
        swords = new Swords();

        pluginManager.registerEvents(new NetherPortalListener(), this);

        for (final World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
        }
        /*if (getUtilitiesFile().getConfig().contains("stage")) {
            SetupStage stage = SetupStage.valueOf(getUtilitiesFile().getConfig().getString("stage"));
            if (stage != null && stage != SetupStage.NONE) {
                SetupStage.setStage(stage);
                getServer().getPluginManager().registerEvents(new SetupListener(), this);
            }
        }*/
    }

    private void setupPersistence() {
        (oppleMap = new OppleMap()).loadFromRedis();

        (creatorsCountMap = new CreatorsCountMap()).loadFromRedis();

        (supportedMap = new SupportedMap()).loadFromRedis();
    }

    public void clearPersistance() {
        //getTeamDelayMap().wipeVals();
        //getPlaytimeMap().wipeVals();
        getOppleMap().wipeVals();
        //getChatModeMap().wipeVals();
        //getChatSpyMap().wipeVals();
        /*getDeathbanMap().wipeVals();
        getDeathbannedMap().wipeVals();*/
        //getPvPTimerMap().wipeVals();
        //getStartingPvPTimerMap().wipeVals();

        if (!mapHandler.isKitMap()) {
            //getRevivedMap().wipeVals();
            getCreatorsCountMap().wipeVals();
            getSupportedMap().wipeVals();
        }

        Main.getInstance().getMapHandler().getStatsHandler().clearAll();
    }


}