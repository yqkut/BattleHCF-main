package cc.stormworth.hcf.customenderpearl;

import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Gate;

public class CustomPearlListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack stack = event.getItem();
            if (stack != null && stack.getType() == Material.ENDER_PEARL) {
                Player player = event.getPlayer();

                Block block = event.getClickedBlock();
                Material material = block.getType();
                if (material.toString().contains("FENCE_GATE") && ((Gate)block.getState().getData()).isOpen() && EnderPearlSettings.ON_OPEN_FENCE_GATE_LAUNCH) {
                    this.launch(player);
                    event.setCancelled(true);
                    return;
                }

                for (String s : EnderPearlSettings.onClickAutoLaunch) {
                    if (material.name().equals(s)) {
                        event.setCancelled(true);
                        this.launch(player);
                    }
                }
            }

        }
    }

    private void launch(Player player) {
        World world = ((CraftPlayer)player).getHandle().getWorld();
        if (!world.isStatic) {
            ItemStack stack = player.getItemInHand();
            world.addEntity(new VEntityEnderPearl17(world, ((CraftPlayer)player).getHandle()));
            if (stack.getAmount() == 1) {
                player.setItemInHand(null);
            } else {
                stack.setAmount(stack.getAmount() - 1);
            }

        }
    }

}
