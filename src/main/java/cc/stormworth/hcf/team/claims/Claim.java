package cc.stormworth.hcf.team.claims;

import cc.stormworth.core.util.gson.serialization.LocationSerializer;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.glass.GlassInfo;
import cc.stormworth.hcf.util.glass.GlassManager;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class Claim implements Iterable<Coordinate> {

    private String world;
    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;
    private String name;

    private final List<Location> walls = new ArrayList<>();

    public Claim(final Location corner1, final Location corner2) {
        this(corner1.getWorld().getName(), corner1.getBlockX(), corner1.getBlockY(),
            corner1.getBlockZ(), corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ());
    }

    public Claim(final Claim copyFrom) {
        this.world = copyFrom.world;
        this.x1 = copyFrom.x1;
        this.y1 = copyFrom.y1;
        this.z1 = copyFrom.z1;
        this.x2 = copyFrom.x2;
        this.y2 = copyFrom.y2;
        this.z2 = copyFrom.z2;
        this.name = copyFrom.name;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= 90; y++) {
                walls.add(new Location(Bukkit.getWorld(world), x, y, x1));
                walls.add(new Location(Bukkit.getWorld(world), x, y, x2));
            }
        }
        for (int z = x1; z <= x2; z++) {
            for (int y = y1; y <= 90; y++) {
                walls.add(new Location(Bukkit.getWorld(world), x1, y, z));
                walls.add(new Location(Bukkit.getWorld(world), x2, y, z));
            }
        }
    }

    public Claim(final String world, final int x1, final int y1, final int z1, final int x2,
        final int y2, final int z2) {
        this.world = world;
        this.x1 = FastMath.min(x1, x2);
        this.x2 = FastMath.max(x1, x2);
        this.y1 = FastMath.min(y1, y2);
        this.y2 = FastMath.max(y1, y2);
        this.z1 = FastMath.min(z1, z2);
        this.z2 = FastMath.max(z1, z2);
    }

    public Claim() {
    }

    public static Claim fromJson(final BasicDBObject obj) {
        final Claim c = new Claim(
            LocationSerializer.deserialize((BasicDBObject) obj.get("Location1")),
            LocationSerializer.deserialize((BasicDBObject) obj.get("Location2")));
        c.setName(obj.getString("Name"));
        return c;
    }

    public static final double priceMod = Main.getInstance().getMapHandler().isKitMap() ? 0.9 : 0.4;

    public static int getPrice(final Claim claim, final Team team, final boolean buying) {
        final int x = FastMath.abs(claim.x1 - claim.x2);
        final int z = FastMath.abs(claim.z1 - claim.z2);
        int blocks = x * z;
        int done = 0;
        double mod = priceMod;
        double curPrice = 0.0;
        while (blocks > 0) {
            --blocks;
            ++done;
            curPrice += mod;
            if (done == 250) {
                done = 0;
                mod += priceMod;
            }
        }
        curPrice *= 0.800000011920929;
        if (buying && team != null) {
            curPrice += 500 * team.getClaims().size();
        }
        return (int) curPrice;
    }

    public BasicDBObject json() {
        final BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("Name", this.name);
        final World world = Main.getInstance().getServer().getWorld(this.getWorld());
        dbObject.put("Location1",
            LocationSerializer.serialize(new Location(world, this.x1, this.y1, this.z1)));
        dbObject.put("Location2",
            LocationSerializer.serialize(new Location(world, this.x2, this.y2, this.z2)));
        return dbObject;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Claim)) {
            return false;
        }
        final Claim claim = (Claim) object;
        return claim.getMaximumPoint().equals(this.getMaximumPoint()) && claim.getMinimumPoint()
            .equals(this.getMinimumPoint());
    }

    public List<Location> getWalls() {

        List<Location> walls = Lists.newLinkedList();

        for (int x = getMinimumPoint().getBlockX(); x <= getMaximumPoint().getBlockX(); x++){
            for (int y = y1; y <= y2; y++) {

                Location loc = new Location(Bukkit.getWorld(world), x, y, getMinimumPoint().getZ());
                Location loc2 = new Location(Bukkit.getWorld(world), x, y, getMaximumPoint().getZ());

                if (!walls.contains(loc)){
                    walls.add(loc);
                }

                if (!walls.contains(loc2)){
                    walls.add(loc2);
                }
            }
        }

        for (int z = (int) getMinimumPoint().getZ(); z <= getMaximumPoint().getZ(); z++) {
            for (int y = y1; y <= y2; y++) {

                Location loc = new Location(Bukkit.getWorld(world), getMinimumPoint().getX(), y, z);
                Location loc2 = new Location(Bukkit.getWorld(world), getMaximumPoint().getX(), y, z);

                if (!walls.contains(loc)){
                    walls.add(loc);
                }

                if (!walls.contains(loc2)){
                    walls.add(loc2);
                }
            }
        }

        return walls;
    }

    public Location getCenter(){
        return new Location(Bukkit.getWorld(world), (x1 + x2) / 2, (y1 + y2) / 2, (z1 + z2) / 2);
    }

    public List<Location> getRoof() {
        List<Location> roof = Lists.newLinkedList();
        for (int x = (int) getMinimumPoint().getX(); x <= getMaximumPoint().getX(); x++) {
            for (int z = (int) getMinimumPoint().getZ(); z <= getMaximumPoint().getZ(); z++) {
                roof.add(new Location(Bukkit.getWorld(world), x, y2, z));
            }
        }
        return roof;
    }

    public List<Location> getFloor() {
        List<Location> floor = Lists.newLinkedList();
        for (int x = (int) getMinimumPoint().getX(); x <= getMaximumPoint().getX(); x++) {
            for (int z = (int) getMinimumPoint().getZ(); z <= getMaximumPoint().getZ(); z++) {
                floor.add(new Location(Bukkit.getWorld(world), x, y1, z));
            }
        }
        return floor;
    }

    public Location getMinimumPoint() {
        return new Location(Main.getInstance().getServer().getWorld(this.world),
            FastMath.min(this.x1, this.x2), FastMath.min(this.y1, this.y2),
            FastMath.min(this.z1, this.z2));
    }

    public Location getMaximumPoint() {
        return new Location(Main.getInstance().getServer().getWorld(this.world),
            FastMath.max(this.x1, this.x2), FastMath.max(this.y1, this.y2),
            FastMath.max(this.z1, this.z2));
    }

    public boolean contains(final int x, final int y, final int z, final String world) {
        return y >= this.y1 && y <= this.y2 && this.contains(x, z, world);
    }

    public boolean contains(final int x, final int z, final String world) {
        return (world == null || world.equalsIgnoreCase(this.world)) && x >= this.x1 && x <= this.x2
            && z >= this.z1 && z <= this.z2;
    }

    public boolean contains(final Location location) {
        return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ(),
            location.getWorld().getName());
    }

    public boolean contains(final Block block) {
        return this.contains(block.getLocation());
    }

    public boolean contains(final Player player) {
        return this.contains(player.getLocation());
    }

    public Set<Player> getPlayers() {
        final Set<Player> players = new HashSet<Player>();
        for (final Player player : Main.getInstance().getServer().getOnlinePlayers()) {
            if (this.contains(player)) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public int hashCode() {
        return this.getMaximumPoint().hashCode() + this.getMinimumPoint().hashCode();
    }

    @Override
    public String toString() {
        final Location corner1 = this.getMinimumPoint();
        final Location corner2 = this.getMaximumPoint();
        return corner1.getBlockX() + ":" + corner1.getBlockY() + ":" + corner1.getBlockZ() + ":"
            + corner2.getBlockX() + ":" + corner2.getBlockY() + ":" + corner2.getBlockZ() + ":"
            + this.name + ":" + this.world;
    }

    public String getFriendlyName() {
        return "(" + this.world + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ") - ("
            + this.world + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ")";
    }

    public Claim expand(final CuboidDirection dir, final int amount) {
        switch (dir) {
            case North: {
                return new Claim(this.world, this.x1 - amount, this.y1, this.z1, this.x2, this.y2,
                    this.z2);
            }
            case South: {
                return new Claim(this.world, this.x1, this.y1, this.z1, this.x2 + amount, this.y2,
                    this.z2);
            }
            case East: {
                return new Claim(this.world, this.x1, this.y1, this.z1 - amount, this.x2, this.y2,
                    this.z2);
            }
            case West: {
                return new Claim(this.world, this.x1, this.y1, this.z1, this.x2, this.y2,
                    this.z2 + amount);
            }
            case Down: {
                return new Claim(this.world, this.x1, this.y1 - amount, this.z1, this.x2, this.y2,
                    this.z2);
            }
            case Up: {
                return new Claim(this.world, this.x1, this.y1, this.z1, this.x2, this.y2 + amount,
                    this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
    }

    public Claim outset(final CuboidDirection dir, final int amount) {
        Claim claim = null;
        switch (dir) {
            case Horizontal: {
                claim = this.expand(CuboidDirection.North, amount)
                    .expand(CuboidDirection.South, amount)
                    .expand(CuboidDirection.East, amount).expand(CuboidDirection.West, amount);
                break;
            }
            case Vertical: {
                claim = this.expand(CuboidDirection.Down, amount)
                    .expand(CuboidDirection.Up, amount);
                break;
            }
            case Both: {
                claim = this.outset(CuboidDirection.Horizontal, amount)
                    .outset(CuboidDirection.Vertical, amount);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
        return claim;
    }

    public boolean isWithin(final int x, final int z, final int radius, final String world) {
        return this.outset(CuboidDirection.Both, radius).contains(x, z, world);
    }

    public void setLocations(final Location loc1, final Location loc2) {
        this.x1 = FastMath.min(loc1.getBlockX(), loc2.getBlockX());
        this.x2 = FastMath.max(loc1.getBlockX(), loc2.getBlockX());
        this.y1 = FastMath.min(loc1.getBlockY(), loc2.getBlockY());
        this.y2 = FastMath.max(loc1.getBlockY(), loc2.getBlockY());
        this.z1 = FastMath.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.z2 = FastMath.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public Location[] getCornerLocations() {
        final World world = Main.getInstance().getServer().getWorld(this.world);
        return new Location[]{new Location(world, this.x1, this.y1, this.z1),
            new Location(world, this.x2, this.y1, this.z2),
            new Location(world, this.x1, this.y1, this.z2),
            new Location(world, this.x2, this.y1, this.z1)};
    }

    @Override
    public Iterator<Coordinate> iterator() {
        return new BorderIterator(this.x1, this.z1, this.x2, this.z2);
    }

    public Iterator<Location> locationIterator() {
        return new CuboidLocationIterator(Bukkit.getWorld(this.getWorld()),
            this.x1,
            this.y1,
            this.z1,
            this.x2,
            this.y2,
            this.z2);
    }

    void erectPillar(Player player, Material material) {
        for (Location corner : this.getCornerLocations()) {
            for (int i = 0; i < player.getLocation().getBlockY() + 60; i++) {
                Location location = corner.clone();
                location.setY(i);

                Main.getInstance().getGlassManager().generateGlassVisual(player,
                    new GlassInfo(GlassManager.GlassType.CLAIM_MAP, location,
                        i % 5 == 0 ? material : Material.GLASS, (byte) 0));
            }
        }
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(final String world) {
        this.world = world;
    }

    public int getX1() {
        return this.x1;
    }

    public void setX1(final int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return this.y1;
    }

    public void setY1(final int y1) {
        this.y1 = y1;
    }

    public int getZ1() {
        return this.z1;
    }

    public void setZ1(final int z1) {
        this.z1 = z1;
    }

    public int getX2() {
        return this.x2;
    }

    public void setX2(final int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return this.y2;
    }

    public void setY2(final int y2) {
        this.y2 = y2;
    }

    public int getZ2() {
        return this.z2;
    }

    public void setZ2(final int z2) {
        this.z2 = z2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public enum BorderDirection {
        POS_X,
        POS_Z,
        NEG_X,
        NEG_Z
    }

    public enum CuboidDirection {
        North,
        East,
        South,
        West,
        Up,
        Down,
        Horizontal,
        Vertical,
        Both,
        Unknown
    }

    public class BorderIterator implements Iterator<Coordinate> {

        int maxX;
        int maxZ;
        int minX;
        int minZ;
        private int x;
        private int z;
        private boolean next;
        private BorderDirection dir;

        public BorderIterator(final int x1, final int z1, final int x2, final int z2) {
            this.next = true;
            this.dir = BorderDirection.POS_Z;
            this.maxX = Claim.this.getMaximumPoint().getBlockX();
            this.maxZ = Claim.this.getMaximumPoint().getBlockZ();
            this.minX = Claim.this.getMinimumPoint().getBlockX();
            this.minZ = Claim.this.getMinimumPoint().getBlockZ();
            this.x = FastMath.min(x1, x2);
            this.z = FastMath.min(z1, z2);
        }

        @Override
        public boolean hasNext() {
            return this.next;
        }

        @Override
        public Coordinate next() {
            if (this.dir == BorderDirection.POS_Z) {
                if (++this.z == this.maxZ) {
                    this.dir = BorderDirection.POS_X;
                }
            } else if (this.dir == BorderDirection.POS_X) {
                if (++this.x == this.maxX) {
                    this.dir = BorderDirection.NEG_Z;
                }
            } else if (this.dir == BorderDirection.NEG_Z) {
                if (--this.z == this.minZ) {
                    this.dir = BorderDirection.NEG_X;
                }
            } else if (this.dir == BorderDirection.NEG_X && --this.x == this.minX) {
                this.next = false;
            }
            return new Coordinate(this.x, this.z);
        }

        @Override
        public void remove() {
        }
    }
}