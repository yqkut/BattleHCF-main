package cc.stormworth.hcf.misc.map.stats;

import cc.stormworth.hcf.misc.map.stats.command.StatsTopCommand;

import java.util.UUID;

public class StatsEntry {
    private boolean modified;
    private final UUID owner;
    private int kills;
    private int deaths;
    private int killstreak;
    private int highestKillstreak;

    public StatsEntry(final UUID owner) {
        this.owner = owner;
    }

    public void addKill() {
        ++this.kills;
        ++this.killstreak;
        if (this.highestKillstreak < this.killstreak) {
            this.highestKillstreak = this.killstreak;
        }
        this.modified = true;
    }

    public void addDeath() {
        ++this.deaths;
        this.killstreak = 0;
        this.modified = true;
    }

    public void clear() {
        this.kills = 0;
        this.deaths = 0;
        this.killstreak = 0;
        this.highestKillstreak = 0;
        this.modified = true;
    }

    public double getKD() {
        if (this.getDeaths() == 0) {
            return 0.0;
        }
        return this.getKills() / this.getDeaths();
    }

    public Number get(final StatsTopCommand.StatsObjective objective) {
        switch (objective) {
            case KILLS: {
                return this.getKills();
            }
            case DEATHS: {
                return this.getDeaths();
            }
            case KD: {
                return this.getKD();
            }
            case HIGHEST_KILLSTREAK: {
                return this.getHighestKillstreak();
            }
            default: {
                return 0;
            }
        }
    }

    protected boolean isModified() {
        return this.modified;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(final int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }

    public int getKillstreak() {
        return this.killstreak;
    }

    public void setKillstreak(final int killstreak) {
        this.killstreak = killstreak;
    }

    public int getHighestKillstreak() {
        return this.highestKillstreak;
    }
}