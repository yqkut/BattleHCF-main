package cc.stormworth.hcf.misc.war.arena;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Objects;

@Getter
@Setter
public final class FactionWarArena {

    private final String name;

    private Location team1Spawn;
    private Location team2Spawn;

    private boolean enabled = false;
    private boolean inUse = false;

    public FactionWarArena(String name) {
        this.name = Preconditions.checkNotNull(name, "Name can't be null.");
    }

    public boolean canEnable() {
        return this.team1Spawn != null && this.team2Spawn != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FactionWarArena)) {
            return false;
        }

        return ((FactionWarArena) obj).getName().equals(this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
