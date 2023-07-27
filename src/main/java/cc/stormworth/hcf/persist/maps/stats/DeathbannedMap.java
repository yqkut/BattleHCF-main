package cc.stormworth.hcf.persist.maps.stats;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.persist.PersistMap;

import java.util.UUID;

public class DeathbannedMap extends PersistMap<Boolean> {
    public DeathbannedMap() {
        super("Deathbaned");
    }

    @Override
    public String getRedisValue(final Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(final String str) {
        return Boolean.valueOf(str);
    }

    public void setDeathbanned(final UUID update, final boolean toggled) {
        this.updateValueAsync(update, toggled);
    }

    public boolean isDeathbanned(final UUID check) {
        if (Main.getInstance().getMapHandler().isKitMap()) return false;
        return this.contains(check) && this.getValue(check);
    }

    public void wipeVals() {
        this.wipeValues();
    }

    public void wipeDeathbans() {
        this.wipeValues();
    }
}