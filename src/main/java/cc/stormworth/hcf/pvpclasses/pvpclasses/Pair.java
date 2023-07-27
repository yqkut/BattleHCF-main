package cc.stormworth.hcf.pvpclasses.pvpclasses;

import lombok.Getter;

@Getter
public class Pair<K, V> {
    public K key;
    public V value;

    public Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
}
