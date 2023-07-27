package cc.stormworth.hcf.team.claims;

import cc.stormworth.core.util.item.ItemUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.glass.GlassManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamMap {

    public static final Material[] MAP_MATERIALS = {Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD,
            Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST,
            Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE,
            Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE,
            Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE};
    private final Material[] mapMaterials;
    private final Set<UUID> mapUsers;

    TeamMap() {
        this.mapMaterials = new Material[]{
                Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_ORE, Material.DIAMOND_ORE,
                Material.EMERALD_ORE, Material.LAPIS_ORE, Material.QUARTZ_ORE, Material.REDSTONE_ORE,
                Material.NETHERRACK, Material.ENDER_STONE, Material.NETHER_BRICK, Material.STONE,
                Material.HARD_CLAY, Material.SMOOTH_BRICK, Material.IRON_BLOCK, Material.GOLD_BLOCK,
                Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK, Material.COBBLESTONE, Material.BRICK
        };

        this.mapUsers = new HashSet<>();
    }

    public void disable() {
        this.mapUsers.clear();
    }

    public void removeFromMapUsers(Player player) {
        this.mapUsers.remove(player.getUniqueId());
    }

    public void showFactionMap(Player player, boolean silent, boolean onlyself) {
        if (this.mapUsers.remove(player.getUniqueId())) {
            Main.getInstance().getGlassManager().clearGlassVisuals(player, GlassManager.GlassType.CLAIM_MAP);
            player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
            return;
        }

        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        if (onlyself && playerTeam == null) {
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        int claimIteration = 0;
        Map<Map.Entry<Claim, Team>, Material> sendMaps = new HashMap<>();

        for (Map.Entry<Claim, Team> regionData : LandBoard.getInstance().getRegionData(player.getLocation(), 32, 256, 32)) {
            Material mat = getMaterial(claimIteration);
            claimIteration++;

            //if (onlyself && playerTeam != null && regionData.getValue() != playerTeam) continue;
            regionData.getKey().erectPillar(player, mat);
            sendMaps.put(regionData, mat);
        }

        if (sendMaps.isEmpty()) {
            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "There are no claims within 32 blocks of you!");
                return;
            }
        }

        this.mapUsers.add(player.getUniqueId());

        if (!silent) {
            for (Map.Entry<Map.Entry<Claim, Team>, Material> mapEntry : sendMaps.entrySet()) {
                Team team = mapEntry.getKey().getValue();
                Claim claim = mapEntry.getKey().getKey();

                if (team.getOwner() == null) {
                    player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.GOLD + team.getName(player) + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.GOLD + team.getName(player));
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.GOLD + claim.getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.GOLD + team.getName());
                }
            }
        }
    }

    public Material getMaterial(int iteration) {
        if (iteration == -1) {
            return (Material.IRON_BLOCK);
        }

        while (iteration >= MAP_MATERIALS.length) {
            iteration = iteration - MAP_MATERIALS.length;
        }

        return (MAP_MATERIALS[iteration]);
    }
}
