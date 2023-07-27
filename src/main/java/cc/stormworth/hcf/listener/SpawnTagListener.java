package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.general.PlayerUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.op.EndEventCommand;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnTagListener implements Listener {

    private static final ArrayList<UUID> Tags = new ArrayList<UUID>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        if (damager != null && damager != event.getEntity()) {
            if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END && EndEventCommand.started) {
                event.setCancelled(true);
                return;
            }
            if (Main.getInstance().getMapHandler().isKitMap()) {
                SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
                SpawnTagHandler.addPassiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
                return;
            }
            if (LandBoard.getInstance().getTeam(event.getEntity().getLocation()) == null ||
                    LandBoard.getInstance().getTeam(event.getEntity().getLocation()) != null &&
                            LandBoard.getInstance().getTeam(event.getEntity().getLocation()).getOwner() == null) {

                SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
                SpawnTagHandler.addOffensiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
                return;
            }

            if (Tags.contains(damager.getUniqueId()) && Tags.contains(event.getEntity().getUniqueId())) {
                SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
                SpawnTagHandler.addOffensiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
                Tags.remove(damager.getUniqueId());
                Tags.remove(event.getEntity().getUniqueId());
            } else if (SpawnTagHandler.isTagged(damager)) {
                if (TimeUnit.MILLISECONDS.toSeconds(SpawnTagHandler.getTag(damager)) <= SpawnTagHandler.getPassiveTime()) {
                    SpawnTagHandler.addPassiveSeconds(damager, SpawnTagHandler.getPassiveTime());
                    SpawnTagHandler.addPassiveSeconds((Player) event.getEntity(), SpawnTagHandler.getPassiveTime());
                    Tags.add(damager.getUniqueId());
                } else {
                    SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
                    SpawnTagHandler.addOffensiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
                }
            } else {
                SpawnTagHandler.addPassiveSeconds(damager, SpawnTagHandler.getPassiveTime());
                SpawnTagHandler.addPassiveSeconds((Player) event.getEntity(), SpawnTagHandler.getPassiveTime());
            }
        }
    }
}