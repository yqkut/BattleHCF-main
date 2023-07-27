package cc.stormworth.hcf.persist;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public abstract class StringPersistMap<T> {
    protected Map<String, T> wrappedMap;
    private final String keyPrefix;

    public StringPersistMap(final String keyPrefix) {
        this.wrappedMap = new HashMap<>();
        this.keyPrefix = keyPrefix;
        this.loadFromRedis();
    }

    public void loadFromRedis() {
        CorePlugin.getInstance().runRedisCommand(redis -> {
            final Map<String, String> results = redis.hgetAll(StringPersistMap.this.keyPrefix);
            for (final Map.Entry<String, String> resultEntry : results.entrySet()) {
                final T object = StringPersistMap.this.getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());
                if (object != null) {
                    StringPersistMap.this.wrappedMap.put(resultEntry.getKey(), object);
                }
            }
            return null;
        });
    }

    protected void wipeValues() {
        this.wrappedMap.clear();
        CorePlugin.getInstance().runRedisCommand(redis -> {
            redis.del(StringPersistMap.this.keyPrefix);
            return null;
        });
    }

    protected void updateValueSync(final String key, final T value) {
        this.wrappedMap.put(key, value);
        CorePlugin.getInstance().runRedisCommand(redis -> {
            redis.hset(StringPersistMap.this.keyPrefix, key, StringPersistMap.this.getRedisValue(StringPersistMap.this.getValue(key)));
            return null;
        });
    }

    protected void updateValueAsync(final String key, final T value) {
        this.wrappedMap.put(key, value);
        new BukkitRunnable() {
            public void run() {
                CorePlugin.getInstance().runRedisCommand(redis -> {
                    redis.hset(StringPersistMap.this.keyPrefix, key, StringPersistMap.this.getRedisValue(StringPersistMap.this.getValue(key)));
                    return null;
                });
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    protected T getValue(final String key) {
        return this.wrappedMap.get(key);
    }

    protected boolean contains(final String key) {
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
