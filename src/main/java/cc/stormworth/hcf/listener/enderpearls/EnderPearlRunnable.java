package cc.stormworth.hcf.listener.enderpearls;

import cc.stormworth.hcf.profile.HCFProfile;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class EnderPearlRunnable implements Runnable{

    @Getter private final Map<UUID, Long> players = Maps.newConcurrentMap();
    @Getter private final Map<UUID, Long> lastDamageSuffocation = Maps.newConcurrentMap();

    @Override
    public void run() {

        if(players.isEmpty()){
            return;
        }

        players.forEach((uuid, time) -> {

            if(time <= System.currentTimeMillis()){

                Player player = Bukkit.getPlayer(uuid);

                if(player == null){
                    players.remove(uuid);
                    return;
                }


                if (!EnderPearlStuckListener.isStuck(player.getLocation()) && !isRecentlyDamaged(player)) {
                    players.remove(uuid);
                    lastDamageSuffocation.remove(uuid);
                    return;
                }

                HCFProfile profile = HCFProfile.get(player);

                if (profile.getLastPearlLocation() == null){
                    players.remove(uuid);
                    lastDamageSuffocation.remove(uuid);
                    return;
                }

                Location location = profile.getLastPearlLocation().getLocation(); //By 6k2

                if (location == null) {
                    players.remove(uuid);
                    lastDamageSuffocation.remove(uuid);
                    return;
                }

                /*TitleBuilder titleBuilder = new TitleBuilder(
                        "&6&lBattle &edetected a pearl &6glitched&e.",
                        "&aLast location teleported.",
                        10, 15, 10);

                titleBuilder.send(player);
                player.sendMessage(CC.translate("&aYou have been teleported to your last location."));*/
                player.teleport(location);

                players.remove(uuid);

                lastDamageSuffocation.remove(uuid);
            }
        });
    }

    private boolean isRecentlyDamaged(Player player){
       if (lastDamageSuffocation.containsKey(player.getUniqueId())) {
           long time = System.currentTimeMillis() - lastDamageSuffocation.get(player.getUniqueId());

           return time < 100;
       }

       return false;
    }
}
