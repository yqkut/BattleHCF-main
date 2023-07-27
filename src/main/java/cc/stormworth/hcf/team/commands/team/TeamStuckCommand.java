package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.listener.SetListener;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import cc.stormworth.hcf.team.claims.LandBoard;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TeamStuckCommand implements Listener {

    private static final double MAX_DISTANCE = 5;

    private static final Set<Integer> warn = new HashSet<>();
    @Getter
    private static final Map<String, Long> warping = new HashMap<>();
    private static final List<String> damaged = Lists.newArrayList();

    static {
        warn.add(300);
        warn.add(270);
        warn.add(240);
        warn.add(210);
        warn.add(180);
        warn.add(150);
        warn.add(120);
        warn.add(90);
        warn.add(60);
        warn.add(30);
        warn.add(10);
        warn.add(5);
        warn.add(4);
        warn.add(3);
        warn.add(2);
        warn.add(1);

        Main.getInstance().getServer().getPluginManager().registerEvents(new TeamStuckCommand(), Main.getInstance());
    }

    @Command(names = {"team stuck", "t stuck", "f stuck", "faction stuck", "fac stuck", "stuck", "team unstuck", "t unstuck", "f unstuck", "faction unstuck", "fac unstuck", "unstuck"}, permission = "")
    public static void teamStuck(final Player sender) {
        if (warping.containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already being warped!");
            return;
        }

        if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return;
        }

        int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : Main.getInstance().getMapHandler().isKitMap() ? 30 : 45;
        warping.put(sender.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));
        CooldownManager.addCooldown(sender.getUniqueId(), CooldownType.STUCK, seconds);

        new BukkitRunnable() {

            private int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : Main.getInstance().getMapHandler().isKitMap() ? 30 : 45;

            private final Location loc = sender.getLocation();

            private final int xStart = (int) loc.getX();
            private final int yStart = (int) loc.getY();
            private final int zStart = (int) loc.getZ();

            private Location nearest;

            @Override
            public void run() {
                if (damaged.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
                    CooldownManager.removeCooldown(sender.getUniqueId(), CooldownType.STUCK);
                    damaged.remove(sender.getName());
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (!sender.isOnline()) {
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (seconds == 5) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            nearest = nearestSafeLocation(sender.getLocation());
                        }
                    }.runTask(Main.getInstance());
                }

                Location loc = sender.getLocation();

                if (seconds <= 0) {
                    if (nearest == null) {
                        kick(sender);
                    } else {
                        if (Main.getInstance().getMapHandler().isKitMap()) {
                            if (Main.getInstance().getServerHandler().isWarzone(sender.getLocation())) {
                                sender.teleport(SetListener.getEndExit());
                            } else {
                                sender.teleport(nearest);
                            }
                        } else {
                            sender.teleport(nearest);
                        }
                        //sender.teleport(nearest);
                        //sender.sendMessage(ChatColor.YELLOW + "Teleported you to the nearest safe area!");
                    }

                    warping.remove(sender.getName());
                    cancel();
                    return;
                }
                if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
                    sender.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
                    CooldownManager.removeCooldown(sender.getUniqueId(), CooldownType.STUCK);
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                seconds--;
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public static Location nearestSafeLocation(Location origin) {
        LandBoard landBoard = LandBoard.getInstance();

        if (landBoard.getClaim(origin) == null) {
            //Bukkit.broadcastMessage("155");
            return (getActualHighestBlock(origin.getBlock()).getLocation().add(0, 1, 0));
        }

        // Start iterating outward on both positive and negative X & Z.
        for (int xPos = 2, xNeg = -2; xPos < 250; xPos += 2, xNeg -= 2) {
            for (int zPos = 2, zNeg = -2; zPos < 250; zPos += 2, zNeg -= 2) {
                Location atPos = origin.clone().add(xPos, 0, zPos);

                // Try to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atPos) == null && !isAdjacentClaimed(atPos)) {
                    //Bukkit.broadcastMessage("Line 166 found unclaimed location with no claims");
                    return (getActualHighestBlock(atPos.getBlock()).getLocation().add(0, 1, 0));
                }

                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                // Try again to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atNeg) == null && !isAdjacentClaimed(atNeg)) {
                    //Bukkit.broadcastMessage("Line 174 found unclaimed location with no claims");
                    return (getActualHighestBlock(atNeg.getBlock()).getLocation().add(0, 1, 0));
                }
            }
        }

        return (null);
    }

    private static Block getActualHighestBlock(Block block) {
        block = block.getWorld().getHighestBlockAt(block.getLocation());

        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return (block);
    }

    private static void kick(Player player) {
        player.setMetadata("loggedout", new FixedMetadataValue(Main.getInstance(), true));
        player.kickPlayer(ChatColor.RED + "We couldn't find a safe location, so we safely logged you out for now. Contact a staff member before logging back on!");
    }

    /**
     * @param base center block
     * @return list of all adjacent locations
     */
    private static List<Location> getAdjacent(Location base) {
        List<Location> adjacent = new ArrayList<>();

        // Add all relevant locations surrounding the base block
        for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                adjacent.add(base.getBlock().getRelative(face).getLocation());
            }
        }

        return adjacent;
    }

    /**
     * @param location location to check for
     * @return if any of it's blockfaces are claimed
     */
    private static boolean isAdjacentClaimed(Location location) {
        for (Location adjacent : getAdjacent(location)) {
            if (LandBoard.getInstance().getClaim(adjacent) != null) {
                return true; // we found a claim on an adjacent block!
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (warping.containsKey(player.getName())) {
                damaged.add(player.getName());
            }
        }
    }
}