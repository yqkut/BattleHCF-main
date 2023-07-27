package cc.stormworth.hcf.persist.maps.misc;

import cc.stormworth.hcf.persist.StringPersistMap;

public class SupportedMap extends StringPersistMap<Boolean> {

    public SupportedMap() {
        super("Supported");
    }

    @Override
    public String getRedisValue(final Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(final String str) {
        return Boolean.valueOf(str);
    }

    public void setSupported(final String update, final boolean toggled) {
        this.updateValueAsync(update, toggled);
    }

    public boolean hasSupported(final String check) {
        return this.contains(check) && this.getValue(check);
    }

    public void wipeVals() {
        this.wipeValues();
    }
}