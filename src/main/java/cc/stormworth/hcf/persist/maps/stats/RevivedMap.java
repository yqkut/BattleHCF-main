package cc.stormworth.hcf.persist.maps.stats;

import cc.stormworth.hcf.persist.PersistMap;

import java.util.UUID;

public class RevivedMap extends PersistMap<Boolean> {
    public RevivedMap() {
        super("Revived");
    }

    @Override
    public String getRedisValue(final Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(final String str) {
        return Boolean.valueOf(str);
    }

    public void setRevived(final UUID update, final boolean toggled) {
        this.updateValueAsync(update, toggled);
    }

    public boolean isRevived(final UUID check) {
        return this.contains(check) ? this.getValue(check) : false;
    }

    public void wipeVals() {
        this.wipeValues();
    }
}