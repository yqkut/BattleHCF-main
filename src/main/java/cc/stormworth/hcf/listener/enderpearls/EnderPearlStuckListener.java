package cc.stormworth.hcf.listener.enderpearls;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderPearlStuckListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL){
            return;
        }

        Player player = event.getPlayer();

        Main.getInstance().getEnderPearlRunnable().getPlayers().put(player.getUniqueId(), System.currentTimeMillis() + TimeUtil.parseTimeLong("2s"));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){

        if(event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION){
            return;
        }

        if(!(event.getEntity() instanceof Player)){
            return;
        }

        Player player = (Player) event.getEntity();

        Main.getInstance().getEnderPearlRunnable().getLastDamageSuffocation().put(player.getUniqueId(), System.currentTimeMillis());
    }

    private static final ImmutableList<Material> ALLOWED_MATERIAL = ImmutableList.of(
            Material.GLASS,
            Material.STAINED_GLASS_PANE,
            Material.STAINED_GLASS,
            Material.ANVIL,
            Material.HOPPER,
            Material.WOOD_STEP,
            Material.STEP,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.CHEST,
            Material.PISTON_BASE,
            Material.PISTON_STICKY_BASE,
            Material.PISTON_EXTENSION,
            Material.PISTON_MOVING_PIECE,
            Material.CAULDRON);

    public static boolean isStuck(Location location){
        Location head = location.clone().add(0, 1, 0);

        return ALLOWED_MATERIAL.contains(head.getBlock().getType());
    }
}
