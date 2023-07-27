package cc.stormworth.hcf.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Set;

public class BaseRegenListener implements Listener {

    Set<Material> ignoredMaterials = Sets.newHashSet(Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.HOPPER, Material.MOB_SPAWNER, Material.BEACON, Material.COAL_BLOCK,
            Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.REDSTONE_BLOCK);

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        final Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        // fill at own team
        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());

        if (teamAt == null){
            return;
        }

        if (playerTeam == teamAt) {
            return;
        }

        if (!teamAt.isRaidable()){
            return;
        }

        teamAt.addRemoveBlockInRaid(block);

        /*if (team != null && !team.isRaidable()) {
            Claim claim = LandBoard.getInstance().getClaim(block.getLocation());
            if (claim != null && team.getClaims().contains(claim)) {
                team.removeRegenBlock(block.getLocation());
            }
        }

        // fill at raid team
        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());
        if (teamAt != null && teamAt.isRaidable()) {
            teamAt.addRaidBlock(block.getLocation());
        }*/
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketEmptyEvent event) {

        final Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());

        if (teamAt == null){
            return;
        }

        if (playerTeam == teamAt) {
            return;
        }

        if (!teamAt.isRaidable()){
            return;
        }

        teamAt.addBlockAddInRaid(block);

        /*// fill at own team
        Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team != null && !team.isRaidable()) {
            Claim claim = LandBoard.getInstance().getClaim(block.getLocation());
            if (claim != null && team.getClaims().contains(claim)) {
                team.addRegenBlock(block.getLocation(), block.getType(), block.getData());
            }
        }

        // fill at raid team
        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());
        if (teamAt != null && teamAt.isRaidable()) {
            teamAt.addRaidBlock(block.getLocation());
        }*/
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!event.getBlock().isLiquid()) return;

        Block block = event.getBlock();
        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());
        if (teamAt != null) {
            if (teamAt.isRaidable()) {
                teamAt.addBlockAddInRaid(block);
            }
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Block block = event.getBlock();

        if (!ignoredMaterials.contains(block.getType())) {
            Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());
            if (teamAt != null && teamAt.isRaidable()) {
                teamAt.addBlockAddInRaid(block);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {

        final Block block = event.getBlock();

        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());

        if (teamAt == null){
            return;
        }

        if (playerTeam == teamAt) {
            return;
        }

        if (!teamAt.isRaidable()){
            return;
        }

        teamAt.addBlockAddInRaid(block);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        final Block block = event.getBlock();

        Team playerTeam = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());

        if (teamAt == null){
            return;
        }

        if (playerTeam == teamAt) {
            return;
        }

        if (!teamAt.isRaidable()){
            return;
        }

        teamAt.addRemoveBlockInRaid(block);
    }
}