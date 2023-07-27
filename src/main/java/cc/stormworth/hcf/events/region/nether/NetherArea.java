package cc.stormworth.hcf.events.region.nether;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.claims.Claim;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetherArea {

    public static List<Material> types = Arrays.asList(Material.NETHERRACK, Material.SOUL_SAND, Material.GLOWSTONE, Material.QUARTZ_ORE);
    @Getter
    private final Set<BlockVector> blocks = new HashSet<>();
    @Getter
    @Setter
    private int remaining = 0; // We don't need a whole set for numbers???

    public void scan() {
        blocks.clear(); // clean storage

        Claim claim = NetherHandler.getClaim();

        if (claim == null) return;

        World world = Bukkit.getWorld(claim.getWorld());
        for (int x = claim.getX1(); x < claim.getX2(); x++) {
            for (int y = claim.getY1(); y < claim.getY2(); y++) {
                for (int z = claim.getZ1(); z < claim.getZ2(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (types.contains(block.getType())) {
                        blocks.add(block.getLocation().toVector().toBlockVector());
                    }
                }
            }
        }
        remaining = blocks.size();
    }

    public void reset() {
        // So we don't have to do any math later
        if (NetherHandler.getClaim() == null || blocks.isEmpty()) return;
        remaining = blocks.size();

        World world = Bukkit.getWorld(NetherHandler.getClaim().getWorld());

        for (BlockVector vector : blocks) {
            if (world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getType() != Material.NETHERRACK
                    && world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getType() != Material.SOUL_SAND
                    && world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getType() != Material.GLOWSTONE
                    && world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getType() != Material.QUARTZ_ORE) {
                world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).setType(types.get(CorePlugin.RANDOM.nextInt(types.size())));
            }
        }
        String message = ChatColor.RED + "[Nether]" + ChatColor.GREEN + " All nether has been reset!";
        Bukkit.getConsoleSender().sendMessage(message);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().getName().equalsIgnoreCase("void") && !HCFProfile.get(player).isDeathBanned())
                .forEach(player -> player.sendMessage(message));
    }
}