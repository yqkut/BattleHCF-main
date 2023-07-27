package cc.stormworth.hcf.persist.maps.stats;

import cc.stormworth.hcf.persist.PersistMap;

import java.util.UUID;

public class OppleMap extends PersistMap<Long> {
    public OppleMap() {
        super("OppleCooldowns");
    }

    @Override
    public String getRedisValue(final Long time) {
        return String.valueOf(time);
    }

    @Override
    public Long getJavaObject(final String str) {
        return Long.parseLong(str);
    }

    public boolean isOnCooldown(final UUID check) {
        return this.getValue(check) != null && this.getValue(check) > System.currentTimeMillis();
    }

    public void useGoldenApple(final UUID update, final long seconds) {
        this.updateValueAsync(update, System.currentTimeMillis() + seconds * 1000L);
    }

    public void resetCooldown(final UUID update) {
        this.updateValueAsync(update, 0L);
    }

    public long getCooldown(final UUID check) {
        return this.contains(check) ? this.getValue(check) : -1L;
    }

    public void wipeVals() {
        this.wipeValues();
    }
}