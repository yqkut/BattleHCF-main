package cc.stormworth.hcf.deathmessage;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.deathmessage.listeners.DamageListener;
import cc.stormworth.hcf.deathmessage.objects.Damage;
import cc.stormworth.hcf.deathmessage.trackers.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeathMessageHandler {

    private static final Map<String, List<Damage>> damage = new HashMap<>();

    public static void init() {
        Main.getInstance().getServer().getPluginManager().registerEvents(new DamageListener(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new GeneralTracker(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new PVPTracker(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new EntityTracker(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new FallTracker(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new ArrowTracker(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new VoidTracker(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new BurnTracker(), Main.getInstance());
    }

    public static List<Damage> getDamage(final Player player) {
        return DeathMessageHandler.damage.get(player.getName());
    }

    public static void addDamage(final Player player, final Damage addedDamage) {
        if (!DeathMessageHandler.damage.containsKey(player.getName())) {
            DeathMessageHandler.damage.put(player.getName(), new ArrayList<>());
        }
        final List<Damage> damageList = DeathMessageHandler.damage.get(player.getName());
        while (damageList.size() > 30) {
            damageList.remove(0);
        }
        damageList.add(addedDamage);
    }

    public static void clearDamage(final Player player) {
        DeathMessageHandler.damage.remove(player.getName());
    }
}