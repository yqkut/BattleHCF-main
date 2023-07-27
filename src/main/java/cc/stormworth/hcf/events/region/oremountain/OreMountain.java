package cc.stormworth.hcf.events.region.oremountain;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.claims.Claim;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.HashSet;
import java.util.Set;


public class OreMountain {

    @Getter
    private final Set<BlockVector> ores = new HashSet<>();
    @Getter
    @Setter
    private int remaining = 0; // We don't need a whole set for numbers???

    public void scan() {
        ores.clear(); // clean storage

        Claim claim = OreMountainHandler.getClaim();

        if (claim == null) return;

        World world = Bukkit.getWorld(claim.getWorld());
        for (int x = claim.getX1(); x < claim.getX2(); x++) {
            for (int y = claim.getY1(); y < claim.getY2(); y++) {
                for (int z = claim.getZ1(); z < claim.getZ2(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType().name().contains("ORE")) {
                        ores.add(block.getLocation().toVector().toBlockVector());
                    }
                }
            }
        }
        remaining = ores.size();
    }

    public void reset() {
        // So we don't have to do any math later
        if (OreMountainHandler.getClaim() == null || ores.isEmpty()) return;
        remaining = ores.size();

        World world = Bukkit.getWorld(OreMountainHandler.getClaim().getWorld());

        for (BlockVector vector : ores) {
            int random = Main.RANDOM.nextInt(60);

            if (!world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getType().name().contains("ORE")) {
                world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).setType(
                        random < 5 ? Material.EMERALD_ORE : random < 5 ?
                                Material.GOLD_ORE : random < 10 ?
                                Material.DIAMOND_ORE : random < 15 ?
                                Material.IRON_ORE : random < 20 ?
                                Material.REDSTONE_ORE : random < 30 ?
                                Material.LAPIS_ORE : random < 40 ?
                                Material.COAL_ORE : Material.GOLD_ORE);
            }
        }
        Bukkit.broadcastMessage(ChatColor.AQUA + "[Ore Mountain]" + ChatColor.GREEN + " All ores has been reset!");
    }
}