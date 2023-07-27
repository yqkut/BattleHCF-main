package cc.stormworth.hcf.misc.lunarclient;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import com.lunarclient.bukkitapi.event.LCPlayerRegisterEvent;
import com.lunarclient.bukkitapi.event.LCPlayerUnregisterEvent;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.serverrule.LunarClientAPIServerRule;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class LunarClientManager implements Listener {

    private final Set<UUID> players;
    private final CooldownManager cooldownManager;
    private final WaypointManager waypointManager;

    public LunarClientManager() {
        this.players = new HashSet<>();

        this.cooldownManager = new CooldownManager();
        this.waypointManager = new WaypointManager();

        LunarClientAPIServerRule.setRule(ServerRule.LEGACY_ENCHANTING, true);
        LunarClientAPIServerRule.setRule(ServerRule.LEGACY_COMBAT, true);

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void disable() {
        this.players.clear();

        if (this.cooldownManager != null) {
            this.cooldownManager.disable();
        }

        if (this.waypointManager != null) {
            this.waypointManager.disable();
        }
    }

    @EventHandler
    public void onPlayerRegisterLC(LCPlayerRegisterEvent event) {
        Player player = event.getPlayer();
        this.players.add(player.getUniqueId());
        LunarClientAPIServerRule.sendServerRule(event.getPlayer());
    }

    @EventHandler
    public void onPlayerUnregisterLC(LCPlayerUnregisterEvent event) {
        this.players.remove(event.getPlayer().getUniqueId());
    }
}