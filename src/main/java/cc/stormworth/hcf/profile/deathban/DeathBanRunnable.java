package cc.stormworth.hcf.profile.deathban;

import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

public class DeathBanRunnable implements Runnable{

    @Getter private final Set<UUID> onlineDeathBannedPlayers = Sets.newHashSet();

    @Override
    public void run() {
        onlineDeathBannedPlayers.removeIf(uuid -> HCFProfile.getByUUID(uuid) == null);

        for(UUID uuid : onlineDeathBannedPlayers){
            HCFProfile profile = HCFProfile.getByUUID(uuid);

            DeathBan deathban = profile.getDeathban();

            if (deathban != null && deathban.isExpired()) {
                deathban.revive(uuid);

                onlineDeathBannedPlayers.remove(uuid);
            }
        }

    }
}
