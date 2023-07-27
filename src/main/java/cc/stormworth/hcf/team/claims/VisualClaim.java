package cc.stormworth.hcf.team.claims;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.region.glowmtn.GlowMountain;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.team.TeamClaimCommand;
import cc.stormworth.hcf.team.commands.team.TeamResizeCommand;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import cc.stormworth.hcf.util.glass.GlassInfo;
import cc.stormworth.hcf.util.glass.GlassManager;
import cc.stormworth.hcf.util.workload.PlacableBlock;
import cc.stormworth.hcf.util.workload.TeamWorkload;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class VisualClaim implements Listener {

    public static int CLAIM_BUFFER_RADIUS = 1;
    public static int CLAIM_ROAD_BUFFER_RADIUS = 3;

    @Getter
    private static final Map<String, VisualClaim> currentMaps = new HashMap<>();
    @Getter
    @NonNull
    private Player player;
    @Getter
    @NonNull
    private Team team;
    @Getter
    @NonNull
    private VisualClaimType type;
    @Getter
    private final boolean bypass;
    @Getter
    private final boolean height;

    @Getter @Setter private Material material;

    @Getter @Setter private int heightAmount;
    @Getter @Setter private int colorGlass;
    @Getter
    @Setter
    private Claim resizing;
    @Getter
    @Setter
    private Location corner1;
    @Getter
    @Setter
    private Location corner2;

    public void draw(boolean silent) {
        player.getInventory().remove(TeamClaimCommand.SELECTION_WAND);

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the claim wand!");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                player.getInventory().addItem(TeamClaimCommand.SELECTION_WAND.clone());
                player.updateInventory();
            }
        }.runTaskLater(Main.getInstance(), 1L);

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());
        Rank rank = profile == null ? Rank.DEFAULT : profile.getRank();
        int maxsize = rank == Rank.KING_PLUS ? 30
                : rank == Rank.BATTLE ? 50
                : rank == Rank.FAMOUS ? 30
                : rank == Rank.STREAMER ? 30
                : rank == Rank.PARTNER ? 50
                : rank.isAboveOrEqual(Rank.SENIORADMINISTRATOR) ? 50 : 0;

        switch (type) {
            case CREATE:
                player.sendMessage(CC.SEPARATOR);
                player.sendMessage(ChatColor.GOLD + "Team land claim started.");
                player.sendMessage(
                        ChatColor.YELLOW + "Left click at a corner of the land you'd like to claim.");
                player.sendMessage(
                        ChatColor.YELLOW + "Right click on the second corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Crouch left click the air to purchase your claim.");
                player.sendMessage(CC.SEPARATOR);
                break;
            case RESIZE:
                player.sendMessage(CC.SEPARATOR);
                player.sendMessage(ChatColor.GOLD + "Team land resize started.");
                player.sendMessage(ChatColor.YELLOW + "Left click in the claim you'd like to resize.");
                player.sendMessage(ChatColor.YELLOW + "Right click on the corner you'd like to resize to.");
                player.sendMessage(ChatColor.YELLOW + "Crouch left click the air to confirm your resize.");
                player.sendMessage(CC.SEPARATOR);
                break;
            case BASE:
                player.sendMessage(CC.SEPARATOR);
                player.sendMessage(ChatColor.GOLD + "Team create base started.");
                player.sendMessage(ChatColor.YELLOW + "Left click inside of the claim you'd like to set as your base.");
                player.sendMessage(ChatColor.YELLOW + "Right click inside of the claim you'd like to set as your base.");
                player.sendMessage(ChatColor.YELLOW + "Crouch left click the air to confirm your base.");
                player.sendMessage(CC.SEPARATOR);
            case FALLTRAP:
                player.sendMessage(CC.SEPARATOR);
                player.sendMessage(ChatColor.GOLD + "Team create falltrap started.");
                player.sendMessage(ChatColor.YELLOW + "Left click inside of the claim you'd like to set as your base.");
                player.sendMessage(ChatColor.YELLOW + "Right click inside of the claim you'd like to set as your base.");
                player.sendMessage(ChatColor.YELLOW + "Crouch left click the air to confirm your falltrap.");
                player.sendMessage(CC.SEPARATOR);
        }

        LandBoard.getTeamMap().showFactionMap(player, false, (CustomTimerCreateCommand.sotwday || Bukkit.getOnlinePlayers().size() >= 600));
    }

    public Set<Claim> getTouchingClaims(Claim claim) {
        Set<Claim> touchingClaims = new HashSet<>();

        for (Coordinate coordinate : claim.outset(Claim.CuboidDirection.Horizontal, 1)) {
            Location loc = new Location(Main.getInstance().getServer().getWorld(claim.getWorld()),
                    coordinate.getX(), 80, coordinate.getZ());
            Map.Entry<Claim, Team> claimAtLocation = LandBoard.getInstance().getRegionData(loc);

            if (claimAtLocation != null) {
                touchingClaims.add(claimAtLocation.getKey());
            }
        }

        return (touchingClaims);
    }

    public void setLoc(int locationId, final Location clicked) {
        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);

        if (!bypass && playerTeam == null) {
            player.sendMessage(
                    ChatColor.RED + "You have to be on a team to " + type.name().toLowerCase() + " land!");
            cancel();
            return;
        }

        if (type == VisualClaimType.CREATE) {
            if (!bypass && !Main.getInstance().getServerHandler().isUnclaimed(clicked)) {
                player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                return;
            }

            if (locationId == 1) {
                if (corner2 != null && isIllegalClaim(new Claim(clicked, corner2), null, team)) {
                    return;
                }

                clearPillarAt(corner1);
                this.corner1 = clicked;
            } else if (locationId == 2) {
                if (corner1 != null && isIllegalClaim(new Claim(corner1, clicked), null, team)) {
                    return;
                }

                clearPillarAt(corner2);
                this.corner2 = clicked;
            }

            TaskUtil.runAsync(Main.getInstance(), () -> this.erectPillar(clicked, Material.EMERALD_BLOCK));

            player.sendMessage(ChatColor.YELLOW + "Set claim's location " + ChatColor.GOLD + locationId
                    + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE
                    + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ()
                    + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");

            if (corner1 != null && corner2 != null) {
                int price = Claim.getPrice(new Claim(corner1, corner2), playerTeam, true);

                int x = FastMath.abs(corner1.getBlockX() - corner2.getBlockX());
                int z = FastMath.abs(corner1.getBlockZ() - corner2.getBlockZ());

                if (!bypass && price > playerTeam.getBalance()) {
                    player.sendMessage(
                            ChatColor.YELLOW + "Claim cost: " + ChatColor.RED + "$" + price + ChatColor.YELLOW
                                    + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), "
                                    + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                } else {
                    player.sendMessage(
                            ChatColor.YELLOW + "Claim cost: " + ChatColor.GREEN + "$" + price + ChatColor.YELLOW
                                    + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), "
                                    + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                }
            }
        } else if (type == VisualClaimType.RESIZE) {
            Map.Entry<Claim, Team> teamAtLocation = LandBoard.getInstance().getRegionData(clicked);

            if (locationId == 1) {
                if (teamAtLocation == null || !teamAtLocation.getValue().isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW
                            + "To resize your claim, please left click in the claim you'd like to resize.");
                    return;
                }

                resizing = teamAtLocation.getKey();
                TaskUtil.runAsync(Main.getInstance(), () -> drawClaim(resizing, Material.LAPIS_BLOCK));
            } else if (locationId == 2) {
                if (resizing == null) {
                    player.sendMessage(ChatColor.YELLOW
                            + "Before you set the location you'd like to resize to, first left click in the claim you'd like to resize.");
                    return;
                }

                final Claim claimClone = new Claim(resizing);

                applyResize(claimClone, clicked);

                if (isIllegalClaim(claimClone, Arrays.asList(resizing, claimClone), team)) {
                    player.sendMessage(CC.translate("&cYou cannot claim that area!"));
                    return;
                }

                this.corner2 = clicked;

                TaskUtil.runAsync(Main.getInstance(), () -> {
                    Main.getInstance().getGlassManager().clearGlassVisuals(player, GlassManager.GlassType.CLAIM_SELECTION);
                    drawClaim(resizing, Material.LAPIS_BLOCK);
                    drawClaim(claimClone, Material.EMERALD_BLOCK);
                });
            }

            if (locationId == 1) {
                player.sendMessage(
                        ChatColor.YELLOW + "Selected claim " + ChatColor.GOLD + teamAtLocation.getKey()
                                .getName() + ChatColor.YELLOW + " to resize.");
            } else {
                player.sendMessage(
                        ChatColor.YELLOW + "Set resize location to " + ChatColor.GREEN + "(" + ChatColor.WHITE
                                + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ()
                                + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
            }

            if (resizing != null && corner2 != null) {
                int oldPrice = Claim.getPrice(resizing, null, false);
                Claim preview = new Claim(resizing);

                applyResize(preview, corner2);

                int newPrice = Claim.getPrice(preview, null, false);
                int cost = newPrice - oldPrice;

                if (!bypass && cost > playerTeam.getBalance()) {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.RED + "$" + cost);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.GREEN + "$" + cost);
                }
            }
        }if (type == VisualClaimType.BASE) {
            List<Claim> claims = team.getClaims();

            if (claims.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You have no claims to set a base in!");
                return;
            }

            Claim claim = null;

            for (Claim c : claims) {
                if (c.contains(clicked)) {
                    claim = c;
                    break;
                }
            }

            if (claim == null) {
                player.sendMessage(ChatColor.RED + "You can only set a base in your own claims!");
                return;
            }

            if (locationId == 1){
                clearPillarAt(corner1);
                corner1 = clicked;
            } else if (locationId == 2) {
                clearPillarAt(corner2);
                corner2 = clicked;
            }

            TaskUtil.runAsync(Main.getInstance(), () -> this.erectPillar(clicked, Material.EMERALD_BLOCK));

            player.sendMessage(ChatColor.YELLOW + "Set location " + ChatColor.GOLD + locationId
                    + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE
                    + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ()
                    + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
        }else if (type == VisualClaimType.FALLTRAP){
            List<Claim> claims = team.getClaims();

            if (claims.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You have no claims to set a base in!");
                return;
            }

            Claim claim = null;

            for (Claim c : claims) {
                if (c.contains(clicked)) {
                    claim = c;
                    break;
                }
            }

            if (claim == null) {
                player.sendMessage(ChatColor.RED + "You can only set a base in your own claims!");
                return;
            }

            if (locationId == 1){
                clearPillarAt(corner1);
                corner1 = clicked;
            } else if (locationId == 2) {
                clearPillarAt(corner2);
                corner2 = clicked;
            }

            TaskUtil.runAsync(Main.getInstance(), () -> this.erectPillar(clicked, Material.GOLD_BLOCK));

            player.sendMessage(ChatColor.YELLOW + "Set location " + ChatColor.GOLD + locationId
                    + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE
                    + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ()
                    + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
        }
    }

    private void drawClaim(Claim claim, Material material) {
        for (Location loc : claim.getCornerLocations()) {
            erectPillar(loc, material);
        }
    }

    private void clearPillarAt(Location location) {
        TaskUtil.runAsync(Main.getInstance(), () -> Main.getInstance().getGlassManager()
                .clearGlassVisuals(player, GlassManager.GlassType.CLAIM_SELECTION, glassInfo -> {
                    Location loc = glassInfo.getLocation();
                    return location != null
                            && loc.getBlockX() == location.getBlockX()
                            && loc.getBlockZ() == location.getBlockZ();
                }));
    }

    private void erectPillar(Location clicked, Material material) {
        for (int i = 0; i < player.getLocation().getBlockY() + 60; i++) {
            Location location = clicked.clone();
            location.setY(i);

            Main.getInstance().getGlassManager().generateGlassVisual(player,
                    new GlassInfo(GlassManager.GlassType.CLAIM_SELECTION, location,
                            i % 5 == 0 ? material : Material.GLASS, (byte) 0));
        }
    }

    public void cancel() {
        if (type == VisualClaimType.CREATE || type == VisualClaimType.RESIZE || type == VisualClaimType.BASE || type == VisualClaimType.FALLTRAP) {
            player.getInventory().remove(TeamClaimCommand.SELECTION_WAND);
            player.getInventory().remove(TeamResizeCommand.SELECTION_WAND);
        }
        TaskUtil.runAsync(Main.getInstance(), () -> {
            Main.getInstance().getGlassManager()
                    .clearGlassVisuals(player, GlassManager.GlassType.CLAIM_SELECTION);
            Main.getInstance().getGlassManager()
                    .clearGlassVisuals(player, GlassManager.GlassType.CLAIM_MAP);
        });

        LandBoard.getTeamMap().showFactionMap(player, false, (CustomTimerCreateCommand.sotwday || Bukkit.getOnlinePlayers().size() >= 600));
        HandlerList.unregisterAll(this);
    }


    private final ImmutableList<Material> BREAKEABLE_BLOCKS = ImmutableList.of(
            Material.GRASS,
            Material.DIRT,
            Material.STONE,
            Material.WOOD,
            Material.LOG,
            Material.LOG_2,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.SAPLING,
            Material.LONG_GRASS,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.STAINED_CLAY,
            Material.DEAD_BUSH,
            Material.SANDSTONE,
            Material.COAL_ORE,
            Material.COAL_ORE,
            Material.WATER_BUCKET,
            Material.COBBLESTONE,
            Material.PUMPKIN,
            Material.CACTUS,
            Material.BROWN_MUSHROOM,
            Material.LAVA_BUCKET
    );
    private final ImmutableList<Material> CANCEL_BLOCKS = ImmutableList.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.HOPPER,
            Material.FURNACE,
            Material.DISPENSER
    );

    public static boolean base = true;

    public void purchaseClaim() {
        if (!bypass && team == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
            cancel();
            return;
        }

        if (corner1 != null && corner2 != null) {

            if (base){
                if (type == VisualClaimType.BASE){
                    Claim claim = new Claim(corner1, corner2);
                    claim.setY1(corner1.getBlockY());
                    claim.setY2(corner2.getBlockY() + heightAmount);

                    int sizeX = Math.abs(claim.getX1() - claim.getX2());
                    int sizeZ = Math.abs(claim.getZ1() - claim.getZ2());

                    if (sizeX < 3 || sizeZ < 3) {
                        player.sendMessage(ChatColor.RED + "Your base is too small! The base has to be at least 3 x 3!");
                        return;
                    }

                    TeamWorkload workloadRunnable = new TeamWorkload(team, TeamWorkdLoadType.BASE, claim.getCenter());

                    Location maxLocation = claim.getMaximumPoint();
                    Location minLocation = claim.getMinimumPoint();

                    for (int x = minLocation.getBlockX(); x <= maxLocation.getBlockX(); x++){
                        for (int z = minLocation.getBlockZ(); z <= maxLocation.getBlockZ(); z++){
                            for (int y = minLocation.getBlockY(); y <= maxLocation.getBlockY(); y++){
                                Location location = new Location(minLocation.getWorld(), x, y, z);

                                Block block = location.getBlock();

                                if (block != null && block.getType() != Material.AIR){
                                    if (!BREAKEABLE_BLOCKS.contains(block.getType()) && CANCEL_BLOCKS.contains(block.getType())) continue;
                                    PlacableBlock placableBlock = new PlacableBlock(location.getWorld().getUID(),
                                            location.getBlockX(),
                                            location.getBlockY(),
                                            location.getBlockZ(),
                                            Material.AIR,
                                            (byte) 0);

                                    workloadRunnable.addWorkload(placableBlock);
                                }
                            }
                        }
                    }

                    for (Location corner : claim.getWalls()){
                        if (CANCEL_BLOCKS.contains(corner.getBlock().getType())) continue;
                        PlacableBlock placableBlock = new PlacableBlock(corner.getWorld().getUID(),
                                corner.getBlockX(),
                                corner.getBlockY(),
                                corner.getBlockZ(),
                                Material.STAINED_GLASS,
                                (byte) colorGlass);

                        workloadRunnable.addWorkload(placableBlock);
                    }

                    for (Location corner : claim.getCornerLocations()){
                        for (int i = corner1.getBlockY(); i < corner2.getBlockY() + heightAmount; i++) {
                            if (CANCEL_BLOCKS.contains(corner.getBlock().getType())) continue;
                            Location location = corner.clone();
                            location.setY(i);

                            PlacableBlock placableBlock = new PlacableBlock(corner.getWorld().getUID(),
                                    location.getBlockX(),
                                    location.getBlockY(),
                                    location.getBlockZ(),
                                    Material.WOOL,
                                    (byte) colorGlass);

                            workloadRunnable.addWorkload(placableBlock);
                        }
                    }

                    for (Location corner : claim.getRoof()){
                        if (!corner.getChunk().isLoaded()) corner.getChunk().load();
                        if (CANCEL_BLOCKS.contains(corner.getBlock().getType())) continue;

                        PlacableBlock placableBlock = new PlacableBlock(corner.getWorld().getUID(),
                                corner.getBlockX(),
                                corner.getBlockY(),
                                corner.getBlockZ(),
                                Material.WOOL,
                                (byte) colorGlass);

                        workloadRunnable.addWorkload(placableBlock);
                    }

                    for (Location corner : claim.getFloor()){
                        if (CANCEL_BLOCKS.contains(corner.getBlock().getType())) continue;
                        PlacableBlock placableBlock = new PlacableBlock(corner.getWorld().getUID(),
                                corner.getBlockX(),
                                corner.getBlockY(),
                                corner.getBlockZ(),
                                Material.WOOL,
                                (byte) colorGlass);

                        workloadRunnable.addWorkload(placableBlock);
                    }

                    cancel();

                    player.sendMessage(CC.translate("&aYou team base creation has been added to queue."));
                    int position = Main.getInstance().getWorKLoadQueue().addWorkload(workloadRunnable);

                    team.sendMessage(CC.translate("&eYou are queued for &aBase build&e, position &6" + position));

                    team.getWorkloadRunnables().put(TeamWorkdLoadType.BASE, workloadRunnable);
                    return;
                } else if (type == VisualClaimType.FALLTRAP){
                    Claim claim = new Claim(corner1, corner2);
                    claim.setY1(1);
                    claim.setY2(corner1.getBlockY());

                    int sizeX = Math.abs(claim.getX1() - claim.getX2());
                    int sizeZ = Math.abs(claim.getZ1() - claim.getZ2());

                    if (sizeX < 3 || sizeZ < 3) {
                        player.sendMessage(ChatColor.RED + "Your falltrap is too small! The falltrap has to be at least 3 x 3!");
                        return;
                    }

                    if (sizeX > 25 || sizeZ > 25) {
                        player.sendMessage(ChatColor.RED + "Your falltrap is too big! The falltrap has to be at most 25 x 25!");
                        return;
                    }

                    TeamWorkload workloadRunnable = new TeamWorkload(team, TeamWorkdLoadType.FALL_TRAP, claim.getCenter());

                    Location maxLocation = claim.getMaximumPoint();
                    Location minLocation = claim.getMinimumPoint();

                    for (int x = minLocation.getBlockX(); x <= maxLocation.getBlockX(); x++){
                        for (int z = minLocation.getBlockZ(); z <= maxLocation.getBlockZ(); z++){
                            for (int y = minLocation.getBlockY(); y <= maxLocation.getBlockY(); y++){
                                Location location = new Location(minLocation.getWorld(), x, y, z);

                                if (CANCEL_BLOCKS.contains(location.getBlock().getType())) continue;

                                if (location.getBlock().getType() == Material.AIR || location.getBlock().getType() == Material.BEDROCK) continue;

                                PlacableBlock placableBlock = new PlacableBlock(location.getWorld().getUID(),
                                        location.getBlockX(),
                                        location.getBlockY(),
                                        location.getBlockZ(),
                                        Material.AIR,
                                        (byte) 0);

                                workloadRunnable.addWorkload(placableBlock);
                            }
                        }
                    }

                    if (material != Material.AIR){
                        for (Location corner : claim.getWalls()) {
                            if (CANCEL_BLOCKS.contains(corner.getBlock().getType())) continue;
                            PlacableBlock placableBlock = new PlacableBlock(corner.getWorld().getUID(),
                                    corner.getBlockX(),
                                    corner.getBlockY(),
                                    corner.getBlockZ(),
                                    material,
                                    (byte) colorGlass);

                            workloadRunnable.addWorkload(placableBlock);
                        }

                        for (Location corner : claim.getFloor()){
                            if (CANCEL_BLOCKS.contains(corner.getBlock().getType()) && corner.getBlock().getType() == Material.BEDROCK) return;

                            PlacableBlock placableBlock = new PlacableBlock(corner.getWorld().getUID(),
                                    corner.getBlockX(),
                                    corner.getBlockY(),
                                    corner.getBlockZ(),
                                    material,
                                    (byte) colorGlass);

                            workloadRunnable.addWorkload(placableBlock);
                        }
                    }

                    cancel();

                    player.sendMessage(CC.translate("&aYou falltrap creation has been added to queue."));
                    int position = Main.getInstance().getWorKLoadQueue().addWorkload(workloadRunnable);
                    team.sendMessage(CC.translate("&eYou are queued for &aFallTrap build&e, position &6" + position));
                    team.getWorkloadRunnables().put(TeamWorkdLoadType.FALL_TRAP, workloadRunnable);
                    return;
                }
            }else{
                if (type == VisualClaimType.BASE) return;
                else if (type == VisualClaimType.FALLTRAP) return;
            }

            int price = Claim.getPrice(new Claim(corner1, corner2), team, true);

            if (!bypass) {
                if (team.getClaims().size() >= Team.MAX_CLAIMS) {
                    player.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is "
                            + Team.MAX_CLAIMS + ".");
                    return;
                }
                if (!team.isCaptain(player.getUniqueId()) && !team.isCoLeader(player.getUniqueId())
                        && !team.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                    return;
                }
                if (team.getBalance() < price) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }
                if (team.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                    return;
                }
            }

            Claim claim = new Claim(corner1, corner2);

            if (isIllegalClaim(claim, null, team)) {
                player.sendMessage(CC.translate("&cYou cannot claim that area!"));
                return;
            }

            claim.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));

            if (!height) {
                claim.setY1(0);
                claim.setY2(256);
            } else {
                claim.setY1(corner1.getBlockY());
                claim.setY2(corner2.getBlockY());
            }

            /*if (!Main.getInstance().getMapHandler().isKitMap() && bypass && (
                    team.hasDTRBitmask(DTRBitmask.NETHER) || team.hasDTRBitmask(DTRBitmask.KOTH)
                            || team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(
                            DTRBitmask.CONQUEST))) {
                int buffer = (this.team.getName().equalsIgnoreCase("citadel")
                        || this.team.getName().equalsIgnoreCase("conquest")
                        || this.team.getName().equalsIgnoreCase("Nether")) ? 200 : 15;

                Location corner1 = claim.getCornerLocations()[0];
                Location corner2 = claim.getCornerLocations()[1];
                Location corner3 = claim.getCornerLocations()[2];
                Location corner4 = claim.getCornerLocations()[3];

                this.claimBuffer(corner1.clone().add(-buffer, 0, -1),
                        corner4.clone().add(buffer, 0, -buffer));
                this.claimBuffer(corner4.clone().add(buffer, 0, -buffer),
                        corner2.clone().add(1, 0, buffer));
                this.claimBuffer(corner2.clone().add(1, 0, buffer), corner3.clone().add(-buffer, 0, 1));
                this.claimBuffer(corner3.clone().add(-buffer, 0, 1), corner1.clone().add(-1, 0, -buffer));
            }*/

            LandBoard.getInstance().setTeamAt(claim, team);
            team.getClaims().add(claim);
            team.flagForSave();

            player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your team!");

            if (!bypass) {
                team.setBalance(team.getBalance() - price);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$"
                        + team.getBalance() + ChatColor.GOLD + " (Price: $" + price + ")");
            }

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_CLAIM_LAND, ImmutableMap.<String, Object>builder()
                    .put("playerId", player.getUniqueId().toString())
                    .put("cost", price)
                    .put("point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ())
                    .put("point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ())
                    .put("date", System.currentTimeMillis())
                    .build()
            );

            cancel();

            if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                VisualClaim.getCurrentMaps().get(player.getName()).cancel();
            }

            if (!Main.getInstance().getMapHandler().isKitMap()) {
                if (team.getName().equalsIgnoreCase("Glowstone")) {
                    if (!Main.getInstance().getGlowHandler().hasGlowMountain()) {
                        Main.getInstance().getGlowHandler().setGlowMountain(new GlowMountain());
                    }
                    Main.getInstance().getGlowHandler().getGlowMountain().scan();
                    Main.getInstance().getGlowHandler().save();
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public void claimBuffer(Location corner1, Location corner2) {
        Team team = (this.team.getName().equalsIgnoreCase("citadel") || this.team.getName()
                .equalsIgnoreCase("conquest")) ?
                Main.getInstance().getTeamHandler().getTeam("RestrictedEvent") :
                this.team.getName().equalsIgnoreCase("Sand") ? Main.getInstance().getTeamHandler()
                        .getTeam("SandZone")
                        : this.team.getName().equalsIgnoreCase("Nether") ?
                        Main.getInstance().getTeamHandler().getTeam("NetherZone") :
                        Main.getInstance().getTeamHandler().getTeam("RestrictedZone");

        Claim bufferclaim = new Claim(corner1, corner2);
        bufferclaim.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        if (!height) {
            bufferclaim.setY1(0);
            bufferclaim.setY2(256);
        } else {
            bufferclaim.setY1(corner1.getBlockY());
            bufferclaim.setY2(corner2.getBlockY());
        }

        LandBoard.getInstance().setTeamAt(bufferclaim, team);
        team.getClaims().add(bufferclaim);
        team.flagForSave();
    }

    public void resizeClaim() {
        if (!bypass && team == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to resize land!");
            cancel();
            return;
        }

        if (resizing != null && corner2 != null) {
            Claim newClaim = new Claim(resizing);
            applyResize(newClaim, corner2);

            int oldPrice = Claim.getPrice(resizing, null, false);
            int newPrice = Claim.getPrice(newClaim, null, false);
            int cost = newPrice - oldPrice;

            if (!bypass) {
                if (!team.isCaptain(player.getUniqueId()) && !team.isCoLeader(player.getUniqueId())
                        && !team.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can resize land.");
                    return;
                }

                if (team.getBalance() < cost) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (team.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot resize land while raidable.");
                    return;
                }
            }

            if (isIllegalClaim(newClaim, null, team)) {
                player.sendMessage(CC.translate("&cYou cannot claim that area!"));
                return;
            }

            LandBoard.getInstance().setTeamAt(resizing, null);
            LandBoard.getInstance().setTeamAt(newClaim, team);
            team.getClaims().remove(resizing);
            team.getClaims().add(newClaim);
            team.flagForSave();

            player.sendMessage(ChatColor.YELLOW + "You have resized this land!");

            if (!bypass) {
                team.setBalance(team.getBalance() - cost);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$"
                        + team.getBalance() + ChatColor.GOLD + " (Price: $" + cost + ")");
            }

            Location minLoc = resizing.getMinimumPoint();
            Location maxLoc = resizing.getMaximumPoint();

            TeamTrackerManager.logAsync(team, TeamActionType.PLAYER_RESIZE_LAND, ImmutableMap.<String, Object>builder()
                    .put("playerId", player.getUniqueId().toString())
                    .put("cost", cost)
                    .put("newPoint1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ())
                    .put("newPoint2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ())
                    .put("date", System.currentTimeMillis())
                    .build()
            );

            cancel();

            if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                VisualClaim.getCurrentMaps().get(player.getName()).cancel();
            }

        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public boolean isIllegalClaim(Claim claim, List<Claim> ignoreNearby, Team team) {
        if (bypass) {
            return (false);
        }

        if (containsOtherClaim(claim)) {
            player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
            return (true);
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
            return (true);
        }

        Set<Claim> touchingClaims = getTouchingClaims(claim);
        Iterator<Claim> teamClaims = touchingClaims.iterator();
        boolean removedSelfClaims = false;

        while (teamClaims.hasNext()) {
            Claim possibleClaim = teamClaims.next();

            if (ignoreNearby != null && ignoreNearby.contains(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            } else if (team.ownsClaim(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            }
        }

        if (team.getClaims().size() != (type == VisualClaimType.RESIZE ? 1 : 0) && !removedSelfClaims) {
            player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
            return (true);
        }

        if (touchingClaims.size() > 1 || (touchingClaims.size() == 1 && !removedSelfClaims)) {
            player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
            return (true);
        }

        int x = Math.abs(claim.getX1() - claim.getX2());
        int z = Math.abs(claim.getZ1() - claim.getZ2());

        if (x < 5 || z < 5) {
            player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least 5 x 5!");
            return (true);
        }

        if (x > 3 * z || z > 3 * x) {
            player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
            return (true);
        }

        return (false);
    }

    public boolean containsOtherClaim(Claim claim) {
        Location maxPoint = claim.getMaximumPoint();
        Location minPoint = claim.getMinimumPoint();
        Team maxTeam = LandBoard.getInstance().getTeam(maxPoint);

        if (maxTeam != null && (type != VisualClaimType.RESIZE || !maxTeam.isMember(player.getUniqueId()))) {
            return (true);
        }

        Team minTeam = LandBoard.getInstance().getTeam(minPoint);

        if (minTeam != null && (type != VisualClaimType.RESIZE || !minTeam.isMember(player.getUniqueId()))) {
            return (true);
        }

        // A Claim doesn't like being iterated when either its X or Z is 0.
        if (Math.abs(claim.getX1() - claim.getX2()) == 0 || Math.abs(claim.getZ1() - claim.getZ2()) == 0) {
            return (false);
        }

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                Location at = new Location(Main.getInstance().getServer().getWorld(claim.getWorld()), x, 80, z);
                Team teamAt = LandBoard.getInstance().getTeam(at);

                if (teamAt != null && (type != VisualClaimType.RESIZE || !teamAt.isMember(player.getUniqueId()))) {
                    return (true);
                }
            }
        }

        return (false);
    }

    public void applyResize(Claim claim, Location location) {
        double furthestDistance = 0D;
        Location furthestCorner = null;

        for (Location corner : claim.getCornerLocations()) {
            double distance = location.distanceSquared(corner);

            if (furthestCorner == null || distance > furthestDistance) {
                furthestDistance = distance;
                furthestCorner = corner;
            }
        }

        claim.setLocations(location, furthestCorner);

        if (!height) {
            claim.setY1(0);
            claim.setY2(256);
        } else {
            claim.setY1(corner1.getBlockY());
            claim.setY2(corner2.getBlockY());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == player && player.getItemInHand() != null) {

            if (player.getItemInHand().getType() == TeamClaimCommand.SELECTION_WAND.getType() && type == VisualClaimType.CREATE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the claiming process.");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }
                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        }
                        break;
                }
                event.setCancelled(true);

            } else if (player.getItemInHand().getType() == TeamResizeCommand.SELECTION_WAND.getType()
                    && type == VisualClaimType.RESIZE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the resizing process.");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            resizeClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }
                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            resizeClaim();
                        }
                        break;
                }
                event.setCancelled(true);
            }else if (player.getItemInHand().getType() == TeamClaimCommand.SELECTION_WAND.getType() && type == VisualClaimType.BASE
                    || player.getItemInHand().getType() == TeamClaimCommand.SELECTION_WAND.getType() && type == VisualClaimType.FALLTRAP) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the claiming process.");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }
                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        }
                        break;
                }
                event.setCancelled(true);

            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (player == event.getPlayer()) {
            if (event.getItemDrop().getItemStack().equals(TeamClaimCommand.SELECTION_WAND)
                    || event.getItemDrop().getItemStack().equals(TeamResizeCommand.SELECTION_WAND)) {
                cancel();
                if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                    VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (player == event.getPlayer()) {
            cancel();
            if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                VisualClaim.getCurrentMaps().get(player.getName()).cancel();
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (player == event.getPlayer()) {
            cancel();
            if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                VisualClaim.getCurrentMaps().get(player.getName()).cancel();
            }
        }
    }
}