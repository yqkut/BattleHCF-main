package cc.stormworth.hcf.deathrefound;

import cc.stormworth.core.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class DeathRefoundListener implements Listener {


    @EventHandler
    public void onKick(PlayerKickEvent event){
        if (CC.strip(event.getReason()).contains("has been kicked for ignored too many packets")){
            Bukkit.getLogger().info(event.getPlayer().getName() + " Kicked for packet spam");
        }
    }

}
