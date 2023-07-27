package cc.stormworth.hcf.util.workload;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.eclipse.EclipseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class EclipseEventSearchLocationsWorkLoad extends ScheduleWorkLoad{

    private static final double MAX_MILLIS_PER_TICK = 2.5;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    private int maxRadius;
    private int radius;
    private int x;
    private int z;

    private final EclipseEvent event;


    public EclipseEventSearchLocationsWorkLoad(EclipseEvent event, int maxRadius, int radius) {
        this.maxRadius = maxRadius;
        this.radius = radius;
        this.event = event;
    }

    @Override
    public void run() {

        if (radius > maxRadius) {
            return;
        }

        if (x >= radius && z >= radius) {
            radius++;
            x = 0;
            z = 0;

            event.setCurrentRadius(radius);
        }

        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

        while (System.nanoTime() < stopTime) {

            if (x <= radius) {
                Location location = getHighestLocation(new Location(Bukkit.getWorld("world"), x, 50, radius));
                Location location2 = getHighestLocation(new Location(Bukkit.getWorld("world"), x, 50, -radius));
                Location location3 = getHighestLocation(new Location(Bukkit.getWorld("world"), -x, 50, -radius));
                Location location4 = getHighestLocation(new Location(Bukkit.getWorld("world"), -x, 50, radius));

                if (location != null) {
                    placeBlock(location);
                }

                if (location2 != null) {
                    placeBlock(location2);
                }

                if (location3 != null) {
                    placeBlock(location3);
                }

                if (location4 != null) {
                    placeBlock(location4);
                }

                x++;
            }

            if (x >= radius && z <= radius) {
                Location location = getHighestLocation(new Location(Bukkit.getWorld("world"), radius, 50, z));
                Location location2 = getHighestLocation(new Location(Bukkit.getWorld("world"), radius, 50, -z));
                Location location3 = getHighestLocation(new Location(Bukkit.getWorld("world"), -radius, 50, -z));
                Location location4 = getHighestLocation(new Location(Bukkit.getWorld("world"), -radius, 50, z));

                if (location != null) {
                    placeBlock(location);
                }

                if (location2 != null) {
                    placeBlock(location2);
                }

                if (location3 != null) {
                    placeBlock(location3);
                }

                if (location4 != null) {
                    placeBlock(location4);
                }

                z++;
            }
        }
    }

    public void placeBlock(Location location){
        Block block = location.getBlock();

        if (!block.getLocation().getChunk().isLoaded()){
            block.getLocation().getChunk().load();
        }

        Main.getInstance().getEventHandler().getEclipseEvent()
                .addChangeBlock(location, location.getBlock().getType(), location.getBlock().getData());

        block.setTypeIdAndData(Material.STAINED_CLAY.getId(), (byte) 14, true);
    }

    public Location getHighestLocation(Location origin) {
        return getHighestLocation(origin, null);
    }

    public Location getHighestLocation(Location origin, Location def) {
        Location cloned = origin.clone();
        World world = cloned.getWorld();
        int x = cloned.getBlockX();
        int y = 90;
        int z = cloned.getBlockZ();

        while (y > origin.getBlockY()) {
            Block block = world.getBlockAt(x, --y, z);
            if (!block.isEmpty() && block.getType() == Material.GRASS) {
                Location next = block.getLocation();
                next.setPitch(origin.getPitch());
                next.setYaw(origin.getYaw());
                return next;
            }
        }

        return def;
    }

    @Override
    public void compute() {
        runTaskTimer(Main.getInstance(), 0, 1L);
    }

    @Override
    public boolean isFinished() {

        if (radius > maxRadius) {
            System.out.println("Finished searching for locations");
            cancel();
            //Main.getInstance().getWorKLoadQueue().addWorkload(eclipseEventWorkLoad);
            return true;
        }

        return false;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
