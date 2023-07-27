package cc.stormworth.hcf.profile.deathban;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


@Getter
public class DeathBan {

    private final long expireAt;

    public DeathBan(long expireAt) {
        this.expireAt = System.currentTimeMillis() + expireAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireAt;
    }

    public void revive(UUID revivedUUID) {
        Player player = Bukkit.getPlayer(revivedUUID);
        if (player != null) {

            TaskUtil.runAsync(Main.getInstance(), () -> Utils.removeThrownPearls(player));

            if (SpawnTagHandler.isTagged(player)) {
                SpawnTagHandler.removeTag(player);
            }

            player.getInventory().clear();
            player.getOpenInventory().getTopInventory().clear();
            player.getInventory().setArmorContents(null);

            HCFProfile profile = HCFProfile.get(player);

            profile.setPvpTimer(new PvPTimer(false));

            if(Bukkit.isPrimaryThread()){
                player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
            }else{
                TaskUtil.run(Main.getInstance(), () -> player.teleport(Main.getInstance().getServerHandler().getSpawnLocation()));
            }

            profile.setDeathban(null);

            if (player.hasMetadata("deathban")) {
                player.removeMetadata("deathban", Main.getInstance());
            }
        }
    }
}
