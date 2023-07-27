package cc.stormworth.hcf.team.claims;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.*;

public class LandBoard implements Listener {

    @Getter
    public static TeamMap teamMap;
    private static LandBoard instance;
    private final Map<String, Multimap<CoordinateSet, Map.Entry<Claim, Team>>> buckets = new HashMap<>();

    public LandBoard() {
        for (World world : Main.getInstance().getServer().getWorlds()) {
            buckets.put(world.getName(), HashMultimap.create());
        }

        teamMap = new TeamMap();

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public static LandBoard getInstance() {
        if (instance == null) {
            instance = new LandBoard();
        }

        return (instance);
    }

    public void loadFromTeams() {
        for (Team team : Main.getInstance().getTeamHandler().getTeams()) {
            for (Claim claim : team.getClaims()) {
                setTeamAt(claim, team);
            }
        }
    }

    public Set<Map.Entry<Claim, Team>> getRegionData(Location center, int xDistance, int yDistance, int zDistance) {
        Location loc1 = new Location(center.getWorld(), center.getBlockX() - xDistance, center.getBlockY() - yDistance, center.getBlockZ() - zDistance);
        Location loc2 = new Location(center.getWorld(), center.getBlockX() + xDistance, center.getBlockY() + yDistance, center.getBlockZ() + zDistance);

        return (getRegionData(loc1, loc2));
    }

    public Set<Map.Entry<Claim, Team>> getRegionData(Location min, Location max) {
        Set<Map.Entry<Claim, Team>> regions = new HashSet<>();
        int step = 1 << CoordinateSet.BITS;

        for (int x = min.getBlockX(); x < max.getBlockX() + step; x += step) {
            for (int z = min.getBlockZ(); z < max.getBlockZ() + step; z += step) {
                CoordinateSet coordinateSet = new CoordinateSet(x, z);

                for (Map.Entry<Claim, Team> regionEntry : buckets.get(min.getWorld().getName()).get(coordinateSet)) {
                    if (!regions.contains(regionEntry)) {
                        if ((max.getBlockX() >= regionEntry.getKey().getX1())
                                && (min.getBlockX() <= regionEntry.getKey().getX2())
                                && (max.getBlockZ() >= regionEntry.getKey().getZ1())
                                && (min.getBlockZ() <= regionEntry.getKey().getZ2())
                                && (max.getBlockY() >= regionEntry.getKey().getY1())
                                && (min.getBlockY() <= regionEntry.getKey().getY2())) {
                            regions.add(regionEntry);
                        }
                    }
                }
            }
        }

        return (regions);
    }

    public Map.Entry<Claim, Team> getRegionData(Location location) {
        for (Map.Entry<Claim, Team> data : buckets.get(location.getWorld().getName()).get(new CoordinateSet(location.getBlockX(), location.getBlockZ()))) {
            if (data.getKey().contains(location)) {
                return (data);
            }
        }

        return (null);
    }

    public Claim getClaim(Location location) {
        Map.Entry<Claim, Team> regionData = getRegionData(location);
        return (regionData == null ? null : regionData.getKey());
    }

    public Team getTeam(Location location) {
        Map.Entry<Claim, Team> regionData = getRegionData(location);
        return regionData == null ? null : regionData.getValue();
    }

    public void setTeamAt(Claim claim, Team team) {

        Map.Entry<Claim, Team> regionData = new AbstractMap.SimpleEntry<>(claim, team);
        int step = 1 << CoordinateSet.BITS;

        for (int x = regionData.getKey().getX1(); x < regionData.getKey().getX2() + step; x += step) {
            for (int z = regionData.getKey().getZ1(); z < regionData.getKey().getZ2() + step; z += step) {
                Multimap<CoordinateSet, Map.Entry<Claim, Team>> worldMap = buckets.get(regionData.getKey().getWorld());

                if (worldMap == null) {
                    continue;
                }

                if (regionData.getValue() == null) {
                    CoordinateSet coordinateSet = new CoordinateSet(x, z);

                    worldMap.get(coordinateSet).removeIf(entry -> entry.getKey().equals(regionData.getKey()));
                } else {
                    worldMap.put(new CoordinateSet(x, z), regionData);
                }
            }
        }

        updateClaim(claim);
    }

    public void updateClaim(Claim modified) {
        ArrayList<VisualClaim> visualClaims = new ArrayList<>(VisualClaim.getCurrentMaps().values());

        for (VisualClaim visualClaim : visualClaims) {
            if (modified.isWithin(visualClaim.getPlayer().getLocation().getBlockX(), visualClaim.getPlayer().getLocation().getBlockZ(), 32, modified.getWorld())) {
                visualClaim.draw(true);
                visualClaim.draw(true);
            }
        }
    }

    public void clear(Team team) {
        for (Claim claim : team.getClaims()) {
            setTeamAt(claim, null);
        }
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        buckets.put(event.getWorld().getName(), HashMultimap.create());
    }
}