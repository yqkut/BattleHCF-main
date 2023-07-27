package cc.stormworth.hcf.persist;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PersistMap<T> {
    protected Map<UUID, T> wrappedMap;
    protected Map<String, T> stringwrappedMap;
    private final String keyPrefix;

    public PersistMap(final String keyPrefix) {
        this.wrappedMap = new HashMap<>();
        this.stringwrappedMap = new HashMap<>();
        this.keyPrefix = keyPrefix;
        this.loadFromRedis();
    }

    public void loadFromRedis() {
        CorePlugin.getInstance().runRedisCommand(redis -> {
            final Map<String, String> results = redis.hgetAll(PersistMap.this.keyPrefix);
            for (final Map.Entry<String, String> resultEntry : results.entrySet()) {
                final T object = PersistMap.this.getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());
                if (object != null) {
                    PersistMap.this.wrappedMap.put(UUID.fromString(resultEntry.getKey()), object);
                }
            }
            return null;
        });
    }

    protected void wipeValues() {
        this.wrappedMap.clear();
        CorePlugin.getInstance().runRedisCommand(redis -> {
            redis.del(PersistMap.this.keyPrefix);
            return null;
        });
    }

    protected void updateValueSync(final UUID key, final T value) {
        this.wrappedMap.put(key, value);
        CorePlugin.getInstance().runRedisCommand(redis -> {
            redis.hset(PersistMap.this.keyPrefix, key.toString(), PersistMap.this.getRedisValue(PersistMap.this.getValue(key)));
            return null;
        });
    }

    protected void updateValueAsync(final UUID key, final T value) {
        this.wrappedMap.put(key, value);
        new BukkitRunnable() {
            public void run() {
                CorePlugin.getInstance().runRedisCommand(redis -> {
                    redis.hset(PersistMap.this.keyPrefix, key.toString(), PersistMap.this.getRedisValue(PersistMap.this.getValue(key)));
                    return null;
                });
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    protected T getValue(final UUID key) {
        return this.wrappedMap.get(key);
    }

    protected boolean contains(final UUID key) {
        return this.wrappedMap.containsKey(key);
    }

    public abstract String getRedisValue(final T p0);

    public T getJavaObjectSafe(final String key, final String redisValue) {
        try {
            return this.getJavaObject(redisValue);
        } catch (Exception e) {
            System.out.println("Error parsing Redis result.");
            System.out.println(" - Prefix: " + this.keyPrefix);
            System.out.println(" - Key: " + key);
            System.out.println(" - Value: " + redisValue);
            return null;
        }
    }

    public abstract T getJavaObject(final String p0);
}