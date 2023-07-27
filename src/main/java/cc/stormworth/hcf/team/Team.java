package cc.stormworth.hcf.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.conquest.ConquestHandler;
import cc.stormworth.hcf.events.conquest.game.ConquestGame;
import cc.stormworth.hcf.events.region.glowmtn.GlowHandler;
import cc.stormworth.hcf.events.region.glowmtn.GlowMountain;
import cc.stormworth.hcf.events.region.oremountain.OreMountain;
import cc.stormworth.hcf.events.region.oremountain.OreMountainHandler;
import cc.stormworth.hcf.listener.SpectatorListener;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.team.dtr.DTRHandler;
import cc.stormworth.hcf.team.event.PlayerRaidTeamEvent;
import cc.stormworth.hcf.team.event.TeamPointsChangeEvent;
import cc.stormworth.hcf.team.event.TeamRaidEvent;
import cc.stormworth.hcf.team.event.TeamUnRaidEvent;
import cc.stormworth.hcf.team.notes.TeamNote;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.chat.ChatMode;
import cc.stormworth.hcf.util.misc.PotionUtil;
import cc.stormworth.hcf.util.player.Players;
import cc.stormworth.hcf.util.workload.PlacableBlock;
import cc.stormworth.hcf.util.workload.TeamWorkload;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.FastMath;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Team {

    // Constants //
    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final String GRAY_LINE =
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
    public static final int MAX_CLAIMS = 2;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    // Internal //
    @Getter
    private boolean needsSave, loading;

    // Persisted //
    @Setter
    @Getter
    private ObjectId uniqueId;
    @Getter
    private String name;

    @Getter
    private Location HQ;

    @Getter
    private int balance;
    @Getter
    private double DTR;

    @Getter
    private long DTRCooldown;
    @Getter
    private final List<Claim> claims = Lists.newArrayList();
    private boolean shouldRegen;

    @Getter
    private final List<Location> blocksAddedInRaid = Lists.newArrayList();
    @Getter
    private final Map<Location, String> blocksRemovedInRaid = Maps.newHashMap();

    @Setter
    @Getter
    private long createAt;
    @Setter
    private boolean dtrRegenFaster = false;
    @Setter
    @Getter
    private long dtrRegenFasterEndAt;
    @Getter
    private final Map<PotionEffectType, Integer> activeEffects = Maps.newHashMap();
    @Getter
    private final List<String> eventDisqualified = Lists.newArrayList();
    @Setter
    @Getter
    private boolean bypassEvent = false;

    @Getter
    private UUID owner = null;
    @Getter
    private final Set<UUID> members = Sets.newHashSet();
    @Getter
    private final Set<UUID> captains = Sets.newHashSet();
    @Getter
    private final Set<UUID> coLeaders = Sets.newHashSet();
    @Getter
    private final Set<UUID> invitations = Sets.newHashSet();
    @Getter
    private final Set<ObjectId> allies = Sets.newHashSet();
    @Getter
    private final Set<ObjectId> requestedAllies = Sets.newHashSet();
    @Getter
    private String announcement;
    @Getter
    private boolean friendlyFire;
    @Getter
    private boolean claimsLocked;
    @Getter
    private boolean disqualified;
    @Getter
    private boolean open;
    @Getter
    private String focus;
    @Getter
    private int lives;
    @Getter
    private int points;
    @Getter
    private int kills;
    @Getter
    private int kothCaptures;
    @Getter
    private int madCaptures;
    @Getter
    private int conquestsCapped;
    @Getter
    private int citadelsCapped;
    @Getter
    private int diamondsMined;
    @Getter
    private int glowstoneMined;
    @Getter
    private int deaths; 
    @Getter
    private int killstreakPoints;
    @Getter
    private int playtimePoints;
    @Getter
    private int addedPoints;
    @Getter
    private int raids;
    @Getter
    private int removedPoints;
    @Getter
    private int spentPoints;
    @Getter
    private int hoppers;
    @Getter
    private int subclaims;
    @Getter
    private int enderManKills;
    @Getter
    private final Set<UUID> historicalMembers = Sets.newHashSet();
    @Setter
    @Getter
    private UUID focused;
    @Getter
    @Setter
    private Location rally;
    private boolean reclaimReward;

    @Getter
    private final Map<UUID, String> pvpClassesMap = Maps.newHashMap();

    @Getter
    @Setter
    private Hologram raidHologram;

    @Getter
    @Setter
    private Location raidBlock;

    @Getter
    private Map<TeamWorkdLoadType, TeamWorkload> workloadRunnables = Maps.newHashMap();
    @Setter
    @Getter
    private Hologram regenBaseHologram;

    @Getter @Setter private boolean useBase = false;

    @Getter
    private final List<TeamNote> notes = Lists.newArrayList();

    public Team(final String name) {
        this.name = name;
    }


    public int getEffectLevel(PotionEffectType potionEffect) {
        return activeEffects.getOrDefault(potionEffect, 0);
    }

    public boolean isRecently() {

        if (createAt == 0) {
            return false;
        }

        return System.currentTimeMillis() - createAt < TimeUnit.MINUTES.toMillis(20);
    }

    public String getName(final Player player, boolean chat) {
        if (this.name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {
            return ChatColor.GOLD + "Glowstone";
        }

        if (this.name.equals(OreMountainHandler.getOreTeamName()) && this.getMembers().size() == 0) {
            return ChatColor.AQUA + "Ore Mountain";
        }

        if (this.owner == null) {
            if (this.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                switch (player.getWorld().getEnvironment()) {
                    case NETHER:
                        return ChatColor.GREEN + "Nether Spawn";
                    case THE_END:
                        return ChatColor.GREEN + "The End Safezone";
                    default:
                        return ChatColor.GREEN + "Spawn";
                }
            } else {
                if (this.hasDTRBitmask(DTRBitmask.KOTH)) {
                    switch (name) {
                        case "EOTW":
                            return ChatColor.DARK_RED + this.getName();
                        case "Citadel":
                        case "End":
                            return ChatColor.DARK_PURPLE + this.getName();
                        case "Hell":
                        case "Nether":
                            return ChatColor.RED + this.getName();
                        case "Palace":
                            return ChatColor.DARK_AQUA + this.getName();
                        case "Mad":
                            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + this.getName();
                        default:
                            return ChatColor.GOLD + this.getName();
                    }
                }

                if (this.hasDTRBitmask(DTRBitmask.CITADEL)) {
                    return ChatColor.DARK_PURPLE + "Citadel";
                }

                if (this.hasDTRBitmask(DTRBitmask.ROAD)) {
                    if (player.getWorld().getEnvironment() == Environment.NETHER) {
                        return ChatColor.RED + this.getName().replace("Road", " Road");
                    }
                    return ChatColor.RED + this.getName().replace("Road", " Road");
                } else {
                    if (this.hasDTRBitmask(DTRBitmask.END_PORTAL)) {
                        return ChatColor.DARK_AQUA + "End Portal";
                    }
                    if (this.hasDTRBitmask(DTRBitmask.RESTRICTED_ZONE)) {
                        return ChatColor.RED + "Restricted Zone";
                    }
                    if (this.hasDTRBitmask(DTRBitmask.RESTRICTED_EVENT)) {
                        return ChatColor.RED + "Restricted Event";
                    }
                    if (this.hasDTRBitmask(DTRBitmask.CONQUEST)) {
                        return ChatColor.GOLD + "Conquest";
                    }
                    if (this.name.equalsIgnoreCase("warzone")) {
                        return ChatColor.RED + "Warzone";
                    }
                    if (this.name.equalsIgnoreCase("Hell")) {
                        return ChatColor.RED + "Hell";
                    }
                    if (this.name.equalsIgnoreCase("Conquest-Mid")) {
                        return ChatColor.GOLD + "Conquest-Mid";
                    }
                }
            }
        }

        if (this.isMember(player.getUniqueId())) {
            return Main.getInstance().getMapHandler().getTeamRelationColor() + this.getName();
        }

        if (this.isAlly(player.getUniqueId())) {
            return Main.getInstance().getMapHandler().getAllyRelationColor() + this.getName();
        }

        if (chat) {
            return ChatColor.GRAY + this.getName();
        }

        return ChatColor.YELLOW + this.getName();
    }

    /*private ChatColor getColorByPosition(Team team) {
        LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamTopCommand.getTopTeams();

        int position = 0;

        for (Team t : sortedTeamPlayerCount.keySet()) {

            if (t.getOwner() == null) {
                continue;
            }

            position++;
            if (position > 3) {
                break;
            }

            if (t.getUniqueId() == team.getUniqueId()) {
                break;
            }
        }

        if (position == 1) {
            return ChatColor.AQUA;
        } else if (position == 2) {
            return ChatColor.DARK_PURPLE;
        } else if (position == 3) {
            return ChatColor.GOLD;
        }

        return ChatColor.YELLOW;
    }*/


    public String getName(final Player player) {
        return getName(player, false);
    }

    public String getRelationColor(Player player) {
        if (this.isMember(player.getUniqueId())) {
            return Main.getInstance().getMapHandler().getTeamRelationColor() + "";
        }
        if (this.isAlly(player.getUniqueId())) {
            return Main.getInstance().getMapHandler().getAllyRelationColor() + "";
        }
        return Main.getInstance().getMapHandler().getDefaultRelationColor() + "";
    }

    public void saveIfNotLoading() {
        if (this.loading) {
            return;
        }
        this.flagForSave();
    }

    public boolean hasDtrRegenCooldown() {
        return dtrRegenFasterEndAt - System.currentTimeMillis() > 0;
    }

    public boolean isDtrRegenFaster() {
        if (dtrRegenFaster && !hasDtrRegenCooldown()) {
            dtrRegenFaster = false;
            return false;
        }

        return dtrRegenFaster;
    }

    public void setShouldregen(boolean shouldRegen) {
        this.shouldRegen = shouldRegen;
    }

    public boolean shouldRegen() {
        return !isRaidable() && shouldRegen;
    }

    public void regenBlocks() {
        if (CustomTimerCreateCommand.isSOTWTimer()) {
            return;
        }

        if (!shouldRegen) {
            return;
        }

        if (workloadRunnables.get(TeamWorkdLoadType.REGEN) != null) {
            return;
        }

        setShouldregen(false);
        saveIfNotLoading();

        if (blocksRemovedInRaid.isEmpty() && blocksAddedInRaid.isEmpty()) {
            return;
        }

        TeamWorkload workloadRunnable = new TeamWorkload(this, TeamWorkdLoadType.REGEN, getHQ());

        if (!blocksAddedInRaid.isEmpty()) {
            for (Location location : blocksAddedInRaid) {

                PlacableBlock placableBlock = new PlacableBlock(location.getWorld().getUID(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ(),
                        Material.AIR,
                        (byte) 0);

                workloadRunnable.addWorkload(placableBlock);
            }
        }


        if (!blocksRemovedInRaid.isEmpty()) {
            for (Map.Entry<Location, String> entry : blocksRemovedInRaid.entrySet()) {

                Location location = entry.getKey();
                String[] split = entry.getValue().split(";");
                Material material = Material.getMaterial(Integer.parseInt(split[0]));
                byte data = Byte.parseByte(split[1]);

                PlacableBlock placableBlock = new PlacableBlock(location.getWorld().getUID(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ(),
                        material,
                        data);

                workloadRunnable.addWorkload(placableBlock);
            }
        }

        blocksAddedInRaid.clear();
        blocksRemovedInRaid.clear();

        Main.getInstance().getWorKLoadQueue().addWorkload(workloadRunnable);
        workloadRunnables.put(TeamWorkdLoadType.REGEN, workloadRunnable);
    }

    public void addRemoveBlockInRaid(Block block) {
        blocksRemovedInRaid.put(block.getLocation(), block.getType().getId() + ";" + block.getData());
        saveIfNotLoading();
    }

    public void addRemoveBlockInRaid(Location location, Material material, byte data) {
        blocksRemovedInRaid.put(location, material.getId() + ";" + data);
        saveIfNotLoading();
    }

    public void addBlockAddInRaid(Block block) {
        blocksAddedInRaid.add(block.getLocation());
        saveIfNotLoading();
    }

    public void addBlockAddInRaid(Location location) {
        blocksAddedInRaid.add(location);
        saveIfNotLoading();
    }

    public void claimReward() {
        if (reclaimReward) {
            return;
        }

        Player leader = Bukkit.getPlayer(owner);

        if (leader == null) {
            return;
        }

        reclaimReward = true;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + leader.getName() + " Partner 3"); //Need key name

        sendMessage("");
        sendMessage(CC.translate(
                "&6&lCongratulations! &eYou Faction is full for first time and you have claimed your reward!"));
        sendMessage("");
    }

    public void addSubclaim() {
        ++subclaims;
        saveIfNotLoading();
    }

    public void removeSubclaim() {
        if (subclaims <= 0) {
            setSubclaims(0);
        } else {
            --subclaims;
        }
        saveIfNotLoading();
    }

    public void addMember(UUID member) {
        if (this.members.add(member)) {
            Player player = Bukkit.getPlayer(member);

            if (player != null && PvPClassHandler.getEquippedKits().containsKey(player.getName())) {

                PvPClass PvPClass = PvPClassHandler.getEquippedKits().get(player.getName());

                if (PvPClass.hasLimit(this)) {

                    PvPClassHandler.getEquippedKits().remove(player.getName());
                    PvPClass.remove(player);
                    cc.stormworth.hcf.pvpclasses.PvPClass.removeInfiniteEffects(player);
                    StringJoiner joiner = new StringJoiner(CC.translate("&7, "));

                    this.getOnlineMembers().stream()
                            .filter(online -> PvPClassHandler.hasKitOn(online, PvPClass))
                            .forEach(online -> joiner.add(online.getName()));

                    player.sendMessage(new String[]{
                            CC.RED + "Your team already exceeded the limit of " + PvPClass.getName() + " class! ("
                                    + PvPClass.getLimit() + ")",
                            CC.RED + "- " + joiner
                    });

                    player.sendMessage("");
                    player.sendMessage(
                            CC.translate("&7 • &6&lClass: &e" + PvPClass.getName() + " &c(Disabled)"));
                    player.sendMessage("");

                    return;
                }

                PvPClass.addLimit(this);
            }

            this.historicalMembers.add(member);

            if (this.loading) return;

            TeamTrackerManager.logAsync(this, TeamActionType.PLAYER_JOINED, ImmutableMap.of(
                    "playerId", member.toString(),
                    "date", System.currentTimeMillis()
            ));

            this.flagForSave();

            if (this.getMembers().size() == Main.getInstance().getMapHandler().getTeamSize()) {
                claimReward();
            }
        }
    }

    public void addCaptain(final UUID captain) {
        if (this.captains.add(captain) && !this.isLoading()) {

            Player player = Bukkit.getPlayer(captain);

            TeamTrackerManager.logAsync(this, TeamActionType.PROMOTED_TO_CAPTAIN, ImmutableMap.of(
                    "playerId", captain.toString(),
                    "date", System.currentTimeMillis()
            ));

            this.flagForSave();
        }
    }

    public void addCoLeader(final UUID co) {
        if (this.coLeaders.add(co) && !this.isLoading()) {

            Player player = Bukkit.getPlayer(co);

            TeamTrackerManager.logAsync(this, TeamActionType.PROMOTED_TO_CO_LEADER, ImmutableMap.of(
                    "playerId", co.toString(),
                    "date", System.currentTimeMillis()
            ));

            this.flagForSave();
        }
    }

    public void removeCaptain(UUID captain) {
        if (this.captains.remove(captain)) {

            Player player = Bukkit.getPlayer(captain);

            TeamTrackerManager.logAsync(this, TeamActionType.DEMOTED_FROM_CAPTAIN, ImmutableMap.of(
                    "playerId", captain.toString(),
                    "date", System.currentTimeMillis()
            ));

            this.flagForSave();
        }
    }

    public void removeCoLeader(final UUID co) {
        if (this.coLeaders.remove(co)) {


            TeamTrackerManager.logAsync(this, TeamActionType.DEMOTED_FROM_CO_LEADER, ImmutableMap.of(
                    "playerId", co.toString(),
                    "date", System.currentTimeMillis()
            ));

            this.flagForSave();
        }
    }

    public void setFriendlyFire(final boolean ff) {
        this.friendlyFire = ff;
        this.flagForSave();
    }

    public void setClaimsLocked(final boolean claimslocked) {
        this.claimsLocked = claimslocked;
        this.flagForSave();
    }

    public boolean addLives(final int lives) {
        if (lives < 0) {
            return false;
        }
        this.lives += lives;
        this.flagForSave();
        return true;
    }

    public boolean removeLives(final int lives) {
        if (this.lives < lives || lives < 0) {
            return false;
        }
        this.lives -= lives;
        this.flagForSave();
        return true;
    }

    public void disband() {
        removeAllEffects();

        try {
            if (this.owner != null) {
                int refund = this.balance;
                for (final Claim claim : this.claims) {
                    refund += Claim.getPrice(claim, this, false);
                }

                HCFProfile profile = HCFProfile.getByUUID(this.owner);

                if (profile != null) {
                    profile.getEconomyData().addBalance(refund);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (final ObjectId allyId : this.getAllies()) {
            Team ally = Main.getInstance().getTeamHandler().getTeam(allyId);
            if (ally != null) {
                ally.getAllies().remove(this.getUniqueId());
            }
        }

        for (final UUID uuid : this.members) {
            HCFProfile profile = HCFProfile.getByUUID(uuid);

            if (profile == null) {
                continue;
            }

            profile.setChatMode(ChatMode.PUBLIC);
        }

        for (Player online : this.getOnlineMembers()) {
            CorePlugin.getInstance().getNametagEngine().reloadPlayer(online);
            CorePlugin.getInstance().getNametagEngine().reloadOthersFor(online);
        }

        Main.getInstance().getTeamHandler().removeTeam(this);
        LandBoard.getInstance().clear(this);

        TaskUtil.runAsync(Main.getInstance(), () -> CorePlugin.getInstance().runRedisCommand(redis -> {
            redis.del(Main.DATABASE_NAME + "." + Team.this.name.toLowerCase());
            return null;
        }));

        this.needsSave = false;
    }

    public void rename(final String newName) {
        final String oldName = this.name;
        Main.getInstance().getTeamHandler().removeTeam(this);
        this.name = newName;

        Main.getInstance().getTeamHandler().setupTeam(this);

        CorePlugin.getInstance().runRedisCommand(redis -> {
            redis.del(Main.DATABASE_NAME + "." + oldName.toLowerCase());
            return null;
        });

        for (Claim claim : this.getClaims()) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        for (Player online : getOnlineMembers()) {
            CorePlugin.getInstance().getNametagEngine().reloadPlayer(online);
        }

        this.flagForSave();
    }

    public void addRaids(final int raid) {
        this.raids = this.raids + raid;
        this.flagForSave();
    }

    public void addPoints(final int addedPoints) {
        this.addedPoints += addedPoints;
        this.recalculatePoints();
        this.flagForSave();

        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_ADDED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", addedPoints
            ));
        }

        getOnlineMembers().forEach(player -> {
            CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
            CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
        });
    }

    public void addKillstreakPoints(final int killstreakPoints) {
        this.killstreakPoints += killstreakPoints;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void addPlaytimePoints(final int playtimePoints) {
        this.playtimePoints += playtimePoints;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void spendPoints(final int points) {
        this.spentPoints += points;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setDTR(double newDTR, Player actor) {

        if (DTR == newDTR) {
            return;
        }

        if (isRaidable() && newDTR > 0.009D && !loading) {
            Bukkit.getPluginManager().callEvent(new TeamUnRaidEvent(this));

            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_NO_LONGER_RAIDABLE, ImmutableMap.of(
                    "date", System.currentTimeMillis()
            ));
        }

        if (!isRaidable() && newDTR <= 0.009D && !loading) {
            Bukkit.getPluginManager().callEvent(new TeamRaidEvent(this));
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of(
                    "date", System.currentTimeMillis()
            ));
        }

        this.DTR = newDTR;

        boolean recentlyset = false;

        if (DTR <= 0.0 && !shouldRegen) {
            setShouldregen(true);
            recentlyset = true;
        }

        if (!Main.getInstance().getMapHandler().isKitMap() && !recentlyset && shouldRegen() && !isRaidable()) {
            regenBlocks();
        }

        for (Player online : getOnlineMembers()) {
            CorePlugin.getInstance().getNametagEngine().reloadPlayer(online);
        }

        this.flagForSave();
    }

    public void recalculatePoints() {
        int basePoints = 0;

        basePoints += this.kills;
        basePoints -= this.deaths;
        basePoints += (this.glowstoneMined / 50);
        basePoints += (this.kothCaptures * 20);
        basePoints += (this.madCaptures * ((Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250)));

        basePoints += this.citadelsCapped * ((Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250));

        basePoints += this.conquestsCapped * (Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250);

        basePoints += this.addedPoints;
        basePoints -= this.removedPoints;
        basePoints -= this.spentPoints;

        this.points = Math.max(basePoints, 0);
    }

    public String[] getPointBreakDown() {
        int basePoints = 0;

        basePoints += this.kills;
        basePoints -= this.deaths;
        basePoints += (glowstoneMined / 50);

        basePoints += (this.kothCaptures * 20);
        basePoints += this.madCaptures * ((Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250));
        basePoints += this.citadelsCapped * ((Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250));
        basePoints += this.conquestsCapped * (Main.getInstance().getMapHandler().isKitMap() ? 2500 : 250);

        basePoints += this.addedPoints;
        basePoints -= this.removedPoints;
        basePoints -= this.spentPoints;

        if (basePoints < 0) {
            basePoints = 0;
        }

        return new String[]{
                "Base Points: " + basePoints,
                "Kills Points: (" + this.kills + " kills) * 1 = " + this.kills,
                "Deaths Points: (" + this.deaths + " deaths) * 1 = " + this.deaths,
                //"EnderManKills Points: (" + this.enderManKills.get() + " endermen) / 300 * 1 = " + (this.enderManKills.get() / 300) * 1,
                "Glowstone Mined Points: (" + this.glowstoneMined + " glowstone) / 200 * 1 = "
                        + (this.glowstoneMined / 50),
                "KOTH Captures Points: (" + this.kothCaptures + " caps) * 30 = " + this.kothCaptures * 30,
                "MAD Captures Points: (" + this.madCaptures + " caps) * 250 or 2500 = " + (
                        Main.getInstance().getMapHandler().isKitMap() ? (this.madCaptures * 2500)
                                : (this.madCaptures * 250)),
                "Citadel Captures Points: (" + this.citadelsCapped + " caps) * 80 = " + (
                        Main.getInstance().getMapHandler().isKitMap() ? (this.citadelsCapped * 2500)
                                : (this.citadelsCapped * 250)),
                "Conquest Captures Points: (" + this.conquestsCapped + " caps) * 60 = " + (
                        Main.getInstance().getMapHandler().isKitMap() ? (this.conquestsCapped * 2500)
                                : (this.conquestsCapped * 250)),
                "killstreak Points: " + this.killstreakPoints,
                "Extra Added Points: " + this.addedPoints,
                "Extra Removed Points: " + this.removedPoints,
                "Spent Points: " + this.spentPoints};
    }

    public void addEnderManKill() {
        this.enderManKills += 1;
        this.flagForSave();
    }

    public void flagForSave() {
        this.needsSave = true;
    }

    public boolean isOwner(final UUID check) {
        return check.equals(this.owner);
    }

    public boolean isMember(final UUID check) {
        return this.members.contains(check);
    }

    public boolean isCaptain(final UUID check) {
        return this.captains.contains(check);
    }

    public boolean isCoLeader(final UUID check) {
        return this.coLeaders.contains(check);
    }

    public void validateAllies() {
        final Iterator<ObjectId> allyIterator = this.getAllies().iterator();
        while (allyIterator.hasNext()) {
            final ObjectId ally = allyIterator.next();
            final Team checkTeam = Main.getInstance().getTeamHandler().getTeam(ally);
            if (checkTeam == null) {
                allyIterator.remove();
            }
        }
    }

    public boolean isAlly(final UUID check) {
        final Team checkTeam = Main.getInstance().getTeamHandler().getTeam(check);
        return checkTeam != null && this.isAlly(checkTeam);
    }

    public boolean isAlly(final Team team) {
        return this.getAllies().contains(team.getUniqueId());
    }

    public boolean ownsLocation(final Location location) {
        return LandBoard.getInstance().getTeam(location) == this;
    }

    public boolean ownsClaim(final Claim claim) {
        return this.claims.contains(claim);
    }

    public boolean removeMember(final UUID member) {
        Player player = Bukkit.getPlayer(member);
        if (player != null && PvPClassHandler.getEquippedKits().containsKey(player.getName())) {

            PvPClassHandler.getEquippedKits().get(player.getName()).removeLimit(this);
        }

        if (player != null) {
            HCFProfile profile = HCFProfile.get(player);

            if (profile != null) {
                profile.setTeam(null);
            }
        }

        this.members.remove(member);
        this.captains.remove(member);
        this.coLeaders.remove(member);

        if (this.isOwner(member)) {
            Iterator<UUID> membersIterator = this.members.iterator();
            this.owner = (membersIterator.hasNext() ? membersIterator.next() : null);
        }

        if (this.DTR > this.getMaxDTR()) {
            this.DTR = this.getMaxDTR();
        }

        if (this.loading) {
            return false;
        }

        TeamTrackerManager.logAsync(this, TeamActionType.MEMBER_REMOVED, ImmutableMap.of(
                "playerId", member.toString(),
                "date", System.currentTimeMillis()
        ));

        this.flagForSave();
        return this.owner == null || this.members.size() == 0;
    }

    public boolean hasDTRBitmask(final DTRBitmask bitmaskType) {
        if (this.getOwner() != null) {
            return false;
        }
        final int dtrInt = (int) this.DTR;
        return (dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask();
    }

    public int getOnlineMemberAmount() {
        return ((int) getMembers().stream().map(Bukkit::getPlayer)
                .filter(exactPlayer -> exactPlayer != null &&
                        !exactPlayer.hasMetadata("invisible") &&
                        !CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(exactPlayer))
                .count());
    }

    public Collection<Player> getOnlineMembers() {
        return this.getMembers().stream()
                .map(member -> Main.getInstance().getServer().getPlayer(member))
                .filter(exactPlayer -> exactPlayer != null &&
                        !exactPlayer.hasMetadata("invisible") &&
                        !exactPlayer.hasMetadata("deathban") &&
                        !SpectatorListener.spectators.contains(exactPlayer.getUniqueId()))
                .collect(Collectors.toList());
    }

    public LinkedList<Player> getOrderOnlineList() {
        LinkedList<Player> players = new LinkedList<>();
        if (Bukkit.getPlayer(getOwner()) != null) {
            players.add(Bukkit.getPlayer(getOwner()));
        }

        for (UUID coLeader : getCoLeaders()) {
            if (Bukkit.getPlayer(coLeader) != null && !captains.contains(coLeader)
                    && coLeader != getOwner()) {
                players.add(Bukkit.getPlayer(coLeader));
            }
        }

        for (UUID captain : getCaptains()) {
            if (Bukkit.getPlayer(captain) != null && !coLeaders.contains(captain)
                    && captain != getOwner()) {
                players.add(Bukkit.getPlayer(captain));
            }
        }

        for (UUID member : getMembers()) {
            if (Bukkit.getPlayer(member) != null && !coLeaders.contains(member)
                    && !captains.contains(member) && member != getOwner()) {
                players.add(Bukkit.getPlayer(member));
            }
        }

        return players;
    }

    public Collection<UUID> getOfflineMembers() {
        final List<UUID> players = new ArrayList<>();
        for (final UUID member : this.getMembers()) {
            final Player exactPlayer = Main.getInstance().getServer().getPlayer(member);
            if (exactPlayer == null || exactPlayer.hasMetadata("invisible") || exactPlayer.hasMetadata(
                    "deathban") || SpectatorListener.spectators.contains(exactPlayer.getUniqueId())) {
                players.add(member);
            }
        }
        return players;
    }

    public int getSize() {
        return this.getMembers().size();
    }

    public boolean isRaidable() {
        return DTR <= 0.009D;
    }

    public void playerDeath(String playerName, UUID uuid, Player killer, double dtrLoss) {

        if (Main.getInstance().getFactionDuelManager().isInMatch(this)) {
            return;
        }

        if (Main.getInstance().getConquestHandler().getGame() != null) {
            if (ConquestGame.teamPoints.containsKey(this.getUniqueId())) {

                ConquestGame.teamPoints.put(this.getUniqueId(),
                        FastMath.max(0, ConquestGame.teamPoints.get(this.getUniqueId()) - ConquestHandler.POINTS_DEATH_PENALTY));

                ConquestGame.teamPoints = ConquestGame.sortByValues(ConquestGame.teamPoints);

                this.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " Your team has lost "
                        + ConquestHandler.POINTS_DEATH_PENALTY + " points because of " + playerName
                        + "'s death!" + ChatColor.AQUA + " (" + ConquestGame.teamPoints.get(this.getUniqueId())
                        + "/" + ConquestHandler.getPointsToWin() + ")");
            }
        }

        double oldDTR = this.DTR;

        double newDTR = FastMath.max(this.DTR - dtrLoss, -0.99);

        TeamTrackerManager.logAsync(this, TeamActionType.MEMBER_DEATH, ImmutableMap.of(
                "playerId", uuid.toString(),
                "dtrLoss", dtrLoss,
                "oldDtr", DTR,
                "newDtr", newDTR,
                "date", System.currentTimeMillis()
        ));

        DTR = newDTR;

        if (isRaidable()) {

            DTR = -0.99;

            if (oldDTR > 0.009D) {

                Bukkit.getPluginManager().callEvent(new TeamRaidEvent(this));

                if (killer != null) {

                    Bukkit.getPluginManager().callEvent(new PlayerRaidTeamEvent(killer, this));

                    TeamTrackerManager.logAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of(
                            "date", System.currentTimeMillis(),
                            "raideableById", killer.getUniqueId().toString()
                    ));

                } else {
                    TeamTrackerManager.logAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of(
                            "date", System.currentTimeMillis()
                    ));
                }
            }

        }

        setDeaths(getDeaths() + 1);

        sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + playerName);
        sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + Team.DTR_FORMAT.format(newDTR));

        if (!Main.getInstance().getServerHandler().isPreEOTW() && !Main.getInstance().getServerHandler()
                .isEOTW()) {

            this.DTRCooldown = Main.getInstance().getEventHandler().getEclipseEvent().isActive() ?
                    System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10) :
                    System.currentTimeMillis() + Main.getInstance().getMapHandler().getRegenTimeDeath();

            DTRHandler.markOnDTRCooldown(this);
        }

        flagForSave();
    }

    public double getDTRIncrement() {
        return this.getDTRIncrement(this.getOnlineMemberAmount());
    }

    public double getDTRIncrement(final int playersOnline) {
        final double dtrPerHour =
                DTRHandler.getBaseDTRIncrement(this.getSize()) * playersOnline;
        return dtrPerHour / 60.0;
    }

    public double getMaxDTR() {
        return DTRHandler.getMaxDTR(this.getSize());
    }

    public void load(final String str) {
        this.load(str, false);
    }

    public void load(final String str, final boolean forceSave) {
        this.loading = true;

        final String[] lines = str.split("\n");

        for (final String line : lines) {
            if (line.indexOf(58) != -1) {
                final String identifier = line.substring(0, line.indexOf(58));
                final String[] lineParts = line.substring(line.indexOf(58) + 1).split(",");
                if (identifier.equalsIgnoreCase("Owner")) {
                    if (!lineParts[0].equals("null")) {
                        this.setOwner(UUID.fromString(lineParts[0].trim()));
                    }
                } else if (identifier.equalsIgnoreCase("UUID")) {
                    this.uniqueId = new ObjectId(lineParts[0].trim());
                } else if (identifier.equalsIgnoreCase("Members")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            this.addMember(UUID.fromString(name.trim()));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("blockToRemove")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            this.addBlockAddInRaid(LocationUtil.convertLocation(name.trim()));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("blocksToAdd")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            String asd = name.trim().replace(" ", "");
                            String location = asd.substring(0, asd.indexOf("|"));

                            String[] matsplit = asd.substring(asd.indexOf("|")).replace("|", "").split(";");
                            Material material = Material.getMaterial(Integer.parseInt(matsplit[0]));
                            byte data = Byte.parseByte(matsplit[1]);
                            this.addRemoveBlockInRaid(LocationUtil.convertLocation(location), material, data);
                        }
                    }
                } else if (identifier.equalsIgnoreCase("CoLeaders")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            this.addCoLeader(UUID.fromString(name.trim()));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("Captains")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            this.addCaptain(UUID.fromString(name.trim()));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("Invited")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            this.getInvitations().add(UUID.fromString(name.trim()));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
                    for (final String name : lineParts) {
                        if (name.length() >= 2) {
                            this.getHistoricalMembers().add(UUID.fromString(name.trim()));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("notes")) {
                    for (String note : line.substring(line.indexOf(58) + 1).split("\\@")) {
                        String[] noteData = note.split("\\|");

                        if (noteData.length == 3) {
                            Date issuedOn = null;

                            try {
                                issuedOn = DATE_FORMAT.parse(noteData[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            this.notes.add(new TeamNote(issuedOn, noteData[1], noteData[2]));
                        }
                    }
                } else if (identifier.equalsIgnoreCase("HQ")) {
                    this.setHQ(this.parseLocation(lineParts));
                } else if (identifier.equalsIgnoreCase("DTR")) {
                    this.setDTR(Double.parseDouble(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Balance")) {
                    this.setBalance(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
                    this.setDTRCooldown(Long.parseLong(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("FriendlyName")) {
                    this.setName(lineParts[0]);
                } else if (identifier.equalsIgnoreCase("Claims")) {
                    for (String claim : lineParts) {
                        claim = claim.replace("[", "").replace("]", "");
                        if (claim.contains(":")) {
                            final String[] split = claim.split(":");
                            final int x1 = Integer.parseInt(split[0].trim());
                            final int y1 = Integer.parseInt(split[1].trim());
                            final int z1 = Integer.parseInt(split[2].trim());
                            final int x2 = Integer.parseInt(split[3].trim());
                            final int y2 = Integer.parseInt(split[4].trim());
                            final int z2 = Integer.parseInt(split[5].trim());
                            final String name2 = split[6].trim();
                            final String world = split[7].trim();
                            final Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
                            claimObj.setName(name2);
                            this.getClaims().add(claimObj);
                        }
                    }
                } else if (identifier.equalsIgnoreCase("Allies")) {
                    if (Main.getInstance().getMapHandler().getAllyLimit() != 0) {
                        for (String ally : lineParts) {
                            ally = ally.replace("[", "").replace("]", "");
                            if (ally.length() != 0) {
                                this.allies.add(new ObjectId(ally.trim()));
                            }
                        }
                    }
                } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
                    if (Main.getInstance().getMapHandler().getAllyLimit() != 0) {
                        for (String requestedAlly : lineParts) {
                            requestedAlly = requestedAlly.replace("[", "").replace("]", "");
                            if (requestedAlly.length() != 0) {
                                this.requestedAllies.add(new ObjectId(requestedAlly.trim()));
                            }
                        }
                    }
                } else if (identifier.equalsIgnoreCase("Announcement")) {
                    this.setAnnouncement(lineParts[0]);
                } else if (identifier.equalsIgnoreCase("FriendlyFire")) {
                    this.setFriendlyFire(Boolean.parseBoolean(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("dtrregenfaster")) {
                    setDtrRegenFaster(Boolean.parseBoolean(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("createAt")) {
                    setCreateAt(Long.parseLong(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("dtrRegenFasterEndAt")) {
                    setDtrRegenFasterEndAt(Long.parseLong(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("activeeffects")) {

                    if (lineParts.length > 1) {
                        for (String effect : lineParts) {
                            String[] effectParts = effect.split(";");
                            if (effectParts.length > 1) {
                                PotionEffectType type = PotionEffectType.getByName(effectParts[0]);
                                if (type != null) {
                                    int level = Integer.parseInt(effectParts[1]);
                                    activeEffects.put(type, level);
                                }
                            }
                        }
                    }

                } else if (identifier.equalsIgnoreCase("ShouldRegen")) {
                    this.setShouldregen(Boolean.parseBoolean(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("claimslocked")) {
                    this.setClaimsLocked(Boolean.parseBoolean(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("disqualified")) {
                    this.setDisqualified(Boolean.parseBoolean(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("open")) {
                    this.setOpen(Boolean.parseBoolean(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Focus")) {
                    this.setFocus(String.valueOf(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Lives")) {
                    this.setLives(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Kills")) {
                    this.setKills(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Deaths")) {
                    this.setDeaths(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("KothCaptures")) {
                    this.setKothCaptures(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("madCaptures")) {
                    this.setMadCaptures(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("ConquestCaptures")) {
                    this.setConquestsCapped(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("PointsAdded")) {
                    this.setAddedPoints(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Raids")) {
                    this.setRaids(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("RemovedPoints")) {
                    this.setRemovedPoints(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("DiamondsMined")) {
                    this.setDiamondsMined(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("GlowstoneMined")) {
                    this.setGlowstoneMined(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("CitadelsCapped")) {
                    this.setCitadelsCapped(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("KillstreakPoints")) {
                    this.setKillstreakPoints(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("PlaytimePoints")) {
                    this.setPlaytimePoints(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Points")) {
                    this.setPoints(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("SpentPoints")) {
                    this.setSpentPoints(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Hoppers")) {
                    this.setHoppers(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("Subclaims")) {
                    this.setSubclaims(Integer.parseInt(lineParts[0]));
                } else if (identifier.equalsIgnoreCase("EnderManKills")) {
                    this.setEnderManKills(Integer.parseInt(lineParts[0]));
                }else if (identifier.equalsIgnoreCase("useBase")){
                    this.setUseBase(Boolean.parseBoolean(lineParts[0]));
                }
            }
        }
        if (this.uniqueId == null) {
            this.uniqueId = new ObjectId();
        }
        this.loading = false;
        this.needsSave = forceSave;
    }

    public String saveString(final boolean toJedis) {
        if (toJedis) {
            this.needsSave = false;
        }

        if (this.loading) {
            return null;
        }

        final StringBuilder teamString = new StringBuilder();
        final StringBuilder members = new StringBuilder();
        final StringBuilder raidBlocks = new StringBuilder();
        final StringBuilder regenBlocks = new StringBuilder();
        final StringBuilder blocksToRemove = new StringBuilder();
        final StringBuilder blocksToAdd = new StringBuilder();
        final StringBuilder captains = new StringBuilder();
        final StringBuilder coLeaders = new StringBuilder();
        final StringBuilder invites = new StringBuilder();
        final StringBuilder historicalMembers = new StringBuilder();
        final StringBuilder notes = new StringBuilder();

        for (final UUID member : this.getMembers()) {
            members.append(member.toString()).append(", ");
        }

        if (!this.getBlocksAddedInRaid().isEmpty()) {
            for (Location location : this.getBlocksAddedInRaid()) {
                blocksToRemove.append(LocationUtil.parseLocation(location)).append(", ");
            }
        }

        if (!this.getBlocksRemovedInRaid().isEmpty()) {
            for (Map.Entry<Location, String> next : getBlocksRemovedInRaid().entrySet()) {
                blocksToAdd.append(LocationUtil.parseLocation(next.getKey()))
                        .append("|")
                        .append(next.getValue())
                        .append(", ");
            }
        }

        final StringBuilder passiveEffectsString = new StringBuilder();

        if (!activeEffects.isEmpty()) {
            activeEffects.forEach((effect, level) -> {
                if (effect != null) {
                    passiveEffectsString.append(effect.getName()).append(";").append(level)
                            .append(", ");
                }
            });
        }

        for (UUID captain : this.getCaptains()) {
            captains.append(captain.toString()).append(", ");
        }

        for (UUID co : this.getCoLeaders()) {
            coLeaders.append(co.toString()).append(", ");
        }

        for (UUID invite : this.getInvitations()) {
            invites.append(invite.toString()).append(", ");
        }

        for (UUID member : this.getHistoricalMembers()) {
            historicalMembers.append(member.toString()).append(", ");
        }

        // Notes are separated by '@' and their stuff by '|'
        for (TeamNote note : this.notes) {
            notes.append(DATE_FORMAT.format(note.getIssuedOn())).append("|")
                    .append(note.getStaff()).append("|")
                    .append(note.getReason()).append("@");
        }

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

        if (raidBlocks.length() > 2) {
            raidBlocks.setLength(raidBlocks.length() - 2);
        }

        if (regenBlocks.length() > 2) {
            regenBlocks.setLength(regenBlocks.length() - 2);
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

        if (historicalMembers.length() > 2) {
            historicalMembers.setLength(historicalMembers.length() - 2);
        }

        if (notes.length() > 1) {
            notes.setLength(notes.length() - 1);
        }

        teamString.append("UUID:").append(this.getUniqueId().toString()).append("\n");
        teamString.append("Owner:").append(this.getOwner()).append('\n');
        teamString.append("CoLeaders:").append(coLeaders).append('\n');
        teamString.append("Captains:").append(captains).append('\n');
        teamString.append("Members:").append(members).append('\n');
        teamString.append("RaidBlocks:").append(raidBlocks).append('\n');
        teamString.append("RegenBlocks:").append(regenBlocks).append('\n');
        teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
        teamString.append("Claims:").append(this.getClaims().toString().replace("\n", "")).append('\n');
        teamString.append("Allies:").append(this.getAllies().toString()).append('\n');
        teamString.append("RequestedAllies:").append(this.getRequestedAllies().toString()).append('\n');
        teamString.append("HistoricalMembers:").append(historicalMembers).append('\n');
        teamString.append("DTR:").append(this.getDTR()).append('\n');
        teamString.append("Balance:").append(this.getBalance()).append('\n');
        teamString.append("DTRCooldown:").append(this.getDTRCooldown()).append('\n');
        teamString.append("FriendlyName:").append(this.getName().replace("\n", "")).append('\n');
        teamString.append("Announcement:")
                .append(String.valueOf(this.getAnnouncement()).replace("\n", "")).append("\n");
        teamString.append("FriendlyFire:").append(this.isFriendlyFire()).append("\n");
        teamString.append("ShouldRegen:").append(this.shouldRegen()).append("\n");
        teamString.append("claimslocked:").append(this.isClaimsLocked()).append("\n");
        teamString.append("disqualified:").append(this.isDisqualified()).append("\n");
        teamString.append("open:").append(this.isOpen()).append("\n");
        teamString.append("Focus:").append(this.getFocus()).append("\n");
        teamString.append("Lives:").append(this.getLives()).append("\n");
        teamString.append("Kills:").append(this.getKills()).append("\n");
        teamString.append("Deaths:").append(this.getDeaths()).append("\n");
        teamString.append("useBase:").append(this.isUseBase()).append("\n");
        teamString.append("DiamondsMined:").append(this.getDiamondsMined()).append("\n");
        teamString.append("GlowstoneMined:").append(this.getGlowstoneMined()).append("\n");
        teamString.append("KothCaptures:").append(this.getKothCaptures()).append("\n");
        teamString.append("madCaptures:").append(this.getMadCaptures()).append("\n");
        teamString.append("ConquestCaptures:").append(this.getConquestsCapped()).append("\n");
        teamString.append("PointsAdded:").append(this.getAddedPoints()).append("\n");
        teamString.append("Raids:").append(this.getRaids()).append("\n");
        teamString.append("RemovedPoints:").append(this.getRemovedPoints()).append("\n");
        teamString.append("CitadelsCapped:").append(this.getCitadelsCapped()).append("\n");
        teamString.append("KillstreakPoints:").append(this.getKillstreakPoints()).append("\n");
        teamString.append("PlaytimePoints:").append(this.getPlaytimePoints()).append("\n");
        teamString.append("Points:").append(this.getPoints()).append("\n");
        teamString.append("SpentPoints:").append(this.getSpentPoints()).append("\n");
        teamString.append("Hoppers:").append(this.getHoppers()).append("\n");
        teamString.append("Subclaims:").append(this.getSubclaims()).append("\n");
        teamString.append("EnderManKills:").append(this.getEnderManKills()).append("\n");
        teamString.append("activeeffects:").append(passiveEffectsString).append("\n");
        teamString.append("createAt:").append(createAt).append("\n");
        teamString.append("dtrregenfaster:").append(dtrRegenFaster).append("\n");
        teamString.append("dtrRegenFasterEndAt:").append(dtrRegenFasterEndAt).append("\n");
        teamString.append("notes:").append(notes).append("\n");

        if (this.getHQ() != null) {
            teamString.append("HQ:").append(this.getHQ().getWorld().getName()).append(",")
                    .append(this.getHQ().getX()).append(",").append(this.getHQ().getY()).append(",")
                    .append(this.getHQ().getZ()).append(",").append(this.getHQ().getYaw()).append(",")
                    .append(this.getHQ().getPitch()).append('\n');
        }

        return teamString.toString();
    }

    private Location parseLocation(final String[] args) {
        if (args.length != 6) {
            return null;
        }

        World world = Main.getInstance().getServer().getWorld(args[0]);
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        float yaw = Float.parseFloat(args[4]);
        float pitch = Float.parseFloat(args[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void sendMessage(final String message) {
        sendMessage(message, null);
    }

    public void sendMessage(final String message, Sound sound) {
        for (Player online : getOnlineMembers()) {
            online.sendMessage(CC.translate(message));
            if (sound != null) {
                online.playSound(online.getLocation(), sound, 1, 1);
            }
        }
    }

    public void sendMessage(final FormatingMessage message) {
        for (Player online : getOnlineMembers()) {
            message.send(online);
        }
    }

    public void sendAllyMessage(final String message) {

        if (getAllies().isEmpty() || getAllies() == null) {
            return;
        }

        for (ObjectId ally : getAllies()) {
            for (Player online : Main.getInstance().getTeamHandler().getTeam(ally).getOnlineMembers()) {
                online.sendMessage(message);
            }
        }
    }

    public void sendAllyMessage(final FormatingMessage message) {
        if (getAllies().isEmpty() || getAllies() == null) {
            return;
        }
        for (ObjectId ally : getAllies()) {
            for (Player online : Main.getInstance().getTeamHandler().getTeam(ally).getOnlineMembers()) {
                message.send(online);
            }
        }
    }

    public void sendTeamInfo(final Player player) {

        if (getOwner() == null) {

            player.sendMessage(GRAY_LINE);

            FormatingMessage teamLine = new FormatingMessage();
            teamLine.text(getName(player));
            teamLine.send(player);

            if (this.HQ != null && this.HQ.getWorld().getEnvironment() != World.Environment.NORMAL) {
                String world =
                        (this.HQ.getWorld().getEnvironment() == World.Environment.NETHER) ? "Nether" : "End";

                player.sendMessage(
                        ChatColor.YELLOW + "Location: " + ChatColor.RED + ((this.HQ == null) ? "None"
                                : (this.HQ.getBlockX() + ", " + this.HQ.getBlockZ() + " (" + world + ")")));

                if (this.name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {

                    GlowHandler glowHandler = Main.getInstance().getGlowHandler();
                    GlowMountain glowMountain = glowHandler.getGlowMountain();

                    player.sendMessage(ChatColor.YELLOW + "Blocks Remaining: " +
                            ChatColor.RED + glowMountain.getRemaining());
                    player.sendMessage(ChatColor.YELLOW + "Resets in: " +
                            ChatColor.RED + GlowHandler.getGlowRespawnTask().getNextRespawnString());

                } else if (this.name.equals(OreMountainHandler.getOreTeamName())
                        && this.getMembers().isEmpty()) {

                    OreMountainHandler oreHandler = Main.getInstance().getOreHandler();
                    OreMountain oreMountain = oreHandler.getOreMountain();

                    player.sendMessage(
                            ChatColor.YELLOW + "Blocks Remaining: " + ChatColor.RED + oreMountain.getRemaining());
                    player.sendMessage(ChatColor.YELLOW + "Resets in: " + ChatColor.RED
                            + OreMountainHandler.getOreRespawnTask().getNextRespawnString());
                }
            } else {

                player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.RED +
                        ((this.HQ == null) ? "None" : (this.HQ.getBlockX() + ", " + this.HQ.getBlockZ())));

                if (this.getName().equalsIgnoreCase("EndPortal")) {
                    player.sendMessage(ChatColor.YELLOW + "In each quadrant");
                }

            }
            if (this.getName().equalsIgnoreCase("Citadel")) {

                Set<ObjectId> cappers = Main.getInstance().getCitadelHandler().getCappers();
                Set<String> capperNames = new HashSet<>();

                for (ObjectId capper : cappers) {
                    Team capperTeam = Main.getInstance().getTeamHandler().getTeam(capper);
                    if (capperTeam != null) {
                        capperNames.add(capperTeam.getName());
                    }
                }

                if (!cappers.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "Currently captured by: " +
                            ChatColor.RED + Joiner.on(", ").join(capperNames));
                }
            }

            player.sendMessage(CC.CHAT_SEPARATOR);
            return;
        }

        Player owner = Main.getInstance().getServer().getPlayer(this.getOwner());

        StringBuilder allies = new StringBuilder();

        FormatingMessage coLeadersJson = new FormatingMessage(ChatColor.YELLOW + "Co-Leaders: ")
                .color(ChatColor.YELLOW);

        FormatingMessage captainsJson = new FormatingMessage(ChatColor.YELLOW + "Captains: ")
                .color(ChatColor.YELLOW);

        if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank()
                .isAboveOrEqual(Rank.SENIORMOD)) {

            captainsJson.command("/manageteam demote " + this.getName())
                    .tooltip("Click to demote captains").color(ChatColor.AQUA);
        }

        FormatingMessage membersJson = new FormatingMessage(ChatColor.YELLOW + "Members: ").color(ChatColor.YELLOW);

        if (Profile.getByUuidIfAvailable(player.getUniqueId())
                .getRank().isAboveOrEqual(Rank.SENIORMOD)) {

            membersJson.command("/manageteam promote " + this.getName())
                    .tooltip("Click to promote members").color(ChatColor.AQUA);
        }

        int onlineMembers = 0;

        for (final ObjectId allyId : this.getAllies()) {

            Team ally = Main.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                allies.append(ally.getName(player)).append(ChatColor.YELLOW)
                        .append("[").append(ChatColor.GREEN)
                        .append(ally.getOnlineMemberAmount())
                        .append("/")
                        .append(ally.getSize()).append(ChatColor.YELLOW)
                        .append("]")
                        .append(ChatColor.GRAY)
                        .append(", ");
            }
        }

        for (final Player onlineMember : this.getOnlineMembers()) {

            ++onlineMembers;

            if (this.isOwner(onlineMember.getUniqueId())) {
                continue;
            }

            FormatingMessage appendTo = membersJson;

            if (this.isCoLeader(onlineMember.getUniqueId())) {
                appendTo = coLeadersJson;
            } else if (this.isCaptain(onlineMember.getUniqueId())) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            HCFProfile hcfProfile = HCFProfile.getByUUID(onlineMember.getUniqueId());

            appendTo.then(onlineMember.getName()).color(ChatColor.GREEN)
                    .then("[").color(ChatColor.YELLOW)
                    .then(hcfProfile.getKills() + "").color(ChatColor.GREEN)
                    .then("]").color(ChatColor.YELLOW);
        }

        for (UUID offlineMember : getOfflineMembers()) {

            if (isOwner(offlineMember)) {
                continue;
            }

            FormatingMessage appendTo = membersJson;

            if (isCoLeader(offlineMember)) {
                appendTo = coLeadersJson;
            } else if (isCaptain(offlineMember)) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(UUIDUtils.name(offlineMember))
                    //.color(hcfProfile.isDeathBanned() ? ChatColor.RED : ChatColor.GRAY)
                    .color(ChatColor.GRAY)
                    .then("[").color(ChatColor.YELLOW)
                    .then(String.valueOf(Main.getInstance().getMapHandler().getStatsHandler().getStats(offlineMember).getKills())).color(ChatColor.GREEN)
                    .then("]").color(ChatColor.YELLOW);
        }

        player.sendMessage(CC.CHAT_SEPARATOR);

        FormatingMessage teamLine = new FormatingMessage();

        teamLine.text(ChatColor.GOLD + this.getName());

        teamLine.then().text(
                ChatColor.GRAY + " [" +
                        onlineMembers + "/" + this.getSize() +
                        "]" +
                        ChatColor.DARK_AQUA + " - ");

        teamLine.then().text(ChatColor.YELLOW + "HQ: " + ChatColor.WHITE +
                ((this.HQ == null) ? "None" : (this.HQ.getBlockX() + ", " + this.HQ.getBlockZ())));

        if (player.isOp()) {

            if (HQ != null) {

                teamLine.command(
                        "/tppos " + this.HQ.getBlockX() + " " +
                                this.HQ.getBlockY() + " " +
                                this.HQ.getBlockZ() + " " +
                                this.HQ.getWorld().getName());

                teamLine.tooltip("§aClick to warp to HQ");
            }

            teamLine.then().text("§3 - §e[Manage]")
                    .color(ChatColor.YELLOW)
                    .command("/manageteam manage " + this.getName())
                    .tooltip("§bClick to manage team");
        }

        if (!isMember(player.getUniqueId())) {
            teamLine.then().text(" - ").color(ChatColor.GRAY);
            teamLine.then().text(
                            CC.translate("&7[&6Focus&7]"))
                    .tooltip("Click to focus this faction").color(ChatColor.GREEN)
                    .command("/t focus " + getName());
        }

        teamLine.send(player);

        if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Allies: " + allies);
        }

        FormatingMessage leader = new FormatingMessage(ChatColor.YELLOW + "Leader: " +
                ((owner == null ||
                        owner.hasMetadata("invisible") ||
                        owner.hasMetadata("deathban") ||
                        SpectatorListener.spectators.contains(owner.getUniqueId())) ?
                        //(profileOwner.isDeathBanned() ? ChatColor.RED : ChatColor.GRAY)
                        (ChatColor.GRAY) : ChatColor.GREEN)
                + UUIDUtils.name(this.getOwner()) + ChatColor.YELLOW + "[" +
                ChatColor.GREEN + Main.getInstance().getMapHandler().getStatsHandler().getStats(this.getOwner()).getKills() + ChatColor.YELLOW
                + "]");

        if (player.isOp()) {
            leader.command("/manageteam leader " + this.getName()).tooltip("§bClick to change leader");
        }

        leader.send(player);

        if (!ChatColor.stripColor(coLeadersJson.toOldMessageFormat()).endsWith("s: ")) {
            coLeadersJson.send(player);
        }

        if (!ChatColor.stripColor(captainsJson.toOldMessageFormat()).endsWith("s: ")) {
            captainsJson.send(player);
        }

        if (!ChatColor.stripColor(membersJson.toOldMessageFormat()).endsWith("s: ")) {
            membersJson.send(player);
        }

        FormatingMessage balance = new FormatingMessage(ChatColor.YELLOW + "Balance: " +
                ChatColor.GOLD + "$" + FastMath.round(getBalance()));

        if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank()
                .isAboveOrEqual(Rank.SENIORMOD)) {

            balance.command("/manageteam balance " + this.getName())
                    .tooltip("§bClick to modify team balance");
        }

        balance.send(player);

        FormatingMessage dtrMessage = new FormatingMessage(
                ChatColor.YELLOW + "Deaths Until Raidable: " + this.getDTRString());

        if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank()
                .isAboveOrEqual(Rank.SENIORMOD)) {
            dtrMessage.command("/manageteam dtr " + this.getName()).tooltip("§bClick to modify team DTR");
        }

        dtrMessage.send(player);

        if (player.getGameMode() == GameMode.CREATIVE && Profile.getByUuidIfAvailable(
                player.getUniqueId()).getRank().isAboveOrEqual(Rank.SENIORMOD)) {
            player.sendMessage(
                    ChatColor.YELLOW + "Claims Locked: " + ChatColor.RED + this.isClaimsLocked());
            player.sendMessage(
                    ChatColor.YELLOW + "Disqualified: " + ChatColor.RED + this.isDisqualified());
            player.sendMessage(ChatColor.YELLOW + "Open: " + ChatColor.RED + this.isOpen());
        }

        player.sendMessage(ChatColor.YELLOW + "Points: " + ChatColor.RED + this.getPoints());

        if (isMember(player.getUniqueId()) || Profile.getByUuidIfAvailable(player.getUniqueId())
                .getRank().isAboveOrEqual(Rank.SENIORMOD)) {

            if (!Main.getInstance().getMapHandler().isKitMap()) {
                FormatingMessage lives = new FormatingMessage(
                        ChatColor.YELLOW + "Lives: " + ChatColor.RED + "❤" + getLives());
                if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank()
                        .isAboveOrEqual(Rank.SENIORMOD)) {
                    lives.command("/manageteam lives " + this.getName())
                            .tooltip("§bClick to modify team lives");
                }
                lives.send(player);
            }

            player.sendMessage(
                    ChatColor.YELLOW + "Friendly Fire: " + ChatColor.RED + this.isFriendlyFire());

            player.sendMessage(
                    ChatColor.YELLOW + "KOTH Captures: " + ChatColor.RED + this.getKothCaptures());
        }
        if (DTRHandler.isOnCooldown(this)) {
            int timeLeft = (int) (this.getDTRCooldown() - System.currentTimeMillis()) / 1000;

            if (!Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().isAboveOrEqual(Rank.SENIORMOD)) {

                player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.GOLD
                        + TimeUtils.formatIntoDetailedString(timeLeft).trim());

            } else {
                FormatingMessage message = new FormatingMessage(ChatColor.YELLOW + "Time Until Regen: ")
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer")
                        .command("/startdtrregen " + this.getName());

                message.then(TimeUtils.formatIntoDetailedString(timeLeft))
                        .color(ChatColor.GOLD).tooltip(ChatColor.GREEN + "Click to remove regeneration timer")
                        .command("/startdtrregen " + this.getName());

                message.send(player);
            }
        }

        if (isMember(player.getUniqueId()) && announcement != null && !announcement.equals("null")) {
            player.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.GOLD + announcement);
        }

        player.sendMessage(CC.CHAT_SEPARATOR);
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Team)) {
            return false;
        }
        final Team other = (Team) obj;
        return other.uniqueId.equals(this.uniqueId);
    }

    public ChatColor getDTRColor() {
        ChatColor dtrColor = ChatColor.GREEN;

        if (DTR / getMaxDTR() <= 0.25) {
            if (isRaidable()) {
                dtrColor = ChatColor.DARK_RED;
            } else {
                dtrColor = ChatColor.YELLOW;
            }
        }

        return (dtrColor);
    }

    public String getDTRSuffix() {
        if (DTRHandler.isRegenerating(this)) {
            return ChatColor.GREEN + "▲";
        } else {
            if (DTRHandler.isOnCooldown(this) || Main.getInstance().getServerHandler().isEOTW()) {
                return ChatColor.RED + "■";
            }
            return ChatColor.GREEN + "◀";
        }
    }

    public void setName(final String name) {
        this.name = name;
        this.flagForSave();
    }

    public void setHQ(Location hq) {
        String oldHQ = this.HQ == null ? "None" : (getHQ().getBlockX() + ", " + getHQ().getBlockY() + ", " + getHQ().getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        this.HQ = hq;

        if (this.loading) return;
        TeamTrackerManager.logAsync(this, TeamActionType.HEADQUARTERS_CHANGED, ImmutableMap.of(
                "oldHq", oldHQ,
                "newHq", newHQ,
                "date", System.currentTimeMillis()
        ));

        saveIfNotLoading();
    }

    public void setBalance(final int balance) {
        this.balance = balance;
        this.flagForSave();
    }

    public void setDTR(double newDTR) {
        setDTR(newDTR, null);
    }

    public String getDTRString() {
        return getDTRColor() + Team.DTR_FORMAT.format(getDTR()) + getDTRSuffix();
    }

    public void setDTRCooldown(final long dtrCooldown) {
        this.DTRCooldown = dtrCooldown;
        this.flagForSave();
    }

    public void setOwner(final UUID owner) {
        this.owner = owner;

        if (owner != null) {
            this.members.add(owner);
            this.coLeaders.remove(owner);
            this.captains.remove(owner);
        }

        if (this.loading) return;

        if (owner != null) {

            TeamTrackerManager.logAsync(this, TeamActionType.LEADER_CHANGED, ImmutableMap.of(
                    "playerId", owner.toString(),
                    "date", System.currentTimeMillis()
            ));

        }

        saveIfNotLoading();
    }

    public void setAnnouncement(final String announcement) {
        this.announcement = announcement;

        if (this.loading) return;
        TeamTrackerManager.logAsync(this, TeamActionType.ANNOUNCEMENT_CHANGED, ImmutableMap.of(
                "newAnnouncement", announcement,
                "date", System.currentTimeMillis()
        ));

        saveIfNotLoading();
    }

    public void setDisqualified(final boolean disqualified) {
        this.disqualified = disqualified;
        this.flagForSave();
    }

    public void setOpen(final boolean open) {
        this.open = open;
        this.flagForSave();
    }

    public void setFocus(final String team) {
        this.focus = team;
        this.flagForSave();
    }

    public void setLives(final int lives) {
        this.lives = lives;
        this.flagForSave();
    }

    public void setPoints(int points) {
        this.points = FastMath.max(points, 0);

        new TeamPointsChangeEvent(this).call();

        recalculatePoints();
        this.flagForSave();

        getOnlineMembers().forEach(player -> {
            CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
            CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
        });
    }

    public void setKills(int kills) {
        this.kills = kills;

        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_ADDED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", 1
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void setKothCaptures(int kothCaptures) {
        this.kothCaptures = kothCaptures;
        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_ADDED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", 30
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void setMadCaptures(int madCaptures) {
        this.madCaptures = madCaptures;
        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_ADDED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", 30
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void setConquestsCapped(int conquestsCapped) {
        this.conquestsCapped = conquestsCapped;

        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_ADDED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", (Main.getInstance().getMapHandler().isKitMap() ? 500 : 250)
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void setDiamondsMined(int diamondsMined) {
        this.diamondsMined = diamondsMined;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setGlowstoneMined(int glowstoneMined) {
        this.glowstoneMined = glowstoneMined;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;

        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_REMOVED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", 1
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void setCitadelsCapped(int citadels) {
        this.citadelsCapped = citadels;

        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_ADDED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", (Main.getInstance().getMapHandler().isKitMap() ? 500 : 100)
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void setKillstreakPoints(int killstreakPoints) {
        this.killstreakPoints = killstreakPoints;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setPlaytimePoints(int playtimePoints) {
        this.playtimePoints = playtimePoints;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setAddedPoints(int addedPoints) {
        this.addedPoints = addedPoints;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setRaids(int raid) {
        this.raids = raid;
        this.flagForSave();
    }

    public void setRemovedPoints(int removedPoints) {
        this.removedPoints = removedPoints;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setSpentPoints(final int points) {
        this.spentPoints = points;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setEnderManKills(final int kills) {
        this.enderManKills = kills;
        this.flagForSave();
    }

    public void setHoppers(final int hoppers) {
        this.hoppers = hoppers;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void addHopper() {
        ++hoppers;
        saveIfNotLoading();
    }

    public void removeHopper() {
        --hoppers;
        saveIfNotLoading();
    }

    public void setSubclaims(final int subclaims) {
        this.subclaims = subclaims;
        this.recalculatePoints();
        this.flagForSave();
    }

    public void removePoints(int points) {
        this.removedPoints += points;

        if (!loading) {
            TeamTrackerManager.logAsync(this, TeamActionType.TEAM_POINTS_REMOVED, ImmutableMap.of(
                    "date", System.currentTimeMillis(),
                    "points", removedPoints
            ));
        }

        this.recalculatePoints();
        this.flagForSave();
    }

    public void upgradeEffect(PotionEffectType effect) {
        int currentLevel = this.getEffectLevel(effect);

        activeEffects.put(effect, currentLevel + 1);

        giveEffectsToAllInClaim();

        flagForSave();
    }

    public void giveEffectsToAllInClaim() {
        activeEffects.forEach((effect, level) -> {
            for (Player player : getOnlineMembers()) {

                Team team = LandBoard.getInstance().getTeam(player.getLocation());

                if (team != this) {
                    continue;
                }

                if (player.hasPotionEffect(effect) && PotionUtil.getPotionEffectLevel(player, effect) < level) {
                    player.removePotionEffect(effect);
                }

                Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(effect, Integer.MAX_VALUE, level - 1));
            }
        });
    }

    public void removeAllEffects() {
        activeEffects.forEach((effect, level) -> {
            for (Player player : getOnlineMembers()) {

                Team team = LandBoard.getInstance().getTeam(player.getLocation());

                if (team != this) {
                    continue;
                }

                PotionEffect activeEffect = Players.getActivePotionEffect(player, effect);

                if (activeEffect != null) {
                    if (activeEffect.getAmplifier() > (level - 1)) {
                        Main.getInstance().getEffectRestorer().getRestores().remove(player.getUniqueId(), new Effect(effect, level - 1));
                        return;
                    }
                }

                player.removePotionEffect(effect);
            }
        });
    }

    public void removeEffect(PotionEffectType effectType) {
        activeEffects.forEach((effect, level) -> {
            if (effectType == effect) {
                for (Player player : getOnlineMembers()) {

                    Team team = LandBoard.getInstance().getTeam(player.getLocation());

                    if (team != this) {
                        continue;
                    }

                    PotionEffect activeEffect = Players.getActivePotionEffect(player, effect);

                    if (activeEffect != null) {
                        if (activeEffect.getAmplifier() > (level - 1)) {
                            Main.getInstance().getEffectRestorer().getRestores().remove(player.getUniqueId(), new Effect(effect, level - 1));
                            return;
                        }
                    }

                    player.removePotionEffect(effect);
                }
            }
        });
    }

    public boolean isInClaim(Player player) {
        return getClaims().stream().anyMatch(claim -> claim.contains(player));
    }

    public boolean contains(UUID uuid) {
        return this.isMember(uuid) || this.isCaptain(uuid) || this.isCoLeader(uuid) || this.isOwner(uuid);
    }

    public boolean contains(Player player) {
        return this.contains(player.getUniqueId());
    }
}