package cc.stormworth.hcf.persist.maps.misc;

import cc.stormworth.hcf.persist.PersistMap;

import java.util.UUID;

public class CreatorsCountMap extends PersistMap<Integer> {
    public CreatorsCountMap() {
        super("CreatorsCount");
    }

    @Override
    public String getRedisValue(final Integer vote) {
        return String.valueOf(vote);
    }

    @Override
    public Integer getJavaObject(final String str) {
        return Integer.parseInt(str);
    }

    public int getVotes(final UUID check) {
        return this.contains(check) ? this.getValue(check) : 0;
    }

    public void setVotes(final UUID update, final int vote) {
        this.updateValueAsync(update, vote);
    }

    public void addVote(final UUID update) {
        this.updateValueAsync(update, this.getVotes(update) + 1);
    }

    public void wipeVals() {
        this.wipeValues();
    }
}