package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.player.Elevator;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SignElevatorListener implements Listener {

    private final List<Material> blockedBlocks = ImmutableList.of(Material.FENCE_GATE, Material.TRAP_DOOR, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.SIGN_POST,
            Material.WALL_SIGN, Material.STRING, Material.TORCH, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.LEVER, Material.TRIPWIRE_HOOK, Material.TRIPWIRE);
    private final List<Material> signMaterials = ImmutableList.of(Material.SIGN_POST, Material.WALL_SIGN);

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !this.signMaterials.contains(event.getClickedBlock().getType())) {
            return;
        }
        final BlockState blockState = event.getClickedBlock().getState();
        if (!(blockState instanceof Sign)) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(player.getLocation());


        if(team != null && team.isRaidable() && team.getMembers().contains(player.getUniqueId())){
            if(AntiGlitchListener.getBlockGlitch().containsKey(player.getUniqueId()) &&
                    AntiGlitchListener.getBlockGlitch().get(player.getUniqueId()) >= System.currentTimeMillis()){
                return;
            }
        }

        final Sign sign = (Sign) blockState;
        if (!sign.getLine(0).contains("[Elevator]")) {
            return;
        }
        //if (event.getItem() != null && player.isSneaking()) return;
        Elevator elevator;
        try {
            elevator = Elevator.valueOf(ChatColor.stripColor(sign.getLine(1).toUpperCase()));
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            return;
        }
        if (elevator == null) {
            player.sendMessage(ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            return;
        }
        final Location toTeleport = elevator.getCalculatedLocation(sign.getLocation(), Elevator.Type.SIGN);
        if (toTeleport == null || blockedBlocks.contains(toTeleport.getBlock().getRelative(BlockFace.DOWN).getType())) {
            player.sendMessage(ChatColor.RED + "There was an issue trying to find a valid location!");
            return;
        }
        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport.add(0.5, 0.0, 0.5));
    }

    @EventHandler
    public void onSignChange(final SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Up")) {
            event.setLine(0, CC.translate("&6[Elevator]"));
            event.setLine(1, "Up");
        }
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Down")) {
            event.setLine(0, CC.translate("&6[Elevator]"));
            event.setLine(1, "Down");
        }
    }
}