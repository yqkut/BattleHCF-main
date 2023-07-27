package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class NetherPortalTrapHandler implements Listener {

    private boolean isInPortal(Player player) {
        return player.getLocation().getBlock().getType() == Material.PORTAL && player.getEyeLocation().getBlock().getType() == Material.PORTAL;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer()))
            return;
        if (!this.isInPortal(event.getPlayer())) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block.getType() != Material.PORTAL) return;

        boolean sendMessage = false;

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    Block newBlock = block.getRelative(x, y, z);
                    if (newBlock.getType() != Material.PORTAL) continue;

                    sendMessage = true;
                    TaskUtil.run(Main.getInstance(), () -> event.getPlayer().sendBlockChange(newBlock.getLocation(), Material.AIR, (byte) 0));
                }
            }
        }
        if (sendMessage) event.getPlayer().sendMessage(CC.YELLOW + "You have disabled this portal.");
    }
}